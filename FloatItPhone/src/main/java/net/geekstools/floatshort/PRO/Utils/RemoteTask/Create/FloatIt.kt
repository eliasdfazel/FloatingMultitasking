package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.Utils.OpenActions
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class FloatIt : AppCompatActivity() {

    val functionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    val securityFunctions by lazy {
        SecurityFunctions(applicationContext)
    }

    val openActions by lazy {
        OpenActions(applicationContext, functionsClassLegacy)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PublicVariable.floatingSizeNumber = functionsClassLegacy.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), resources.displayMetrics).toInt()

        val aPackageName = intent.getStringExtra("PackageName")!!
        val aClassName = intent.getStringExtra("ClassName")!!

        val sharedPreferencesPosition = getSharedPreferences(aClassName, Context.MODE_PRIVATE)


        val xPosition = sharedPreferencesPosition.getInt("X", 137)
        val yPosition = sharedPreferencesPosition.getInt("Y", 137)

        if (securityFunctions.isAppLocked(aPackageName)) {

            SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                override fun authenticatedFloatIt(extraInformation: Bundle?) {
                    super.authenticatedFloatIt(extraInformation)
                    Log.d(this@FloatIt.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                    openActions.startProcess(aPackageName, aClassName, xPosition, yPosition, PublicVariable.floatingViewsHW)

                }

                override fun failedAuthenticated() {
                    super.failedAuthenticated()
                    Log.d(this@FloatIt.javaClass.simpleName, "FailedAuthenticated")
                }

                override fun invokedPinPassword() {
                    super.invokedPinPassword()
                    Log.d(this@FloatIt.javaClass.simpleName, "InvokedPinPassword")
                }
            }

            startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
                putExtra("PackageName", aPackageName)
                putExtra("ClassName", aClassName)
                putExtra("PrimaryColor", functionsClassLegacy.extractDominantColor(functionsClassLegacy.applicationIcon(packageName)))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())


        } else {

            openActions.startProcess(aPackageName, aClassName, xPosition, yPosition, PublicVariable.floatingViewsHW)

        }

        this@FloatIt.finish()
    }

}