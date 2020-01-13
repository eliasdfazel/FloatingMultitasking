/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/13/20 9:58 AM
 * Last modified 1/13/20 9:54 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.RemoteTask.Create;

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
