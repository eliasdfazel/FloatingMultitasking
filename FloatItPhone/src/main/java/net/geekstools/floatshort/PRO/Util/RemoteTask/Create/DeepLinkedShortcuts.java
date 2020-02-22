/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/13/20 9:58 AM
 * Last modified 1/13/20 9:56 AM
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


public class DeepLinkedShortcuts extends Activity {

    String PackageName;

    private String htmlSymbol = "20%7C%20";
    private String htmlSymbolDelete = "0%7C%20";

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        FunctionsClass functionsClass = new FunctionsClass(getApplicationContext(), DeepLinkedShortcuts.this);
        FunctionsClassRunServices functionsClassRunServices = new FunctionsClassRunServices(getApplicationContext());

        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);
        PublicVariable.HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size, this.getResources().getDisplayMetrics());

        try {
            String incomingURI = getIntent().getDataString();
            PackageName = incomingURI.substring(incomingURI.lastIndexOf(htmlSymbol) + 1);
            PackageName = PackageName.replace(htmlSymbolDelete, "");

            if (functionsClass.loadCustomIcons()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
                loadCustomIcons.load();
                FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
            }

            functionsClassRunServices.runUnlimitedShortcutsServicePackage(PackageName);
        } catch (Exception e) {
            finish();
            return;
        }

        finish();
    }
}