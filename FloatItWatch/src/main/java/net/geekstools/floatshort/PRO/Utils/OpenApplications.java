/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils;

import android.app.Activity;
import android.os.Bundle;

import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;

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
