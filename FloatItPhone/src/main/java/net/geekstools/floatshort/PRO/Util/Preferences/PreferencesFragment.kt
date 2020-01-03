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

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass

class PreferencesFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {


    lateinit var functionsClass: FunctionsClass

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_screen, rootKey)

        functionsClass = FunctionsClass(context, activity)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)



    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this@PreferencesFragment)

    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this@PreferencesFragment)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }
}