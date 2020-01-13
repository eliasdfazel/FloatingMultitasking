/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/13/20 9:16 AM
 * Last modified 1/13/20 9:16 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.Authentication.PinPassword

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.android.synthetic.main.auth_verification_waiting.*
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug

class PasswordVerification : Activity() {

    private lateinit var functionsClass: FunctionsClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_verification_waiting)

        functionsClass = FunctionsClass(applicationContext, this@PasswordVerification)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.default_color_light)
        window.navigationBarColor = getColor(R.color.default_color_light)

        fullView.setBackgroundColor(getColor(R.color.default_color_light))

        if (intent.hasExtra("RESET_PASSWORD_BY_FINGER_PRINT")) {
            functionsClass.savePreference(".Password", "Pin", "0")
            Handler().postDelayed({
                startActivity(Intent(applicationContext, HandlePinPassword::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
            }, 2333)
        } else {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(intent)
                    .addOnSuccessListener(this@PasswordVerification) { pendingDynamicLinkData ->
                        // Get deep link from result (may be null if no link is found)
                        var deepLink: Uri? = null
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.link
                        }

                        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
                        val intent = intent
                        val emailLink = intent.data!!.toString()

                        FunctionsClassDebug.PrintDebug("*** Email Verified ***")

                        functionsClass.savePreference(".Password", "Pin", "0")
                        Handler().postDelayed({
                            startActivity(Intent(applicationContext, HandlePinPassword::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
                        }, 2333)
                    }
                    .addOnFailureListener(this) {

                    }
        }
    }

    override fun onBackPressed() {
        this@PasswordVerification.finish()
    }
}