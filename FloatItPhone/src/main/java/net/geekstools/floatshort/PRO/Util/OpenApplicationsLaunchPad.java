/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:18 PM
 * Last modified 11/11/19 7:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util;

import android.app.Activity;
import android.os.Bundle;

import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;

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
