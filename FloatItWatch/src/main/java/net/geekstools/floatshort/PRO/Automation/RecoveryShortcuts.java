/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 7:11 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import androidx.annotation.Nullable;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.Configurations;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;

public class RecoveryShortcuts extends Service {

    String PackageName;
    String[] appData;

    boolean runService = true;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        FunctionsClass functionsClass = new FunctionsClass(getApplicationContext());
        functionsClass.loadSavedColor();

        PublicVariable.alpha = 133;
        PublicVariable.opacity = 255;
        if (!sharedPreferences.getBoolean("hide", false)) {
            PublicVariable.transparencyEnabled = false;
        } else if (sharedPreferences.getBoolean("hide", false)) {
            PublicVariable.transparencyEnabled = true;
        }
        PublicVariable.floatingSizeNumber = 33;
        PublicVariable.floatingViewsHW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber, this.getResources().getDisplayMetrics());

        if (getApplicationContext().getFileStreamPath(".uFile").exists()) {
            try {
                appData = functionsClass.readFileLine(".uFile");
                for (int navItem = 0; navItem < appData.length; navItem++) {
                    runService = true;
                    if (PublicVariable.floatingShortcutsList != null) {
                        for (int check = 0; check < PublicVariable.floatingShortcutsList.size(); check++) {
                            if (appData[navItem].equals(PublicVariable.floatingShortcutsList.get(check))) {
                                runService = false;
                            }
                        }
                    }

                    if (runService == true) {
                        try {
                            PackageName = appData[navItem];
                            functionsClass.runUnlimitedShortcutsServiceRecovery(PackageName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (PublicVariable.allFloatingCounter == 0) {
                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getBoolean("stable", true) == false) {
                        stopService(new Intent(getApplicationContext(), BindServices.class));
                    }
                }
            }
        } else {
            startActivity(new Intent(getApplicationContext(), Configurations.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        if (PublicVariable.allFloatingCounter == 0) {
            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean("stable", true) == false) {
                stopService(new Intent(getApplicationContext(), BindServices.class));
            }
        }
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
