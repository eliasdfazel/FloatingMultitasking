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
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.Folders.FoldersApplicationsSelectionProcess.AppSelectionList
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
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

    var endEdited = ""

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
                    val selectedAppsPreview = instanceOfFoldersConfigurationsActivity.layoutInflater.inflate(R.layout.selected_apps_item, null) as RelativeLayout

                    val imageView = functionsClassLegacy.initShapesImage(selectedAppsPreview, R.id.appSelectedItem)
                    imageView?.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) {
                        loadCustomIcons!!.getDrawableIconForPackage(includedPackagesInFolder[i], functionsClassLegacy.shapedAppIcon(includedPackagesInFolder[i]))
                    } else {
                        functionsClassLegacy.shapedAppIcon(includedPackagesInFolder[i])
                    })

                    viewHolderBinder.selectedApp.addView(selectedAppsPreview)
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
                floatingServices
                        .runUnlimitedFoldersService(adapterItems[position].category)
            }
        }

        viewHolderBinder.selectedApp.setOnLongClickListener {
            if (adapterItems[position].category != context.packageName) {
                PublicVariable.itemPosition = position
                val categoryName = adapterItems[position].category
                functionsClassLegacy.popupOptionFolders(instanceOfFoldersConfigurationsActivity, context,
                        viewHolderBinder.itemView,
                        categoryName, position)
            }
            true
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


                if (PublicVariable.folderName.isEmpty()) {
                    PublicVariable.folderName = PublicVariable.folderName + "_" + System.currentTimeMillis()
                }

                fileIO.readFileLinesAsArray(adapterItems[position].category)?.let {

                    for (appContent in it) {
                        context.deleteFile(appContent + adapterItems[position].category)
                        fileIO.saveFileAppendLine(PublicVariable.folderName, appContent)
                        fileIO.saveFile(appContent + PublicVariable.folderName, appContent)
                    }
                }

                if (functionsClassLegacy.loadRecoveryIndicatorCategory(adapterItems[position].category)) {
                    fileIO.removeLine(".uCategory", adapterItems[position].category)
                    fileIO.saveFileAppendLine(".uCategory", PublicVariable.folderName)
                }

                fileIO.removeLine(".categoryInfo", adapterItems[position].category)
                fileIO.saveFileAppendLine(".categoryInfo", PublicVariable.folderName)
                context.deleteFile(adapterItems[position].category)
                context.startActivity(Intent(context, AppSelectionList::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        } else {
            SearchEngine.clearSearchDataToForceReload()

            if (PublicVariable.folderName.isEmpty()) {
                PublicVariable.folderName = PublicVariable.folderName + "_" + System.currentTimeMillis()
            }

            fileIO.saveFileAppendLine(".categoryInfo", PublicVariable.folderName)
            fileIO.saveFileEmpty(PublicVariable.folderName)
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