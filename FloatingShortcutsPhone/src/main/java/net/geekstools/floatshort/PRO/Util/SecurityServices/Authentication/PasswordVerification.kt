package net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class PasswordVerification : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                    if (firebaseAuth.isSignInWithEmailLink(emailLink)) {


                    }
                }
                .addOnFailureListener(this) {

                }
    }
}