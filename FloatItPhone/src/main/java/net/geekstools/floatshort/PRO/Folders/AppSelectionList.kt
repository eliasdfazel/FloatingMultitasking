/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/21/20 6:18 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders

import android.content.pm.ApplicationInfo
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.Folders.Extensions.loadInstalledAppsData
import net.geekstools.floatshort.PRO.Folders.Extensions.setupConfirmButtonUI
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.AppSelectionListAdapter
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.SavedAppsListPopupAdapter
import net.geekstools.floatshort.PRO.Folders.UI.AppsConfirmButton
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.AdvanceAppSelectionListBinding
import java.util.*

class AppSelectionList : AppCompatActivity(),
        ConfirmButtonProcessInterface {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
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
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    lateinit var advanceAppSelectionListBinding: AdvanceAppSelectionListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        advanceAppSelectionListBinding = AdvanceAppSelectionListBinding.inflate(layoutInflater)
        setContentView(advanceAppSelectionListBinding.root)

        advanceAppSelectionListBinding.temporaryFallingIcon.bringToFront()
        advanceAppSelectionListBinding.confirmLayout.bringToFront()

        functionsClass.initShapesImage(advanceAppSelectionListBinding.firstSplitIcon)
        functionsClass.initShapesImage(advanceAppSelectionListBinding.secondSplitIcon)

        if (functionsClass.appThemeTransparent()) {
            functionsClass.setThemeColorFloating(this@AppSelectionList, advanceAppSelectionListBinding.root, true)
        } else {
            functionsClass.setThemeColorFloating(this@AppSelectionList, advanceAppSelectionListBinding.root, false)
        }

        recyclerViewLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        advanceAppSelectionListBinding.recyclerListView.layoutManager = recyclerViewLayoutManager

        val typeface = Typeface.createFromAsset(assets, "upcil.ttf")
        advanceAppSelectionListBinding.loadingDescription.typeface = typeface
        advanceAppSelectionListBinding.loadingDescription.setTextColor(PublicVariable.colorLightDarkOpposite)
        advanceAppSelectionListBinding.loadingDescription.text = PublicVariable.categoryName
        advanceAppSelectionListBinding.appSelectedCounterView.typeface = typeface
        advanceAppSelectionListBinding.appSelectedCounterView.bringToFront()

        if (functionsClass.loadRecoveryIndicatorCategory(PublicVariable.categoryName)) {
            advanceAppSelectionListBinding.folderNameView.text = "${PublicVariable.categoryName} \uD83D\uDD04"
        } else {
            advanceAppSelectionListBinding.folderNameView.text = PublicVariable.categoryName
        }

        advanceAppSelectionListBinding.folderNameView.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        advanceAppSelectionListBinding.folderNameView.rippleColor = ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        val layerDrawableLoadLogo = getDrawable(R.drawable.ic_launcher_layer) as LayerDrawable?
        val gradientDrawableLoadLogo = layerDrawableLoadLogo!!.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable
        gradientDrawableLoadLogo.setTint(PublicVariable.primaryColor)
        advanceAppSelectionListBinding.loadingLogo.setImageDrawable(layerDrawableLoadLogo)

        if (PublicVariable.themeLightDark) {
            advanceAppSelectionListBinding.loadingProgress.indeterminateDrawable.colorFilter = PorterDuffColorFilter(PublicVariable.darkMutedColor, PorterDuff.Mode.MULTIPLY)
        } else if (!PublicVariable.themeLightDark) {
            advanceAppSelectionListBinding.loadingProgress.indeterminateDrawable.colorFilter = PorterDuffColorFilter(PublicVariable.vibrantColor, PorterDuff.Mode.MULTIPLY)
        }

        appsConfirmButton = setupConfirmButtonUI(this@AppSelectionList)

        loadInstalledAppsData()
    }

    override fun onStart() {
        super.onStart()

        if (functionsClass.returnAPI() < 24) {

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

            if (getFileStreamPath(PublicVariable.categoryName.toString() + ".SplitOne").exists()) {

                advanceAppSelectionListBinding.firstSplitIcon.setImageDrawable(if (functionsClass.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitOne"), functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitOne"))) else functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitOne")))

            } else {

                val addOne = getDrawable(R.drawable.add_quick_app)
                addOne!!.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175f))
                advanceAppSelectionListBinding.firstSplitIcon.setImageDrawable(addOne)

            }

            if (getFileStreamPath(PublicVariable.categoryName.toString() + ".SplitTwo").exists()) {

                advanceAppSelectionListBinding.secondSplitIcon.setImageDrawable(if (functionsClass.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitTwo"), functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitOne"))) else functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitTwo")))

            } else {

                val addTwo = getDrawable(R.drawable.add_quick_app)
                addTwo!!.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175f))
                advanceAppSelectionListBinding.secondSplitIcon.setImageDrawable(addTwo)

            }

            advanceAppSelectionListBinding.firstSplitIcon.setOnClickListener {
                if (getFileStreamPath(PublicVariable.categoryName).exists() && functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                    selectedAppsListItem.clear()

                    val savedLine = functionsClass.readFileLine(PublicVariable.categoryName)
                    for (aSavedLine in savedLine) {
                        selectedAppsListItem.add(AdapterItems(
                                functionsClass.appName(aSavedLine),
                                aSavedLine,
                                if (functionsClass.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClass.shapedAppIcon(aSavedLine)) else functionsClass.shapedAppIcon(aSavedLine)))
                    }
                    val savedAppsListPopupAdapter = SavedAppsListPopupAdapter(applicationContext, functionsClass,
                            selectedAppsListItem, 1,
                            appsConfirmButton, this@AppSelectionList)
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
                if (getFileStreamPath(PublicVariable.categoryName).exists() && functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                    selectedAppsListItem.clear()

                    val savedLine = functionsClass.readFileLine(PublicVariable.categoryName)
                    for (aSavedLine in savedLine) {
                        selectedAppsListItem.add(AdapterItems(
                                functionsClass.appName(aSavedLine),
                                aSavedLine,
                                if (functionsClass.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClass.shapedAppIcon(aSavedLine)) else functionsClass.shapedAppIcon(aSavedLine)))
                    }
                    val savedAppsListPopupAdapter = SavedAppsListPopupAdapter(applicationContext, functionsClass,
                            selectedAppsListItem, 2,
                            appsConfirmButton, this@AppSelectionList)
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

        functionsClass.navigateToClass(FoldersConfigurations::class.java, this@AppSelectionList)
    }

    /*ConfirmButtonProcess*/
    override fun savedShortcutCounter() {

        advanceAppSelectionListBinding.appSelectedCounterView.text = functionsClass.countLineInnerFile(PublicVariable.categoryName).toString()
    }

    override fun showSavedShortcutList() {

        if (getFileStreamPath(PublicVariable.categoryName).exists()
                && functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {

            selectedAppsListItem.clear()

            val savedLine = functionsClass.readFileLine(PublicVariable.categoryName)
            for (aSavedLine in savedLine) {
                selectedAppsListItem.add(AdapterItems(
                        functionsClass.appName(aSavedLine),
                        aSavedLine,
                        if (functionsClass.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClass.shapedAppIcon(aSavedLine)) else functionsClass.shapedAppIcon(aSavedLine)))
            }
            val savedAppsListPopupAdapter = SavedAppsListPopupAdapter(applicationContext, functionsClass,
                    selectedAppsListItem, 1,
                    appsConfirmButton,
                    this@AppSelectionList)

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


        if (getFileStreamPath(PublicVariable.categoryName.toString() + ".SplitOne").exists()) {
            advanceAppSelectionListBinding.firstSplitIcon.setImageDrawable(if (functionsClass.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitOne"), functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitOne"))) else functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitOne")))
        }
        if (getFileStreamPath(PublicVariable.categoryName.toString() + ".SplitTwo").exists()) {
            advanceAppSelectionListBinding.secondSplitIcon.setImageDrawable(if (functionsClass.customIconsEnable()) loadCustomIcons.getDrawableIconForPackage(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitTwo"), functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitOne"))) else functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName.toString() + ".SplitTwo")))
        }
    }
    /*ConfirmButtonProcess*/
}