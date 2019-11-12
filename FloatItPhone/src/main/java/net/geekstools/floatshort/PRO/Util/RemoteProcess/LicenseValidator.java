/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:18 PM
 * Last modified 11/11/19 7:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.RemoteProcess;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;


public class LicenseValidator extends Service {

    private static final String BASE64_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoO5yM+7oonMW+q8QYFXfc/Vemd5LHcOo9NVgFGezNpRmNedXI86EnuJX2rGWfTI4EHnb1Fkw" +
                    "KkWfTnN6mus9ghwYomEoVQWIvXteoKo0PuGItQ8WSbFOSxvTnB8AMQ+y0EqMJ9TI+vjYNFoHVNnEf2ui10+TbnFzto2r3Z+0ZuHS1KxFBEhH" +
                    "GwsokFn//CZ1vUZrdrN7MnL3zsPcjRS2k15JKdeZv+y+0N5Do5Q+taPNUbJ8HQlCny1V/o9ocfuzq9EvI4vZ1hReFJJOk2KyYVXF9O1D3Bb5" +
                    "iE2Xza/2B3cjpH+N26QfCXKhk+KjOQ41K3qoKW843MUvz13h/yOmfwIDAQAB";
    private static final byte[] SALT = new byte[]{
            -16, -13, 30, -128, -103, -57, 74, -64, 53, 88, -97, -45, 77, -113, -36, -113, -11, 32, -64, 89
    };
    FunctionsClass functionsClass;
    LicenseChecker licenseChecker;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (functionsClass.returnAPI() < 26) {
            startForeground(111, bindServiceLOW());
        } else {
            startForeground(111, bindServiceHigherAPI26());
        }

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        licenseChecker = new LicenseChecker(
                getApplicationContext(),
                new ServerManagedPolicy(getApplicationContext(), new AESObfuscator(SALT, getPackageName(), deviceId)),
                BASE64_PUBLIC_KEY
        );
        final LicenseCheckerCallback licenseCheckerCallback = new LicenseCheckerCallback() {
            @Override
            public void allow(int reason) {
                functionsClass.saveFileAppendLine(".License", String.valueOf(reason));
                stopSelf();
            }

            @Override
            public void dontAllow(int reason) {
                sendBroadcast(new Intent(getString(R.string.license)));
            }

            @Override
            public void applicationError(int errorCode) {
            }
        };
        licenseChecker.checkAccess(licenseCheckerCallback);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClass = new FunctionsClass(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            licenseChecker.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Notification bindServiceHigherAPI26() {
        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = null;
            notificationChannel = new NotificationChannel(getPackageName(), getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);

            notificationBuilder.setColor(getColor(R.color.default_color));
            notificationBuilder.setContentTitle(Html.fromHtml("<small><font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info_desc) + "</font></small>"));
            notificationBuilder.setTicker(getResources().getString(R.string.updating_info));
            notificationBuilder.setSmallIcon(R.drawable.ic_notification);
            notificationBuilder.setAutoCancel(false);
            notificationBuilder.setProgress(0, 0, true);
            notificationBuilder.setChannelId(getPackageName());
        }

        return notificationBuilder.build();
    }

    protected Notification bindServiceLOW() {
        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext());
        notificationBuilder.setColor(getColor(R.color.default_color));
        notificationBuilder.setContentTitle(Html.fromHtml("<small><font color='" + getColor(R.color.default_color_darker) + "'>" + getString(R.string.license_info_desc) + "</font></small>"));
        notificationBuilder.setTicker(getResources().getString(R.string.updating_info));
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setProgress(0, 0, true);

        return notificationBuilder.build();
    }
}
