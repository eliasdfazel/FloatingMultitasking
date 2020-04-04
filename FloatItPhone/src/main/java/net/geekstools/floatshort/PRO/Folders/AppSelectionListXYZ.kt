package net.geekstools.floatshort.PRO.Folders

import android.content.pm.ApplicationInfo
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.AppSavedListAdapter
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.AppSelectionListAdapter
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.AdvanceAppSelectionListBinding
import java.util.*

class AppSelectionListXYZ : AppCompatActivity(), View.OnClickListener {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val listPopupWindow: ListPopupWindow by lazy {
        ListPopupWindow(applicationContext)
    }


    lateinit var recyclerViewLayoutManager: LinearLayoutManager
    val applicationInfoList: ArrayList<ApplicationInfo> = ArrayList<ApplicationInfo>()
    lateinit var appSelectionListAdapter: RecyclerView.Adapter<AppSelectionListAdapter.ViewHolder>
    val adapterItems: ArrayList<AdapterItems> = ArrayList<AdapterItems>()

    val mapIndexFirstItem: LinkedHashMap<String, Int> = LinkedHashMap<String, Int>()
    val mapIndexLastItem: LinkedHashMap<String, Int> = LinkedHashMap<String, Int>()
    val mapRangeIndex: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>()

    val selectedAppsListItem: ArrayList<AdapterItems> = ArrayList<AdapterItems>()
    lateinit var advanceSavedListAdapter: AppSavedListAdapter

    var resetAdapter = false

    private val loadCustomIcons: LoadCustomIcons by lazy {
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
            functionsClass.setThemeColorFloating(this@AppSelectionListXYZ, advanceAppSelectionListBinding.root, true)
        } else {
            functionsClass.setThemeColorFloating(this@AppSelectionListXYZ, advanceAppSelectionListBinding.root, false)
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
    }

    override fun onClick(view: View?) {

    }

    fun loadInstalledAppsData() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    }

    fun LoadApplicationsIndex() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {

    }

    fun setupFastScrollingIndexing() {

    }
}