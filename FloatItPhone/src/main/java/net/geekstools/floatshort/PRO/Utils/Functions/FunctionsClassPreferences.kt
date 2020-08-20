/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/20/20 5:22 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.BindServices

class FunctionsClassPreferences (private val context: Context) {

    private val functionsClassIO: FunctionsClassIO = FunctionsClassIO(context)

    fun savePreference(PreferenceName: String?, KEY: String?, VALUE: String?) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putString(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String?, KEY: String?, VALUE: Int) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putInt(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String?, KEY: String?, VALUE: Long) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putLong(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String?, KEY: String?, VALUE: Float) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putFloat(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String?, KEY: String?, VALUE: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putBoolean(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun saveDefaultPreference(KEY: String?, VALUE: String?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putString(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun saveDefaultPreference(KEY: String?, VALUE: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putInt(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun saveDefaultPreference(KEY: String?, VALUE: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putBoolean(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun readPreference(PreferenceName: String?, KEY: String?, defaultVALUE: String?): String? {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getString(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String?, KEY: String?, defaultVALUE: Int): Int {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getInt(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String?, KEY: String?, defaultVALUE: Long): Long {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getLong(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String?, KEY: String?, defaultVALUE: Float): Float {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getFloat(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String?, KEY: String?, defaultVALUE: Boolean): Boolean {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getBoolean(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String?, defaultVALUE: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String?, defaultVALUE: String?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String?, defaultVALUE: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY, defaultVALUE)
    }

    /**
     * @param switch True Means Enable Lite Preferences & False Means Restore Default Preferences
     * @return Success Of Preferences Editing
     **/
    fun switchLitePreference(switch: Boolean) : Boolean {

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val defaultSharedPreferencesEditor = defaultSharedPreferences.edit()

        if (switch) {

            functionsClassIO.saveFileEmpty(".LitePreferenceCheckpoint")

            /*OFF Control Panel*/
            defaultSharedPreferencesEditor.putBoolean("stable", false)
            context.stopService(Intent(context, BindServices::class.java))

            /*Dark App Theme*/
            defaultSharedPreferencesEditor.putString(".themeColor", "2")


            /*OFF Blurry Theme*/
            defaultSharedPreferencesEditor.putBoolean("blur", false)

            /*OFF Transparent Theme*/
            defaultSharedPreferencesEditor.putBoolean("transparent", false)

            /*OFF Floating Splash*/
            defaultSharedPreferencesEditor.putBoolean("floatingSplash", false)

            /*OFF Floating Transparency*/
            defaultSharedPreferencesEditor.putInt("autoTrans", 255)
            defaultSharedPreferencesEditor.putInt("autoTransProgress", 0)

            /*No Icon Theme*/
            saveDefaultPreference("customIcon", context.packageName)

            /*No Icon Shape*/
            defaultSharedPreferencesEditor.putInt("iconShape", 0)

            saveDefaultPreference("LitePreferences", true)

        } else {

            context.deleteFile(".LitePreferenceCheckpoint")

            /*OFF Control Panel*/
            defaultSharedPreferencesEditor.putBoolean("stable", true)
            context.startService(Intent(context, BindServices::class.java))

            /*Dark App Theme*/
            defaultSharedPreferencesEditor.putString(".themeColor", "2")


            /*OFF Blurry Theme*/
            defaultSharedPreferencesEditor.putBoolean("blur", true)

            /*OFF Transparent Theme*/
            defaultSharedPreferencesEditor.putBoolean("transparent", true)

            /*OFF Floating Splash*/
            defaultSharedPreferencesEditor.putBoolean("floatingSplash", true)

            /*OFF Floating Transparency*/
            defaultSharedPreferencesEditor.putInt("autoTrans", 113)
            defaultSharedPreferencesEditor.putInt("autoTransProgress", 95)

            /*No Icon Theme*/
            saveDefaultPreference("customIcon", context.packageName)

            /*No Icon Shape*/
            defaultSharedPreferencesEditor.putInt("iconShape", 0)

            saveDefaultPreference("LitePreferences", true)

        }

        /*Apply All Lite Preferences*/
        return defaultSharedPreferencesEditor.commit()
    }
}