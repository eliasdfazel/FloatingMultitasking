/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/28/20 12:48 PM
 * Last modified 3/28/20 12:38 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteTask.Remove;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Bluetooth;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Floating;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Gps;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Nfc;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Time;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Wifi;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.AppUnlimitedShortcuts;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;

public class RemoveAll extends Service {

    FunctionsClass functionsClass;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        /*Apps*/
        Intent App_Unlimited_Shortcuts = new Intent(getApplicationContext(), AppUnlimitedShortcuts.class);
        App_Unlimited_Shortcuts.putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Shortcuts);

        Intent App_Unlimited_Wifi = new Intent(getApplicationContext(), net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.App_Unlimited_Wifi.class);
        App_Unlimited_Wifi.putExtra("pack", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Wifi);

        Intent App_Unlimited_Bluetooth = new Intent(getApplicationContext(), net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.App_Unlimited_Bluetooth.class);
        App_Unlimited_Bluetooth.putExtra("pack", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Bluetooth);

        Intent App_Unlimited_Gps = new Intent(getApplicationContext(), net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.App_Unlimited_Gps.class);
        App_Unlimited_Gps.putExtra("pack", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Gps);

        Intent App_Unlimited_Nfc = new Intent(getApplicationContext(), net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.App_Unlimited_Nfc.class);
        App_Unlimited_Nfc.putExtra("pack", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Nfc);

        Intent App_Unlimited_Time = new Intent(getApplicationContext(), net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.App_Unlimited_Time.class);
        App_Unlimited_Time.putExtra("pack", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Time);

        /*Categories*/
        Intent Category_Unlimited_Category = new Intent(getApplicationContext(), Folder_Unlimited_Floating.class);
        Category_Unlimited_Category.putExtra("categoryName", getString(R.string.remove_all_floatings));
        startService(Category_Unlimited_Category);

        Intent Category_Unlimited_Wifi = new Intent(getApplicationContext(), Folder_Unlimited_Wifi.class);
        Category_Unlimited_Wifi.putExtra("categoryName", getString(R.string.remove_all_floatings));
        startService(Category_Unlimited_Wifi);

        Intent Category_Unlimited_Bluetooth = new Intent(getApplicationContext(), Folder_Unlimited_Bluetooth.class);
        Category_Unlimited_Bluetooth.putExtra("categoryName", getString(R.string.remove_all_floatings));
        startService(Category_Unlimited_Bluetooth);

        Intent Category_Unlimited_Gps = new Intent(getApplicationContext(), Folder_Unlimited_Gps.class);
        Category_Unlimited_Gps.putExtra("categoryName", getString(R.string.remove_all_floatings));
        startService(Category_Unlimited_Gps);

        Intent Category_Unlimited_Nfc = new Intent(getApplicationContext(), Folder_Unlimited_Nfc.class);
        Category_Unlimited_Nfc.putExtra("categoryName", getString(R.string.remove_all_floatings));
        startService(Category_Unlimited_Nfc);

        Intent Category_Unlimited_Time = new Intent(getApplicationContext(), Folder_Unlimited_Time.class);
        Category_Unlimited_Time.putExtra("categoryName", getString(R.string.remove_all_floatings));
        startService(Category_Unlimited_Time);

        /*HIS*/
        Intent App_Unlimited_HIS = new Intent(getApplicationContext(), net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.App_Unlimited_HIS.class);
        App_Unlimited_HIS.putExtra("packageName", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_HIS);

        /*Widgets*/
        Intent Widget_Unlimited_Floating = new Intent(getApplicationContext(), net.geekstools.floatshort.PRO.Widgets.FloatingServices.WidgetUnlimitedFloating.class);
        Widget_Unlimited_Floating.putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings));
        startService(Widget_Unlimited_Floating);

        /*Frequently*/
        Intent App_Unlimited_Shortcuts_Frequently = new Intent(getApplicationContext(), net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.App_Unlimited_Shortcuts_Frequently.class);
        App_Unlimited_Shortcuts_Frequently.putExtra("PackageName", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Shortcuts_Frequently);

        stopSelf();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClass = new FunctionsClass(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
