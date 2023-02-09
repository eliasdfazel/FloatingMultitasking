/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:57 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Folders.FoldersAdapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.Folders.FoldersApplicationsSelectionProcess.AppSelectionList
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import java.util.*

class FoldersListAdapter(private val instanceOfFoldersConfigurationsActivity: FoldersConfigurations,
                         private val context: Context,
                         private val adapterItems: ArrayList<AdapterItems>) : RecyclerView.Adapter<FoldersListAdapter.ViewHolder>() {

    var functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(context)
    var fileIO: FileIO = FileIO(context)
    var floatingServices: FloatingServices = FloatingServices(context)

    var loadCustomIcons: LoadCustomIcons? = null

    private var cardFolderDrawable: LayerDrawable? = null

    init {
        PublicVariable.floatingSizeNumber = functionsClassLegacy.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), context.resources.displayMetrics).toInt()

        cardFolderDrawable = context.getDrawable(R.drawable.card_folder_drawable) as LayerDrawable?
        val folderItemBackground = cardFolderDrawable?.findDrawableByLayerId(R.id.folder_item_background)
        folderItemBackground?.setTint(PublicVariable.colorLightDark)
        cardFolderDrawable?.alpha = 7

        if (functionsClassLegacy.customIconsEnable()) {
            loadCustomIcons = LoadCustomIcons(context, functionsClassLegacy.customIconPackageName())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false))
    }

    override fun onBindViewHolder(viewHolderBinder: ViewHolder, position: Int) {

        viewHolderBinder.categoryItem.background = cardFolderDrawable
        viewHolderBinder.categoryName.setTextColor(PublicVariable.colorLightDarkOpposite)
        viewHolderBinder.categoryName.setHintTextColor(functionsClassLegacy.setColorAlpha(PublicVariable.colorLightDarkOpposite, 175f))

        val folderName = adapterItems[position].category
        val includedPackagesInFolder = adapterItems[position].packageNames

        if (functionsClassLegacy.loadRecoveryIndicatorCategory(folderName)) {
            viewHolderBinder.categoryName.setText("${adapterItems[position].category} \uD83D\uDD04")
        } else {
            viewHolderBinder.categoryName.setText(adapterItems[position].category)
        }

        viewHolderBinder.runCategory.text = folderName[0].toString().toUpperCase(Locale.getDefault())

        if (folderName == context.packageName) {

            viewHolderBinder.runCategory.text = context.getString(R.string.index_item)
            viewHolderBinder.categoryName.setText("")

            viewHolderBinder.selectedApp.removeAllViews()

        } else {

            val autoFile = context.getFileStreamPath(folderName)

            if (autoFile.exists() && autoFile.isFile) {
                viewHolderBinder.selectedApp.removeAllViews()
                var previewItems = 7

                if (includedPackagesInFolder.size < 7) {
                    previewItems = includedPackagesInFolder.size
                }

                for (i in 0 until previewItems) {
                    val selectedAppsPreview = instanceOfFoldersConfigurationsActivity.layoutInflater.inflate(R.layout.selected_apps_item, null) as RelativeLayout

                    val imageView = functionsClassLegacy.initShapesImage(selectedAppsPreview, R.id.appSelectedItem)
                    imageView?.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) {
                        loadCustomIcons!!.getDrawableIconForPackage(includedPackagesInFolder[i], functionsClassLegacy.shapedAppIcon(includedPackagesInFolder[i]))
                    } else {
                        functionsClassLegacy.shapedAppIcon(includedPackagesInFolder[i])
                    })

                    viewHolderBinder.selectedApp.addView(selectedAppsPreview)

                }
            }
        }

        viewHolderBinder.runCategory.setOnClickListener {
            if (adapterItems[position].category != context.packageName) {
                floatingServices
                        .runUnlimitedFoldersService(adapterItems[position].category)
            }
        }

        viewHolderBinder.runCategory.setOnLongClickListener {
            if (adapterItems[position].category != context.packageName) {
                PublicVariable.itemPosition = position
                val categoryName = adapterItems[position].category
                functionsClassLegacy.popupOptionFolders(instanceOfFoldersConfigurationsActivity, context,
                        viewHolderBinder.itemView,
                        categoryName, position)
            }
            true
        }

        viewHolderBinder.addApp.setOnClickListener {

            PublicVariable.folderName = adapterItems[position].category

            context.startActivity(Intent(context, AppSelectionList::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

        }

        val rippleEffectFolderLogo = context.getDrawable(R.drawable.ripple_effect_folder_logo) as RippleDrawable?
        rippleEffectFolderLogo!!.setDrawableByLayerId(R.id.folder_logo_layer, functionsClassLegacy.shapesDrawablesCategory(viewHolderBinder.runCategory))
        rippleEffectFolderLogo.setDrawableByLayerId(android.R.id.mask, functionsClassLegacy.shapesDrawablesCategory(viewHolderBinder.runCategory))
        val categoryLogoLayer = rippleEffectFolderLogo.findDrawableByLayerId(R.id.folder_logo_layer) as Drawable
        val categoryMask = rippleEffectFolderLogo.findDrawableByLayerId(android.R.id.mask) as Drawable
        categoryLogoLayer.setTint(PublicVariable.primaryColorOpposite)
        categoryMask.setTint(PublicVariable.primaryColor)
        viewHolderBinder.runCategory.background = rippleEffectFolderLogo
    }

    override fun getItemCount(): Int {
        return adapterItems.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var categoryItem: RelativeLayout = view.findViewById<View>(R.id.categoryItem) as RelativeLayout
        var selectedApp: LinearLayout = view.findViewById<View>(R.id.selectedApps) as LinearLayout
        var categoryName: TextView = view.findViewById<View>(R.id.categoryName) as TextView
        var addApp: ImageView = view.findViewById<View>(R.id.addApps) as ImageView
        var runCategory: TextView = view.findViewById<View>(R.id.runCategory) as TextView
    }
}