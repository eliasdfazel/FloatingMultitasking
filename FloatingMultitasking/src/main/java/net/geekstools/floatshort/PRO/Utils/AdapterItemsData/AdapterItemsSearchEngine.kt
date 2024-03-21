/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.AdapterItemsData

import android.appwidget.AppWidgetProviderInfo
import android.graphics.drawable.Drawable

class AdapterItemsSearchEngine {

    var appName: String? = null
    var packageName: String? = null
    var className: String? = null

    var folderName: String? = null
    var packageNames: Array<String>? = null

    var classNameProviderWidget: String? = null
    var configClassNameWidget: String? = null
    var widgetLabel: String? = null
    var appWidgetProviderInfo: AppWidgetProviderInfo? = null
    var appWidgetId: Int? = null
    var addedWidgetRecovery: Boolean? = null

    var appIcon: Drawable? = null

    var searchResultType: Int? = null

    constructor(appName: String?, packageName: String?, className: String?, appIcon: Drawable?,
                searchResultType: Int?) {
        this.appName = appName
        this.packageName = packageName
        this.className = className
        this.appIcon = appIcon

        this.searchResultType = searchResultType
    }

    constructor(folderName: String?, packageNames: Array<String>?,
                searchResultType: Int?) {
        this.folderName = folderName
        this.packageNames = packageNames

        this.searchResultType = searchResultType
    }

    constructor(AppName: String?, PackageName: String?, classNameProviderWidget: String?, configClassNameWidget: String?,
                widgetLabel: String?, AppIcon: Drawable?, appWidgetProviderInfo: AppWidgetProviderInfo?, appWidgetId: Int?,
                searchResultType: Int?) {
        this.appName = AppName
        this.packageName = PackageName
        this.classNameProviderWidget = classNameProviderWidget
        this.configClassNameWidget = configClassNameWidget
        this.widgetLabel = widgetLabel
        this.appIcon = AppIcon
        this.appWidgetProviderInfo = appWidgetProviderInfo
        this.appWidgetId = appWidgetId

        this.searchResultType = searchResultType
    }
}