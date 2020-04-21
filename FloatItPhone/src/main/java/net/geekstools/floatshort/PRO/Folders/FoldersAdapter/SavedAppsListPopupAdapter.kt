/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/21/20 9:24 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FoldersAdapter

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import net.geekstools.floatshort.PRO.Folders.UI.AppsConfirmButton
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import java.util.*

class SavedAppsListPopupAdapter(private val context: Context, private val functionsClass: FunctionsClass,
                                private val selectedAppsListItem: ArrayList<AdapterItems>, private val splitNumber: Int,
                                private val appsConfirmButton: AppsConfirmButton,
                                private val confirmButtonProcessInterface: ConfirmButtonProcessInterface) : BaseAdapter() {

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
            savedAppsListPopupAdapterViewHolder.confirmItem?.visibility = View.INVISIBLE
        }

        val drawConfirm = context.getDrawable(R.drawable.ripple_effect_confirm) as LayerDrawable?
        val backConfirm = drawConfirm!!.findDrawableByLayerId(R.id.backgroundTemporary)
        backConfirm.setTint(PublicVariable.primaryColorOpposite)
        savedAppsListPopupAdapterViewHolder.confirmItem?.background = drawConfirm
        savedAppsListPopupAdapterViewHolder.textAppName?.setTextColor(context.getColor(R.color.light))

        savedAppsListPopupAdapterViewHolder.imgIcon?.setImageDrawable(selectedAppsListItem[position].appIcon)
        savedAppsListPopupAdapterViewHolder.textAppName?.text = selectedAppsListItem[position].appName


        savedAppsListPopupAdapterViewHolder.deleteItem?.setOnClickListener {

            context.deleteFile(selectedAppsListItem[position].packageName
                    + PublicVariable.categoryName)

            functionsClass.removeLine(PublicVariable.categoryName,
                    selectedAppsListItem[position].packageName)

            confirmButtonProcessInterface.shortcutDeleted()

            confirmButtonProcessInterface.savedShortcutCounter()
        }

        savedAppsListPopupAdapterViewHolder.confirmItem?.setOnClickListener {

            if (splitNumber == 1) {

                functionsClass
                        .saveFile(PublicVariable.categoryName.toString()
                                + ".SplitOne", selectedAppsListItem[position].packageName)
            } else if (splitNumber == 2) {

                functionsClass
                        .saveFile(PublicVariable.categoryName.toString()
                                + ".SplitTwo", selectedAppsListItem[position].packageName)
            }

            confirmButtonProcessInterface.showSplitShortcutPicker()

            appsConfirmButton.makeItVisible()
        }


        val rippleDrawableBack = context.getDrawable(R.drawable.saved_app_shortcut_whole) as RippleDrawable?
        val gradientDrawableBackground = rippleDrawableBack?.findDrawableByLayerId(R.id.backgroundTemporary)
        val gradientDrawableBackMask = rippleDrawableBack?.findDrawableByLayerId(android.R.id.mask)

        rippleDrawableBack?.setColor(ColorStateList.valueOf(PublicVariable.colorLightDark))
        gradientDrawableBackground?.setTint(PublicVariable.primaryColorOpposite)
        gradientDrawableBackMask?.setTint(PublicVariable.colorLightDark)

        savedAppsListPopupAdapterViewHolder.items?.background = rippleDrawableBack

        return convertView
    }
}