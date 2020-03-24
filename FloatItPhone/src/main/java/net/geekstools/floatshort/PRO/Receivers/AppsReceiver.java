/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;

public class AppsReceiver extends BroadcastReceiver {

    FunctionsClass functionsClass;

    @Override
    public void onReceive(Context context, Intent intent) {
        functionsClass = new FunctionsClass(context);
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
            try {
                String packageName = intent.getData().getEncodedSchemeSpecificPart();

                functionsClass.removeLine(".uFile", packageName);
                functionsClass.addAppShortcuts();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
