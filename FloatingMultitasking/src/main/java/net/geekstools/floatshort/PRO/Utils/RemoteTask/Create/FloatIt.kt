package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.Utils.OpenActions
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PreferencesIO
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class FloatIt : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(this@FloatIt.javaClass.simpleName, "Extended Float It")

        val functionsClassLegacy = FunctionsClassLegacy(applicationContext)

        val preferencesIO = PreferencesIO(applicationContext)

        val securityFunctions = SecurityFunctions(applicationContext)

        val openActions = OpenActions(applicationContext, functionsClassLegacy)

        PublicVariable.floatingSizeNumber = preferencesIO.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), resources.displayMetrics).toInt()

        if (intent.hasExtra("PackageName")
            && functionsClassLegacy.appIsInstalled(intent.getStringExtra("PackageName")!!)) {

            val aPackageName = intent.getStringExtra("PackageName")!!

            val aClassName = if (intent.hasExtra("ClassName")) {

                intent.getStringExtra("ClassName")

            } else {

                null

            }

            val sharedPreferencesPosition = getSharedPreferences(aPackageName, Context.MODE_PRIVATE)

            val xPosition = sharedPreferencesPosition.getInt("X", 137)
            val yPosition = sharedPreferencesPosition.getInt("Y", 137)

            Log.d(this@FloatIt.javaClass.simpleName, "Extended Float It: ${functionsClassLegacy.FreeForm()}")
            openingCheckpoint(functionsClassLegacy, securityFunctions, openActions,
                aPackageName, aClassName, xPosition, yPosition)

            if (!functionsClassLegacy.FreeForm()) {

                Toast.makeText(applicationContext, "Enable Extended Float It In Preferences ⚙️", Toast.LENGTH_LONG).show()

            }

        } else {

            this@FloatIt.finish()

        }

    }

    private fun openingCheckpoint(functionsClassLegacy: FunctionsClassLegacy, securityFunctions: SecurityFunctions, openActions: OpenActions,
        aPackageName: String, aClassName: String?, xPosition: Int, yPosition: Int) {

        if (securityFunctions.isAppLocked(aPackageName)) {

            invokeSecurityServices(functionsClassLegacy, openActions)

        } else {

            if (aClassName.isNullOrEmpty()) {

                FloatingServices(applicationContext)
                    .runUnlimitedShortcutsServicePackage(aPackageName)

                openActions.startProcess(aPackageName, xPosition, yPosition, PublicVariable.floatingViewsHW)

            } else {

                FloatingServices(applicationContext)
                    .runUnlimitedShortcutsService(aPackageName, aClassName)

                openActions.startProcess(aPackageName, aClassName, xPosition, yPosition, PublicVariable.floatingViewsHW)

            }

            this@FloatIt.finish()

        }

    }

    private fun invokeSecurityServices(functionsClassLegacy: FunctionsClassLegacy, openActions: OpenActions) {

        val aPackageName = intent.getStringExtra("PackageName")!!

        val aClassName = if (intent.hasExtra("ClassName")) {

            intent.getStringExtra("ClassName")

        } else {

            null

        }

        val sharedPreferencesPosition = getSharedPreferences(aClassName, Context.MODE_PRIVATE)

        val xPosition = sharedPreferencesPosition.getInt("X", 137)
        val yPosition = sharedPreferencesPosition.getInt("Y", 137)

        SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

            override fun authenticatedFloatIt() {
                super.authenticatedFloatIt()
                Log.d(this@FloatIt.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                if (aClassName == null) {

                    openActions.startProcess(aPackageName, xPosition, yPosition, PublicVariable.floatingViewsHW)

                } else {

                    openActions.startProcess(aPackageName, aClassName, xPosition, yPosition, PublicVariable.floatingViewsHW)

                }

                this@FloatIt.finish()

            }

            override fun failedAuthenticated() {
                super.failedAuthenticated()
                Log.d(this@FloatIt.javaClass.simpleName, "Failed Authenticated")

                this@FloatIt.finish()

            }

            override fun invokedPinPassword() {
                super.invokedPinPassword()
                Log.d(this@FloatIt.javaClass.simpleName, "Invoked Pin Password")
            }

        }

        startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
            putExtra("PackageName", aPackageName)
            putExtra("ClassName", aClassName)
            putExtra("PrimaryColor", functionsClassLegacy.extractDominantColor(functionsClassLegacy.applicationIcon(packageName)))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())

    }

}