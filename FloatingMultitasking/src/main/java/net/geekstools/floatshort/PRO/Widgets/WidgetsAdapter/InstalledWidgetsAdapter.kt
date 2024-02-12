/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Widgets.WidgetsAdapter

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
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Widgets.WidgetConfigurations

class InstalledWidgetsAdapter(private val widgetConfigurationsActivity: WidgetConfigurations, private val context: Context,
                              private val adapterItems: ArrayList<AdapterItems>, private val appWidgetHost: AppWidgetHost) : RecyclerView.Adapter<InstalledWidgetsAdapter.ViewHolder>() {

    private val functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(context)

    companion object {
        const val WIDGET_CONFIGURATION_REQUEST = 666
        const val SYSTEM_WIDGET_PICKER = 333
        const val SYSTEM_WIDGET_PICKER_CONFIGURATION = 111

        var pickedWidgetPackageName: String? = null
        var pickedWidgetClassNameProvider: String? = null
        var pickedWidgetConfigClassName: String? = null
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
            functionsClassLegacy.doVibrate(77)

            InstalledWidgetsAdapter.pickedWidgetPackageName = adapterItems[position].packageName
            InstalledWidgetsAdapter.pickedWidgetClassNameProvider = adapterItems[position].classNameProviderWidget
            InstalledWidgetsAdapter.pickedWidgetConfigClassName = adapterItems[position].configClassNameWidget
            InstalledWidgetsAdapter.pickedAppWidgetProviderInfo = adapterItems[position].appWidgetProviderInfo

            InstalledWidgetsAdapter.pickedWidgetId = appWidgetHost.allocateAppWidgetId()
            InstalledWidgetsAdapter.pickedWidgetLabel = adapterItems[position].widgetLabel


            if (InstalledWidgetsAdapter.pickedWidgetPackageName != null &&
                    InstalledWidgetsAdapter.pickedWidgetClassNameProvider != null) {

                val widgetProvider = ComponentName.createRelative(InstalledWidgetsAdapter.pickedWidgetPackageName!!, InstalledWidgetsAdapter.pickedWidgetClassNameProvider!!)

                if (InstalledWidgetsAdapter.pickedAppWidgetProviderInfo.configure != null
                        && InstalledWidgetsAdapter.pickedWidgetConfigClassName != null) {


                    try {
                        val widgetConfigurationActivity = ComponentName.createRelative(InstalledWidgetsAdapter.pickedWidgetPackageName!!, InstalledWidgetsAdapter.pickedWidgetConfigClassName!!)

                        if (context.packageManager.getActivityInfo(widgetConfigurationActivity, PackageManager.GET_META_DATA).exported) {

                            val intentWidgetBind = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pickedWidgetId)
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, widgetProvider)
                            }
                            widgetConfigurationsActivity.startActivityForResult(intentWidgetBind, InstalledWidgetsAdapter.WIDGET_CONFIGURATION_REQUEST)

                            val intentWidgetConfiguration = Intent().apply {
                                action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
                                component = widgetConfigurationActivity
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pickedWidgetId)
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,  /*pickedAppWidgetProviderInfo.*/widgetProvider)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                            }
                            widgetConfigurationsActivity.startActivityForResult(intentWidgetConfiguration, InstalledWidgetsAdapter.WIDGET_CONFIGURATION_REQUEST)

                        } else {

                        }
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }

                } else {

                    val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pickedWidgetId)
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, widgetProvider)
                    widgetConfigurationsActivity.startActivityForResult(intent, InstalledWidgetsAdapter.WIDGET_CONFIGURATION_REQUEST)
                }
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