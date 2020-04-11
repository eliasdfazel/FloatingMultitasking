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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Bluetooth;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Floating;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Gps;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Nfc;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Time;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Folder_Unlimited_Wifi;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForApplications;
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForBluetooth;
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForFrequentlyApplications;
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForGps;
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForHIS;
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForNfc;
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForTime;
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForWifi;

public class RemoveAllActivity extends Activity {

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);

        /*Apps*/
        Intent App_Unlimited_Shortcuts = new Intent(getApplicationContext(), FloatingShortcutsForApplications.class);
        App_Unlimited_Shortcuts.putExtra("PackageName", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Shortcuts);

        Intent App_Unlimited_Wifi = new Intent(getApplicationContext(), FloatingShortcutsForWifi.class);
        App_Unlimited_Wifi.putExtra("pack", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Wifi);

        Intent App_Unlimited_Bluetooth = new Intent(getApplicationContext(), FloatingShortcutsForBluetooth.class);
        App_Unlimited_Bluetooth.putExtra("pack", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Bluetooth);

        Intent App_Unlimited_Gps = new Intent(getApplicationContext(), FloatingShortcutsForGps.class);
        App_Unlimited_Gps.putExtra("pack", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Gps);

        Intent App_Unlimited_Nfc = new Intent(getApplicationContext(), FloatingShortcutsForNfc.class);
        App_Unlimited_Nfc.putExtra("pack", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Nfc);

        Intent App_Unlimited_Time = new Intent(getApplicationContext(), FloatingShortcutsForTime.class);
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
        Intent App_Unlimited_HIS = new Intent(getApplicationContext(), FloatingShortcutsForHIS.class);
        App_Unlimited_HIS.putExtra("packageName", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_HIS);

        /*Widgets*/
        Intent Widget_Unlimited_Floating = new Intent(getApplicationContext(), net.geekstools.floatshort.PRO.Widgets.FloatingServices.WidgetUnlimitedFloating.class);
        Widget_Unlimited_Floating.putExtra(getString(R.string.remove_all_floatings), getString(R.string.remove_all_floatings));
        startService(Widget_Unlimited_Floating);

        /*Frequently*/
        Intent App_Unlimited_Shortcuts_Frequently = new Intent(getApplicationContext(), FloatingShortcutsForFrequentlyApplications.class);
        App_Unlimited_Shortcuts_Frequently.putExtra("PackageName", getString(R.string.remove_all_floatings));
        startService(App_Unlimited_Shortcuts_Frequently);

        finish();
    }
}
