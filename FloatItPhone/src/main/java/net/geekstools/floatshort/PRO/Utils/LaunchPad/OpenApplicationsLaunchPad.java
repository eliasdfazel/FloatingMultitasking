/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:36 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.LaunchPad;

import android.app.Activity;
import android.os.Bundle;

import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;

public class OpenApplicationsLaunchPad extends Activity {

    FunctionsClass functionsClass;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        functionsClass = new FunctionsClass(getApplicationContext(), OpenApplicationsLaunchPad.this);
        String packageName = getIntent().getStringExtra("packageName");
        try {
            if (getIntent().hasExtra("className")) {
                String className = getIntent().getStringExtra("className");
                functionsClass.openApplicationFromActivity(packageName, className);
            } else {
                functionsClass.openApplicationFromActivity(packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            functionsClass.openApplicationFromActivity(packageName);
        }
        finish();
    }
}
