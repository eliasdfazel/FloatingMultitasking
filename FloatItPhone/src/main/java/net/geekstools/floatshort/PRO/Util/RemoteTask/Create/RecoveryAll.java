/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/13/20 9:58 AM
 * Last modified 1/13/20 9:54 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.RemoteTask.Create;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity;

public class RecoveryAll extends Service {

    FunctionsClass functionsClass;
    FunctionsClassSecurity functionsClassSecurity;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        if (functionsClass.securityServicesSubscribed() && !FunctionsClassSecurity.AuthOpenAppValues.getAlreadyAuthenticating()) {
            FunctionsClassSecurity.AuthOpenAppValues.setAuthComponentName(getPackageName());
            FunctionsClassSecurity.AuthOpenAppValues.setAuthSecondComponentName(getPackageName());
            FunctionsClassSecurity.AuthOpenAppValues.setAuthRecovery(true);

            functionsClassSecurity.openAuthInvocation();
            FunctionsClassSecurity.AuthOpenAppValues.setAlreadyAuthenticating(true);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("RECOVERY_AUTHENTICATED");
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("RECOVERY_AUTHENTICATED")) {
                        startService(new Intent(getApplicationContext(), RecoveryShortcuts.class));
                        startService(new Intent(getApplicationContext(), RecoveryFolders.class));
                        startService(new Intent(getApplicationContext(), RecoveryWidgets.class));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            stopForeground(Service.STOP_FOREGROUND_REMOVE);
                            stopForeground(true);
                        }
                        stopSelf();
                    }
                }
            };
            try {
                registerReceiver(broadcastReceiver, intentFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            startService(new Intent(getApplicationContext(), RecoveryShortcuts.class));
            startService(new Intent(getApplicationContext(), RecoveryFolders.class));
            startService(new Intent(getApplicationContext(), RecoveryWidgets.class));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(Service.STOP_FOREGROUND_REMOVE);
                stopForeground(true);
            }
            stopSelf();
        }

        return functionsClass.serviceMode();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        functionsClass = new FunctionsClass(getApplicationContext());
        functionsClassSecurity = new FunctionsClassSecurity(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(333, functionsClass.bindServiceNotification(),Service.STOP_FOREGROUND_REMOVE);
        } else {
            startForeground(333, functionsClass.bindServiceNotification());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getApplicationContext(), BindServices.class));
        } else {
            startService(new Intent(getApplicationContext(), BindServices.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(Service.STOP_FOREGROUND_REMOVE);
            stopForeground(true);
        }
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
