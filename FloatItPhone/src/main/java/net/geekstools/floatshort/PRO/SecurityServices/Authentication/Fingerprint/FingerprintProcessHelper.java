/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.Authentication.Fingerprint;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.Utils.FunctionsClassSecurity;
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcessNEW.PinPassword.HandlePinPassword;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug;

import java.util.Locale;

@Deprecated
public class FingerprintProcessHelper extends FingerprintManager.AuthenticationCallback {

    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 1300;

    private Activity activity;
    private Context context;

    private final FingerprintManager fingerprintManager;

    private final ImageView imageView;
    private final TextView errorTextView;
    private final EditText password;

    private final Callback callback;

    int initHintTextColor = 0, failedCounter = 0;

    private CancellationSignal cancellationSignal;
    private boolean selfCancelled;
    private Runnable resetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            errorTextView.setTextColor(initHintTextColor);
            if (!password.isShown()) {
                errorTextView.setText(errorTextView.getResources().getString(R.string.fingerprint_hint));
            }
            imageView.setImageResource(R.drawable.draw_finger_print);
        }
    };

    public FingerprintProcessHelper(Activity activity, Context context, FingerprintManager fingerprintManager, ImageView icon, TextView errorTextView, EditText password, Callback callback) {
        this.activity = activity;
        this.context = context;

        this.fingerprintManager = fingerprintManager;

        this.imageView = icon;
        this.errorTextView = errorTextView;
        this.password = password;

        this.callback = callback;

        this.initHintTextColor = errorTextView.getCurrentTextColor();
    }

    public boolean isFingerprintAuthAvailable() {
        return fingerprintManager.isHardwareDetected()
                && fingerprintManager.hasEnrolledFingerprints();
    }

    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable()) {
            return;
        }
        cancellationSignal = new CancellationSignal();
        selfCancelled = false;
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0 /* flags */, this, null);

        imageView.setImageResource(R.drawable.draw_finger_print);
    }

    public void stopListening() {
        if (cancellationSignal != null) {
            selfCancelled = true;
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!selfCancelled) {
            showError(errString);
            if (FunctionsClassSecurity.AuthOpenAppValues.getAuthForgotPinPassword()) {
                ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                        .setUrl("https://floating-shortcuts-pro.firebaseapp.com")
                        .setDynamicLinkDomain("floatshort.page.link")
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                context.getPackageName(),
                                true, /* installIfNotAvailable */
                                "23" /* minimumVersion */
                        )
                        .build();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.setLanguageCode(Locale.getDefault().getLanguage());
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                firebaseAuth.sendSignInLinkToEmail(firebaseUser.getEmail().toString(), actionCodeSettings).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FunctionsClassDebug.Companion.PrintDebug("*** Password Verification Email Sent To ${firebaseUser.email} ***");
                        new FunctionsClass(context).Toast(context.getString(R.string.passwordResetSent), Gravity.BOTTOM, context.getColor(R.color.red_transparent));

                        errorTextView.setText(context.getString(R.string.passwordResetSent));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.finish();
                            }
                        }, 1000);
                    }
                });
            } else {
                if (new FunctionsClass(context).readPreference(".Password", "Pin", "0").equals("0") && new FunctionsClass(context).securityServicesSubscribed()) {
                    context.startActivity(new Intent(context, HandlePinPassword.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                } else {
                    context.sendBroadcast(new Intent("SHOW_PASSWORD"));
                }
            }

            imageView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    callback.onError();
                }
            }, ERROR_TIMEOUT_MILLIS);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showError(helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        showError(imageView.getResources().getString(R.string.fingerprint_not_recognized));
        failedCounter++;
        if (failedCounter >= 3) {
            if (FunctionsClassSecurity.AuthOpenAppValues.getAuthForgotPinPassword()) {
                ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                        .setUrl("https://floating-shortcuts-pro.firebaseapp.com")
                        .setDynamicLinkDomain("floatshort.page.link")
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                context.getPackageName(),
                                true, /* installIfNotAvailable */
                                "23" /* minimumVersion */
                        )
                        .build();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.setLanguageCode(Locale.getDefault().getLanguage());
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                firebaseAuth.sendSignInLinkToEmail(firebaseUser.getEmail().toString(), actionCodeSettings).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FunctionsClassDebug.Companion.PrintDebug("*** Password Verification Email Sent To ${firebaseUser.email} ***");
                        new FunctionsClass(context).Toast(context.getString(R.string.passwordResetSent), Gravity.BOTTOM, context.getColor(R.color.red_transparent));

                        errorTextView.setText(context.getString(R.string.passwordResetSent));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.finish();
                            }
                        }, 1000);
                    }
                });
            } else {
                if (new FunctionsClass(context).readPreference(".Password", "Pin", "0").equals("0") && new FunctionsClass(context).securityServicesSubscribed()) {
                    context.startActivity(new Intent(context, HandlePinPassword.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                } else {
                    context.sendBroadcast(new Intent("SHOW_PASSWORD"));
                }
            }
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        errorTextView.removeCallbacks(resetErrorTextRunnable);
        imageView.setImageResource(R.drawable.draw_finger_print_success);

        errorTextView.setTextColor(errorTextView.getResources().getColor(R.color.success_color, null));
        errorTextView.setText(errorTextView.getResources().getString(R.string.fingerprint_success));

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onAuthenticated();
            }
        }, SUCCESS_DELAY_MILLIS);
    }

    private void showError(CharSequence error) {
        imageView.setImageResource(R.drawable.draw_finger_print_error);
        errorTextView.setText(error);
        errorTextView.setTextColor(errorTextView.getResources().getColor(R.color.warning_color, null));

        errorTextView.removeCallbacks(resetErrorTextRunnable);
        errorTextView.postDelayed(resetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    public interface Callback {
        void onAuthenticated();

        void onError();
    }
}
