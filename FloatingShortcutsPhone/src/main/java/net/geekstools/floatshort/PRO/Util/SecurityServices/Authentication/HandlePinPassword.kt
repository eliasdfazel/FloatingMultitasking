package net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.password_handler_views.*
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable
import java.util.*

class HandlePinPassword : Activity() {

    lateinit var functionClass: FunctionsClass
    lateinit var functionsClassSecurity: FunctionsClassSecurity

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser

    var currentPasswordExist: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_handler_views)

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
        firebaseUser = firebaseAuth.currentUser!!

        pinFullViewScrollView.setBackgroundColor(PublicVariable.primaryColor)
        pinFullView.setBackgroundColor(PublicVariable.primaryColor)

        textInputPasswordCurrent.boxBackgroundColor = functionClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.89f)
        textInputPassword.boxBackgroundColor = functionClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.89f)
        textInputPasswordRepeat.boxBackgroundColor = functionClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.89f)

        if (PublicVariable.themeLightDark) {
            passwordCurrent.setTextColor(getColor(R.color.dark))
            password.setTextColor(getColor(R.color.dark))
            passwordRepeat.setTextColor(getColor(R.color.dark))
        } else {

        }

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


            /*firebaseAuth.sendPasswordResetEmail(firebaseUser.email.toString()*//*, actionCodeSettings*//*).addOnSuccessListener {
                FunctionsClassDebug.PrintDebug("*** Password Reset Email Sent To ${firebaseUser.email} ***")



                functionClass.Toast(getString(R.string.passwordResetSent), Gravity.BOTTOM, getColor(R.color.red_transparent))
            }*/

            /**/

            firebaseAuth.sendSignInLinkToEmail(firebaseUser.email.toString(), actionCodeSettings).addOnSuccessListener {
                FunctionsClassDebug.PrintDebug("*** Password Verification Email Sent To ${firebaseUser.email} ***")



                functionClass.Toast(getString(R.string.passwordResetSent), Gravity.BOTTOM, getColor(R.color.red_transparent))
            }
        }
    }

    override fun onBackPressed() {
        this@HandlePinPassword.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
                    val passwordToSave = functionsClassSecurity.encryptEncodedData(password.text.toString(), firebaseUser.uid).asList().toString()
                    functionClass.savePreference(".Password", "Pin", passwordToSave)

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
            val currentPassword = functionsClassSecurity.decryptEncodedData(functionsClassSecurity.rawStringToByteArray(functionClass.readPreference(".Password", "Pin", packageName)), firebaseUser.uid)
            if (passwordCurrent.text.isNullOrBlank()) {
                passwordCurrent.setText("")
                passwordCurrent.error = getString(R.string.passwordError)
            } else {
                if (passwordCurrent.text.toString() == currentPassword) {
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