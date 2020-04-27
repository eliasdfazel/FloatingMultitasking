/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 11:11 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsPopupOptionsFloatingShortcuts
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.imageview.customshapes.ShapesImage
import java.util.*

class PopupShortcutsOptionAdapter(private val context: Context,
                                  private val adapterItems: ArrayList<AdapterItemsPopupOptionsFloatingShortcuts>,
                                  private val className: String, private val packageName: String,
                                  private val startId: Int) : BaseAdapter() {

    private val functionsClass: FunctionsClass = FunctionsClass(context)

    override fun getView(position: Int, initialConvertView: View?, parent: ViewGroup?): View? {
        var convertView = initialConvertView

        var viewHolder: ViewHolder? = null
        if (convertView == null) {

            val layoutInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.item_popup_category_noshape, null)

            viewHolder = ViewHolder()
            viewHolder.items = convertView.findViewById<View>(R.id.items) as RelativeLayout
            viewHolder.imgIcon = convertView.findViewById<View>(R.id.iconItem) as ShapesImage
            viewHolder.textAppName = convertView.findViewById<View>(R.id.itemAppName) as TextView

            convertView.tag = viewHolder

        } else {

            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.imgIcon!!.setImageDrawable(adapterItems[position].itemIcon)
        viewHolder.textAppName!!.text = adapterItems[position].itemTitle

        val itemsListColor = if (functionsClass.appThemeTransparent()) {
            functionsClass.setColorAlpha(PublicVariable.colorLightDark, 50f)
        } else {
            PublicVariable.colorLightDark
        }

        val drawPopupShortcut = context.getDrawable(R.drawable.popup_shortcut_whole) as LayerDrawable?
        val backPopupShortcut = drawPopupShortcut!!.findDrawableByLayerId(R.id.backgroundTemporary)
        backPopupShortcut.setTint(itemsListColor)

        viewHolder.items!!.background = drawPopupShortcut
        viewHolder.textAppName!!.setTextColor(PublicVariable.colorLightDarkOpposite)

        convertView?.setOnClickListener {

            if (adapterItems[position].itemTitle == context.getString(R.string.pin)) {

                context.sendBroadcast(Intent("Pin_App_$className").putExtra("startId", startId))

            } else if (adapterItems[position].itemTitle == context.getString(R.string.unpin)) {

                context.sendBroadcast(Intent("Unpin_App_$className").putExtra("startId", startId))

            } else if (adapterItems[position].itemTitle == context.getString(R.string.remove)) {

                context.sendBroadcast(Intent("Remove_App_$className").putExtra("startId", startId))

            }

            context.sendBroadcast(Intent("Hide_PopupListView_Shortcuts"))
        }

        return convertView
    }

    override fun getCount(): Int {

        return adapterItems.size
    }

    override fun getItem(position: Int): AdapterItemsPopupOptionsFloatingShortcuts? {

        return adapterItems[position]
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    internal class ViewHolder {
        var items: RelativeLayout? = null
        var imgIcon: ImageView? = null
        var textAppName: TextView? = null
    }
}