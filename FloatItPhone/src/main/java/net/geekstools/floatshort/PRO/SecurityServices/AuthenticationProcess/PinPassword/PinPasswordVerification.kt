/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/24/20 7:03 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.PinPassword

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.databinding.AuthVerificationWaitingBinding

class PinPasswordVerification : Activity() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private lateinit var authVerificationWaitingBinding: AuthVerificationWaitingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authVerificationWaitingBinding = AuthVerificationWaitingBinding.inflate(layoutInflater)
        setContentView(authVerificationWaitingBinding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.default_color_light)
        window.navigationBarColor = getColor(R.color.default_color_light)

        authVerificationWaitingBinding.fullView.setBackgroundColor(getColor(R.color.default_color_light))

        if (intent.hasExtra("RESET_PASSWORD_BY_FINGER_PRINT")) {

            functionsClass.savePreference(".Password", "Pin", "0")

            Handler().postDelayed({

                startActivity(Intent(applicationContext, PinPasswordConfigurations::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

            }, 2333)

        } else {

            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(intent)
                    .addOnSuccessListener(this@PinPasswordVerification) { pendingDynamicLinkData ->

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

                            startActivity(Intent(applicationContext, PinPasswordConfigurations::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

                        }, 2333)
                    }
                    .addOnFailureListener(this) {

                    }
        }
    }

    override fun onBackPressed() {

        this@PinPasswordVerification.finish()
    }
}