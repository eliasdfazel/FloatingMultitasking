package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.UI

import android.content.ComponentName
import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.UI.Extensions.setupAthenticationUIWindow
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass

class AuthenticationUI : FragmentActivity() {

    /*
    *
    * define timer
    *
    *
    * */

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    companion object {
        lateinit var authenticationCallback: AuthenticationCallback
    }

    private var attemptCounter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAthenticationUIWindow()

        val packageName: String = intent.getStringExtra("PackageName")!!
        val className: String = intent.getStringExtra("ClassName")!!
        val activityInformation = packageManager.getActivityInfo(ComponentName(packageName, className),0)

        val biometricManager = BiometricManager.from(applicationContext)
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS){

            val authenticationExecutor = ContextCompat.getMainExecutor(applicationContext)

            val biometricCallback = object: BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errorString: CharSequence) {
                    super.onAuthenticationError(errorCode, errorString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    authenticationCallback.failedAuthenticated()
                }

                override fun onAuthenticationSucceeded(authenticationResult: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(authenticationResult)

                    authenticationCallback.authenticatedFloatingShortcuts()

                    this@AuthenticationUI.finish()
                }
            }

            val biometricPrompt = BiometricPrompt(this@AuthenticationUI,
                    authenticationExecutor,
                    biometricCallback)

            val biometricPromptPromptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("${functionsClass.activityLabel(activityInformation)} 🔒")
                    .setSubtitle("")
                    .setDescription("")
                    .setDeviceCredentialAllowed(true)
                    .build()

            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {

                biometricPrompt
                        .authenticate(biometricPromptPromptInfo)
            } else {

                authenticationCallback
                        .failedAuthenticated()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        this@AuthenticationUI.finish()
    }
}