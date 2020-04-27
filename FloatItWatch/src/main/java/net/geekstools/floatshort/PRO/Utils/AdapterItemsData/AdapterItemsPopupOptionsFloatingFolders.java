/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 11:21 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.AdapterItemsData;

import android.graphics.drawable.Drawable;

public class AdapterItemsPopupOptionsFloatingFolders {

    String itemAppPackageName, itemTitle;
    Drawable itemIcon;

    public AdapterItemsPopupOptionsFloatingFolders(String itemTitle, String itemAppPackageName, Drawable itemIcon) {
        this.itemTitle = itemTitle;
        this.itemAppPackageName = itemAppPackageName;
        this.itemIcon = itemIcon;
    }

    public String getItemAppPackageName() {
        return this.itemAppPackageName;
    }

    public String getItemTitle() {
        return this.itemTitle;
    }

    public Drawable getItemIcon() {
        return this.itemIcon;
    }
}
