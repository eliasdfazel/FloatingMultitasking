/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/25/20 11:57 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.AdapterItemsData;

import android.graphics.drawable.Drawable;

public class AdapterItems {

    String titleText, packageName, appName;
    Drawable appIcon;

    public AdapterItems(String appName, String packageName, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
    }

    public AdapterItems(String titleText, Drawable appIcon) {
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
