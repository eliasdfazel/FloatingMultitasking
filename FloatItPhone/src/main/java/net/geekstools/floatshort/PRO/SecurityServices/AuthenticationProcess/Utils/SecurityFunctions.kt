/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.biometric.BiometricManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.PinPassword.PinPasswordConfigurations
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import java.io.File
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class SecurityFunctions (var context: Context) {

    fun canPerformFingerprintProcess() : Boolean {
        val biometricManager = BiometricManager.from(context)

        return (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
    }

    /*Lock/Unlock Apps*/
    fun doLockApps(identifier: String) {

        if (FunctionsClassLegacy(context).readPreference(".Password", "Pin", "0") == "0") {

            context.startActivity(Intent(context, PinPasswordConfigurations::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        } else {

            FunctionsClassLegacy(context).savePreference(".LockedApps", identifier, true)
        }
    }

    fun doUnlockApps(identifier: String) {
        FunctionsClassLegacy(context).savePreference(".LockedApps", identifier, false)
    }

    /*Lock Functions*/
    fun isAppLocked(identifier: String): Boolean {
        val functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(context)

        return functionsClassLegacy.readPreference(".LockedApps", identifier, false)
    }

    fun saveEncryptedPinPassword(plainTextPassword: String) {
        val functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(context)

        val passwordToSave = encryptEncodedData(plainTextPassword, FirebaseAuth.getInstance().currentUser!!.uid).asList().toString()
        functionsClassLegacy.savePreference(".Password", "Pin", passwordToSave)
    }

    fun isEncryptedPinPasswordEqual(plainTextPassword: String): Boolean {
        var passwordEqual = false

        val encryptedPassword = encryptEncodedData(plainTextPassword, FirebaseAuth.getInstance().currentUser!!.uid).asList().toString()
        val currentPassword = FunctionsClassLegacy(context).readPreference(".Password", "Pin", "0")

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
        val storageReferenceLockedApps = firebaseStorage.getReference("FloatingMultitasking/Security/" + "Services" + "/" + firebaseUser!!.email + "/" + firebaseUser.uid + "/" + ".LockedApps.xml")
        val uploadTaskLockedApps = storageReferenceLockedApps.putFile(urilockedAppsFile)
        uploadTaskLockedApps.addOnSuccessListener {

        }.addOnFailureListener {

        }

        val pinPasswordFile = File("/data/data/" + context.packageName + "/shared_prefs/.Password.xml")
        val uriPinPasswordFile = Uri.fromFile(pinPasswordFile)
        val storageReferencePinPassword = firebaseStorage.getReference("FloatingMultitasking/Security/" + "Services" + "/" + firebaseUser!!.email + "/" + firebaseUser.uid + "/" + ".Password.xml")
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