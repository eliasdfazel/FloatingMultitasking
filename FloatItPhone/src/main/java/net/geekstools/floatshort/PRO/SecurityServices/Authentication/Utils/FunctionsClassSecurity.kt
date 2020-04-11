/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 3:43 PM
 * Last modified 3/26/20 3:01 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.Authentication.Utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Context.ACCESSIBILITY_SERVICE
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Base64
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import net.geekstools.floatshort.PRO.Checkpoint
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.UI.Adapter.SearchEngineAdapter
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.AuthActivityHelper
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.AuthenticationDialogFragment
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.PinPassword.HandlePinPassword
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.PinPassword.PasswordVerification
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver
import net.geekstools.floatshort.PRO.Utils.UI.Splash.FloatingSplash
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widgets.WidgetConfigurations
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

class FunctionsClassSecurity (var context: Context) {

    /*Lock/Unlock Apps*/
    fun doLockApps(PackageName: String) {
        if (FunctionsClass(context).readPreference(".Password", "Pin", "0") == "0") {
            context.startActivity(Intent(context, HandlePinPassword::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        } else {
            FunctionsClass(context).savePreference(".LockedApps", PackageName, true)
        }
    }

    fun doUnlockApps(PackageName: String) {
        FunctionsClass(context).savePreference(".LockedApps", PackageName, false)
    }

    /*Finger-Print Functions*/

    companion object AuthOpenAppValues {
        var alreadyAuthenticating: Boolean = false

        var authComponentName: String? = null /*Package Name*/
        var authSecondComponentName: String? = null /*Class Name*/

        var authPositionX: Int = 0
        var authPositionY: Int = 0
        var authHW: Int = 0

        var authWidgetProviderClassName: String? = null

        var authFloatingShortcuts: Boolean = false

        var authSingleUnlockIt: Boolean = false
        var authFolderUnlockIt: Boolean = false
        var authForgotPinPassword: Boolean = false

        var authWidgetConfigurations: Boolean = false
        var authWidgetConfigurationsUnlock: Boolean = false
        var authFloatingWidget: Boolean = false

        var authSingleSplitIt: Boolean = false
        var authPairSplitIt: Boolean = false
        var authFreeform: Boolean = false

        var keyStore: KeyStore? = null
        var keyGenerator: KeyGenerator? = null

        var authClassNameCommand: String? = null

        var anchorView: View? = null

        var authRecovery: Boolean = false

        var authSearchEngine: Boolean = false
    }

    fun resetAuthAppValues() {
        Handler().postDelayed({
            alreadyAuthenticating = false

            authComponentName = null
            authSecondComponentName = null

            authPositionX = 0
            authPositionY = 0
            authHW = 0

            authWidgetProviderClassName = null

            authFloatingShortcuts = false

            authSingleUnlockIt = false
            authFolderUnlockIt = false
            authForgotPinPassword = false

            authWidgetConfigurations = false
            authWidgetConfigurationsUnlock = false
            authFloatingWidget = false

            authSingleSplitIt = false
            authPairSplitIt = false
            authFreeform = false

            keyStore = null
            keyGenerator = null

            authClassNameCommand = null

            anchorView = null

            authRecovery = false

            authSearchEngine = false
        }, 1000)
    }

    inner class InvokeAuth(appCompatActivity: AppCompatActivity, private var cipher: Cipher?, private var keyName: String) {
        init {
            if (fingerprintSensorAvailable() && fingerprintEnrolled()) {
                if (initCipher(this.cipher!!, this.keyName)) {
                    FunctionsClassDebug.PrintDebug("*** Finger Print Available ***")

                    val fingerprintAuthenticationDialogFragment = AuthenticationDialogFragment()
                    fingerprintAuthenticationDialogFragment.setCryptoObject(FingerprintManager.CryptoObject(this.cipher!!))

                    if (!appCompatActivity.isFinishing) {
                        Handler().post {
                            fingerprintAuthenticationDialogFragment.show(appCompatActivity.supportFragmentManager, context.packageName)
                        }
                    }
                }
            } else {
                FunctionsClassDebug.PrintDebug("*** Finger Print Not Available ***")

                val fingerprintAuthenticationDialogFragment = AuthenticationDialogFragment()
                if (!appCompatActivity.isFinishing) {
                    Handler().post {
                        fingerprintAuthenticationDialogFragment.show(appCompatActivity.supportFragmentManager, context.packageName)
                    }
                }
            }
        }
    }

    fun openAuthInvocation() {
        context.startActivity(Intent(context, AuthActivityHelper::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
    }

    fun initCipher(cipher: Cipher, keyName: String): Boolean {
        return try {
            keyStore!!.load(null)
            val key = keyStore!!.getKey(keyName, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            true
        } catch (e: KeyPermanentlyInvalidatedException) {
            false
        } catch (e: KeyStoreException) {
            false
        } catch (e: CertificateException) {
            false
        } catch (e: UnrecoverableKeyException) {
            false
        } catch (e: IOException) {
            false
        } catch (e: NoSuchAlgorithmException) {
            false
        } catch (e: InvalidKeyException) {
            false
        } catch (e: KotlinNullPointerException) {
            false
        }
    }

    fun Authed(activity: Activity, withFingerprint: Boolean, cryptoObject: FingerprintManager.CryptoObject?) {
        if (withFingerprint) {
            assert(cryptoObject != null)
            tryEncrypt(activity, cryptoObject!!.cipher)
        } else {

        }
    }

    private fun authConfirmed(activity: Activity, encrypted: ByteArray?) {
        if (encrypted != null) {

            if (authFloatingShortcuts) {
                FunctionsClassDebug.PrintDebug("*** Authentication Confirmed | Opening... ***")

                if (FunctionsClass(context).splashReveal()) {
                    val splashReveal = Intent(context, FloatingSplash::class.java)
                    splashReveal.putExtra("packageName", authComponentName)
                    splashReveal.putExtra("className", authSecondComponentName)
                    splashReveal.putExtra("X", authPositionX)
                    splashReveal.putExtra("Y", authPositionY)
                    splashReveal.putExtra("HW", authHW)
                    context.startService(splashReveal)
                } else {
                    if (FunctionsClass(context).FreeForm()) {
                        FunctionsClass(context).openApplicationFreeForm(authComponentName,
                                authPositionX,
                                FunctionsClass(context).displayX() / 2,
                                authPositionY,
                                FunctionsClass(context).displayY() / 2
                        )
                    } else {
                        FunctionsClass(context).appsLaunchPad(authComponentName, authSecondComponentName)
                    }
                }



            } else if (authSingleUnlockIt) {
                FunctionsClassDebug.PrintDebug("*** Authentication Confirmed | Single Unlock It ***")

                if (authWidgetConfigurationsUnlock) {
                    doUnlockApps(authSecondComponentName!! + authWidgetProviderClassName)
                } else {
                    doUnlockApps(authComponentName!!)
                }
            } else if (authFolderUnlockIt) {
                if (context.getFileStreamPath(authComponentName).exists() && context.getFileStreamPath(authComponentName).isFile()) {
                    val packageNames = FunctionsClass(context).readFileLine(authComponentName)
                    for (packageName in packageNames!!) {
                        doUnlockApps(packageName)
                    }
                    doUnlockApps(authComponentName!!)
                    uploadLockedAppsData()
                }
            } else if (authForgotPinPassword) {
                FunctionsClassDebug.PrintDebug("*** Authentication Confirmed | Forgot Pin Password ***")

                context.startActivity(Intent(context, PasswordVerification::class.java)
                        .putExtra("RESET_PASSWORD_BY_FINGER_PRINT", "RESET_PASSWORD_BY_FINGER_PRINT")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
            } else if (authSingleSplitIt) {
                FunctionsClassDebug.PrintDebug("*** Authentication Confirmed | Split It ***")

                if (!FunctionsClass(context).AccessibilityServiceEnabled() && !FunctionsClass(context).SettingServiceRunning(InteractionObserver::class.java)) {
                    context.startActivity(Intent(context, Checkpoint::class.java)
                            .putExtra(context.getString(R.string.splitIt), context.packageName)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } else {
                    val accessibilityManager = context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
                    PublicVariable.splitSinglePackage = authComponentName
                    PublicVariable.splitSingleClassName = authSecondComponentName
                    val event = AccessibilityEvent.obtain()
                    event.setSource(anchorView)
                    event.eventType = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                    event.action = 69201
                    event.className = authClassNameCommand
                    event.text.add(context.packageName)
                    accessibilityManager.sendAccessibilityEvent(event)
                }
            } else if (authPairSplitIt) {
                if (!FunctionsClass(context).AccessibilityServiceEnabled() && !FunctionsClass(context).SettingServiceRunning(InteractionObserver::class.java)) {
                    context.startActivity(Intent(context, Checkpoint::class.java)
                            .putExtra(context.getString(R.string.splitIt), context.packageName)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } else {
                    val accessibilityManager = context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
                    val event = AccessibilityEvent.obtain()
                    event.setSource(anchorView)
                    event.eventType = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                    event.action = 10296
                    event.className = authClassNameCommand
                    event.text.add(context.packageName)
                    accessibilityManager.sendAccessibilityEvent(event)
                }
            } else if (authFloatingWidget) {
                FunctionsClassDebug.PrintDebug("*** Authentication Confirmed | Floating Widget ***")

                Thread {
                    val widgetDataInterface = Room.databaseBuilder(context, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(object : RoomDatabase.Callback() {
                                override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                    super.onCreate(supportSQLiteDatabase)
                                }

                                override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                    super.onOpen(supportSQLiteDatabase)

                                }
                            })
                            .build()

                    val widgetDataModelsReallocation = widgetDataInterface.initDataAccessObject()
                            .loadWidgetByClassNameProviderWidget(authSecondComponentName!!, authWidgetProviderClassName!!)

                    activity.runOnUiThread {
                        FunctionsClass(context).runUnlimitedWidgetService(widgetDataModelsReallocation.WidgetId,
                                widgetDataModelsReallocation.WidgetLabel)
                    }
                }.start()
            } else if (authWidgetConfigurations) {
                FunctionsClassDebug.PrintDebug("*** Authenticated | Open Widget Configurations ***")

                WidgetConfigurations.Companion.alreadyAuthenticatedWidgets = true
            } else if (authSearchEngine) {
                FunctionsClassDebug.PrintDebug("*** Authenticated | Run Search Engine ***")

                SearchEngineAdapter.alreadyAuthenticatedSearchEngine = true
                context.sendBroadcast(Intent("SEARCH_ENGINE_AUTHENTICATED"))
            } else if (authRecovery) {
                FunctionsClassDebug.PrintDebug("*** Authenticated | Perform Recovery ***")

                context.sendBroadcast(Intent("RECOVERY_AUTHENTICATED"))
            } else {
                FunctionsClassDebug.PrintDebug("*** Authentication Confirmed | Opening... ***")

                if (FunctionsClass(context).splashReveal()) {
                    val splashReveal = Intent(context, FloatingSplash::class.java)
                    splashReveal.putExtra("packageName", authComponentName)
                    splashReveal.putExtra("X", authPositionX)
                    splashReveal.putExtra("Y", authPositionY)
                    splashReveal.putExtra("HW", authHW)
                    context.startService(splashReveal)
                } else {
                    if (FunctionsClass(context).FreeForm()) {
                        FunctionsClass(context).openApplicationFreeForm(authComponentName,
                                authPositionX,
                                FunctionsClass(context).displayX() / 2,
                                authPositionY,
                                FunctionsClass(context).displayY() / 2
                        )
                    } else {
                        FunctionsClass(context).appsLaunchPad(authComponentName)
                    }
                }
            }

            resetAuthAppValues()
        } else {

        }
    }

    fun authError() {

    }

    private fun tryEncrypt(activity: Activity, cipher: Cipher) {
        try {
            val encrypted = cipher.doFinal("Geeks Empire".toByteArray())
            authConfirmed(activity, encrypted)
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        }

    }

    fun createKey(keyName: String, invalidatedByBiometricEnrollment: Boolean) {
        try {
            keyStore!!.load(null)

            val builder = KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)
            }
            keyGenerator!!.init(builder.build())
            keyGenerator!!.generateKey()
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

    /*
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */

    /*Lock Functions*/
    fun isAppLocked(authComponentName: String): Boolean {
        return FunctionsClass(context).readPreference(".LockedApps", authComponentName, false)
    }

    fun saveEncryptedPinPassword(plainTextPassword: String) {
        val passwordToSave = encryptEncodedData(plainTextPassword, FirebaseAuth.getInstance().currentUser!!.uid).asList().toString()
        FunctionsClass(context).savePreference(".Password", "Pin", passwordToSave)
    }

    fun isEncryptedPinPasswordEqual(plainTextPassword: String): Boolean {
        var passwordEqual = false

        val encryptedPassword = encryptEncodedData(plainTextPassword, FirebaseAuth.getInstance().currentUser!!.uid).asList().toString()
        val currentPassword = FunctionsClass(context).readPreference(".Password", "Pin", "0")

        passwordEqual = (encryptedPassword == currentPassword)

        return passwordEqual
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
        val firebaseStorage = FirebaseStorage.getInstance()

        val lockedAppsFile = File("/data/data/" + context.packageName + "/shared_prefs/.LockedApps.xml")
        val urilockedAppsFile = Uri.fromFile(lockedAppsFile)
        val storageReferenceLockedApps = firebaseStorage.getReference("/Security/" + "Services" + "/" + firebaseUser!!.email + "/" + firebaseUser.uid + "/" + ".LockedApps.xml")
        val uploadTaskLockedApps = storageReferenceLockedApps.putFile(urilockedAppsFile)
        uploadTaskLockedApps.addOnSuccessListener {

        }.addOnFailureListener {

        }

        val pinPasswordFile = File("/data/data/" + context.packageName + "/shared_prefs/.Password.xml")
        val uriPinPasswordFile = Uri.fromFile(pinPasswordFile)
        val storageReferencePinPassword = firebaseStorage.getReference("/Security/" + "Services" + "/" + firebaseUser!!.email + "/" + firebaseUser.uid + "/" + ".Password.xml")
        val uploadTaskPinPassword = storageReferencePinPassword.putFile(uriPinPasswordFile)
        uploadTaskPinPassword.addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun downloadLockedAppsData() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val firebaseStorage = FirebaseStorage.getInstance()
        val firebaseStorageReference = firebaseStorage.reference

        val uploadLockedAppsFileXml = firebaseStorageReference.child("/Security/" + "Services" + "/" + firebaseUser!!.email + "/" + firebaseUser.uid + "/" + ".LockedApps.xml")
        uploadLockedAppsFileXml.getFile(File("/data/data/" + context.packageName + "/shared_prefs/" + ".LockedApps.xml"))
                .addOnSuccessListener {

                }.addOnFailureListener {

                }

        val pinPasswordFileXml = firebaseStorageReference.child("/Security/" + "Services" + "/" + firebaseUser!!.email + "/" + firebaseUser.uid + "/" + ".Password.xml")
        pinPasswordFileXml.getFile(File("/data/data/" + context.packageName + "/shared_prefs/" + ".Password.xml"))
                .addOnSuccessListener {

                }.addOnFailureListener {

                }
    }
}