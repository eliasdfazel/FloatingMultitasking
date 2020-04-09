package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.Utils

import android.util.Log

interface AuthenticationCallback {
    fun authenticatedFloatingShortcuts() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "AuthenticatedFloatingShortcuts")

    }
    fun authenticatedFloatingFolders() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "AuthenticatedFloatingFolders")

    }

    fun authenticatedFloatingWidgets() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "AuthenticatedFloatingWidgets")

    }
    fun authenticatedFloatingWidgetsConfiguration() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "AuthenticatedFloatingWidgetsConfiguration")

    }

    fun authenticatedSearchEngine() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "AuthenticatedSearchEngine")

    }

    fun authenticatedSplitSingle() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "AuthenticatedSplitSingle")

    }
    fun authenticatedSplitPair() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "AuthenticatedSplitPair")

    }

    fun authenticatedFreeForm() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "AuthenticatedFreeForm")

    }

    fun authenticatedForgotPinPassword() {
        Log.d(this@AuthenticationCallback.javaClass.simpleName, "AuthenticatedForgotPinPassword")

    }

    fun failedAuthenticated()
}