/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.AdapterItemsData;

import android.app.PendingIntent;
import android.appwidget.AppWidgetProviderInfo;
import android.graphics.drawable.Drawable;

public class AdapterItems {

    CharSequence charTitle;
    String packageName, classNameProviderWidget, ConfigClassNameWidget, appName, category, times, widgetLabel,
            notificationAppName, notificationTitle, notificationText, notificationId, notificationTime, notificationPackage;

    String[] packageNames;

    int appWidgetId, searchResultType;

    Drawable appIcon, run, widgetPreview,
            notificationAppIcon, notificationLargeIcon;

    boolean addedWidgetRecovery;

    PendingIntent notificationIntent;

    AppWidgetProviderInfo appWidgetProviderInfo;

    public AdapterItems(String AppName, String PackageName, Drawable AppIcon) {
        this.appName = AppName;
        this.packageName = PackageName;
        this.appIcon = AppIcon;
    }

    public AdapterItems(String AppName, String PackageName, Drawable AppIcon, int searchResultType) {
        this.appName = AppName;
        this.packageName = PackageName;
        this.appIcon = AppIcon;
        this.searchResultType = searchResultType;
    }

    public AdapterItems(String appName, String packageName, Drawable icon, String times) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = icon;
        this.times = times;
    }

    public AdapterItems(String packageName, String appName) {
        this.packageName = packageName;
        this.appName = appName;
    }

    public AdapterItems(CharSequence title, Drawable icon) {
        this.charTitle = title;
        this.appIcon = icon;
    }

    public AdapterItems(String packageName, Drawable icon) {
        this.packageName = packageName;
        this.appIcon = icon;
    }

    public AdapterItems(String category, String[] packageNames, int searchResultType) {
        this.category = category;
        this.packageNames = packageNames;
        this.searchResultType = searchResultType;
    }

    public AdapterItems(String category, String[] packageNames, String times) {
        this.category = category;
        this.packageNames = packageNames;
        this.times = times;
    }

    public AdapterItems(String AppName, String PackageName, String classNameProviderWidget, String configClassNameWidget, String widgetLabel, Drawable AppIcon, Drawable widgetPreview, AppWidgetProviderInfo appWidgetProviderInfo) {
        this.appName = AppName;
        this.packageName = PackageName;
        this.classNameProviderWidget = classNameProviderWidget;
        this.ConfigClassNameWidget = configClassNameWidget;
        this.widgetLabel = widgetLabel;
        this.appIcon = AppIcon;
        this.widgetPreview = widgetPreview;
        this.appWidgetProviderInfo = appWidgetProviderInfo;
    }

    public AdapterItems(String AppName, String PackageName, String classNameProviderWidget, String configClassNameWidget, String widgetLabel, Drawable AppIcon, AppWidgetProviderInfo appWidgetProviderInfo, int appWidgetId, boolean addedWidgetRecovery, int searchResultType) {
        this.appName = AppName;
        this.packageName = PackageName;
        this.classNameProviderWidget = classNameProviderWidget;
        this.ConfigClassNameWidget = configClassNameWidget;
        this.widgetLabel = widgetLabel;
        this.appIcon = AppIcon;
        this.appWidgetProviderInfo = appWidgetProviderInfo;
        this.appWidgetId = appWidgetId;
        this.addedWidgetRecovery = addedWidgetRecovery;
        this.searchResultType = searchResultType;
    }

    public AdapterItems(String notificationTime, String notificationPackage,
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

    public String getWidgetLabel() {
        return this.widgetLabel;
    }

    public String getClassNameProviderWidget() {
        return this.classNameProviderWidget;
    }

    public String getConfigClassNameWidget() {
        return this.ConfigClassNameWidget;
    }

    public String[] getPackageNames() {
        return this.packageNames;
    }

    public int getAppWidgetId() {
        return this.appWidgetId;
    }

    public int getSearchResultType(){
        return this.searchResultType;
    }
    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public Drawable getWidgetPreview() {
        return this.widgetPreview;
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

    public boolean getAddedWidgetRecovery() {
        return this.addedWidgetRecovery;
    }

    public PendingIntent getNotificationIntent() {
        return this.notificationIntent;
    }

    public AppWidgetProviderInfo getAppWidgetProviderInfo() {
        return this.appWidgetProviderInfo;
    }
}
