/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/13/20 9:16 AM
 * Last modified 1/13/20 9:16 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.Authentication;

import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyProperties;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

public class AuthActivityHelper extends AppCompatActivity {

    static final String DEFAULT_KEY_NAME = "default_key";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";

    FunctionsClass functionsClass;
    FunctionsClassSecurity functionsClassSecurity;

    RelativeLayout authBackground;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.auth_transparent);

        functionsClass = new FunctionsClass(getApplicationContext(), AuthActivityHelper.this);
        functionsClassSecurity = new FunctionsClassSecurity(AuthActivityHelper.this, getApplicationContext());

        authBackground = (RelativeLayout) findViewById(R.id.authBackground);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        try {
            window.setStatusBarColor(
                    functionsClass.setColorAlpha(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())),
                            0.50f)
            );
            window.setNavigationBarColor(
                    functionsClass.setColorAlpha(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())),
                            0.50f)
            );

            authBackground.setBackgroundColor(
                    functionsClass.setColorAlpha(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())),
                            0.50f)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        /*Finger-Print Authentication Invocation*/
        try {
            if (functionsClassSecurity.fingerprintSensorAvailable() && functionsClassSecurity.fingerprintEnrolled()) {
                KeyguardManager keyguardManager =
                        (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                FingerprintManager fingerprintManager =
                        (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                if (fingerprintManager.isHardwareDetected()) {
                    if (fingerprintManager.hasEnrolledFingerprints()) {
                        if (keyguardManager.isKeyguardSecure()) {

                            try {
                                FunctionsClassSecurity.AuthOpenAppValues.setKeyStore(KeyStore.getInstance("AndroidKeyStore"));
                            } catch (KeyStoreException e) {
                                throw new RuntimeException("Failed to get an instance of KeyStore", e);
                            }
                            try {
                                FunctionsClassSecurity.AuthOpenAppValues.setKeyGenerator(KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"));
                            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                                throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
                            }
                            final Cipher defaultCipher;
                            try {
                                defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                                        + KeyProperties.BLOCK_MODE_CBC + "/"
                                        + KeyProperties.ENCRYPTION_PADDING_PKCS7);
                            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                                throw new RuntimeException("Failed to get an instance of Cipher", e);
                            }

                            functionsClassSecurity.createKey(DEFAULT_KEY_NAME, true);
                            functionsClassSecurity.createKey(KEY_NAME_NOT_INVALIDATED, false);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    functionsClassSecurity.new InvokeAuth(AuthActivityHelper.this, defaultCipher, DEFAULT_KEY_NAME);
                                }
                            }, 333);

                        }
                    }
                }
            } else {
                functionsClassSecurity.new InvokeAuth(AuthActivityHelper.this, null, DEFAULT_KEY_NAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*Finger-Print Authentication Invocation*/
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }
}
