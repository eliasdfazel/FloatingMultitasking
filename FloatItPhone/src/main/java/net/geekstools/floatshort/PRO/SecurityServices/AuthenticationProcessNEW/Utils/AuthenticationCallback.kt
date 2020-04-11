package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.Utils

import android.os.Bundle
import android.util.Log

interface AuthenticationCallback {

    fun authenticatedFloatIt(extraInformation: Bundle?) {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "AuthenticatedFloatIt")

    }

    fun failedAuthenticated() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "FailedAuthenticated")

    }

    fun invokedPinPassword() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "InvokedPinPassword")

    }
}