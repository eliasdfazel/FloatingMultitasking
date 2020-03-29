/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/28/20 5:49 PM
 * Last modified 3/28/20 5:08 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Widget.WidgetsAdapter

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations
import java.util.*

class InstalledWidgetsAdapter(private val widgetConfigurationsActivity: WidgetConfigurations, private val context: Context,
                              private val adapterItems: ArrayList<AdapterItems>, private val appWidgetHost: AppWidgetHost) : RecyclerView.Adapter<InstalledWidgetsAdapter.ViewHolder>() {

    private val functionsClass: FunctionsClass = FunctionsClass(context)

    companion object {
        const val WIDGET_CONFIGURATION_REQUEST = 666
        const val SYSTEM_WIDGET_PICKER = 333
        const val SYSTEM_WIDGET_PICKER_CONFIGURATION = 111

        lateinit var pickedWidgetPackageName: String
        lateinit var pickedWidgetClassNameProvider: String
        lateinit var pickedWidgetConfigClassName: String
        lateinit var pickedAppWidgetProviderInfo: AppWidgetProviderInfo

        var pickedWidgetId = 0
        lateinit var pickedWidgetLabel: String
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.widget_installed_items, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.widgetPreview.setImageDrawable(adapterItems[position].widgetPreview)
        viewHolder.widgetLabel.text = adapterItems[position].widgetLabel
        viewHolder.widgetLabel.setTextColor(if (PublicVariable.themeLightDark) context.getColor(R.color.dark) else context.getColor(R.color.light))

        viewHolder.widgetitem.setOnClickListener {

        }

        viewHolder.widgetitem.setOnLongClickListener {
            functionsClass.doVibrate(77)

            pickedWidgetPackageName = adapterItems[position].packageName
            pickedWidgetClassNameProvider = adapterItems[position].classNameProviderWidget
            pickedWidgetConfigClassName = adapterItems[position].configClassNameWidget
            pickedAppWidgetProviderInfo = adapterItems[position].appWidgetProviderInfo

            pickedWidgetId = appWidgetHost.allocateAppWidgetId()
            pickedWidgetLabel = adapterItems[position].widgetLabel
            val widgetProvider = ComponentName.createRelative(pickedWidgetPackageName, pickedWidgetClassNameProvider)

            if (pickedAppWidgetProviderInfo.configure != null) {
                val configure = ComponentName.createRelative(pickedWidgetPackageName, pickedWidgetConfigClassName)

                try {
                    if (context.packageManager.getActivityInfo(configure, PackageManager.GET_META_DATA).exported) {

                        val intentWidgetBind = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pickedWidgetId)
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, widgetProvider)
                        }
                        widgetConfigurationsActivity.startActivityForResult(intentWidgetBind, WIDGET_CONFIGURATION_REQUEST)

                        val intentWidgetConfiguration = Intent().apply {
                            action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
                            component = configure
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pickedWidgetId)
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,  /*pickedAppWidgetProviderInfo.*/widgetProvider)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        }
                        widgetConfigurationsActivity.startActivityForResult(intentWidgetConfiguration, WIDGET_CONFIGURATION_REQUEST)

                    } else {

                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            } else {

                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pickedWidgetId)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, widgetProvider)
                widgetConfigurationsActivity.startActivityForResult(intent, WIDGET_CONFIGURATION_REQUEST)
            }

            false
        }
    }

    override fun getItemCount(): Int {

        return adapterItems.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var widgetitem: RelativeLayout = view.findViewById<RelativeLayout>(R.id.widgetItem) as RelativeLayout
        var widgetPreview: ImageView = view.findViewById<ImageView>(R.id.widgetPreview) as ImageView
        var widgetLabel: TextView = view.findViewById<TextView>(R.id.widgetLabel) as TextView
    }
}