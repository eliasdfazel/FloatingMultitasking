package net.geekstools.floatshort.PRO.Util.RemoteTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

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

                        stopSelf();
                    }
                }
            };
            registerReceiver(broadcastReceiver, intentFilter);
        } else {
            startService(new Intent(getApplicationContext(), RecoveryShortcuts.class));
            startService(new Intent(getApplicationContext(), RecoveryFolders.class));
            startService(new Intent(getApplicationContext(), RecoveryWidgets.class));

            stopSelf();
        }

        return functionsClass.serviceMode();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        functionsClass = new FunctionsClass(getApplicationContext());
        functionsClassSecurity = new FunctionsClassSecurity(getApplicationContext());

        if (functionsClass.returnAPI() >= 26) {
            startForeground(333, functionsClass.bindServiceNotification());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
