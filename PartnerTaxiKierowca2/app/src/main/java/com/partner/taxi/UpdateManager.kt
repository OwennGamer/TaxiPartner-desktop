package com.partner.taxi

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

object UpdateManager {
    private const val MIME_APK = "application/vnd.android.package-archive"

    fun checkForUpdates(
        activity: AppCompatActivity,
        showUpToDateToast: Boolean = false,
        onReady: (() -> Unit)? = null
    ) {
        ApiClient.apiService.getMobileUpdate(platform = "android")
            .enqueue(object : Callback<MobileUpdateResponse> {
                override fun onResponse(
                    call: Call<MobileUpdateResponse>,
                    response: Response<MobileUpdateResponse>
                ) {
                    val body = response.body()
                    val updateData = body?.data
                    val success = response.isSuccessful && body?.status == "success" &&
                            !updateData?.version.isNullOrBlank() && !updateData?.url.isNullOrBlank()

                    activity.runOnUiThread {
                        if (success && updateData != null) {
                            val latestVersion = updateData.version!!.trim()
                            if (isNewerVersion(latestVersion, BuildConfig.VERSION_NAME)) {
                                showUpdateDialog(activity, updateData, onReady)
                            } else {
                                if (showUpToDateToast) {
                                    Toast.makeText(
                                        activity,
                                        activity.getString(R.string.update_no_new_version),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                onReady?.invoke()
                            }
                        } else {
                            if (showUpToDateToast) {
                                Toast.makeText(
                                    activity,
                                    activity.getString(R.string.update_check_failed),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            onReady?.invoke()
                        }
                    }
                }

                override fun onFailure(call: Call<MobileUpdateResponse>, t: Throwable) {
                    activity.runOnUiThread {
                        if (showUpToDateToast) {
                            Toast.makeText(
                                activity,
                                activity.getString(R.string.update_check_error, t.localizedMessage ?: ""),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        onReady?.invoke()
                    }
                }
            })
    }

    private fun showUpdateDialog(
        activity: AppCompatActivity,
        data: MobileUpdateData,
        onReady: (() -> Unit)?
    ) {
        if (activity.isFinishing) {
            onReady?.invoke()
            return
        }

        val mandatory = data.mandatory == true
        val builder = AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.update_available_title, data.version))
            .setMessage(buildDialogMessage(activity, data))
            .setPositiveButton(R.string.update_action_install) { _, _ ->
                downloadAndInstall(activity, data)
            }

        if (!mandatory) {
            builder.setNegativeButton(R.string.update_action_later) { _, _ ->
                onReady?.invoke()
            }
            builder.setCancelable(true)
        } else {
            builder.setCancelable(false)
        }

        builder.show()
    }

    private fun buildDialogMessage(context: Context, data: MobileUpdateData): String {
        val message = StringBuilder()
        data.changelog?.takeIf { it.isNotBlank() }?.let {
            message.append(context.getString(R.string.update_changelog_title)).append('\n')
            message.append(it.trim()).append('\n').append('\n')
        }
        message.append(
            context.getString(
                R.string.update_current_version,
                BuildConfig.VERSION_NAME
            )
        )
        message.append('\n')
        message.append(context.getString(R.string.update_latest_version, data.version))
        if (data.mandatory == true) {
            message.append('\n').append('\n')
            message.append(context.getString(R.string.update_mandatory_note))
        }
        return message.toString()
    }

    private fun downloadAndInstall(activity: AppCompatActivity, data: MobileUpdateData) {
        val url = data.url ?: return
        val downloadManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val versionLabel = data.version ?: "latest"
        val targetFile = File(
            activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "taxi-partner-$versionLabel.apk"
        )
        if (targetFile.exists()) {
            targetFile.delete()
        }

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle(activity.getString(R.string.update_download_title))
            setDescription(activity.getString(R.string.update_download_description, versionLabel))
            setMimeType(MIME_APK)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationUri(Uri.fromFile(targetFile))
        }

        val downloadId = downloadManager.enqueue(request)
        Toast.makeText(
            activity,
            activity.getString(R.string.update_download_started),
            Toast.LENGTH_SHORT
        ).show()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val finishedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (finishedId == downloadId) {
                    try {
                        activity.unregisterReceiver(this)
                    } catch (_: IllegalArgumentException) {
                        // Receiver already unregistered
                    }
                    installApk(activity, downloadManager, downloadId, targetFile)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("DEPRECATION")
            activity.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    private fun installApk(
        activity: Activity,
        downloadManager: DownloadManager,
        downloadId: Long,
        apkFile: File
    ) {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        cursor?.use {
            if (it.moveToFirst()) {
                val status = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                if (status != DownloadManager.STATUS_SUCCESSFUL) {
                    Toast.makeText(activity, R.string.update_download_failed, Toast.LENGTH_LONG).show()
                    return
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canInstall = activity.packageManager.canRequestPackageInstalls()
            if (!canInstall) {
                AlertDialog.Builder(activity)
                    .setTitle(R.string.update_permission_title)
                    .setMessage(R.string.update_permission_message)
                    .setPositiveButton(R.string.update_permission_action) { _, _ ->
                        val intent = Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:${activity.packageName}")
                        )
                        activity.startActivity(intent)
                        Toast.makeText(
                            activity,
                            R.string.update_permission_toast,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                return
            }
        }

        val contentUri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.provider",
            apkFile
        )

        val installIntent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            data = contentUri
            type = MIME_APK
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
        }

        runCatching {
            activity.startActivity(installIntent)
        }.onFailure {
            Toast.makeText(activity, R.string.update_install_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split('.').mapNotNull { it.toIntOrNull() }
        val currentParts = current.split('.').mapNotNull { it.toIntOrNull() }
        val max = maxOf(latestParts.size, currentParts.size)
        for (i in 0 until max) {
            val l = latestParts.getOrNull(i) ?: 0
            val c = currentParts.getOrNull(i) ?: 0
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}