/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/25/20 4:42 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widgets.WidgetsAdapter

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.graphics.drawable.LayerDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widgets.Utils.FunctionsClassWidgets
import net.geekstools.floatshort.PRO.Widgets.WidgetConfigurations
import java.util.*

class ConfiguredWidgetsAdapter(private val widgetConfigurationsActivity: WidgetConfigurations,
                               private val adapterItems: ArrayList<AdapterItems>, private val appWidgetManager: AppWidgetManager, private val appWidgetHost: AppWidgetHost) : RecyclerView.Adapter<ConfiguredWidgetsAdapter.ViewHolder>() {

    private val functionsClass: FunctionsClass = FunctionsClass(widgetConfigurationsActivity)
    private val functionsClassWidgets: FunctionsClassWidgets = FunctionsClassWidgets(widgetConfigurationsActivity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(widgetConfigurationsActivity).inflate(R.layout.widget_configured_items, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val appWidgetProviderInfo = adapterItems[position].appWidgetProviderInfo
        val appWidgetId = adapterItems[position].appWidgetId

        widgetConfigurationsActivity.createWidget(widgetConfigurationsActivity, viewHolder.widgetPreview,
                appWidgetManager, appWidgetHost,
                appWidgetProviderInfo, appWidgetId)

        viewHolder.widgetLabel.setText(if (adapterItems[position].addedWidgetRecovery) adapterItems[position].widgetLabel + " " + "\uD83D\uDD04" else adapterItems[position].widgetLabel)
        viewHolder.widgetLabel.setTextColor(if (PublicVariable.themeLightDark) widgetConfigurationsActivity.getColor(R.color.dark) else widgetConfigurationsActivity.getColor(R.color.light))

        val drawFloatTheWidget = widgetConfigurationsActivity.getDrawable(R.drawable.draw_open) as LayerDrawable?
        val backFloatTheWidget = drawFloatTheWidget?.findDrawableByLayerId(R.id.backgroundTemporary)
        backFloatTheWidget?.setTint(functionsClass.extractDominantColor(functionsClass.applicationIcon(adapterItems[position].packageName)))

        viewHolder.floatTheWidget.setImageDrawable(drawFloatTheWidget)
        viewHolder.floatTheWidget.setOnClickListener {

            functionsClass.runUnlimitedWidgetService(adapterItems[position].appWidgetId, adapterItems[position].widgetLabel)
        }

        viewHolder.floatTheWidget.setOnLongClickListener { view ->
            val appWidgetProviderInfoLongClick = adapterItems[position].appWidgetProviderInfo

            functionsClassWidgets.popupOptionWidget(widgetConfigurationsActivity, widgetConfigurationsActivity, view,
                    adapterItems[position].packageName,
                    adapterItems[position].classNameProviderWidget,
                    adapterItems[position].appWidgetId,
                    adapterItems[position].widgetLabel,
                    if (appWidgetProviderInfoLongClick.loadPreviewImage(widgetConfigurationsActivity, DisplayMetrics.DENSITY_LOW) != null) {
                        appWidgetProviderInfoLongClick.loadPreviewImage(widgetConfigurationsActivity, DisplayMetrics.DENSITY_HIGH)
                    } else {
                        appWidgetProviderInfoLongClick.loadIcon(widgetConfigurationsActivity, DisplayMetrics.DENSITY_HIGH)
                    },
                    adapterItems[position].addedWidgetRecovery,
                    functionsClass)

            functionsClass.doVibrate(77)

            false
        }

        viewHolder.widgetLabel.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (textView.length() > 0) {

                    CoroutineScope(Dispatchers.IO).launch {

                        val widgetDataInterface = Room.databaseBuilder(widgetConfigurationsActivity, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                .fallbackToDestructiveMigration()
                                .addCallback(object : RoomDatabase.Callback() {
                                    override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                        super.onCreate(supportSQLiteDatabase)
                                    }

                                    override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                        super.onOpen(supportSQLiteDatabase)
                                    }
                                })
                                .build()
                        widgetDataInterface.initDataAccessObject().updateWidgetLabelByWidgetIdSuspend(adapterItems[position].appWidgetId, textView.text.toString().replace("\uD83D\uDD04", ""))
                        widgetDataInterface.close()

                        withContext(Dispatchers.Main) {
                            //Edit Folder Name
                            SearchEngine.clearSearchDataToForceReload()

                            widgetConfigurationsActivity.forceLoadConfiguredWidgets()
                        }
                    }
                }
            }
            true
        }
    }

    override fun getItemCount(): Int {

        return adapterItems.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var widgetItem: RelativeLayout = view.findViewById<RelativeLayout>(R.id.widgetItem) as RelativeLayout
        var widgetPreview: RelativeLayout = view.findViewById<RelativeLayout>(R.id.widgetPreview) as RelativeLayout
        var floatTheWidget: ImageView = view.findViewById<ImageView>(R.id.floatTheWidget) as ImageView
        var widgetLabel: TextInputEditText = view.findViewById<TextInputEditText>(R.id.widgetLabel) as TextInputEditText
    }
}