package com.partner.taxi

import android.Manifest
import android.content.Context
import android.util.Base64
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityStartTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA,
        Manifest.permission.POST_NOTIFICATIONS
    )

    @Test
    fun reopeningAppWithActiveSessionOpensDashboard() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val deviceId = "testDevice"
        SessionManager.saveDeviceId(context, deviceId)
        val exp = (System.currentTimeMillis() / 1000) + 3600
        val header = Base64.encodeToString("{}".toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
        val payloadJson = "{\"exp\":$exp,\"device_id\":\"$deviceId\"}"
        val payload = Base64.encodeToString(payloadJson.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
        val token = "$header.$payload.signature"
        SessionManager.saveToken(context, token)
        SessionManager.saveSessionId(context, "session123")
        SessionManager.saveVehiclePlate(context, "XYZ123")

        Intents.init()
        ActivityScenario.launch(LoginActivity::class.java)
        Intents.intended(hasComponent(DashboardActivity::class.java.name))
        Intents.release()
    }
}