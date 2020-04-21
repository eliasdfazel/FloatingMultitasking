/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/21/20 6:18 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FoldersAdapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import net.geekstools.floatshort.PRO.Folders.UI.AppsConfirmButton
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import java.util.*

class SavedAppsListPopupAdapterXYZ(val context: Context, val functionsClass: FunctionsClass,
                                   val selectedAppsListItem: ArrayList<AdapterItems>, val splitNumber: Int,
                                   val appsConfirmButton: AppsConfirmButton,
                                   val confirmButtonProcessInterface: ConfirmButtonProcessInterface) : BaseAdapter() {

    internal class ViewHolder {
        var items: RelativeLayout? = null
        var imgIcon: ImageView? = null
        var textAppName: TextView? = null
        var deleteItem: Button? = null
        var confirmItem: Button? = null
    }

    private var layoutInflaterLayout = 0

    init {
        when (functionsClass.shapesImageId()) {
            1 -> layoutInflaterLayout = R.layout.item_saved_app_droplet
            2 -> layoutInflaterLayout = R.layout.item_saved_app_circle
            3 -> layoutInflaterLayout = R.layout.item_saved_app_square
            4 -> layoutInflaterLayout = R.layout.item_saved_app_squircle
            0 -> layoutInflaterLayout = R.layout.item_saved_app_noshape
        }
    }

    override fun getCount(): Int {

        return selectedAppsListItem.size
    }

    override fun getItem(position: Int): AdapterItems {

        return selectedAppsListItem[position]
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getView(position: Int, initialConvertView: View?, parent: ViewGroup?): View? {

        var convertView = initialConvertView
        var savedAppsListPopupAdapterViewHolder: ViewHolder? = null

        if (convertView == null) {
            val layoutInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(layoutInflaterLayout, null)

            savedAppsListPopupAdapterViewHolder = ViewHolder()

            savedAppsListPopupAdapterViewHolder.items = convertView.findViewById<View>(R.id.items) as RelativeLayout
            savedAppsListPopupAdapterViewHolder.imgIcon = convertView.findViewById<View>(R.id.iconViewItem) as ImageView
            savedAppsListPopupAdapterViewHolder.textAppName = convertView.findViewById<View>(R.id.titleViewItem) as TextView
            savedAppsListPopupAdapterViewHolder.deleteItem = convertView.findViewById<View>(R.id.deleteItem) as Button
            savedAppsListPopupAdapterViewHolder.confirmItem = convertView.findViewById<View>(R.id.confirmItem) as Button

            convertView.tag = savedAppsListPopupAdapterViewHolder

        } else {

            savedAppsListPopupAdapterViewHolder = convertView.tag as ViewHolder

        }

        if (functionsClass.returnAPI() < 24) {
            savedAppsListPopupAdapterViewHolder.confirmItem.visibility = View.INVISIBLE
        }

        return convertView
    }
}