/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 6:52 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.support.wearable.complications.ProviderUpdateRequester
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsApplications
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.RemoteTask.RecoveryComplication
import net.geekstools.imageview.customshapes.ShapesImage
import java.util.*

class ApplicationsViewAdapter(private val context: Context,
                              private val adapterItemsApplications: ArrayList<AdapterItemsApplications>) : RecyclerView.Adapter<ApplicationsViewAdapter.ViewHolder>() {

    private val functionsClass: FunctionsClass = FunctionsClass(context)

    var layoutInflaterView = 0

    init {
        when (functionsClass.shapesImageId()) {
            1 -> layoutInflaterView = R.layout.item_card_list_droplet
            2 -> layoutInflaterView = R.layout.item_card_list_circle
            3 -> layoutInflaterView = R.layout.item_card_list_square
            4 -> layoutInflaterView = R.layout.item_card_list_squircle
            5 -> layoutInflaterView = R.layout.item_card_list_cut_circle
            0 -> layoutInflaterView = R.layout.item_card_list_noshape
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(layoutInflaterView, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolderBinder: ViewHolder, position: Int) {

        viewHolderBinder.appName.setTextColor(PublicVariable.colorLightDarkOpposite)
        viewHolderBinder.appIcon.setImageDrawable(adapterItemsApplications[position].AppIcon)
        viewHolderBinder.appName.text = adapterItemsApplications[position].AppName

        if (functionsClass.loadRecoveryIndicator(adapterItemsApplications[position].PackageName)) {
            viewHolderBinder.recoveryIndicator.visibility = View.VISIBLE
        } else {
            viewHolderBinder.recoveryIndicator.visibility = View.INVISIBLE
        }

        viewHolderBinder.fullItemView.setOnClickListener {

            val packageName = adapterItemsApplications[position].PackageName
            val className = adapterItemsApplications[position].ClassName

            functionsClass.runUnlimitedShortcutsService(packageName, className)

            Handler().postDelayed({
                viewHolderBinder.recoveryIndicator.visibility = View.VISIBLE

                val requester = ProviderUpdateRequester(context, ComponentName(context, RecoveryComplication::class.java))
                requester.requestUpdate(functionsClass.readPreference("ComplicationProviderService", "ComplicationedId", 0))
            }, 300)
        }

        viewHolderBinder.fullItemView.setOnLongClickListener {

            val packageName = adapterItemsApplications[position].PackageName
            functionsClass.appsLaunchPad(packageName)

            true
        }

        viewHolderBinder.recoveryIndicator.setOnClickListener {
            functionsClass.removeLine(".uFile", adapterItemsApplications[position].PackageName)

            viewHolderBinder.recoveryIndicator.visibility = View.INVISIBLE

            functionsClass.updateRecoverShortcuts()

            val requester = ProviderUpdateRequester(context, ComponentName(context, RecoveryComplication::class.java))
            requester.requestUpdate(functionsClass.readPreference("ComplicationProviderService", "ComplicationedId", 0))
        }
    }

    override fun getItemCount(): Int {

        return adapterItemsApplications.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var fullItemView: RelativeLayout = view.findViewById<RelativeLayout>(R.id.fullItemView) as RelativeLayout
        var appIcon: ShapesImage = view.findViewById<ShapesImage>(R.id.appIconView) as ShapesImage
        var appName: TextView = view.findViewById<TextView>(R.id.appNameView) as TextView
        var recoveryIndicator: Button = view.findViewById<Button>(R.id.recoveryIndicator) as Button
    }
}