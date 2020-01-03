/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/2/20 10:52 PM
 * Last modified 1/2/20 10:52 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.Preferences

import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.preferences_activity_view.*
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable

class PreferencesActivity : AppCompatActivity() {

    lateinit var functionsClass: FunctionsClass

    override fun getTheme(): Theme? {
        val theme = super.getTheme()
        if (PublicVariable.themeLightDark) {
            theme.applyStyle(R.style.GeeksEmpire_Preference_Light, true)
        } else if (!PublicVariable.themeLightDark) {
            theme.applyStyle(R.style.GeeksEmpire_Preference_Dark, true)
        }
        return theme
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences_activity_view)

        functionsClass = FunctionsClass(applicationContext, this@PreferencesActivity)

        if (functionsClass.appThemeTransparent()) {
            functionsClass.setThemeColorPreferences(fullPreferencesActivity, preferencesToolbar, true, getString(R.string.settingTitle), functionsClass.appVersionName(packageName))
        } else {
            functionsClass.setThemeColorPreferences(fullPreferencesActivity, preferencesToolbar, false, getString(R.string.settingTitle), functionsClass.appVersionName(packageName))
        }
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerPreferencesFragment, PreferencesFragment(), "PreferencesFragment")
                .commit()

    }
}