/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 2:29 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteTask.Create;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;

import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassRunServices;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

public class RemoteRecoveryActivity extends Activity {

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        FunctionsClass functionsClass = new FunctionsClass(getApplicationContext());
        FunctionsClassRunServices functionsClassRunServices = new FunctionsClassRunServices(getApplicationContext());

        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);
        PublicVariable.HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size, this.getResources().getDisplayMetrics());

        String packageName = getIntent().getStringExtra("packageName");

        if (functionsClass.customIconsEnable()) {
            LoadCustomIcons loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
            loadCustomIcons.load();
            FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIconsNumber());
        }

        functionsClassRunServices.runUnlimitedShortcutsServicePackage(packageName);
        finish();
    }
}
