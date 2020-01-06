/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/6/20 8:58 AM
 * Last modified 1/6/20 8:43 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.*
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.hybrid_view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.geekstools.floatshort.PRO.Automation.Apps.AppAutoFeatures
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter.CardHybridAdapter
import net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter.HybridSectionedGridRecyclerViewAdapter
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug.Companion.PrintDebug
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Util.GeneralAdapters.AdapterItems
import net.geekstools.floatshort.PRO.Util.GeneralAdapters.RecycleViewSmoothLayoutGrid
import net.geekstools.floatshort.PRO.Util.IAP.InAppBilling
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingManager
import net.geekstools.floatshort.PRO.Util.Preferences.PreferencesActivity
import net.geekstools.floatshort.PRO.Util.RemoteTask.RecoveryFolders
import net.geekstools.floatshort.PRO.Util.RemoteTask.RecoveryShortcuts
import net.geekstools.floatshort.PRO.Util.RemoteTask.RecoveryWidgets
import net.geekstools.floatshort.PRO.Util.SearchEngine.SearchEngineAdapter
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.PinPassword.HandlePinPassword
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Util.UI.SimpleGestureFilterSwitch
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations
import kotlin.math.hypot

class ApplicationsView : Activity(), View.OnClickListener, OnLongClickListener, OnTouchListener, SimpleGestureFilterSwitch.SimpleGestureListener {

    private lateinit var functionsClass: FunctionsClass
    private lateinit var functionsClassSecurity: FunctionsClassSecurity

    private lateinit var applicationInfoList: List<ResolveInfo>

    private lateinit var mapIndexFirstItem: LinkedHashMap<String, Int>
    private lateinit var mapIndexLastItem: LinkedHashMap<String, Int>
    private lateinit var mapRangeIndex: LinkedHashMap<Int, String>
    private lateinit var indexItems: java.util.NavigableMap<String, Int>
    private lateinit var indexList: ArrayList<String?>

    private lateinit var applicationsAdapterItems: ArrayList<AdapterItems>
    private lateinit var sections: ArrayList<HybridSectionedGridRecyclerViewAdapter.Section>
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<CardHybridAdapter.ViewHolder>
    private lateinit var recyclerViewLayoutManager: GridLayoutManager

    private lateinit var searchAdapterItems: ArrayList<AdapterItems>

    private var installedPackageName: String? = null
    private var installedAppName: String? = null
    private var installedAppIcon: Drawable? = null

    var hybridItem: Int = 0
    var lastIntentItem: Int = 0

    private lateinit var freqApps: Array<String>
    lateinit var freqCounter: IntArray
    var loadFreq = false

    private lateinit var simpleGestureFilterSwitch: SimpleGestureFilterSwitch

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    private lateinit var loadCustomIcons: LoadCustomIcons

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hybrid_view)

        functionsClass = FunctionsClass(applicationContext, this@ApplicationsView)
        functionsClassSecurity = FunctionsClassSecurity(this@ApplicationsView, applicationContext)

        functionsClass.setThemeColorFloating(MainView, functionsClass.appThemeTransparent())
        functionsClass.ChangeLog(this@ApplicationsView, false)

        simpleGestureFilterSwitch = SimpleGestureFilterSwitch(applicationContext, this@ApplicationsView)

        recyclerViewLayoutManager = RecycleViewSmoothLayoutGrid(applicationContext, functionsClass.columnCount(105), OrientationHelper.VERTICAL, false)
        loadView.layoutManager = recyclerViewLayoutManager

        indexList = ArrayList<String?>()
        sections = ArrayList<HybridSectionedGridRecyclerViewAdapter.Section>()
        indexItems = java.util.TreeMap<String, Int>()

        applicationInfoList = ArrayList<ResolveInfo>()
        applicationsAdapterItems = ArrayList<AdapterItems>()

        mapIndexFirstItem = LinkedHashMap<String, Int>()
        mapIndexLastItem = LinkedHashMap<String, Int>()
        mapRangeIndex = LinkedHashMap<Int, String>()


        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
        }

        /*All Loading Process*/
        initiateLoadingProcessAll()
        /*All Loading Process*/

        val drawPreferenceAction: LayerDrawable = getDrawable(R.drawable.draw_pref_action) as LayerDrawable
        val backPreferenceAction: Drawable = drawPreferenceAction.findDrawableByLayerId(R.id.backtemp)
        backPreferenceAction.setTint(PublicVariable.primaryColorOpposite)
        actionButton.setImageDrawable(drawPreferenceAction)

        switchWidgets.setTextColor(getColor(R.color.light))
        switchCategories.setTextColor(getColor(R.color.light))
        if (PublicVariable.themeLightDark /*light*/ && functionsClass.appThemeTransparent() /*transparent*/) {
            switchWidgets.setTextColor(getColor(R.color.dark))
            switchCategories.setTextColor(getColor(R.color.dark))
        }

        switchCategories.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        switchCategories.rippleColor = ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        switchWidgets.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        switchWidgets.rippleColor = ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        recoveryAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        recoveryAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        automationAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        automationAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        try {
            val drawRecoverFloatingCategories = getDrawable(R.drawable.draw_recovery)!!.mutate() as LayerDrawable
            val backRecoverFloatingCategories = drawRecoverFloatingCategories.findDrawableByLayerId(R.id.backtemp).mutate()
            backRecoverFloatingCategories.setTint(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)

            val drawRecoverFloatingWidgets = getDrawable(R.drawable.draw_recovery_widgets)!!.mutate() as LayerDrawable
            val backRecoverFloatingWidgets = drawRecoverFloatingWidgets.findDrawableByLayerId(R.id.backtemp).mutate()
            backRecoverFloatingWidgets.setTint(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)

            recoverFloatingCategories.setImageDrawable(drawRecoverFloatingCategories)
            recoverFloatingWidgets.setImageDrawable(drawRecoverFloatingWidgets)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        actionButton.setOnClickListener {
            functionsClass.doVibrate(33)

            if (!PublicVariable.actionCenter) {

                val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(recoveryAction, actionButton.x.toInt(), actionButton.y.toInt(), finalRadius.toFloat(), functionsClass.DpToInteger(13).toFloat())
                circularReveal.duration = 777
                circularReveal.interpolator = AccelerateInterpolator()
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        recoveryAction.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }
                })

                functionsClass.openActionMenuOption(fullActionViews, actionButton, fullActionViews.isShown)
            } else {
                recoveryAction.visibility = View.VISIBLE

                val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(recoveryAction, actionButton.x.toInt(), actionButton.y.toInt(), functionsClass.DpToInteger(13).toFloat(), finalRadius.toFloat())
                circularReveal.duration = 1300
                circularReveal.interpolator = AccelerateInterpolator()
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        recoveryAction.visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }
                })
            }
        }

        switchCategories.setOnClickListener {
            try {
                functionsClass.navigateToClass(FoldersConfigurations::class.java,
                        ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        switchWidgets.setOnClickListener {
            try {
                if (functionsClass.networkConnection() && firebaseAuth.currentUser != null) {

                    if (functionsClass.floatingWidgetsPurchased() || functionsClass.appVersionName(packageName).contains("[BETA]")) {
                        try {
                            functionsClass.navigateToClass(WidgetConfigurations::class.java,
                                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        InAppBilling.ItemIAB = BillingManager.iapFloatingWidgets
                        startActivity(Intent(applicationContext, InAppBilling::class.java)
                                .putExtra("UserEmailAddress", functionsClass.readPreference(".UserInformation", "userEmail", null)),
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.down_up, android.R.anim.fade_out).toBundle())
                    }
                } else {
                    Toast.makeText(applicationContext, getString(R.string.internetError), Toast.LENGTH_LONG).show()

                    if (firebaseAuth.currentUser == null) {
                        Toast.makeText(applicationContext, getString(R.string.authError), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        automationAction.setOnClickListener {
            functionsClass.doVibrate(50)

            val intent = Intent(applicationContext, AppAutoFeatures::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.up_down, android.R.anim.fade_out).toBundle())
        }

        recoveryAction.setOnClickListener {
            Intent(applicationContext, RecoveryShortcuts::class.java).let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(it)
            }
        }
        recoverFloatingCategories.setOnClickListener {
            Intent(applicationContext, RecoveryFolders::class.java).let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(it)
            }

            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
            recoverFloatingCategories.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    recoverFloatingCategories.visibility = View.INVISIBLE
                }

                override fun onAnimationStart(animation: Animation?) {

                }

            })
        }
        recoverFloatingWidgets.setOnClickListener {
            Intent(applicationContext, RecoveryWidgets::class.java).let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(it)
            }

            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
            recoverFloatingWidgets.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    recoverFloatingWidgets.visibility = View.INVISIBLE
                }

                override fun onAnimationStart(animation: Animation?) {

                }

            })
        }

        actionButton.setOnLongClickListener {
            Handler().postDelayed({
                val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@ApplicationsView, actionButton, "transition")
                Intent().let {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    it.setClass(this@ApplicationsView, PreferencesActivity::class.java)
                    startActivity(it, activityOptionsCompat.toBundle())
                }
            }, 113)

            true
        }

        switchCategories.setOnLongClickListener {
            if (!recoverFloatingCategories.isShown) {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_show)
                recoverFloatingCategories.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        recoverFloatingCategories.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
                recoverFloatingCategories.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }
                    override fun onAnimationEnd(animation: Animation) {
                        recoverFloatingCategories.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }

            true
        }
        switchWidgets.setOnLongClickListener {
            if (!recoverFloatingWidgets.isShown) {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_show)
                recoverFloatingWidgets.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        recoverFloatingWidgets.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
                recoverFloatingWidgets.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        recoverFloatingWidgets.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }

            true
        }

        /*Search Engine*/
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        /*Search Engine*/

        firebaseAuth = FirebaseAuth.getInstance()

        if (BuildConfig.VERSION_NAME.contains("[BETA]") && !functionsClass.readPreference(".UserInformation", "SubscribeToBeta", false)) {
            FirebaseMessaging.getInstance().subscribeToTopic("BETA").addOnSuccessListener {
                functionsClass.savePreference(".UserInformation", "SubscribeToBeta", true)
            }.addOnFailureListener {

            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
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

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        return false
    }

    override fun onClick(view: View?) {

    }

    override fun onLongClick(view: View?): Boolean {

        return true
    }

    override fun onSwipe(direction: Int) {

    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }

    private fun initiateLoadingProcessAll() {
        if (functionsClass.appThemeTransparent()) {
            loadingSplash.setBackgroundColor(Color.TRANSPARENT)
        } else {
            loadingSplash.setBackgroundColor(window.navigationBarColor)
        }

        if (PublicVariable.themeLightDark) {
            loadingProgress.indeterminateDrawable.colorFilter = PorterDuffColorFilter(PublicVariable.themeTextColor, PorterDuff.Mode.MULTIPLY)
        } else if (!PublicVariable.themeLightDark) {
            loadingProgress.indeterminateDrawable.colorFilter = PorterDuffColorFilter(PublicVariable.themeColor, PorterDuff.Mode.MULTIPLY)
        }

        val layerDrawableLoadLogo: LayerDrawable = getDrawable(R.drawable.ic_launcher_layer) as LayerDrawable
        val gradientDrawableLoadLogo: BitmapDrawable = layerDrawableLoadLogo!!.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable
        gradientDrawableLoadLogo.setTint(PublicVariable.primaryColor)
        loadLogo.setImageDrawable(layerDrawableLoadLogo)

        loadApplicationsData()
    }

    private fun loadApplicationsData() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
        indexView.removeAllViews()

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons.load()
            PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons())
        }

        applicationInfoList = packageManager.queryIntentActivities(Intent().apply {
            this.action = Intent.ACTION_MAIN
            this.addCategory(Intent.CATEGORY_LAUNCHER)
        }, PackageManager.GET_RESOLVED_FILTER)
        val applicationInfoListSorted = applicationInfoList.sortedWith(ResolveInfo.DisplayNameComparator(packageManager))

        var itemOfIndex = 1
        var newChar: String? = null
        var oldChar: String? = null

        applicationInfoListSorted.asFlow()
                .onEach {

                }
                .filter {

                    (packageManager.getLaunchIntentForPackage(it.activityInfo.packageName) != null)
                }
                .map {

                    it
                }
                .onCompletion {
                    val splashAnimation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
                    loadingSplash.startAnimation(splashAnimation)
                    splashAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                            if (loadFreq == true) {
                                /*
                                 * Load Frequently Used Applications
                                 */
                            }
                            switchFloating.visibility = View.VISIBLE

                            /*
                             * Load Rest Of Applications
                             */
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            loadingSplash.visibility = View.INVISIBLE
                        }
                    })
                }
                .withIndex().collect {
                    try {
                        installedPackageName = it.value.activityInfo.packageName
                        installedAppName = functionsClass.activityLabel(it.value.activityInfo)

                        newChar = installedAppName!!.substring(0, 1).toUpperCase(java.util.Locale.getDefault())

                        if (it.index == 0) {
                            sections.add(HybridSectionedGridRecyclerViewAdapter.Section(hybridItem, newChar))
                        } else {

                            if (oldChar != newChar) {
                                sections.add(HybridSectionedGridRecyclerViewAdapter.Section(hybridItem, newChar))

                                indexList.add(newChar)
                                itemOfIndex = 1
                            }
                        }

                        installedAppIcon = if (functionsClass.loadCustomIcons()) {
                            loadCustomIcons.getDrawableIconForPackage(installedPackageName, functionsClass.shapedAppIcon(installedPackageName))
                        } else {
                            functionsClass.shapedAppIcon(installedPackageName)
                        }

                        applicationsAdapterItems.add(AdapterItems(installedAppName, installedPackageName, installedAppIcon, SearchEngineAdapter.SearchResultType.SearchShortcuts))

                    } catch (e: Exception) {
                        e.printStackTrace()
                        this.cancel()
                    } finally {
                        indexList.add(newChar)
                        indexItems[newChar] = itemOfIndex++

                        hybridItem += 1

                        lastIntentItem = it.index
                        oldChar = installedAppName!!.substring(0, 1).toUpperCase(java.util.Locale.getDefault())
                    }
                }

        recyclerViewAdapter = CardHybridAdapter(this@ApplicationsView, applicationContext, applicationsAdapterItems)

        if (intent.getStringArrayExtra("freq") != null) {
            freqApps = intent.getStringArrayExtra("freq")
            val freqLength = intent.getIntExtra("num", -1)
            PublicVariable.freqApps = freqApps
            PublicVariable.freqLength = freqLength
            if (freqLength > 1) {
                loadFreq = true
            }
        }

        if (!loadFreq) {
            MainView.removeView(freqList)
            val layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            )
            nestedScrollView.layoutParams = layoutParams
        } else {
            val layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.freqList)
            scrollRelativeLayout.setPadding(0, scrollRelativeLayout.paddingTop, 0, 0)
            nestedScrollView.layoutParams = layoutParams
            nestedIndexScrollView.setPadding(0, 0, 0, functionsClass.DpToInteger(66))
        }

        recyclerViewAdapter.notifyDataSetChanged()
        val sectionsData = arrayOfNulls<HybridSectionedGridRecyclerViewAdapter.Section>(sections.size)
        val hybridSectionedGridRecyclerViewAdapter = HybridSectionedGridRecyclerViewAdapter(
                applicationContext,
                R.layout.hybrid_sections,
                R.id.section_text,
                loadView,
                recyclerViewAdapter
        )

        hybridSectionedGridRecyclerViewAdapter.setSections(sections.toArray(sectionsData))
        loadView.adapter = hybridSectionedGridRecyclerViewAdapter

        recyclerViewLayoutManager.scrollToPosition(0)
        nestedScrollView.scrollTo(0, 0)

        loadApplicationsIndex().await().also {
            if (it) {
                loadInstalledCustomIconPackages()
            }
        }

        loadSearchEngineData().await()

        try {
            if (intent.hasExtra("goHome")) {
                if (intent.getBooleanExtra("goHome", false)) {
                    Intent(Intent.ACTION_MAIN).let {
                        it.addCategory(Intent.CATEGORY_HOME)
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(it)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()

            this@ApplicationsView.finish()
        }
    }

    private fun loadApplicationsIndex() = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {
        indexView.removeAllViews()

        val indexCount = indexList.size
        for (indexNumber in 0 until indexCount) {
            try {
                val indexText = indexList[indexNumber]!!
                if (mapIndexFirstItem[indexText] == null /*avoid duplication*/) {
                    mapIndexFirstItem[indexText] = indexNumber
                }
                mapIndexLastItem[indexText] = indexNumber
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val indexListFinal: List<String> = ArrayList(mapIndexFirstItem.keys)
        indexListFinal.forEach { indexText ->
            val sideIndexItem = layoutInflater.inflate(R.layout.side_index_item, null) as TextView
            sideIndexItem.text = indexText.toUpperCase(java.util.Locale.getDefault())
            sideIndexItem.setTextColor(PublicVariable.colorLightDarkOpposite)
            indexView.addView(sideIndexItem)
        }

        val sideIndexItem = layoutInflater.inflate(R.layout.side_index_item, null) as TextView
        Handler().postDelayed({
            var upperRange = (indexView.y - sideIndexItem.height).toInt()
            for (number in 0 until indexView.childCount) {
                val indexText = (indexView.getChildAt(number) as TextView).text.toString()
                val indexRange = (indexView.getChildAt(number).y + indexView.y + sideIndexItem.height).toInt()
                for (jRange in upperRange..indexRange) {
                    mapRangeIndex[jRange] = indexText
                }

                upperRange = indexRange
            }

            setupFastScrollingIndexing()
        },700)
    }

    private fun loadInstalledCustomIconPackages() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
        try {
            val packageManager = applicationContext.packageManager
            //ACTION: com.novalauncher.THEME
            //CATEGORY: com.novalauncher.category.CUSTOM_ICON_PICKER
            val intentCustomIcons = Intent()
            intentCustomIcons.action = "com.novalauncher.THEME"
            intentCustomIcons.addCategory("com.novalauncher.category.CUSTOM_ICON_PICKER")
            val resolveInfos = packageManager.queryIntentActivities(intentCustomIcons, 0)
            try {
                PublicVariable.customIconsPackages.clear()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            for (resolveInfo in resolveInfos) {
                PrintDebug("CustomIconPackages ::: " + resolveInfo.activityInfo.packageName)
                PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            this.cancel()
        }
    }

    /*Indexing*/
    private fun setupFastScrollingIndexing() {
        val popupIndexBackground = getDrawable(R.drawable.ic_launcher_balloon)!!.mutate()
        popupIndexBackground.setTint(PublicVariable.primaryColorOpposite)
        popupIndex.background = popupIndexBackground

        nestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
        nestedIndexScrollView.visibility = View.VISIBLE

        val popupIndexOffsetY = (PublicVariable.statusBarHeight + PublicVariable.actionBarHeight + if (functionsClass.UsageStatsEnabled()) functionsClass.DpToInteger(7) else functionsClass.DpToInteger(7)).toFloat()
        nestedIndexScrollView.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    if (!functionsClass.litePreferencesEnabled()) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                            popupIndex.text = indexText
                            popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                            popupIndex.visibility = View.VISIBLE
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!functionsClass.litePreferencesEnabled()) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            if (!popupIndex.isShown) {
                                popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                                popupIndex.visibility = View.VISIBLE
                            }
                            popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                            popupIndex.text = indexText
                            try {
                                nestedScrollView.smoothScrollTo(
                                        0,
                                        loadView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                                )
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            if (popupIndex.isShown) {
                                popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                                popupIndex.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (functionsClass.litePreferencesEnabled()) {
                        try {
                            nestedScrollView.smoothScrollTo(
                                    0,
                                    loadView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                            )
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        if (popupIndex.isShown) {
                            try {
                                nestedScrollView.smoothScrollTo(
                                        0,
                                        loadView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                                )
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                            popupIndex.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            true
        }
    }
    /*Indexing*/

    /*Search Engine*/
    private fun loadSearchEngineData() = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {

        if (SearchEngineAdapter.allSearchResultItems.isEmpty()) {
            searchAdapterItems = ArrayList<AdapterItems>()

            //Loading Applications
            applicationInfoList = packageManager.queryIntentActivities(Intent().apply {
                this.action = Intent.ACTION_MAIN
                this.addCategory(Intent.CATEGORY_LAUNCHER)
            }, PackageManager.GET_RESOLVED_FILTER)
            val applicationInfoListSorted = applicationInfoList.sortedWith(ResolveInfo.DisplayNameComparator(packageManager))

            applicationInfoListSorted.asFlow()
                    .filter {

                        (packageManager.getLaunchIntentForPackage(it.activityInfo.packageName) != null)
                    }
                    .map {

                        it
                    }
                    .collect {
                        try {
                            installedPackageName = it.activityInfo.packageName
                            installedAppName = functionsClass.activityLabel(it.activityInfo)

                            installedAppIcon = if (functionsClass.loadCustomIcons()) {
                                loadCustomIcons.getDrawableIconForPackage(installedPackageName, functionsClass.shapedAppIcon(installedPackageName))
                            } else {
                                functionsClass.shapedAppIcon(installedPackageName)
                            }

                            searchAdapterItems.add(AdapterItems(installedAppName, installedPackageName, installedAppIcon, SearchEngineAdapter.SearchResultType.SearchShortcuts))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {

                        }
                    }

            //Loading Folders
            try {
                getFileStreamPath(".categoryInfo").readLines().forEach {
                    try {
                        searchAdapterItems.add(AdapterItems(it, functionsClass.readFileLine(it), SearchEngineAdapter.SearchResultType.SearchFolders))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //Loading Widgets
            val appWidgetManager = AppWidgetManager.getInstance(this@ApplicationsView)
            val widgetDataInterface = Room.databaseBuilder(applicationContext, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                            super.onCreate(supportSQLiteDatabase)
                        }

                        override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                            super.onOpen(supportSQLiteDatabase)
                        }
                    })
                    .build()
            val widgetDataModels = widgetDataInterface.initDataAccessObject().getAllWidgetDataCoroutines()

            if (widgetDataModels.isNotEmpty()) {
                widgetDataModels.forEach { widgetDataModel ->
                    try {

                        val appWidgetId: Int = widgetDataModel.WidgetId
                        val packageName: String = widgetDataModel.PackageName
                        val className: String = widgetDataModel.ClassNameProvider
                        val configClassName: String? = widgetDataModel.ConfigClassName

                        PrintDebug("*** $appWidgetId *** PackageName: $packageName - ClassName: $className - Configure: $configClassName ***")

                        if (functionsClass.appIsInstalled(packageName)) {

                            val appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
                            val newAppName = functionsClass.appName(packageName)
                            val appIcon = if (functionsClass.loadCustomIcons()) loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.shapedAppIcon(packageName)) else functionsClass.shapedAppIcon(packageName)

                            searchAdapterItems.add(AdapterItems(
                                    newAppName,
                                    packageName,
                                    className,
                                    configClassName,
                                    widgetDataModel.WidgetLabel,
                                    appIcon,
                                    appWidgetProviderInfo,
                                    appWidgetId,
                                    widgetDataModel.Recovery!!,
                                    SearchEngineAdapter.SearchResultType.SearchWidgets
                            ))
                        } else {
                            widgetDataInterface.initDataAccessObject().deleteByWidgetClassNameProviderWidget(packageName, className)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            val searchRecyclerViewAdapter = SearchEngineAdapter(applicationContext, searchAdapterItems)
            if (searchAdapterItems.size > 0) {
                setupSearchView(searchRecyclerViewAdapter)
            }
        } else {
            searchAdapterItems = SearchEngineAdapter.allSearchResultItems

            val searchRecyclerViewAdapter = SearchEngineAdapter(applicationContext, searchAdapterItems)
            if (searchAdapterItems.size > 0) {
                setupSearchView(searchRecyclerViewAdapter)
            }
        }
    }

    private fun setupSearchView(searchRecyclerViewAdapter: SearchEngineAdapter) {
        searchView.setAdapter(searchRecyclerViewAdapter)

        searchView.setDropDownBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        searchView.isVerticalScrollBarEnabled = false
        searchView.scrollBarSize = 0

        searchView.setTextColor(PublicVariable.colorLightDarkOpposite)
        searchView.compoundDrawableTintList = ColorStateList.valueOf(functionsClass.setColorAlpha(PublicVariable.colorLightDarkOpposite, 175f))

        val layerDrawableSearchIcon = getDrawable(R.drawable.search_icon) as RippleDrawable?
        val backgroundTemporarySearchIcon = layerDrawableSearchIcon!!.findDrawableByLayerId(R.id.backgroundTemporary)
        val frontTemporarySearchIcon = layerDrawableSearchIcon.findDrawableByLayerId(R.id.frontTemporary)
        val frontDrawableSearchIcon = layerDrawableSearchIcon.findDrawableByLayerId(R.id.frontDrawable)
        backgroundTemporarySearchIcon.setTint(PublicVariable.colorLightDarkOpposite)
        frontTemporarySearchIcon.setTint(PublicVariable.colorLightDark)
        frontDrawableSearchIcon.setTint(if (PublicVariable.themeLightDark) functionsClass.manipulateColor(PublicVariable.primaryColor, 0.90f) else functionsClass.manipulateColor(PublicVariable.primaryColor, 3.00f))

        layerDrawableSearchIcon.setLayerInset(2,
                functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13))

        searchIcon.setImageDrawable(layerDrawableSearchIcon)
        searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
        searchIcon.visibility = View.VISIBLE

        textInputSearchView.hintTextColor = ColorStateList.valueOf(PublicVariable.primaryColorOpposite)
        textInputSearchView.boxStrokeColor = PublicVariable.primaryColor

        var backgroundTemporaryInput = GradientDrawable()
        try {
            val layerDrawableBackgroundInput = getDrawable(R.drawable.background_search_input) as LayerDrawable?
            backgroundTemporaryInput = layerDrawableBackgroundInput!!.findDrawableByLayerId(R.id.backgroundTemporary) as GradientDrawable
            backgroundTemporaryInput.setTint(PublicVariable.colorLightDark)
            textInputSearchView.background = layerDrawableBackgroundInput
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            textInputSearchView.background = null
        }

        val finalBackgroundTemporaryInput = backgroundTemporaryInput
        searchIcon.setOnClickListener {
            val bundleSearchEngineUsed = Bundle()
            bundleSearchEngineUsed.putParcelable("USER_USED_SEARCH_ENGINE", firebaseAuth.currentUser)
            bundleSearchEngineUsed.putInt("TYPE_USED_SEARCH_ENGINE", SearchEngineAdapter.SearchResultType.SearchFolders)

            val firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)
            firebaseAnalytics.logEvent(SearchEngineAdapter.SEARCH_ENGINE_USED_LOG, bundleSearchEngineUsed)

            if (functionsClass.securityServicesSubscribed()) {
                if (functionsClass.readPreference(".Password", "Pin", "0") == "0" && functionsClass.securityServicesSubscribed()) {
                    startActivity(Intent(applicationContext, HandlePinPassword::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
                } else {
                    if (!SearchEngineAdapter.alreadyAuthenticatedSearchEngine) {
                        if (functionsClass.securityServicesSubscribed()) {
                            FunctionsClassSecurity.AuthOpenAppValues.authComponentName = getString(R.string.securityServices)
                            FunctionsClassSecurity.AuthOpenAppValues.authSecondComponentName = packageName
                            FunctionsClassSecurity.AuthOpenAppValues.authSearchEngine = true

                            functionsClassSecurity.openAuthInvocation()

                            val intentFilter = IntentFilter()
                            intentFilter.addAction("SEARCH_ENGINE_AUTHENTICATED")
                            val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context, intent: Intent) {
                                    if (intent.action == "SEARCH_ENGINE_AUTHENTICATED") {
                                        performSearchEngine(finalBackgroundTemporaryInput)
                                    }
                                }
                            }
                            try {
                                registerReceiver(broadcastReceiver, intentFilter)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        performSearchEngine(finalBackgroundTemporaryInput)
                    }
                }
            } else {
                performSearchEngine(finalBackgroundTemporaryInput)
            }
        }
    }

    private fun performSearchEngine(finalBackgroundTemporaryInput: GradientDrawable) {
        Handler().postDelayed({
            if (functionsClass.searchEngineSubscribed()) {
                textInputSearchView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                textInputSearchView.visibility = View.VISIBLE

                searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                searchIcon.visibility = View.INVISIBLE

                val valueAnimatorCornerDown = ValueAnimator.ofInt(functionsClass.DpToInteger(51), functionsClass.DpToInteger(7))
                valueAnimatorCornerDown.duration = 777
                valueAnimatorCornerDown.addUpdateListener { animator ->
                    val animatorValue = animator.animatedValue as Int

                    textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                    finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
                }
                valueAnimatorCornerDown.start()

                val valueAnimatorScalesUp = ValueAnimator.ofInt(functionsClass.DpToInteger(51), switchWidgets.width)
                valueAnimatorScalesUp.duration = 777
                valueAnimatorScalesUp.addUpdateListener { animator ->
                    val animatorValue = animator.animatedValue as Int
                    textInputSearchView.layoutParams.width = animatorValue
                    textInputSearchView.requestLayout()
                }
                valueAnimatorScalesUp.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        searchView.requestFocus()
                        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)
                        Handler().postDelayed({
                            searchFloatIt.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_up_bounce_interpolator))
                            searchFloatIt.visibility = View.VISIBLE
                            searchClose.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_up_bounce_interpolator))
                            searchClose.visibility = View.VISIBLE
                        }, 555)
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                valueAnimatorScalesUp.start()

                searchFloatIt.setOnClickListener {
                    if (!searchView.text.toString().isEmpty() && SearchEngineAdapter.allSearchResultItems.size > 0 && searchView.text.toString().length >= 2) {
                        SearchEngineAdapter.allSearchResultItems.forEach { searchResultItem ->
                            when (searchResultItem.searchResultType) {
                                SearchEngineAdapter.SearchResultType.SearchShortcuts -> {
                                    functionsClass.runUnlimitedShortcutsService(searchResultItem.packageName)
                                }
                                SearchEngineAdapter.SearchResultType.SearchFolders -> {
                                    functionsClass.runUnlimitedFolderService(searchResultItem.category)
                                }
                                SearchEngineAdapter.SearchResultType.SearchWidgets -> {
                                    functionsClass
                                            .runUnlimitedWidgetService(searchResultItem.appWidgetId,
                                                    searchResultItem.widgetLabel)
                                }
                            }
                        }
                    }
                }

                searchView.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                    }
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                    }
                    override fun afterTextChanged(s: Editable) {

                    }
                })

                searchView.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                     override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                         if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                             if (SearchEngineAdapter.allSearchResultItems.size == 1 && !searchView.text.toString().isEmpty() && searchView.text.toString().length >= 2) {
                                 when (SearchEngineAdapter.allSearchResultItems[0].searchResultType) {
                                     SearchEngineAdapter.SearchResultType.SearchShortcuts -> {
                                         functionsClass.runUnlimitedShortcutsService(SearchEngineAdapter.allSearchResultItems[0].packageName)
                                     }
                                     SearchEngineAdapter.SearchResultType.SearchFolders -> {
                                         functionsClass.runUnlimitedFolderService(SearchEngineAdapter.allSearchResultItems[0].category)
                                     }
                                     SearchEngineAdapter.SearchResultType.SearchWidgets -> {
                                         functionsClass
                                                 .runUnlimitedWidgetService(SearchEngineAdapter.allSearchResultItems[0].appWidgetId,
                                                         SearchEngineAdapter.allSearchResultItems[0].widgetLabel)
                                     }
                                 }

                                 searchView.setText("")

                                 val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                 inputMethodManager.hideSoftInputFromWindow(searchView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                                 val valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51))
                                 valueAnimatorCornerUp.duration = 777
                                 valueAnimatorCornerUp.addUpdateListener { animator ->
                                     val animatorValue = animator.animatedValue as Int
                                     textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                                     finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
                                 }
                                 valueAnimatorCornerUp.start()

                                 val valueAnimatorScales = ValueAnimator.ofInt(textInputSearchView.width, functionsClass.DpToInteger(51))
                                 valueAnimatorScales.duration = 777
                                 valueAnimatorScales.addUpdateListener { animator ->
                                     val animatorValue = animator.animatedValue as Int
                                     textInputSearchView.layoutParams.width = animatorValue
                                     textInputSearchView.requestLayout()
                                 }
                                 valueAnimatorScales.addListener(object : Animator.AnimatorListener {
                                     override fun onAnimationStart(animation: Animator) {

                                     }

                                     override fun onAnimationEnd(animation: Animator) {
                                         textInputSearchView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                                         textInputSearchView.visibility = View.INVISIBLE
                                         searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                                         searchIcon.visibility = View.VISIBLE
                                         searchFloatIt.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                                         searchFloatIt.visibility = View.INVISIBLE
                                         searchClose.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                                         searchClose.visibility = View.INVISIBLE
                                     }

                                     override fun onAnimationCancel(animation: Animator) {

                                     }

                                     override fun onAnimationRepeat(animation: Animator) {

                                     }
                                 })
                                 valueAnimatorScales.start()
                             } else {
                                 if (SearchEngineAdapter.allSearchResultItems.size > 0 && searchView.text.toString().length >= 2) {
                                     searchView.showDropDown()
                                 }
                             }
                         }

                         return false
                     }
                 })

                searchClose.setOnClickListener {
                    searchView.setText("")

                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(searchView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                    val valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51))
                    valueAnimatorCornerUp.duration = 777
                    valueAnimatorCornerUp.addUpdateListener { animator ->
                        val animatorValue = animator.animatedValue as Int
                        textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                        finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
                    }
                    valueAnimatorCornerUp.start()

                    val valueAnimatorScales = ValueAnimator.ofInt(textInputSearchView.width, functionsClass.DpToInteger(51))
                    valueAnimatorScales.duration = 777
                    valueAnimatorScales.addUpdateListener { animator ->
                        val animatorValue = animator.animatedValue as Int
                        textInputSearchView.layoutParams.width = animatorValue
                        textInputSearchView.requestLayout()
                    }
                    valueAnimatorScales.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            textInputSearchView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                            textInputSearchView.visibility = View.INVISIBLE
                            searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                            searchIcon.visibility = View.VISIBLE
                            searchFloatIt.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                            searchFloatIt.visibility = View.INVISIBLE
                            searchClose.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                            searchClose.visibility = View.INVISIBLE
                        }

                        override fun onAnimationCancel(animation: Animator) {

                        }

                        override fun onAnimationRepeat(animation: Animator) {

                        }
                    })
                    valueAnimatorScales.start()
                }
            } else {
                InAppBilling.ItemIAB = BillingManager.iapSearchEngines

                startActivity(Intent(applicationContext, InAppBilling::class.java),
                        ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
            }
        }, 99)
    }
    /*Search Engine*/
}