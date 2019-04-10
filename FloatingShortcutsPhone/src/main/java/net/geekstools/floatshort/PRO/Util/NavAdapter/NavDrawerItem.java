package net.geekstools.floatshort.PRO.Util.NavAdapter;

import android.app.PendingIntent;
import android.graphics.drawable.Drawable;

public class NavDrawerItem {

    CharSequence charTitle;
    String packageName, appName, category, times, notificationAppName, notificationTitle, notificationText, notificationId, notificationTime, notificationPackage;
    String[] packageNames;
    Drawable appIcon, run, notificationAppIcon, notificationLargeIcon;
    PendingIntent notificationIntent;

    public NavDrawerItem(String AppName, String PackageName, Drawable AppIcon) {
        this.appName = AppName;
        this.packageName = PackageName;
        this.appIcon = AppIcon;
    }

    public NavDrawerItem(String title, String pack, Drawable icon, String times) {
        this.appName = title;
        this.packageName = pack;
        this.appIcon = icon;
        this.times = times;
    }

    public NavDrawerItem(String title, String desc) {
        this.packageName = title;
        this.appName = desc;
    }

    public NavDrawerItem(CharSequence title, Drawable icon) {
        this.charTitle = title;
        this.appIcon = icon;
    }

    public NavDrawerItem(String title, Drawable icon) {
        this.packageName = title;
        this.appIcon = icon;
    }

    public NavDrawerItem(String category, String[] packageNames) {
        this.category = category;
        this.packageNames = packageNames;
    }

    public NavDrawerItem(String category, String[] packageNames, String times) {
        this.category = category;
        this.packageNames = packageNames;
        this.times = times;
    }

    public NavDrawerItem(String notificationTime, String notificationPackage,
                         String notificationAppName, String notificationTitle, String notificationText, Drawable notificationAppIcon, Drawable notificationLargeIcon,
                         String notificationId, PendingIntent notificationIntent) {
        this.notificationTime = notificationTime;
        this.notificationPackage = notificationPackage;
        this.notificationAppName = notificationAppName;
        this.notificationTitle = notificationTitle;
        this.notificationText = notificationText;
        this.notificationAppIcon = notificationAppIcon;
        this.notificationLargeIcon = notificationLargeIcon;
        this.notificationId = notificationId;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public CharSequence getCharTitle() {
        return this.charTitle;
    }

    public String getAppName() {
        return this.appName;
    }

    public String getCategory() {
        return this.category;
    }

    public String getTimes() {
        return this.times;
    }

    public String getNotificationTime() {
        return this.notificationTime;
    }

    public String getNotificationPackage() {
        return this.notificationPackage;
    }

    public String getNotificationAppName() {
        return this.notificationAppName;
    }

    public String getNotificationTitle() {
        return this.notificationTitle;
    }

    public String getNotificationText() {
        return this.notificationText;
    }

    public String getNotificationId() {
        return this.notificationId;
    }

    public String[] getPackName() {
        return this.packageNames;
    }

    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public Drawable getRunIcon() {
        return this.run;
    }

    public Drawable getNotificationAppIcon() {
        return this.notificationAppIcon;
    }

    public Drawable getNotificationLargeIcon() {
        return this.notificationLargeIcon;
    }

    public PendingIntent getNotificationIntent() {
        return this.notificationIntent;
    }
}
