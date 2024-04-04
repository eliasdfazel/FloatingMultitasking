/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint

import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.setupAuthenticationUIText
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.setupAuthenticationUIWindow
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.PinPassword.AuthenticationPinPassword
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy

class AuthenticationFingerprint : FragmentActivity() {

    val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    companion object {
        var attemptCounter: Int = 0
    }

    private val maximumAttempt: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAuthenticationUIWindow()

        val dialogueTitle: String = setupAuthenticationUIText()

        val biometricManager = BiometricManager.from(applicationContext)
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS){

            val authenticationExecutor = ContextCompat.getMainExecutor(applicationContext)

            val biometricCallback = object: BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errorString: CharSequence) {
                    super.onAuthenticationError(errorCode, errorString)

                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> {

                            SecurityInterfaceHolder.authenticationCallback
                                    .failedAuthenticated()

                            this@AuthenticationFingerprint.finish()
                        }
                        BiometricPrompt.ERROR_CANCELED -> {

                            SecurityInterfaceHolder.authenticationCallback
                                    .failedAuthenticated()

                            this@AuthenticationFingerprint.finish()
                        }
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {

                            SecurityInterfaceHolder.authenticationCallback
                                    .failedAuthenticated()

                            this@AuthenticationFingerprint.finish()
                        }
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> {

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> {

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        BiometricPrompt.ERROR_LOCKOUT -> {

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        BiometricPrompt.ERROR_TIMEOUT -> {

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        else -> {

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    SecurityInterfaceHolder.authenticationCallback
                            .failedAuthenticated()

                    attemptCounter++
                    if (attemptCounter == maximumAttempt) {

                        triggerPinPasswordFragment(dialogueTitle)

                    }

                }

                override fun onAuthenticationSucceeded(authenticationResult: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(authenticationResult)

                    SecurityInterfaceHolder.authenticationCallback
                            .authenticatedFloatIt()

                    attemptCounter = 0
                    this@AuthenticationFingerprint.finish()
                }
            }

            val biometricPrompt = BiometricPrompt(this@AuthenticationFingerprint,
                    authenticationExecutor,
                    biometricCallback)

            val biometricPromptPromptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("$dialogueTitle")
                    .setSubtitle("")
                    .setDescription("")
                    .setDeviceCredentialAllowed(true)
                    .build()

            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {

                biometricPrompt
                        .authenticate(biometricPromptPromptInfo)

            } else {

                triggerPinPasswordFragment(dialogueTitle)

            }
        }
    }

    override fun onDestroy() {
        SecurityInterfaceHolder.authenticationCallback.failedAuthenticated()

        super.onDestroy()
    }

    override fun onBackPressed() {
        attemptCounter = 0

        SecurityInterfaceHolder.authenticationCallback.failedAuthenticated()

        this@AuthenticationFingerprint.finish()
    }

    private fun triggerPinPasswordFragment(dialogueTitle: String) {
        SecurityInterfaceHolder.authenticationCallback.invokedPinPassword()

        val extraInformation = Bundle()
        extraInformation.putString("DialogueTitle", dialogueTitle)
        extraInformation.putInt("PrimaryColor", intent.getIntExtra("PrimaryColor", getColor(R.color.default_color)))

        val authenticationPinPassword: AuthenticationPinPassword = AuthenticationPinPassword()
        authenticationPinPassword.arguments = extraInformation
        authenticationPinPassword.show(supportFragmentManager, this@AuthenticationFingerprint.javaClass.simpleName)
    }
}