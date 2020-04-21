/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/21/20 10:30 AM
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
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.Folders.AppSelectionList
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.imageview.customshapes.ShapesImage
import java.util.*

class FoldersListAdapter(var foldersConfigurations: FoldersConfigurations, var context: Context, var adapterItems: ArrayList<AdapterItems>) : RecyclerView.Adapter<FoldersListAdapter.ViewHolder>() {

    var functionsClass: FunctionsClass = FunctionsClass(context)

    var imageView: ShapesImage? = null
    var freqLayout: RelativeLayout? = null

    var endEdited = ""

    var loadCustomIcons: LoadCustomIcons? = null

    init {
        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39)
        PublicVariable.HW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size.toFloat(), context.resources.displayMetrics).toInt()

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons = LoadCustomIcons(context, functionsClass.customIconPackageName())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false))
    }

    override fun onBindViewHolder(viewHolderBinder: ViewHolder, position: Int) {

        val cardFolderDrawable = context.getDrawable(R.drawable.card_folder_drawable) as LayerDrawable?
        val folderItemBackground = cardFolderDrawable!!.findDrawableByLayerId(R.id.folder_item_background)
        folderItemBackground.setTint(PublicVariable.colorLightDark)
        cardFolderDrawable.alpha = 7
        viewHolderBinder.categoryItem.background = cardFolderDrawable
        viewHolderBinder.categoryName.setTextColor(PublicVariable.colorLightDarkOpposite)
        viewHolderBinder.categoryName.setHintTextColor(functionsClass.setColorAlpha(PublicVariable.colorLightDarkOpposite, 175f))

        val folderName = adapterItems[position].category
        val includedPackagesInFolder = adapterItems[position].packageNames

        if (functionsClass.loadRecoveryIndicatorCategory(folderName)) {
            viewHolderBinder.categoryName.setText(adapterItems[position].category + " " + "\uD83D\uDD04")
        } else {
            viewHolderBinder.categoryName.setText(adapterItems[position].category)
        }

        viewHolderBinder.runCategory.text = folderName[0].toString().toUpperCase()

        if (folderName == context.packageName) {

            viewHolderBinder.runCategory.text = context.getString(R.string.index_item)
            viewHolderBinder.categoryName.setText("")
            viewHolderBinder.addApp.visibility = View.INVISIBLE

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
                    freqLayout = foldersConfigurations.layoutInflater.inflate(R.layout.selected_apps_item, null) as RelativeLayout

                    imageView = functionsClass.initShapesImage(freqLayout, R.id.appSelectedItem)
                    imageView?.setImageDrawable(if (functionsClass.customIconsEnable()) {
                        loadCustomIcons!!.getDrawableIconForPackage(includedPackagesInFolder[i], functionsClass.shapedAppIcon(includedPackagesInFolder[i]))
                    } else {
                        functionsClass.shapedAppIcon(includedPackagesInFolder[i])
                    })

                    viewHolderBinder.selectedApp.addView(freqLayout)
                    viewHolderBinder.addApp.visibility = View.VISIBLE

                    val addAppsDrawable = context.getDrawable(R.drawable.ic_add_apps)
                    addAppsDrawable?.setTint(PublicVariable.primaryColorOpposite)
                    viewHolderBinder.addApp.setImageDrawable(addAppsDrawable)
                }
            }
        }
        viewHolderBinder.categoryName.clearFocus()
        viewHolderBinder.categoryName.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                folderNameProcess(textView.text.toString().replace(" \uD83D\uDD04", ""), position)
            }
            true
        }

        viewHolderBinder.categoryName.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                endEdited = editable.toString()

                if (viewHolderBinder.addApp.isShown) {
                    viewHolderBinder.addApp.visibility = View.INVISIBLE
                }
            }
        })

        viewHolderBinder.addApp.setOnClickListener {
            if (adapterItems[position].category != context.packageName) {
                folderNameProcess(adapterItems[position].category, position)
            } else {
                if (endEdited.length > 0) {
                    folderNameProcess(endEdited, position)
                }
            }
        }

        viewHolderBinder.selectedApp.setOnClickListener {
            if (adapterItems[position].category != context.packageName) {
                functionsClass
                        .runUnlimitedFolderService(adapterItems[position].category)
            }
        }

        viewHolderBinder.selectedApp.setOnLongClickListener {
            if (adapterItems[position].category != context.packageName) {
                PublicVariable.itemPosition = position
                val categoryName = adapterItems[position].category
                functionsClass.popupOptionFolders(foldersConfigurations, context,
                        viewHolderBinder.itemView,
                        categoryName, position)
            }
            true
        }

        viewHolderBinder.runCategory.setOnClickListener {
            if (adapterItems[position].category != context.packageName) {
                functionsClass
                        .runUnlimitedFolderService(adapterItems[position].category)
            }
        }

        viewHolderBinder.runCategory.setOnLongClickListener {
            if (adapterItems[position].category != context.packageName) {
                PublicVariable.itemPosition = position
                val categoryName = adapterItems[position].category
                functionsClass.popupOptionFolders(foldersConfigurations, context,
                        viewHolderBinder.itemView,
                        categoryName, position)
            }
            true
        }

        val rippleEffectFolderLogo = context.getDrawable(R.drawable.ripple_effect_folder_logo) as RippleDrawable?
        rippleEffectFolderLogo!!.setDrawableByLayerId(R.id.folder_logo_layer, functionsClass.shapesDrawablesCategory(viewHolderBinder.runCategory))
        rippleEffectFolderLogo.setDrawableByLayerId(android.R.id.mask, functionsClass.shapesDrawablesCategory(viewHolderBinder.runCategory))
        val categoryLogoLayer = rippleEffectFolderLogo.findDrawableByLayerId(R.id.folder_logo_layer) as Drawable
        val categoryMask = rippleEffectFolderLogo.findDrawableByLayerId(android.R.id.mask) as Drawable
        categoryLogoLayer.setTint(PublicVariable.primaryColorOpposite)
        categoryMask.setTint(PublicVariable.primaryColor)
        viewHolderBinder.runCategory.background = rippleEffectFolderLogo
    }

    override fun getItemCount(): Int {
        return adapterItems.size
    }

    private fun folderNameProcess(currentFolderName: String, position: Int) {
        PublicVariable.folderName = currentFolderName

        val file = context.getFileStreamPath(adapterItems[position].category)
        if (file.exists() && file.isFile) { //Edit Folder Name
            if (adapterItems[position].category == PublicVariable.folderName) {
                PublicVariable.folderName = adapterItems[position].category
                context.startActivity(Intent(context, AppSelectionList::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else { //Edit Folder Name
                SearchEngine.clearSearchDataToForceReload()

                val appsContent = functionsClass.readFileLine(adapterItems[position].category)

                if (PublicVariable.folderName.isEmpty()) {
                    PublicVariable.folderName = PublicVariable.folderName + "_" + System.currentTimeMillis()
                }

                for (appContent in appsContent) {
                    context.deleteFile(appContent + adapterItems[position].category)
                    functionsClass.saveFileAppendLine(PublicVariable.folderName, appContent)
                    functionsClass.saveFile(appContent + PublicVariable.folderName, appContent)
                }
                if (functionsClass.loadRecoveryIndicatorCategory(adapterItems[position].category)) {
                    functionsClass.removeLine(".uCategory", adapterItems[position].category)
                    functionsClass.saveFileAppendLine(".uCategory", PublicVariable.folderName)
                }
                functionsClass.removeLine(".categoryInfo", adapterItems[position].category)
                functionsClass.saveFileAppendLine(".categoryInfo", PublicVariable.folderName)
                context.deleteFile(adapterItems[position].category)
                context.startActivity(Intent(context, AppSelectionList::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        } else {
            SearchEngine.clearSearchDataToForceReload()

            if (PublicVariable.folderName.isEmpty()) {
                PublicVariable.folderName = PublicVariable.folderName + "_" + System.currentTimeMillis()
            }

            functionsClass.saveFileAppendLine(".categoryInfo", PublicVariable.folderName)
            functionsClass.saveFileEmpty(PublicVariable.folderName)
            context.startActivity(Intent(context, AppSelectionList::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var categoryItem: RelativeLayout = view.findViewById<View>(R.id.categoryItem) as RelativeLayout
        var selectedApp: LinearLayout = view.findViewById<View>(R.id.selectedApps) as LinearLayout
        var categoryName: EditText = view.findViewById<View>(R.id.categoryName) as EditText
        var addApp: ImageView = view.findViewById<View>(R.id.addApps) as ImageView
        var runCategory: TextView = view.findViewById<View>(R.id.runCategory) as TextView
    }
}