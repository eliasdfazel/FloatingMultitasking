/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/25/20 6:57 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteProcess

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.Settings
import android.text.Html
import com.google.android.vending.licensing.AESObfuscator
import com.google.android.vending.licensing.LicenseChecker
import com.google.android.vending.licensing.LicenseCheckerCallback
import com.google.android.vending.licensing.ServerManagedPolicy
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class LicenseValidator : Service() {

    companion object {
        private const val BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoO5yM+7oonMW+q8QYFXfc/Vemd5LHcOo9NVgFGezNpRmNedXI86EnuJX2rGWfTI4EHnb1Fkw" +
                "KkWfTnN6mus9ghwYomEoVQWIvXteoKo0PuGItQ8WSbFOSxvTnB8AMQ+y0EqMJ9TI+vjYNFoHVNnEf2ui10+TbnFzto2r3Z+0ZuHS1KxFBEhH" +
                "GwsokFn//CZ1vUZrdrN7MnL3zsPcjRS2k15JKdeZv+y+0N5Do5Q+taPNUbJ8HQlCny1V/o9ocfuzq9EvI4vZ1hReFJJOk2KyYVXF9O1D3Bb5" +
                "iE2Xza/2B3cjpH+N26QfCXKhk+KjOQ41K3qoKW843MUvz13h/yOmfwIDAQAB"

        private val SALT = byteArrayOf(
                -16, -13, 30, -128, -103, -57, 74, -64, 53, 88, -97, -45, 77, -113, -36, -113, -11, 32, -64, 89
        )
    }

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    lateinit var licenseChecker: LicenseChecker

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(666, functionsClass.notificationCreator(
                Html.fromHtml("<small><font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info_desc) + "</font></small>").toString(),
                getString(R.string.updating_info),
                666
        ))

        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        licenseChecker = LicenseChecker(
                applicationContext,
                ServerManagedPolicy(applicationContext, AESObfuscator(SALT, packageName, deviceId)),
                BASE64_PUBLIC_KEY
        )

        val licenseCheckerCallback: LicenseCheckerCallback = object : LicenseCheckerCallback {

            override fun allow(reason: Int) {

                functionsClass.saveFileAppendLine(".License", reason.toString())

                this@LicenseValidator.stopSelf()
            }

            override fun applicationError(errorCode: Int) {

            }

            override fun dontAllow(reason: Int) {

                sendBroadcast(Intent(getString(R.string.license)))

            }

        }

        licenseChecker.checkAccess(licenseCheckerCallback)

        return Service.START_NOT_STICKY
    }
}