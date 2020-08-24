/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 6:17 AM
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
import net.geekstools.floatshort.PRO.Utils.Functions.Debug
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class AuthenticationFingerprint : FragmentActivity() {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
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
                    Debug.PrintDebug("*** ${errorCode}. ${errorString} ***")

                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            Debug.PrintDebug("*** ERROR USER CANCELED ***")

                            SecurityInterfaceHolder.authenticationCallback
                                    .failedAuthenticated()

                            this@AuthenticationFingerprint.finish()
                        }
                        BiometricPrompt.ERROR_CANCELED -> {
                            Debug.PrintDebug("*** ERROR CANCELED ***")

                            SecurityInterfaceHolder.authenticationCallback
                                    .failedAuthenticated()

                            this@AuthenticationFingerprint.finish()
                        }
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            Debug.PrintDebug("*** ERROR LOCKOUT PERMANENT ***")

                            SecurityInterfaceHolder.authenticationCallback
                                    .failedAuthenticated()

                            this@AuthenticationFingerprint.finish()
                        }
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                            Debug.PrintDebug("*** ERROR HW NOT PRESENT ***")

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                            Debug.PrintDebug("*** ERROR HW UNAVAILABLE ***")

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        BiometricPrompt.ERROR_LOCKOUT -> {
                            Debug.PrintDebug("*** ERROR LOCKOUT ***")

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        BiometricPrompt.ERROR_TIMEOUT -> {
                            Debug.PrintDebug("*** ERROR TIMEOUT ***")

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        else -> {
                            Debug.PrintDebug("*** ERROR UNKNOWN ***")

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Debug.PrintDebug("*** Authentication Failed ***")

                    SecurityInterfaceHolder.authenticationCallback
                            .failedAuthenticated()

                    attemptCounter++
                    if (attemptCounter == maximumAttempt) {

                        triggerPinPasswordFragment(dialogueTitle)
                    }
                }

                override fun onAuthenticationSucceeded(authenticationResult: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(authenticationResult)
                    Debug.PrintDebug("*** Authentication Succeeded ***")

                    SecurityInterfaceHolder.authenticationCallback
                            .authenticatedFloatIt(null)

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