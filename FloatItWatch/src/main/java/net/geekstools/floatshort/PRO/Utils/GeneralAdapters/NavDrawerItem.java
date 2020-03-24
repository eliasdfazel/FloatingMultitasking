/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.GeneralAdapters;

import android.graphics.drawable.Drawable;

public class NavDrawerItem {

    String titleText, packageName, appName;
    Drawable appIcon;

    public NavDrawerItem(String appName, String packageName, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
    }

    public NavDrawerItem(String titleText, Drawable appIcon) {
        this.titleText = titleText;
        this.appIcon = appIcon;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getAppName() {
        return this.appName;
    }

    public Drawable getIcon() {
        return this.appIcon;
    }

    public String getTitleText() {
        return this.titleText;
    }
}
