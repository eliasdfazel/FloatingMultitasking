/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/14/20 4:22 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SearchEngine.Widgets.Extensions

import androidx.constraintlayout.widget.ConstraintLayout
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.Widgets.WidgetActivity
import net.geekstools.floatshort.PRO.Utils.UI.PopupIndexedFastScroller.Factory.calculateNavigationBarHeight

fun WidgetActivity.setupWidgetActivityUserInterface() {

    functionsClassLegacy.doVibrate(159)

    val searchEngineLayoutParams = searchEngineWidgetActivityBinding.searchEngineViewInclude.root.layoutParams as ConstraintLayout.LayoutParams
    searchEngineLayoutParams.setMargins(searchEngineLayoutParams.marginStart, searchEngineLayoutParams.topMargin, searchEngineLayoutParams.marginEnd, calculateNavigationBarHeight(resources) + searchEngineLayoutParams.bottomMargin)

    window.navigationBarColor = getColor(R.color.default_color)

}