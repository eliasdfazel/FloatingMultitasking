package net.geekstools.floatshort.PRO;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.wearable.activity.WearableActivity;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.FirebaseApp;

import net.geekstools.floatshort.PRO.Shortcuts.ListViewOff;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;

import io.fabric.sdk.android.Fabric;

public class Configurations extends WearableActivity {

    FunctionsClass functionsClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());
        FirebaseApp.initializeApp(getApplicationContext());

        setAmbientEnabled();
        functionsClass = new FunctionsClass(getApplicationContext(), Configurations.this);
        functionsClass.loadSavedColor();

        try {
            if (functionsClass.appVersionName(getPackageName()).contains("[BETA]")) {
                functionsClass.savePreference(".BETA", "isBetaTester", true);
                functionsClass.savePreference(".BETA", "installedVersionCode", functionsClass.appVersionCode(getPackageName()));
                functionsClass.savePreference(".BETA", "installedVersionName", functionsClass.appVersionName(getPackageName()));
                functionsClass.savePreference(".BETA", "deviceModel", functionsClass.getDeviceName());
                functionsClass.savePreference(".BETA", "userRegion", functionsClass.getCountryIso());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] Permissions = {
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET,
                Manifest.permission.VIBRATE
        };
        requestPermissions(Permissions, 666);
        if (!Settings.canDrawOverlays(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), PermissionDialogue.class));
        } else {
            Intent listViewOff = new Intent(getApplicationContext(), ListViewOff.class);
            listViewOff.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(listViewOff);
        }
        functionsClass.updateRecoverShortcuts();
        finish();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
        } else {
        }
    }
}
