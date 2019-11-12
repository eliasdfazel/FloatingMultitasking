/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:18 PM
 * Last modified 11/11/19 7:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.RemoteTask;

import android.app.Activity;
import android.os.Bundle;

import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons;

public class CreateFloatingShortcuts extends Activity {

    FunctionsClass functionsClass;

    String packageName, className;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        functionsClass = new FunctionsClass(getApplicationContext(), CreateFloatingShortcuts.this);

        packageName = getIntent().getStringExtra("PackageName");
        className = getIntent().getStringExtra("ClassName");

        if (functionsClass.loadCustomIcons()) {
            LoadCustomIcons loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
            loadCustomIcons.load();
            FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
        }

        functionsClass.runUnlimitedShortcutsServiceHIS(packageName, className);

        finish();
    }
}
