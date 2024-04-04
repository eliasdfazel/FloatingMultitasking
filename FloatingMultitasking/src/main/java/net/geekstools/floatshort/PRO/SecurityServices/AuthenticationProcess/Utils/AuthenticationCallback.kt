package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils

import android.util.Log

interface AuthenticationCallback {

    fun authenticatedFloatIt() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "Authenticated FloatIt")

    }

    fun failedAuthenticated() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "Failed Authenticated")

    }

    fun invokedPinPassword() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "Invoked Pin Password")

    }
}