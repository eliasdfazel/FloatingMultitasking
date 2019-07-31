package net.geekstools.floatshort.PRO.Util.Functions

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.AuthActivityHelper
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.FingerprintAuthenticationDialogFragment
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.*

class FunctionsClassSecurity {

    lateinit var activity: Activity
    lateinit var context: Context

    constructor(activity: Activity, context: Context) {
        this.activity = activity
        this.context = context
    }

    constructor(context: Context) {
        this.context = context
    }

    init {

    }

    /*Finger-Print Functions*/
    val KEY_NAME_NOT_INVALIDATED = "key_not_invalidated"
    val DEFAULT_KEY_NAME = "default_key"

    companion object AuthOpenAppValues {
        var authComponentName: String? = null

        var authOrderNumber: Int = 0
        var authPositionX: Int = 0
        var authPositionY: Int = 0
        var authHW: Int = 0

        var authUnlockIt = false

        var keyStore: KeyStore? = null
        var keyGenerator: KeyGenerator? = null
    }

    inner class InvokeAuth(internal var cipher: Cipher, internal var keyName: String) {
        init {
            if (initCipher(this.cipher, this.keyName)) {
                val fingerprintAuthenticationDialogFragment = FingerprintAuthenticationDialogFragment()
                fingerprintAuthenticationDialogFragment.setCryptoObject(FingerprintManager.CryptoObject(this.cipher))
                fingerprintAuthenticationDialogFragment.show(activity.fragmentManager, context.packageName)
            }
        }
    }

    fun openAuthInvocation() {
        val activityOptions = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out)
        context.startActivity(Intent(context, AuthActivityHelper::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), activityOptions.toBundle())
    }

    fun initCipher(cipher: Cipher, keyName: String): Boolean {
        try {
            AuthOpenAppValues.keyStore!!.load(null)
            val key = AuthOpenAppValues.keyStore!!.getKey(keyName, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return true
        } catch (e: KeyPermanentlyInvalidatedException) {
            return false
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }

    }

    fun Authed(withFingerprint: Boolean, cryptoObject: FingerprintManager.CryptoObject?) {
        if (withFingerprint) {
            assert(cryptoObject != null)
            tryEncrypt(cryptoObject!!.cipher)
        }
    }

    fun authConfirmed(encrypted: ByteArray?) {
        if (encrypted != null) {
            FunctionsClassDebug.PrintDebug("*** Authentication Confirmed ***")

            if (AuthOpenAppValues.authUnlockIt) {
                FunctionsClass(context).savePreference(".LockedApps", authComponentName, false)
            } else {
                FunctionsClass(context).appsLaunchPad(FunctionsClassSecurity.AuthOpenAppValues.authComponentName)
            }
        } else {

        }
    }

    fun authError() {

        println("Auth Error")

    }

    private fun tryEncrypt(cipher: Cipher) {
        try {
            val encrypted = cipher.doFinal("Geeks Empire".toByteArray())
            authConfirmed(encrypted)
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        }

    }

    fun createKey(keyName: String, invalidatedByBiometricEnrollment: Boolean) {
        try {
            AuthOpenAppValues.keyStore!!.load(null)

            val builder = KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)
            }
            AuthOpenAppValues.keyGenerator!!.init(builder.build())
            AuthOpenAppValues.keyGenerator!!.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        } catch (e: CertificateException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    fun fingerprintSensorAvailable(): Boolean {
        val fingerprintManagerCompat = FingerprintManagerCompat.from(context)

        return fingerprintManagerCompat.isHardwareDetected
    }

    fun fingerprintEnrolled(): Boolean {
        val fingerprintManagerCompat = FingerprintManagerCompat.from(context)

        return fingerprintManagerCompat.hasEnrolledFingerprints()
    }

    fun isAppLocked(authComponentName: String): Boolean {
        return FunctionsClass(context).readPreference(".LockedApps", authComponentName, false)
    }
}