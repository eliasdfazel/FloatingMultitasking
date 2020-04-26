/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/26/20 5:50 AM
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

        PublicVariable.floatingSizeNumber = functionsClass.readDefaultPreference("floatingSize", 39);
        PublicVariable.floatingViewsHW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber, this.getResources().getDisplayMetrics());

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
