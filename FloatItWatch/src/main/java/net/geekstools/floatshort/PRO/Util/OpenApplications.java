package net.geekstools.floatshort.PRO.Util;

import android.app.Activity;
import android.os.Bundle;

import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;

public class OpenApplications extends Activity {

    FunctionsClass functionsClass;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        functionsClass = new FunctionsClass(getApplicationContext(), OpenApplications.this);
        String packageName = getIntent().getStringExtra("packageName");
        try {
            if (getIntent().hasExtra("className")) {
                String className = getIntent().getStringExtra("className");
                functionsClass.openApplication(packageName, className);
            } else {
                functionsClass.openApplication(packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            functionsClass.openApplication(packageName);
        }
        finish();
    }
}