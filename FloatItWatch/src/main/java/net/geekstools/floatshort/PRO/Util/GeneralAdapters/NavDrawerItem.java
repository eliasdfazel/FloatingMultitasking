/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:18 PM
 * Last modified 11/11/19 7:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.GeneralAdapters;

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
