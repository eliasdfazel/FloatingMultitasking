/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:18 PM
 * Last modified 11/11/19 7:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation.Alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.TypedValue;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;

public class AlarmAlertBroadcastReceiver extends BroadcastReceiver {

    FunctionsClass functionsClass;

    String setTime;
    int position;

    @Override
    public void onReceive(Context context, Intent intent) {
        setTime = intent.getStringExtra("time");
        position = intent.getIntExtra("position", -1);

        functionsClass = new FunctionsClass(context);
        StaticWakeLock.lockOn(context);

        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);
        PublicVariable.HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size, context.getResources().getDisplayMetrics());

        try {
            String[] contents = functionsClass.readFileLine(setTime);
            if (contents != null) {
                if (contents.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(new Intent(context, BindServices.class));
                    } else {
                        context.startService(new Intent(context, BindServices.class));
                    }
                }
            }
            for (String content : contents) {
                if (content.contains("APP")) {
                    functionsClass.runUnlimitedTime(content.replace("APP", ""));
                } else if (content.contains("CATEGORY")) {
                    functionsClass.runUnlimitedFolderTime(content.replace("CATEGORY", ""),
                            functionsClass.readFileLine(content.replace("CATEGORY", "")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent nextAlarm = new Intent(context, SetupAlarms.class);
        nextAlarm.putExtra("time", setTime);
        nextAlarm.putExtra("position", position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(nextAlarm);
        } else {
            context.startService(nextAlarm);
        }
    }
}
