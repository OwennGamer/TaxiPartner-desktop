package com.partner.taxi

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Streams handled errors and crashes to the backend so support can diagnose
 * mobile incidents just like desktop ones.
 */
object RemoteLogService {

    private val client = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build()

    private val executor = Executors.newSingleThreadExecutor(object : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            return Thread(runnable, "mobile-remote-log").apply { isDaemon = true }
        }
    })

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    @Volatile
    private var appContext: Context? = null

    @Volatile
    private var installed = false

    @Volatile
    private var previousHandler: Thread.UncaughtExceptionHandler? = null

    fun install(context: Context) {
        if (installed) return
        synchronized(this) {
            if (installed) return
            appContext = context.applicationContext
            previousHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                logCrash(
                    summary = "Nieobsłużony wyjątek: ${throwable.javaClass.simpleName}",
                    throwable = throwable
                )
                previousHandler?.uncaughtException(thread, throwable)
            }
            installed = true
        }
    }

    fun logHandledException(summary: String, throwable: Throwable, metadata: Map<String, Any?>? = null) {
        log(
            level = "ERROR",
            summary = summary,
            throwable = throwable,
            details = null,
            metadata = metadata,
            urgent = false
        )
    }

    fun logWarning(summary: String, details: String? = null, metadata: Map<String, Any?>? = null) {
        log(
            level = "WARN",
            summary = summary,
            throwable = null,
            details = details,
            metadata = metadata,
            urgent = false
        )
    }

    fun logInfo(summary: String, details: String? = null, metadata: Map<String, Any?>? = null) {
        log(
            level = "INFO",
            summary = summary,
            throwable = null,
            details = details,
            metadata = metadata,
            urgent = false
        )
    }

    fun logCrash(summary: String, throwable: Throwable, metadata: Map<String, Any?>? = null) {
        log(
            level = "FATAL",
            summary = summary,
            throwable = throwable,
            details = null,
            metadata = metadata,
            urgent = true
        )
    }

    fun logMessage(
        level: String,
        summary: String,
        details: String? = null,
        metadata: Map<String, Any?>? = null
    ) {
        log(
            level = level,
            summary = summary,
            throwable = null,
            details = details,
            metadata = metadata,
            urgent = false
        )
    }

    private fun log(
        level: String,
        summary: String?,
        throwable: Throwable?,
        details: String?,
        metadata: Map<String, Any?>?,
        urgent: Boolean
    ) {
        val ctx = appContext ?: return
        val payload = JSONObject()
        val safeLevel = normalizeLevel(level)
        val safeSummary = when {
            !summary.isNullOrBlank() -> summary.trim()
            throwable?.message.isNullOrBlank() -> "Nieznany błąd aplikacji"
            else -> throwable!!.message!!
        }

        payload.put("level", safeLevel)
        payload.put("summary", safeSummary)
        payload.put("source", "mobile")
        payload.put("app_version", BuildConfig.VERSION_NAME)
        payload.put("occurred_at", System.currentTimeMillis())

        val effectiveMessage = when {
            !details.isNullOrBlank() -> details.trim()
            throwable?.message.isNullOrBlank() -> null
            else -> throwable!!.message
        }
        effectiveMessage?.let { payload.put("message", it) }

        throwable?.let { payload.put("stacktrace", stackTraceToString(it)) }

        resolveDriverId(ctx)?.let { payload.put("driver_id", it) }
        resolveVehiclePlate(ctx)?.let { payload.put("license_plate", it) }

        metadata?.takeIf { it.isNotEmpty() }?.let { payload.put("metadata", it.toJson()) }

        val latch = if (urgent) CountDownLatch(1) else null
        executor.execute {
            try {
                submit(payload)
            } catch (ex: Exception) {
                Log.w("RemoteLogService", "Nie udało się wysłać logu: ${ex.message}")
            } finally {
                latch?.countDown()
            }
        }
        if (urgent) {
            try {
                latch?.await(2, TimeUnit.SECONDS)
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    private fun submit(payload: JSONObject) {
        val requestBody = payload.toString().toRequestBody(jsonMediaType)
        val requestBuilder = Request.Builder()
            .url(ApiClient.BASE_URL + "log_error.php")
            .post(requestBody)
            .addHeader("Accept", "application/json")
            .addHeader("Device-Id", resolveDeviceId() ?: "android_app")

        ApiClient.jwtToken?.takeIf { it.isNotBlank() }?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        client.newCall(requestBuilder.build()).execute().use { response ->
            if (!response.isSuccessful) {
                Log.w(
                    "RemoteLogService",
                    "Serwer zwrócił kod ${response.code} podczas wysyłki logu"
                )
            }
        }
    }

    private fun normalizeLevel(level: String?): String {
        return when (level?.uppercase()) {
            "DEBUG", "INFO", "WARN", "ERROR", "FATAL" -> level.uppercase()
            else -> "ERROR"
        }
    }

    private fun stackTraceToString(throwable: Throwable): String {
        val sw = StringWriter()
        PrintWriter(sw).use { pw ->
            throwable.printStackTrace(pw)
        }
        return sw.toString()
    }

    private fun Map<String, Any?>.toJson(): JSONObject {
        val json = JSONObject()
        for ((key, value) in this) {
            when (value) {
                null -> json.put(key, JSONObject.NULL)
                is Number, is Boolean, is String -> json.put(key, value)
                is Map<*, *> -> {
                    val nested = value.entries
                        .filter { it.key is String }
                        .associate { it.key as String to it.value }
                    json.put(key, nested.toJson())
                }
                else -> json.put(key, value.toString())
            }
        }
        return json
    }

    private fun resolveDriverId(context: Context): String? {
        return SessionManager.getDriverId(context).trim().takeIf { it.isNotEmpty() }
    }

    private fun resolveVehiclePlate(context: Context): String? {
        return SessionManager.getVehiclePlate(context)?.trim()?.takeIf { it.isNotEmpty() }
    }

    @SuppressLint("HardwareIds")
    private fun resolveDeviceId(): String? {
        ApiClient.deviceId?.trim()?.takeIf { it.isNotEmpty() }?.let { return it }
        val ctx = appContext ?: return null
        SessionManager.getDeviceId(ctx).trim().takeIf { it.isNotEmpty() }?.let { return it }
        return runCatching {
            Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID)
        }.getOrNull()?.trim()?.takeIf { it.isNotEmpty() }
    }
}