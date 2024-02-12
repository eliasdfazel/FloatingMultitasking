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

    var AppName: String? = null
    var PackageName: String? = null
    var ClassName: String? = null

    var folderName: String? = null
    var packageNames: Array<String>? = null

    var classNameProviderWidget: String? = null
    var configClassNameWidget: String? = null
    var widgetLabel: String? = null
    var appWidgetProviderInfo: AppWidgetProviderInfo? = null
    var appWidgetId: Int? = null
    var addedWidgetRecovery: Boolean? = null

    var AppIcon: Drawable? = null

    var searchResultType: Int? = null

    constructor(AppName: String?, PackageName: String?, ClassName: String?, AppIcon: Drawable?,
                searchResultType: Int?) {
        this.AppName = AppName
        this.PackageName = PackageName
        this.ClassName = ClassName
        this.AppIcon = AppIcon

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
        this.AppName = AppName
        this.PackageName = PackageName
        this.classNameProviderWidget = classNameProviderWidget
        this.configClassNameWidget = configClassNameWidget
        this.widgetLabel = widgetLabel
        this.AppIcon = AppIcon
        this.appWidgetProviderInfo = appWidgetProviderInfo
        this.appWidgetId = appWidgetId

        this.searchResultType = searchResultType
    }
}