/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.TypedValue;

import androidx.annotation.Nullable;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;

public class RecoveryBluetooth extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        FunctionsClass functionsClass = new FunctionsClass(getApplicationContext());
        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);
        PublicVariable.HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size, this.getResources().getDisplayMetrics());

        if (getFileStreamPath(".auto" + getClass().getSimpleName().replace("Recovery", "")).exists()
                && getFileStreamPath(".auto" + getClass().getSimpleName().replace("Recovery", "")).isFile()) {
            try {
                String[] PackageNames = functionsClass.readFileLine(
                        ".auto" + getClass().getSimpleName().replace("Recovery", ""));
                if (PackageNames.length > 0) {
                    for (String PackageName : PackageNames) {
                        functionsClass.runUnlimitedBluetooth(PackageName);
                    }

                    Intent steady = new Intent(getApplicationContext(), BindServices.class);
                    steady.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (functionsClass.returnAPI() < 26) {
                        startService(steady);
                    } else {
                        startForegroundService(steady);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (getFileStreamPath(".auto" + getClass().getSimpleName().replace("Recovery", "") + "Category").exists()
                && getFileStreamPath(".auto" + getClass().getSimpleName().replace("Recovery", "") + "Category").isFile()) {
            try {
                String[] CategoryNames = functionsClass.readFileLine(
                        ".auto" + getClass().getSimpleName().replace("Recovery", "") + "Category");
                if (CategoryNames.length > 0) {
                    for (String CategoryName : CategoryNames) {
                        functionsClass.runUnlimitedFolderBluetooth(CategoryName, functionsClass.readFileLine(CategoryName));
                    }

                    Intent steady = new Intent(getApplicationContext(), BindServices.class);
                    steady.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (functionsClass.returnAPI() < 26) {
                        startService(steady);
                    } else {
                        startForegroundService(steady);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        stopSelf();
        return functionsClass.serviceMode();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
