/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/11/20 10:46 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.PinPassword

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import net.geekstools.floatshort.PRO.Preferences.PreferencesActivity
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.UserInterfaceExtraData
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PreferencesIO
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.databinding.AuthHandlerViewsBinding

class PinPasswordConfigurations : Activity() {

    private lateinit var functionClassLegacy: FunctionsClassLegacy
    private lateinit var securityFunctions: SecurityFunctions

    private val preferencesIO: PreferencesIO by lazy {
        PreferencesIO(applicationContext)
    }

    private var currentPasswordExist: Boolean = false

    private lateinit var authHandlerViewsBinding: AuthHandlerViewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authHandlerViewsBinding = AuthHandlerViewsBinding.inflate(layoutInflater)
        setContentView(authHandlerViewsBinding.root)

        functionClassLegacy = FunctionsClassLegacy(applicationContext)
        securityFunctions = SecurityFunctions(applicationContext)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = PublicVariable.primaryColor
        window.navigationBarColor = PublicVariable.primaryColor
        if (PublicVariable.themeLightDark) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }

        authHandlerViewsBinding.pinFullViewScrollView.setBackgroundColor(PublicVariable.primaryColor)
        authHandlerViewsBinding.pinFullView.setBackgroundColor(PublicVariable.primaryColor)

        authHandlerViewsBinding.textInputPasswordCurrent.boxBackgroundColor = functionClassLegacy.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.91f)
        authHandlerViewsBinding.textInputPinPassword.boxBackgroundColor = functionClassLegacy.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.91f)
        authHandlerViewsBinding.textInputPasswordRepeat.boxBackgroundColor = functionClassLegacy.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.91f)

        authHandlerViewsBinding.textInputPasswordCurrent.defaultHintTextColor = ColorStateList.valueOf(PublicVariable.colorLightDarkOpposite)
        authHandlerViewsBinding.textInputPinPassword.defaultHintTextColor = ColorStateList.valueOf(PublicVariable.colorLightDarkOpposite)
        authHandlerViewsBinding.textInputPasswordRepeat.defaultHintTextColor = ColorStateList.valueOf(PublicVariable.colorLightDarkOpposite)

        authHandlerViewsBinding.passwordCurrent.setTextColor(PublicVariable.colorLightDarkOpposite)
        authHandlerViewsBinding.pinPasswordEditText.setTextColor(PublicVariable.colorLightDarkOpposite)
        authHandlerViewsBinding.passwordRepeat.setTextColor(PublicVariable.colorLightDarkOpposite)

        currentPasswordExist = (preferencesIO.readPreference(".Password", "Pin", "0") != "0")
        if (currentPasswordExist) {
            authHandlerViewsBinding.textInputPasswordCurrent.visibility = View.VISIBLE
            authHandlerViewsBinding.forgotPassword.visibility = View.VISIBLE
        }

        authHandlerViewsBinding.pinPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        authHandlerViewsBinding.passwordRepeat.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        authHandlerViewsBinding.pinPasswordEditText.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {

            }

            false
        }

        authHandlerViewsBinding.passwordRepeat.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (currentPasswordExist) {
                    editPinPassword()
                } else {
                    saveNewPinPassword()
                }
            }

            false
        }

        authHandlerViewsBinding.donePassword.setOnClickListener {
            if (currentPasswordExist) {
                editPinPassword()
            } else {
                saveNewPinPassword()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        authHandlerViewsBinding.forgotPassword.setOnClickListener {
            functionClassLegacy.doVibrate(113)
            authHandlerViewsBinding.spinKitView.visibility = View.VISIBLE

            if (securityFunctions.canPerformFingerprintProcess()) {

                SecurityInterfaceHolder.authenticationCallback = object : AuthenticationCallback {

                    override fun authenticatedFloatIt(extraInformation: Bundle?) {
                        super.authenticatedFloatIt(extraInformation)
                        Log.d(this@PinPasswordConfigurations.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

                    }

                    override fun failedAuthenticated() {
                        super.failedAuthenticated()
                        Log.d(this@PinPasswordConfigurations.javaClass.simpleName, "FailedAuthenticated")

                    }

                    override fun invokedPinPassword() {
                        super.invokedPinPassword()
                        Log.d(this@PinPasswordConfigurations.javaClass.simpleName, "InvokedPinPassword")

                    }
                }

                startActivity(Intent(applicationContext, AuthenticationFingerprint::class.java).apply {
                    putExtra(UserInterfaceExtraData.OtherTitle, getString(R.string.changePin))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, 0).toBundle())

            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!securityFunctions.canPerformFingerprintProcess()) {
            authHandlerViewsBinding.securityWarningIcon.visibility = View.VISIBLE
            authHandlerViewsBinding.securityWarningText.visibility = View.VISIBLE

            authHandlerViewsBinding.securityWarningIcon.setOnClickListener {
                functionClassLegacy.doVibrate(99)

                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                } else {
                    Intent(Settings.ACTION_SECURITY_SETTINGS);
                }
                startActivity(intent)
            }

            authHandlerViewsBinding.securityWarningText.setOnClickListener {
                functionClassLegacy.doVibrate(99)

                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                } else {
                    Intent(Settings.ACTION_SECURITY_SETTINGS);
                }
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, PreferencesActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        this@PinPasswordConfigurations.finish()
    }

    private fun saveNewPinPassword() {
        if (authHandlerViewsBinding.pinPasswordEditText.text.isNullOrBlank() || authHandlerViewsBinding.passwordRepeat.text.isNullOrBlank()) {
            authHandlerViewsBinding.pinPasswordEditText.setText("")
            authHandlerViewsBinding.passwordRepeat.setText("")

            authHandlerViewsBinding.textInputPinPassword.error = getString(R.string.passwordError)
            authHandlerViewsBinding.textInputPasswordRepeat.error = getString(R.string.passwordError)
        } else {
            if ((authHandlerViewsBinding.pinPasswordEditText.text.toString() == authHandlerViewsBinding.passwordRepeat.text.toString())) {
                try {
                    securityFunctions.saveEncryptedPinPassword(authHandlerViewsBinding.pinPasswordEditText.text.toString())

                    this@PinPasswordConfigurations.finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                authHandlerViewsBinding.pinPasswordEditText.setText("")
                authHandlerViewsBinding.passwordRepeat.setText("")

                authHandlerViewsBinding.textInputPinPassword.error = getString(R.string.passwordError)
                authHandlerViewsBinding.textInputPasswordRepeat.error = getString(R.string.passwordError)
            }
        }
    }

    private fun editPinPassword() {
        try {
            if (authHandlerViewsBinding.passwordCurrent.text.isNullOrBlank()) {
                authHandlerViewsBinding.passwordCurrent.setText("")
                authHandlerViewsBinding.passwordCurrent.error = getString(R.string.passwordError)
            } else {
                if (securityFunctions.isEncryptedPinPasswordEqual(authHandlerViewsBinding.passwordCurrent.text.toString())) {
                    saveNewPinPassword()
                } else {
                    authHandlerViewsBinding. passwordCurrent.setText("")
                    authHandlerViewsBinding.passwordCurrent.error = getString(R.string.passwordError)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}