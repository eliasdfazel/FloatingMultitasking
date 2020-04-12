package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint

import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.setupAuthenticationUIText
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.setupAuthenticationUIWindow
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.PinPassword.AuthenticationPinPasswordUI
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug

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
                    FunctionsClassDebug.PrintDebug("*** ${errorCode}. ${errorString} ***")

                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            FunctionsClassDebug.PrintDebug("*** ERROR USER CANCELED ***")

                            SecurityInterfaceHolder.authenticationCallback
                                    .failedAuthenticated()

                            this@AuthenticationFingerprint.finish()
                        }
                        BiometricPrompt.ERROR_CANCELED -> {
                            FunctionsClassDebug.PrintDebug("*** ERROR CANCELED ***")

                            SecurityInterfaceHolder.authenticationCallback
                                    .failedAuthenticated()

                            this@AuthenticationFingerprint.finish()
                        }
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            FunctionsClassDebug.PrintDebug("*** ERROR LOCKOUT PERMANENT ***")

                            SecurityInterfaceHolder.authenticationCallback
                                    .failedAuthenticated()

                            this@AuthenticationFingerprint.finish()
                        }
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                            FunctionsClassDebug.PrintDebug("*** ERROR HW NOT PRESENT ***")

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                            FunctionsClassDebug.PrintDebug("*** ERROR HW UNAVAILABLE ***")

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        BiometricPrompt.ERROR_LOCKOUT -> {
                            FunctionsClassDebug.PrintDebug("*** ERROR LOCKOUT ***")

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        BiometricPrompt.ERROR_TIMEOUT -> {
                            FunctionsClassDebug.PrintDebug("*** ERROR TIMEOUT ***")

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                        else -> {
                            FunctionsClassDebug.PrintDebug("*** ERROR UNKNOWN ***")

                            triggerPinPasswordFragment(dialogueTitle)
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    FunctionsClassDebug.PrintDebug("*** Authentication Failed ***")

                    SecurityInterfaceHolder.authenticationCallback
                            .failedAuthenticated()

                    attemptCounter++
                    if (attemptCounter == maximumAttempt) {

                        triggerPinPasswordFragment(dialogueTitle)
                    }
                }

                override fun onAuthenticationSucceeded(authenticationResult: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(authenticationResult)
                    FunctionsClassDebug.PrintDebug("*** Authentication Succeeded ***")

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

        val authenticationPinPasswordUI: AuthenticationPinPasswordUI = AuthenticationPinPasswordUI()
        authenticationPinPasswordUI.arguments = extraInformation
        authenticationPinPasswordUI.show(supportFragmentManager, this@AuthenticationFingerprint.javaClass.simpleName)
    }
}