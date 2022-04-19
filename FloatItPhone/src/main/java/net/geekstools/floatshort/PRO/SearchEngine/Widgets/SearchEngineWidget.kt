/*
 * Copyright © 2022 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/19/22, 3:40 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SearchEngine.Widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import net.geekstools.floatshort.PRO.R

class SearchEngineWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)



     }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        if (context != null) {

            appWidgetIds?.let {

                for (i in appWidgetIds.indices) {

                    val currentWidgetId = appWidgetIds[i]

                    val searchEngineIntent = Intent(context, WidgetActivity::class.java)
                    searchEngineIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    val searchEnginePendingIntent = PendingIntent.getActivity(context, 0, searchEngineIntent, PendingIntent.FLAG_UPDATE_CURRENT or  PendingIntent.FLAG_IMMUTABLE)

                    val remoteViews = RemoteViews(context.packageName, R.layout.search_engine_widget_view)
                    remoteViews.setOnClickPendingIntent(R.id.searchInvocationView, searchEnginePendingIntent)

                    appWidgetManager?.updateAppWidget(currentWidgetId, remoteViews)

                }

            }

        }

    }

}