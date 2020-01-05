/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/5/20 6:28 AM
 * Last modified 1/5/20 6:27 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts

import android.animation.Animator
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.view.ViewAnimationUtils
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.hybrid_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Util.UI.SimpleGestureFilterSwitch
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.hypot

class ApplicationsView : Activity(), View.OnClickListener, OnLongClickListener, OnTouchListener, SimpleGestureFilterSwitch.SimpleGestureListener {

    private lateinit var functionsClass: FunctionsClass
    private lateinit var functionsClassSecurity: FunctionsClassSecurity

    private lateinit var applicationInfoList: List<ResolveInfo>

    private lateinit var mapIndexFirstItem: Map<String, Int>
    private lateinit var mapIndexLastItem: Map<String, Int>
    private lateinit var mapRangeIndex: Map<Int, String>
    private lateinit var indexItems: NavigableMap<String, Int>
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
        indexItems = TreeMap<String, Int>()

        applicationInfoList = ArrayList<ResolveInfo>()
        applicationsAdapterItems = ArrayList<AdapterItems>()
        mapIndexFirstItem = LinkedHashMap<String, Int>()
        mapIndexLastItem = LinkedHashMap<String, Int>()
        mapRangeIndex = LinkedHashMap<Int, String>()

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
        }

        initiateApplicationsLoadingProcess()

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


    private fun initiateApplicationsLoadingProcess() {
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

        indexView.removeAllViews()

        loadApplicationsLimited()
    }
    private fun loadApplicationsLimited() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
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
        var oldChar: String? = null
        applicationInfoListSorted.forEachIndexed { resolveInfoIndex, resolveInfo ->
            try {
                installedPackageName = resolveInfo.activityInfo.packageName
                installedAppName = functionsClass.activityLabel(resolveInfo.activityInfo)

                val newChar = installedAppName!!.substring(0, 1).toUpperCase(Locale.getDefault())

                if (resolveInfoIndex == 0) {
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
                indexList.add(newChar)
                indexItems[newChar] = itemOfIndex++

                hybridItem += 1

                lastIntentItem = resolveInfoIndex
                oldChar = installedAppName!!.substring(0, 1).toUpperCase(Locale.getDefault())
            } catch (e: Exception) {
                e.printStackTrace()
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


        /*
        *
        * Call Different Coroutine to Load
        * Index Items
        * Custom Icon Packages
        * Search Items
        *
        *
        *
        * */
    }
}