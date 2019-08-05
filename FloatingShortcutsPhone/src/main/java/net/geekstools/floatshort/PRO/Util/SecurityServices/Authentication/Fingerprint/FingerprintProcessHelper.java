package net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.Fingerprint;

import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.R;

public class FingerprintProcessHelper extends FingerprintManager.AuthenticationCallback {

    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 1300;

    private Context context;

    private final FingerprintManager fingerprintManager;

    private final ImageView imageView;
    private final TextView errortextview;
    private final EditText password;

    private final Callback callback;

    int initHintTextColor = 0, failedCounter = 0;

    private CancellationSignal cancellationSignal;
    private boolean selfCancelled;
    private Runnable resetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            errortextview.setTextColor(initHintTextColor);
            if (!password.isShown()) {
                errortextview.setText(errortextview.getResources().getString(R.string.fingerprint_hint));
            }
            imageView.setImageResource(R.drawable.draw_finger_print);
        }
    };

    public FingerprintProcessHelper(Context context, FingerprintManager fingerprintManager, ImageView icon, TextView errorTextView, EditText password, Callback callback) {
        this.context = context;

        this.fingerprintManager = fingerprintManager;

        this.imageView = icon;
        this.errortextview = errorTextView;
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
            context.sendBroadcast(new Intent("SHOW_PASSWORD"));

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
            context.sendBroadcast(new Intent("SHOW_PASSWORD"));
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        errortextview.removeCallbacks(resetErrorTextRunnable);
        imageView.setImageResource(R.drawable.draw_finger_print_success);

        errortextview.setTextColor(errortextview.getResources().getColor(R.color.success_color, null));
        errortextview.setText(errortextview.getResources().getString(R.string.fingerprint_success));

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onAuthenticated();
            }
        }, SUCCESS_DELAY_MILLIS);
    }

    private void showError(CharSequence error) {
        imageView.setImageResource(R.drawable.draw_finger_print_error);
        errortextview.setText(error);
        errortextview.setTextColor(errortextview.getResources().getColor(R.color.warning_color, null));

        errortextview.removeCallbacks(resetErrorTextRunnable);
        errortextview.postDelayed(resetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    public interface Callback {
        void onAuthenticated();

        void onError();
    }
}
