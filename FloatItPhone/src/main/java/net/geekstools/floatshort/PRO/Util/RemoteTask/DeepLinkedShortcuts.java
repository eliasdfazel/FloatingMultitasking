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
import android.util.TypedValue;

import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug;
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

            functionsClass.runUnlimitedShortcutsService(PackageName);
        } catch (Exception e) {
            finish();
            return;
        }

        finish();
    }
}
