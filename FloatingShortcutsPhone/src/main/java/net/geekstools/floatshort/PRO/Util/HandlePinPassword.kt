package net.geekstools.floatshort.PRO.Util

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.pin_layout.*
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable

class HandlePinPassword : Activity() {

    lateinit var functionClass: FunctionsClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pin_layout)

        functionClass = FunctionsClass(applicationContext, this@HandlePinPassword)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = functionClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f)
        window.navigationBarColor = functionClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f)

        pinFullView.setBackgroundColor(functionClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f))

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                FunctionsClassDebug.PrintDebug("*** Pin Password == ${s.toString()} ***")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        password.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                /*If Password Match Authed*/
                //
//                    try {
//                        functionsClassSecurity.encryptEncodedData(password.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid()).asList().toString()
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        functionsClassSecurity.decryptEncodedData(functionsClassSecurity.rawStringToByteArray(password.getText().toString()), FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    functionsClass.appsLaunchPad(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
            }

            false
        }
    }
}