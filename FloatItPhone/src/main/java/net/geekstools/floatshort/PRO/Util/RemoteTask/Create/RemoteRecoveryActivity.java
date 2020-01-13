/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/13/20 9:58 AM
 * Last modified 1/13/20 9:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.RemoteTask.Create;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;

import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassRunServices;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons;

public class RemoteRecoveryActivity extends Activity {

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        FunctionsClass functionsClass = new FunctionsClass(getApplicationContext(), this);
        FunctionsClassRunServices functionsClassRunServices = new FunctionsClassRunServices(getApplicationContext());

        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);
        PublicVariable.HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size, this.getResources().getDisplayMetrics());

        String packageName = getIntent().getStringExtra("packageName");

        if (functionsClass.loadCustomIcons()) {
            LoadCustomIcons loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
            loadCustomIcons.load();
            FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
        }

        functionsClassRunServices.runUnlimitedShortcutsServicePackage(packageName);
        finish();
    }
}
