package net.geekstools.floatshort.PRO.Folders

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.Folders.Extensions.loadInstalledAppsData
import net.geekstools.floatshort.PRO.Folders.Extensions.setupConfirmButtonUI
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.AppSavedListAdapter
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.AppSelectionListAdapter
import net.geekstools.floatshort.PRO.Folders.UI.AppsConfirmButton
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonViewInterface
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassSecurity
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.AdvanceAppSelectionListBinding
import java.util.*

class AppSelectionList : AppCompatActivity(), View.OnClickListener,
        ConfirmButtonProcessInterface, ConfirmButtonViewInterface {

    val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    val functionsClassSecurity: FunctionsClassSecurity by lazy {
        FunctionsClassSecurity(applicationContext)
    }

    private val listPopupWindow: ListPopupWindow by lazy {
        ListPopupWindow(applicationContext)
    }


    lateinit var recyclerViewLayoutManager: LinearLayoutManager
    var applicationInfoList: ArrayList<ApplicationInfo> = ArrayList<ApplicationInfo>()
    lateinit var appSelectionListAdapter: RecyclerView.Adapter<AppSelectionListAdapter.ViewHolder>
    val adapterItems: ArrayList<AdapterItems> = ArrayList<AdapterItems>()

    val mapIndexFirstItem: LinkedHashMap<String, Int> = LinkedHashMap<String, Int>()
    val mapIndexLastItem: LinkedHashMap<String, Int> = LinkedHashMap<String, Int>()
    val mapRangeIndex: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>()

    private val selectedAppsListItem: ArrayList<AdapterItems> = ArrayList<AdapterItems>()
    lateinit var advanceSavedListAdapter: AppSavedListAdapter

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

        setupConfirmButtonUI(this@AppSelectionList)

        loadInstalledAppsData()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        functionsClass.navigateToClass(FoldersConfigurations::class.java, this@AppSelectionList)
    }

    override fun onClick(view: View?) {

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
            advanceSavedListAdapter = AppSavedListAdapter(applicationContext, functionsClass,
                    selectedAppsListItem, 1,
                    this@AppSelectionList)

            listPopupWindow.setAdapter(advanceSavedListAdapter)
            listPopupWindow.setBackgroundDrawable(null)
            listPopupWindow.anchorView = advanceAppSelectionListBinding.popupAnchorView
            listPopupWindow.width = ListPopupWindow.WRAP_CONTENT
            listPopupWindow.height = ListPopupWindow.WRAP_CONTENT
            listPopupWindow.isModal = true
            listPopupWindow.setOnDismissListener {

                sendBroadcast(Intent(getString(R.string.visibilityActionAdvance)))
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
        sendBroadcast(Intent(getString(R.string.visibilityActionAdvance)))
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

    /*ConfirmButtonViewInterface*/
    override fun makeItVisible(appsConfirmButton: AppsConfirmButton) {
        val drawShow = applicationContext.getDrawable(R.drawable.draw_saved_show) as LayerDrawable
        val backgroundTemporary  = drawShow.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary.setTint(PublicVariable.primaryColorOpposite)
        appsConfirmButton.background = drawShow

        if (!appsConfirmButton.isShown) {
            appsConfirmButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
            appsConfirmButton.visibility = View.VISIBLE
        }
    }

    override fun startCustomAnimation(appsConfirmButton: AppsConfirmButton, animation: Animation?) {
        if (animation == null) {

            appsConfirmButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_confirm_button))

        } else {

            appsConfirmButton.startAnimation(animation)

        }
    }

    override fun setDismissBackground(appsConfirmButton: AppsConfirmButton) {
        val drawDismiss = applicationContext.getDrawable(R.drawable.draw_saved_dismiss) as LayerDrawable
        val backgroundTemporary: Drawable = drawDismiss.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary.setTint(PublicVariable.primaryColor)

        appsConfirmButton.background = drawDismiss
    }
    /*ConfirmButtonViewInterface*/
}