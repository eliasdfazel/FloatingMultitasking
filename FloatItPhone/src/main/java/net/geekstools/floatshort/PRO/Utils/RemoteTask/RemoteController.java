/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/25/20 2:16 PM
 * Last modified 3/25/20 2:00 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryAll;
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Remove.RemoveAll;

public class RemoteController extends Service {

    FunctionsClass functionsClass;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getStringExtra("RemoteController").equals("CancelRemote")) {
            if (functionsClass.automationFeatureEnable() || functionsClass.ControlPanel()) {
                /**/
            } else {
                stopService(new Intent(getApplicationContext(), BindServices.class));
            }
        } else if (intent.getStringExtra("RemoteController").equals("RecoverAll")) {
            startService(new Intent(getApplicationContext(), RecoveryAll.class));
        } else if (intent.getStringExtra("RemoteController").equals("RemoveAll")) {
            startService(new Intent(getApplicationContext(), RemoveAll.class));
        } else if (intent.getStringExtra("RemoteController").equals("Sticky_Edge")) {
            sendBroadcast(new Intent("Sticky_Edge"));
        } else if (intent.getStringExtra("RemoteController").equals("Sticky_Edge_No")) {
            sendBroadcast(new Intent("Sticky_Edge_No"));
        }
        stopSelf();
        return functionsClass.serviceMode();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClass = new FunctionsClass(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
