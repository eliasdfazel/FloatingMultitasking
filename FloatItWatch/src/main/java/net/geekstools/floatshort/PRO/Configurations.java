/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/25/20 12:13 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.wearable.activity.WearableActivity;
import android.util.TypedValue;

import com.google.firebase.FirebaseApp;

import net.geekstools.floatshort.PRO.Shortcuts.ApplicationsViewWatch;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;

public class Configurations extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());

        FunctionsClass functionsClass = new FunctionsClass(getApplicationContext());
        functionsClass.loadSavedColor();

        if (!BuildConfig.DEBUG) {
            functionsClass.savePreference(".UserInformation", "isBetaTester", functionsClass.appVersionName(getPackageName()).equals("[BETA]") ? true : false);
            functionsClass.savePreference(".UserInformation", "installedVersionCode", functionsClass.appVersionCode(getPackageName()));
            functionsClass.savePreference(".UserInformation", "installedVersionName", functionsClass.appVersionName(getPackageName()));
            functionsClass.savePreference(".UserInformation", "deviceModel", functionsClass.getDeviceName());
            functionsClass.savePreference(".UserInformation", "userRegion", functionsClass.getCountryIso());

            if (functionsClass.appVersionName(getPackageName()).equals("[BETA]")) {
                functionsClass.saveDefaultPreference("JoinedBetaProgrammer", true);
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("theme", Context.MODE_PRIVATE);
        PublicVariable.alpha = 133;
        PublicVariable.opacity = 255;
        if (!sharedPreferences.getBoolean("hide", false)) {
            PublicVariable.hide = false;
        } else if (sharedPreferences.getBoolean("hide", false)) {
            PublicVariable.hide = true;
        }
        PublicVariable.size = 33;
        PublicVariable.HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size, getResources().getDisplayMetrics());

        String[] Permissions = {
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET,
                Manifest.permission.VIBRATE
        };
        requestPermissions(Permissions, 666);

        if (!Settings.canDrawOverlays(getApplicationContext())) {

            startActivity(new Intent(getApplicationContext(), PermissionDialogue.class));

        } else {
            functionsClass.updateRecoverShortcuts();

            Intent applicationsViewWatch = new Intent(getApplicationContext(), ApplicationsViewWatch.class);
            applicationsViewWatch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(applicationsViewWatch);
        }


        finish();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }
}
