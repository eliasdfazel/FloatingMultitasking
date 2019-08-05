package net.geekstools.floatshort.PRO.Util.RemoteTask

import android.app.Activity
import android.os.Bundle
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity

class FloatingWidgetHomeScreenShortcuts : Activity() {

    lateinit var functionsClass: FunctionsClass
    lateinit var functionsClassSecurity: FunctionsClassSecurity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        functionsClass = FunctionsClass(applicationContext, this@FloatingWidgetHomeScreenShortcuts)
        functionsClassSecurity = FunctionsClassSecurity(this@FloatingWidgetHomeScreenShortcuts, applicationContext)

        val packageName = intent.getStringExtra("PackageName")
        val appWidgetId = intent.getIntExtra("ShortcutsId", -1)
        val widgetLabel = intent.getStringExtra("ShortcutLabel")

        if (functionsClassSecurity.isAppLocked(packageName + appWidgetId)) {
            FunctionsClassSecurity.AuthOpenAppValues.authWidgetId = appWidgetId
            FunctionsClassSecurity.AuthOpenAppValues.authComponentName = widgetLabel
            FunctionsClassSecurity.AuthOpenAppValues.authSecondComponentName = packageName
            FunctionsClassSecurity.AuthOpenAppValues.authFloatingWidget = true

            functionsClassSecurity.openAuthInvocation()
        } else {
            functionsClass.runUnlimitedWidgetService(appWidgetId, widgetLabel)
        }

        finish()
    }
}