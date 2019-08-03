package net.geekstools.floatshort.PRO.Util.Functions

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.net.Uri
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.AuthActivityHelper
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.FingerprintAuthenticationDialogFragment
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

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

    /*Lock/Unlock Apps*/
    fun doLockApps(PackageName: String) {
        FunctionsClass(context).savePreference(".LockedApps", PackageName, true)
    }

    fun doUnlockApps(PackageName: String) {
        FunctionsClass(context).savePreference(".LockedApps", PackageName, false)
    }

    /*Finger-Print Functions*/
    val KEY_NAME_NOT_INVALIDATED = "key_not_invalidated"
    val DEFAULT_KEY_NAME = "default_key"

    companion object AuthOpenAppValues {
        var authComponentName: String? = null

        var authPositionX: Int = 0
        var authPositionY: Int = 0
        var authHW: Int = 0

        var authSingleUnlockIt = false
        var authFolderUnlockIt = false
        var authForgotPinPassword = false

        var keyStore: KeyStore? = null
        var keyGenerator: KeyGenerator? = null
    }

    fun resetAuthAppValues() {
        AuthOpenAppValues.authComponentName = null

        AuthOpenAppValues.authPositionX = 0
        AuthOpenAppValues.authPositionY = 0
        AuthOpenAppValues.authHW = 0

        AuthOpenAppValues.authSingleUnlockIt = false
        AuthOpenAppValues.authFolderUnlockIt = false

        AuthOpenAppValues.keyStore = null
        AuthOpenAppValues.keyGenerator = null
    }

    inner class InvokeAuth(internal var cipher: Cipher?, internal var keyName: String) {
        init {
            if (fingerprintSensorAvailable() && fingerprintEnrolled()) {
                if (initCipher(this.cipher!!, this.keyName)) {
                    FunctionsClassDebug.PrintDebug("*** Finger Print Available ***")

                    val fingerprintAuthenticationDialogFragment = FingerprintAuthenticationDialogFragment()
                    fingerprintAuthenticationDialogFragment.setCryptoObject(FingerprintManager.CryptoObject(this.cipher))
                    fingerprintAuthenticationDialogFragment.show(activity.fragmentManager, context.packageName)
                }
            } else {
                FunctionsClassDebug.PrintDebug("*** Finger Print Not Available ***")

                val fingerprintAuthenticationDialogFragment = FingerprintAuthenticationDialogFragment()
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
        } else {

        }
    }

    fun authConfirmed(encrypted: ByteArray?) {
        if (encrypted != null) {
            FunctionsClassDebug.PrintDebug("*** Authentication Confirmed ***")

            if (AuthOpenAppValues.authSingleUnlockIt) {
                doUnlockApps(AuthOpenAppValues.authComponentName!!)
            } else if (AuthOpenAppValues.authForgotPinPassword) {
                /*
                *
                * */
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

    /*(En/De)crypt Functions*/
    @Throws(Exception::class)
    fun generateEncryptionKey(passwordKey: String): SecretKeySpec {

        return SecretKeySpec(passwordKey.toByteArray(), "AES")
    }

    fun generatePasswordKey(rawString: String): String {
        val rawPasswordString = rawString + "0000000000000000"
        val passwordKey: String = rawPasswordString.substring(0, 16)
        return passwordKey
    }

    @Throws(Exception::class)
    fun encryptEncodedData(plainText: String, rawString: String): ByteArray {
        //First Encode
        //Second Encrypt

        val encodedText: String = encodeStringBase64(plainText)

        var cipher: Cipher? = null
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher!!.init(Cipher.ENCRYPT_MODE, generateEncryptionKey(generatePasswordKey(rawString)))

        return cipher.doFinal(encodedText.toByteArray(Charset.defaultCharset()))
    }

    @Throws(Exception::class)
    fun decryptEncodedData(encryptedByteArray: ByteArray, rawString: String): String? {
        //First Decrypt
        //Second Decode
        var plainText: String? = null

        try {
            var cipherD: Cipher? = null
            cipherD = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipherD!!.init(Cipher.DECRYPT_MODE, generateEncryptionKey(generatePasswordKey(rawString)))
            val decryptString = String(cipherD.doFinal(encryptedByteArray), Charset.defaultCharset())

            plainText = decodeStringBase64(decryptString)
        } catch (e: Exception) {
            e.printStackTrace()

            plainText = encryptedByteArray.toString()
        }

        return plainText
    }

    @Throws(Exception::class)
    fun encodeStringBase64(plainText: String): String {
        return Base64.encodeToString(plainText.toByteArray(), Base64.DEFAULT)
    }

    @Throws(Exception::class)
    fun decodeStringBase64(encodedText: String): String {
        return String(Base64.decode(encodedText, Base64.DEFAULT))
    }

    fun rawStringToByteArray(rawString: String): ByteArray {
        var listOfRawString = rawString.replace("[", "").replace("]", "").split(",")

        var resultByteArray = ByteArray(listOfRawString.size)
        for (aByte in listOfRawString.withIndex()) {
            try {
                resultByteArray[aByte.index] = aByte.value.replace("\\s".toRegex(), "").toByte()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return resultByteArray
    }

    /*Upload/Download Functions .LockedApps*/
    fun uploadLockedAppsData() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val lockedAppsFile = File("/data/data/" + context.packageName + "/shared_prefs/.LockedApps.xml")
        val urilockedAppsFile = Uri.fromFile(lockedAppsFile)
        val firebaseStorage = FirebaseStorage.getInstance()

        val storageReference = firebaseStorage.getReference("/Security/" + "Services" + "/" + firebaseUser!!.email + "/" + firebaseUser.uid + "/" + ".LockedApps.xml")
        val uploadTask = storageReference.putFile(urilockedAppsFile)
        uploadTask.addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun downloadLockedAppsData() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val firebaseStorage = FirebaseStorage.getInstance()
        val firebaseStorageReference = firebaseStorage.reference
        val allUploadedImageFilesXml = firebaseStorageReference.child("/Security/" + "Services" + "/" + firebaseUser!!.email + "/" + firebaseUser.uid + "/" + ".LockedApps.xml")

        allUploadedImageFilesXml.getFile(File("/data/data/" + context.packageName + "/shared_prefs/" + ".LockedApps.xml"))
                .addOnSuccessListener {

                }.addOnFailureListener {

                }
    }
}