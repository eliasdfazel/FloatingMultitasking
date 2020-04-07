/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 2:32 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.LaunchPad;

import android.app.Activity;
import android.os.Bundle;

import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;

public class OpenApplicationsLaunchPad extends Activity {

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);

        FunctionsClass functionsClass = new FunctionsClass(getApplicationContext());

        String packageName = getIntent().getStringExtra("packageName");

        try {
            if (getIntent().hasExtra("className")) {

                String className = getIntent().getStringExtra("className");

                functionsClass.
                        openApplicationFromActivity(OpenApplicationsLaunchPad.this,
                                packageName,
                                className);

            } else {

                functionsClass
                        .openApplicationFromActivity(OpenApplicationsLaunchPad.this,
                                packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();

            functionsClass
                    .openApplicationFromActivity(OpenApplicationsLaunchPad.this,
                            packageName);
        }

        finish();
    }
}
