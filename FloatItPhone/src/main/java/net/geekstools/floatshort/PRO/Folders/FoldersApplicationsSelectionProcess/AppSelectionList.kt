/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FoldersApplicationsSelectionProcess

import android.content.pm.ApplicationInfo
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.Folders.Extensions.loadInstalledAppsData
import net.geekstools.floatshort.PRO.Folders.Extensions.setupConfirmButtonUI
import net.geekstools.floatshort.PRO.Folders.FoldersApplicationsSelectionProcess.Adapter.AppSelectionListAdapter
import net.geekstools.floatshort.PRO.Folders.FoldersApplicationsSelectionProcess.Adapter.SavedAppsListPopupAdapter
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.Folders.UI.AppsConfirmButton
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.ApplicationThemeController
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.AdvanceAppSelectionListBinding

class AppSelectionList : AppCompatActivity(),
        ConfirmButtonProcessInterface {

    val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    val fileIO: FileIO by lazy {
        FileIO(applicationContext)
    }

    private val applicationThemeController: ApplicationThemeController by lazy {
        ApplicationThemeController(applicationContext)
    }

    private val listPopupWindow: ListPopupWindow by lazy {
        ListPopupWindow(applicationContext)
    }

    private lateinit var recyclerViewLayoutManager: LinearLayoutManager
    var applicationInfoList: ArrayList<ApplicationInfo> = ArrayList<ApplicationInfo>()
    lateinit var appSelectionListAdapter: RecyclerView.Adapter<AppSelectionListAdapter.ViewHolder>
    val installedAppsListItem: ArrayList<AdapterItems> = ArrayList<AdapterItems>()

    private val selectedAppsListItem: ArrayList<AdapterItems> = ArrayList<AdapterItems>()

    var appsConfirmButton: AppsConfirmButton? = null

    var resetAdapter = false

    val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClassLegacy.customIconPackageName())
    }

    lateinit var advanceAppSelectionListBinding: AdvanceAppSelectionListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        advanceAppSelectionListBinding = AdvanceAppSelectionListBinding.inflate(layoutInflater)
        setContentView(advanceAppSelectionListBinding.root)

        advanceAppSelectionListBinding.temporaryFallingIcon.bringToFront()
        advanceAppSelectionListBinding.confirmLayout.bringToFront()

        functionsClassLegacy.initShapesImage(advanceAppSelectionListBinding.firstSplitIcon)
        functionsClassLegacy.initShapesImage(advanceAppSelectionListBinding.secondSplitIcon)

        applicationThemeController.setThemeColorFloating(this@AppSelectionList, advanceAppSelectionListBinding.root, functionsClassLegacy.appThemeTransparent())

        recyclerViewLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        advanceAppSelectionListBinding.recyclerListView.layoutManager = recyclerViewLayoutManager

        val typeface = Typeface.createFromAsset(assets, "ubuntu.ttf")
        advanceAppSelectionListBinding.loadingDescription.typeface = typeface
        advanceAppSelectionListBinding.loadingDescription.setTextColor(PublicVariable.colorLightDarkOpposite)
        advanceAppSelectionListBinding.loadingDescription.text = PublicVariable.folderName

        advanceAppSelectionListBinding.appSelectedCounterView.typeface = typeface
        advanceAppSelectionListBinding.appSelectedCounterView.bringToFront()

        if (PublicVariable.folderName == "FloatingFolder") {

            advanceAppSelectionListBinding.folderNameView.requestFocus()

            advanceAppSelectionListBinding.folderNameView.setText("")

        } else {

            if (functionsClassLegacy.loadRecoveryIndicatorCategory(PublicVariable.folderName)) {
                advanceAppSelectionListBinding.folderNameView.setText("\uD83D\uDD04 ${PublicVariable.folderName}")
            } else {
                advanceAppSelectionListBinding.folderNameView.setText(PublicVariable.folderName)
            }

        }

        advanceAppSelectionListBinding.folderNameBackground.setBackgroundColor(if (functionsClassLegacy.appThemeTransparent()) functionsClassLegacy.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)

        advanceAppSelectionListBinding.folderNameView.setTextColor(PublicVariable.colorLightDarkOpposite)
        advanceAppSelectionListBinding.folderNameView.setHintTextColor(PublicVariable.colorLightDarkOpposite)

        val layerDrawableLoadLogo = getDrawable(R.drawable.ic_launcher_layer) as LayerDrawable?
        val gradientDrawableLoadLogo = layerDrawableLoadLogo?.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable?
        gradientDrawableLoadLogo?.setTint(PublicVariable.primaryColor)
        advanceAppSelectionListBinding.loadingLogo.setImageDrawable(layerDrawableLoadLogo)

        if (PublicVariable.themeLightDark) {
            advanceAppSelectionListBinding.loadingProgress.indeterminateDrawable.colorFilter = PorterDuffColorFilter(PublicVariable.darkMutedColor, PorterDuff.Mode.MULTIPLY)
        } else if (!PublicVariable.themeLightDark) {
            advanceAppSelectionListBinding.loadingProgress.indeterminateDrawable.colorFilter = PorterDuffColorFilter(PublicVariable.vibrantColor, PorterDuff.Mode.MULTIPLY)
        }

        appsConfirmButton = setupConfirmButtonUI(this@AppSelectionList)

        advanceAppSelectionListBinding.folderNameView.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                PublicVariable.folderName = textView.text.toString()

            }
            true
        }

        advanceAppSelectionListBinding.folderNameView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                PublicVariable.folderName = s.toString()

            }

            override fun afterTextChanged(s: Editable?) {}

        })

        loadInstalledAppsData()
    }

    override fun onStart() {
        super.onStart()

        if (functionsClassLegacy.returnAPI() < 24) {

            advanceAppSelectionListBinding.firstSplitIcon.visibility = View.INVISIBLE
            advanceAppSelectionListBinding.secondSplitIcon.visibility = View.INVISIBLE
            advanceAppSelectionListBinding.splitHint.visibility = View.INVISIBLE

            val padTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
            advanceAppSelectionListBinding.recyclerListView.setPaddingRelative(
                    advanceAppSelectionListBinding.recyclerListView.paddingStart,
                    padTop,
                    advanceAppSelectionListBinding.recyclerListView.paddingEnd,
                    advanceAppSelectionListBinding.recyclerListView.paddingBottom)

            val layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            advanceAppSelectionListBinding.nestedScrollView.layoutParams = layoutParams

        } else {
            advanceAppSelectionListBinding.splitHint.setTextColor(PublicVariable.primaryColorOpposite)

            if (getFileStreamPath(PublicVariable.folderName.toString() + ".SplitOne").exists()) {

                advanceAppSelectionListBinding.firstSplitIcon.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitOne"), functionsClassLegacy.shapedAppIcon(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitOne"))) else functionsClassLegacy.shapedAppIcon(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitOne")))

            } else {

                val addOne = getDrawable(R.drawable.add_quick_app)
                addOne!!.setTint(functionsClassLegacy.setColorAlpha(PublicVariable.primaryColorOpposite, 175f))
                advanceAppSelectionListBinding.firstSplitIcon.setImageDrawable(addOne)

            }

            if (getFileStreamPath(PublicVariable.folderName.toString() + ".SplitTwo").exists()) {

                advanceAppSelectionListBinding.secondSplitIcon.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitTwo"), functionsClassLegacy.shapedAppIcon(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitOne"))) else functionsClassLegacy.shapedAppIcon(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitTwo")))

            } else {

                val addTwo = getDrawable(R.drawable.add_quick_app)
                addTwo!!.setTint(functionsClassLegacy.setColorAlpha(PublicVariable.primaryColorOpposite, 175f))
                advanceAppSelectionListBinding.secondSplitIcon.setImageDrawable(addTwo)

            }

            advanceAppSelectionListBinding.firstSplitIcon.setOnClickListener {
                if (getFileStreamPath(PublicVariable.folderName).exists() && fileIO.fileLinesCounter(PublicVariable.folderName) > 0) {
                    selectedAppsListItem.clear()

                    fileIO.readFileLinesAsArray(PublicVariable.folderName)?.let {

                        for (aSavedLine in it) {
                            selectedAppsListItem.add(AdapterItems(
                                    functionsClassLegacy.applicationName(aSavedLine),
                                    aSavedLine,
                                    if (functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClassLegacy.shapedAppIcon(aSavedLine)) else functionsClassLegacy.shapedAppIcon(aSavedLine)))
                        }
                    }

                    val savedAppsListPopupAdapter = appsConfirmButton?.let { it1 ->
                        SavedAppsListPopupAdapter(applicationContext, functionsClassLegacy, fileIO,
                                selectedAppsListItem, 1,
                                it1, this@AppSelectionList)
                    }
                    listPopupWindow.setAdapter(savedAppsListPopupAdapter)
                    listPopupWindow.anchorView = advanceAppSelectionListBinding.popupAnchorView
                    listPopupWindow.width = android.widget.ListPopupWindow.WRAP_CONTENT
                    listPopupWindow.height = android.widget.ListPopupWindow.WRAP_CONTENT
                    listPopupWindow.isModal = true
                    listPopupWindow.setBackgroundDrawable(null)
                    listPopupWindow.show()
                    listPopupWindow.setOnDismissListener {

                        appsConfirmButton?.makeItVisible()
                    }

                    appsConfirmButton?.setDismissBackground()
                }
            }

            advanceAppSelectionListBinding.secondSplitIcon.setOnClickListener {
                if (getFileStreamPath(PublicVariable.folderName).exists() && fileIO.fileLinesCounter(PublicVariable.folderName) > 0) {
                    selectedAppsListItem.clear()

                    fileIO.readFileLinesAsArray(PublicVariable.folderName)?.let {

                        for (aSavedLine in it) {
                            selectedAppsListItem.add(AdapterItems(
                                    functionsClassLegacy.applicationName(aSavedLine),
                                    aSavedLine,
                                    if (functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClassLegacy.shapedAppIcon(aSavedLine)) else functionsClassLegacy.shapedAppIcon(aSavedLine)))
                        }
                    }

                    val savedAppsListPopupAdapter = appsConfirmButton?.let { it1 ->
                        SavedAppsListPopupAdapter(applicationContext, functionsClassLegacy, fileIO,
                                selectedAppsListItem, 2,
                                it1, this@AppSelectionList)
                    }
                    listPopupWindow.setAdapter(savedAppsListPopupAdapter)
                    listPopupWindow.anchorView = advanceAppSelectionListBinding.popupAnchorView
                    listPopupWindow.width = android.widget.ListPopupWindow.WRAP_CONTENT
                    listPopupWindow.height = android.widget.ListPopupWindow.WRAP_CONTENT
                    listPopupWindow.isModal = true
                    listPopupWindow.setBackgroundDrawable(null)
                    listPopupWindow.show()
                    listPopupWindow.setOnDismissListener {

                        appsConfirmButton?.makeItVisible()
                    }

                    appsConfirmButton?.setDismissBackground()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        this@AppSelectionList.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        functionsClassLegacy.navigateToClass(FoldersConfigurations::class.java, this@AppSelectionList)

    }

    /*ConfirmButtonProcess*/
    override fun confirmed() {
        Log.d(this@AppSelectionList.javaClass.simpleName, "Confirmed -> ${PublicVariable.folderName}")

        folderNameProcess(PublicVariable.folderName, advanceAppSelectionListBinding.folderNameView.text.toString())

    }

    override fun savedShortcutCounter() {

        val folderCounter = fileIO.fileLinesCounter(PublicVariable.folderName)

        advanceAppSelectionListBinding.appSelectedCounterView.text = folderCounter.toString()

        if (folderCounter == 1) {

            folderNameProcess(PublicVariable.folderName, advanceAppSelectionListBinding.folderNameView.text.toString())

        }

    }

    override fun showSavedShortcutList() {

        if (getFileStreamPath(PublicVariable.folderName).exists()
                && fileIO.fileLinesCounter(PublicVariable.folderName) > 0) {

            selectedAppsListItem.clear()

            fileIO.readFileLinesAsArray(PublicVariable.folderName)?.let {

                for (aSavedLine in it) {
                    selectedAppsListItem.add(AdapterItems(
                            functionsClassLegacy.applicationName(aSavedLine),
                            aSavedLine,
                            if (functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClassLegacy.shapedAppIcon(aSavedLine)) else functionsClassLegacy.shapedAppIcon(aSavedLine)))
                }
            }

            val savedAppsListPopupAdapter = appsConfirmButton?.let {
                SavedAppsListPopupAdapter(applicationContext, functionsClassLegacy, fileIO,
                        selectedAppsListItem, 1,
                        it,
                        this@AppSelectionList)
            }

            listPopupWindow.setAdapter(savedAppsListPopupAdapter)
            listPopupWindow.setBackgroundDrawable(null)
            listPopupWindow.anchorView = advanceAppSelectionListBinding.popupAnchorView
            listPopupWindow.width = ListPopupWindow.WRAP_CONTENT
            listPopupWindow.height = ListPopupWindow.WRAP_CONTENT
            listPopupWindow.isModal = true
            listPopupWindow.setOnDismissListener {

                appsConfirmButton?.makeItVisible()
            }
            listPopupWindow.show()
        }
    }

    override fun hideSavedShortcutList() {

        listPopupWindow.dismiss()
    }

    override fun shortcutDeleted() {

        resetAdapter = true

        loadInstalledAppsData()

        listPopupWindow.dismiss()
    }

    override fun showSplitShortcutPicker() {
        listPopupWindow.dismiss()


        if (getFileStreamPath(PublicVariable.folderName.toString() + ".SplitOne").exists()) {
            advanceAppSelectionListBinding.firstSplitIcon.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitOne"), functionsClassLegacy.shapedAppIcon(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitOne"))) else functionsClassLegacy.shapedAppIcon(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitOne")))
        }
        if (getFileStreamPath(PublicVariable.folderName.toString() + ".SplitTwo").exists()) {
            advanceAppSelectionListBinding.secondSplitIcon.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitTwo"), functionsClassLegacy.shapedAppIcon(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitOne"))) else functionsClassLegacy.shapedAppIcon(fileIO.readFile(PublicVariable.folderName.toString() + ".SplitTwo")))
        }
    }
    /*ConfirmButtonProcess*/

    private fun folderNameProcess(currentFolderName: String, newFolderName: String) {

        if (getFileStreamPath(".categoryInfo").exists()) {

            val file = getFileStreamPath(newFolderName)

            if (file.exists() && file.isFile) { //Edit Folder Name

                if (newFolderName == currentFolderName) {

                    //

                } else { //Edit Folder Name
                    SearchEngine.clearSearchDataToForceReload()

                    fileIO.readFileLinesAsArray(newFolderName)?.let {

                        for (appContent in it) {
                            deleteFile(appContent + newFolderName)
                            fileIO.saveFileAppendLine(PublicVariable.folderName, appContent)
                            fileIO.saveFile(appContent + PublicVariable.folderName, appContent)
                        }

                    }

                    if (functionsClassLegacy.loadRecoveryIndicatorCategory(newFolderName)) {
                        fileIO.removeLine(".uCategory", newFolderName)
                        fileIO.saveFileAppendLine(".uCategory", PublicVariable.folderName)
                    }

                    fileIO.removeLine(".categoryInfo", newFolderName)
                    fileIO.saveFileAppendLine(".categoryInfo", PublicVariable.folderName)
                    deleteFile(newFolderName)
                }

            } else {

                SearchEngine.clearSearchDataToForceReload()

                fileIO.saveFileAppendLine(".categoryInfo", PublicVariable.folderName)

            }

        } else {

            SearchEngine.clearSearchDataToForceReload()

            fileIO.saveFileAppendLine(".categoryInfo", PublicVariable.folderName)

        }

    }

}