/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/14/20 12:14 PM
 * Last modified 1/14/20 12:14 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.Authentication.PinPassword

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.auth_handler_views.*
import net.geekstools.floatshort.PRO.Preferences.PreferencesActivity
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable
import java.util.*


class HandlePinPassword : Activity() {

    private lateinit var functionClass: FunctionsClass
    private lateinit var functionsClassSecurity: FunctionsClassSecurity

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    private var currentPasswordExist: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_handler_views)

        functionClass = FunctionsClass(applicationContext, this@HandlePinPassword)
        functionsClassSecurity = FunctionsClassSecurity(this@HandlePinPassword, applicationContext)

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

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            Toast.makeText(applicationContext, getString(R.string.authError), Toast.LENGTH_LONG).show()

            this@HandlePinPassword.finish()
            return
        }
        firebaseUser = firebaseAuth.currentUser!!

        pinFullViewScrollView.setBackgroundColor(PublicVariable.primaryColor)
        pinFullView.setBackgroundColor(PublicVariable.primaryColor)

        textInputPasswordCurrent.boxBackgroundColor = functionClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.91f)
        textInputPassword.boxBackgroundColor = functionClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.91f)
        textInputPasswordRepeat.boxBackgroundColor = functionClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.91f)

        textInputPasswordCurrent.defaultHintTextColor = ColorStateList.valueOf(PublicVariable.colorLightDarkOpposite)
        textInputPassword.defaultHintTextColor = ColorStateList.valueOf(PublicVariable.colorLightDarkOpposite)
        textInputPasswordRepeat.defaultHintTextColor = ColorStateList.valueOf(PublicVariable.colorLightDarkOpposite)

        passwordCurrent.setTextColor(PublicVariable.colorLightDarkOpposite)
        password.setTextColor(PublicVariable.colorLightDarkOpposite)
        passwordRepeat.setTextColor(PublicVariable.colorLightDarkOpposite)

        currentPasswordExist = (functionClass.readPreference(".Password", "Pin", "0") != "0")
        if (currentPasswordExist) {
            textInputPasswordCurrent.visibility = View.VISIBLE
            forgotPassword.visibility = View.VISIBLE
        }

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                FunctionsClassDebug.PrintDebug("*** Pin Password == ${editable.toString()} ***")
            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        passwordRepeat.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                FunctionsClassDebug.PrintDebug("*** Pin Password == ${editable.toString()} ***")
            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        password.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {

            }

            false
        }

        passwordRepeat.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (currentPasswordExist) {
                    editPinPassword()
                } else {
                    saveNewPinPassword()
                }
            }

            false
        }

        donePassword.setOnClickListener {
            if (currentPasswordExist) {
                editPinPassword()
            } else {
                saveNewPinPassword()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        forgotPassword.setOnClickListener {
            functionClass.doVibrate(113)
            spinKitView.visibility = View.VISIBLE

            if (functionsClassSecurity.fingerprintSensorAvailable() && functionsClassSecurity.fingerprintEnrolled()) {
                FunctionsClassSecurity.AuthOpenAppValues.authComponentName = getString(R.string.securityServices)
                FunctionsClassSecurity.AuthOpenAppValues.authSecondComponentName = packageName
                FunctionsClassSecurity.AuthOpenAppValues.authForgotPinPassword = true

                functionsClassSecurity.openAuthInvocation()
            } else {
                val actionCodeSettings = ActionCodeSettings.newBuilder()
                        .setUrl("https://floating-shortcuts-pro.firebaseapp.com")
                        .setDynamicLinkDomain("floatshort.page.link")
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                applicationContext.packageName,
                                true, /* installIfNotAvailable */
                                "23" /* minimumVersion */
                        )
                        .build()
                firebaseAuth.setLanguageCode(Locale.getDefault().language)

                firebaseAuth.sendSignInLinkToEmail(firebaseUser.email.toString(), actionCodeSettings).addOnSuccessListener {
                    FunctionsClassDebug.PrintDebug("*** Password Verification Email Sent To ${firebaseUser.email} ***")
                    functionClass.Toast(getString(R.string.passwordResetSent), Gravity.BOTTOM, getColor(R.color.red_transparent))

                    Handler().postDelayed({
                        spinKitView.visibility = View.INVISIBLE
                    }, 1333)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!functionsClassSecurity.fingerprintEnrolled()) {
            securityWarningIcon.visibility = View.VISIBLE
            securityWarningText.visibility = View.VISIBLE

            securityWarningIcon.setOnClickListener {
                functionClass.doVibrate(99)

                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                } else {
                    Intent(Settings.ACTION_SECURITY_SETTINGS);
                }
                startActivity(intent)
            }

            securityWarningText.setOnClickListener {
                functionClass.doVibrate(99)

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
        this@HandlePinPassword.finish()
    }

    private fun saveNewPinPassword() {
        if (password.text.isNullOrBlank() || passwordRepeat.text.isNullOrBlank()) {
            password.setText("")
            passwordRepeat.setText("")

            textInputPassword.error = getString(R.string.passwordError)
            textInputPasswordRepeat.error = getString(R.string.passwordError)
        } else {
            if ((password.text.toString() == passwordRepeat.text.toString())) {
                try {
                    functionsClassSecurity.saveEncryptedPinPassword(password.text.toString())
                    functionsClassSecurity.uploadLockedAppsData()

                    this@HandlePinPassword.finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                password.setText("")
                passwordRepeat.setText("")

                textInputPassword.error = getString(R.string.passwordError)
                textInputPasswordRepeat.error = getString(R.string.passwordError)
            }
        }
    }

    private fun editPinPassword() {
        try {
            if (passwordCurrent.text.isNullOrBlank()) {
                passwordCurrent.setText("")
                passwordCurrent.error = getString(R.string.passwordError)
            } else {
                if (functionsClassSecurity.isEncryptedPinPasswordEqual(passwordCurrent.text.toString())) {
                    saveNewPinPassword()
                } else {
                    passwordCurrent.setText("")
                    passwordCurrent.error = getString(R.string.passwordError)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}