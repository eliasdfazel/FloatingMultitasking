/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 2:18 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;

import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;


public class DeepLinkedFolders extends Activity {

    String CategoryName;

    private String htmlSymbol = "/";
    private String htmlSymbolDelete = "%20%7C%20Floating%20Category";

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        FunctionsClass functionsClass = new FunctionsClass(getApplicationContext());

        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);
        PublicVariable.HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size, this.getResources().getDisplayMetrics());

        try {
            String incomingURI = getIntent().getDataString();
            FunctionsClassDebug.Companion.PrintDebug("URI >> " + incomingURI);
            CategoryName = incomingURI.substring(incomingURI.lastIndexOf(htmlSymbol) + 1);
            CategoryName = CategoryName.replace(htmlSymbolDelete, "");

            functionsClass.runUnlimitedFolderService(CategoryName);
        } catch (Exception e) {
            finish();
            return;
        }

        finish();
    }
}
