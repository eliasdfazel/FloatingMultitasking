/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:18 PM
 * Last modified 11/11/19 7:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;

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
