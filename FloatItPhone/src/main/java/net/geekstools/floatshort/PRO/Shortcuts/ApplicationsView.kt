/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 7:00 PM
 * Last modified 3/26/20 6:19 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
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
import android.net.Uri
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
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.geekstools.floatshort.PRO.Automation.Apps.AppAutoFeatures
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.Preferences.PreferencesActivity
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchResultType
import net.geekstools.floatshort.PRO.SearchEngine.UI.Adapter.SearchEngineAdapter
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.PinPassword.HandlePinPassword
import net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter.CardHybridAdapter
import net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter.HybridSectionedGridRecyclerViewAdapter
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsApplications
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsSearchEngine
import net.geekstools.floatshort.PRO.Utils.Functions.*
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug.Companion.PrintDebug
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassSecurity.AuthOpenAppValues.authComponentName
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassSecurity.AuthOpenAppValues.authHW
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassSecurity.AuthOpenAppValues.authPositionX
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassSecurity.AuthOpenAppValues.authPositionY
import net.geekstools.floatshort.PRO.Utils.GeneralAdapters.RecycleViewSmoothLayoutGrid
import net.geekstools.floatshort.PRO.Utils.IAP.InAppBilling
import net.geekstools.floatshort.PRO.Utils.IAP.Util.PurchasesCheckpoint
import net.geekstools.floatshort.PRO.Utils.IAP.billing.BillingManager
import net.geekstools.floatshort.PRO.Utils.InAppUpdate.InAppUpdateProcess
import net.geekstools.floatshort.PRO.Utils.RemoteProcess.LicenseValidator
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryFolders
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryShortcuts
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryWidgets
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.SwipeGestureListener
import net.geekstools.floatshort.PRO.Utils.UI.PopupDialogue.WaitingDialogue
import net.geekstools.floatshort.PRO.Utils.UI.PopupDialogue.WaitingDialogueLiveData
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations
import net.geekstools.floatshort.PRO.databinding.HybridApplicationViewBinding
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.math.hypot

class ApplicationsView : AppCompatActivity(), View.OnClickListener, OnLongClickListener, OnTouchListener, GestureListenerInterface {

    private lateinit var functionsClassDataActivity: FunctionsClassDataActivity

    private lateinit var functionsClass: FunctionsClass
    private lateinit var functionsClassRunServices: FunctionsClassRunServices
    private lateinit var functionsClassSecurity: FunctionsClassSecurity
    private lateinit var functionsClassDialogues: FunctionsClassDialogues

    private lateinit var applicationInfoList: List<ResolveInfo>

    private lateinit var mapIndexFirstItem: LinkedHashMap<String, Int>
    private lateinit var mapIndexLastItem: LinkedHashMap<String, Int>
    private lateinit var mapRangeIndex: LinkedHashMap<Int, String>
    private lateinit var indexItems: NavigableMap<String, Int>
    private lateinit var indexList: ArrayList<String?>

    private lateinit var applicationsAdapterItems: ArrayList<AdapterItemsApplications>
    private lateinit var sections: ArrayList<HybridSectionedGridRecyclerViewAdapter.Section>
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<CardHybridAdapter.ViewHolder>
    private lateinit var recyclerViewLayoutManager: GridLayoutManager

    private var installedPackageName: String? = null
    private var installedClassName: String? = null
    private var installedAppName: String? = null
    private var installedAppIcon: Drawable? = null

    var hybridItem: Int = 0
    var lastIntentItem: Int = 0

    private lateinit var frequentlyUsedAppsList: Array<String>
    private lateinit var frequentlyUsedAppsCounter: IntArray
    var loadFreq = false

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(applicationContext, this@ApplicationsView)
    }

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var waitingDialogue: Dialog

    private lateinit var loadCustomIcons: LoadCustomIcons

    private lateinit var hybridApplicationViewBinding: HybridApplicationViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hybridApplicationViewBinding = HybridApplicationViewBinding.inflate(layoutInflater)
        setContentView(hybridApplicationViewBinding.root)

        functionsClassDataActivity = FunctionsClassDataActivity(this@ApplicationsView)

        functionsClass = FunctionsClass(applicationContext)
        functionsClassRunServices = FunctionsClassRunServices(applicationContext)
        functionsClassSecurity = FunctionsClassSecurity(applicationContext)
        functionsClassDialogues = FunctionsClassDialogues(functionsClassDataActivity, functionsClass)

        functionsClass.loadSavedColor()
        functionsClass.checkLightDarkTheme()

        functionsClass.setThemeColorFloating(this, hybridApplicationViewBinding.MainView, functionsClass.appThemeTransparent())
        functionsClassDialogues.changeLog()

        recyclerViewLayoutManager = RecycleViewSmoothLayoutGrid(applicationContext, functionsClass.columnCount(105), OrientationHelper.VERTICAL, false)
        hybridApplicationViewBinding.applicationsListView.layoutManager = recyclerViewLayoutManager

        indexList = ArrayList<String?>()
        sections = ArrayList<HybridSectionedGridRecyclerViewAdapter.Section>()
        indexItems = TreeMap<String, Int>()

        applicationInfoList = ArrayList<ResolveInfo>()
        applicationsAdapterItems = ArrayList<AdapterItemsApplications>()

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
        val backPreferenceAction: Drawable = drawPreferenceAction.findDrawableByLayerId(R.id.backgroundTemporary)
        backPreferenceAction.setTint(PublicVariable.primaryColorOpposite)
        hybridApplicationViewBinding.actionButton.setImageDrawable(drawPreferenceAction)

        hybridApplicationViewBinding.switchWidgets.setTextColor(getColor(R.color.light))
        hybridApplicationViewBinding.switchCategories.setTextColor(getColor(R.color.light))
        if (PublicVariable.themeLightDark /*light*/ && functionsClass.appThemeTransparent() /*transparent*/) {
            hybridApplicationViewBinding.switchWidgets.setTextColor(getColor(R.color.dark))
            hybridApplicationViewBinding.switchCategories.setTextColor(getColor(R.color.dark))
        }

        hybridApplicationViewBinding.switchCategories.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        hybridApplicationViewBinding.switchCategories.rippleColor = ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        hybridApplicationViewBinding.switchWidgets.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        hybridApplicationViewBinding.switchWidgets.rippleColor = ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        hybridApplicationViewBinding.recoveryAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        hybridApplicationViewBinding.recoveryAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        hybridApplicationViewBinding.automationAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        hybridApplicationViewBinding.automationAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        val drawRecoverFloatingCategories = getDrawable(R.drawable.draw_recovery)?.mutate() as LayerDrawable?
        val backRecoverFloatingCategories = drawRecoverFloatingCategories?.findDrawableByLayerId(R.id.backgroundTemporary)?.mutate()
        backRecoverFloatingCategories?.setTint(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)

        val drawRecoverFloatingWidgets = getDrawable(R.drawable.draw_recovery_widgets)?.mutate() as LayerDrawable?
        val backRecoverFloatingWidgets = drawRecoverFloatingWidgets?.findDrawableByLayerId(R.id.backgroundTemporary)?.mutate()
        backRecoverFloatingWidgets?.setTint(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)

        hybridApplicationViewBinding.recoverFloatingCategories.setImageDrawable(drawRecoverFloatingCategories)
        hybridApplicationViewBinding.recoverFloatingWidgets.setImageDrawable(drawRecoverFloatingWidgets)

        hybridApplicationViewBinding.actionButton.setOnClickListener {
            functionsClass.doVibrate(33)

            if (!PublicVariable.actionCenter) {

                val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(hybridApplicationViewBinding.recoveryAction, hybridApplicationViewBinding.actionButton.x.toInt(), hybridApplicationViewBinding.actionButton.y.toInt(), finalRadius.toFloat(), functionsClass.DpToInteger(13).toFloat())
                circularReveal.duration = 777
                circularReveal.interpolator = AccelerateInterpolator()
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        hybridApplicationViewBinding.recoveryAction.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }
                })

                functionsClass.openActionMenuOption(this@ApplicationsView, hybridApplicationViewBinding.fullActionViews, hybridApplicationViewBinding.actionButton, hybridApplicationViewBinding.fullActionViews.isShown)
            } else {
                hybridApplicationViewBinding.recoveryAction.visibility = View.VISIBLE

                val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(hybridApplicationViewBinding.recoveryAction, hybridApplicationViewBinding.actionButton.x.toInt(), hybridApplicationViewBinding.actionButton.y.toInt(), functionsClass.DpToInteger(13).toFloat(), finalRadius.toFloat())
                circularReveal.duration = 1300
                circularReveal.interpolator = AccelerateInterpolator()
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        hybridApplicationViewBinding.recoveryAction.visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }
                })
            }
        }

        hybridApplicationViewBinding.switchCategories.setOnClickListener {
            try {
                functionsClass.navigateToClass(this@ApplicationsView, FoldersConfigurations::class.java,
                        ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        hybridApplicationViewBinding.switchWidgets.setOnClickListener {
            if (functionsClass.networkConnection() && firebaseAuth.currentUser != null) {

                if (functionsClass.floatingWidgetsPurchased()) {

                    functionsClass.navigateToClass(this@ApplicationsView, WidgetConfigurations::class.java,
                            ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))

                } else {
                    InAppBilling.ItemIAB = BillingManager.iapFloatingWidgets

                    startActivity(Intent(applicationContext, InAppBilling::class.java)
                            .putExtra("UserEmailAddress", functionsClass.readPreference(".UserInformation", "userEmail", null)),
                            ActivityOptions.makeCustomAnimation(applicationContext, R.anim.down_up, android.R.anim.fade_out).toBundle())

                }
            } else {
                if (functionsClass.networkConnection()) {
                    Toast.makeText(applicationContext, getString(R.string.internetError), Toast.LENGTH_LONG).show()
                }

                if (firebaseAuth.currentUser == null) {
                    Toast.makeText(applicationContext, getString(R.string.authError), Toast.LENGTH_LONG).show()
                }
            }
        }

        hybridApplicationViewBinding.automationAction.setOnClickListener {
            functionsClass.doVibrate(50)

            val intent = Intent(applicationContext, AppAutoFeatures::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.up_down, android.R.anim.fade_out).toBundle())
        }

        hybridApplicationViewBinding.recoveryAction.setOnClickListener {
            Intent(applicationContext, RecoveryShortcuts::class.java).let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(it)
            }
        }
        hybridApplicationViewBinding.recoverFloatingCategories.setOnClickListener {
            Intent(applicationContext, RecoveryFolders::class.java).let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(it)
            }

            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
            hybridApplicationViewBinding.recoverFloatingCategories.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    hybridApplicationViewBinding.recoverFloatingCategories.visibility = View.INVISIBLE
                }

                override fun onAnimationStart(animation: Animation?) {

                }

            })
        }
        hybridApplicationViewBinding.recoverFloatingWidgets.setOnClickListener {
            Intent(applicationContext, RecoveryWidgets::class.java).let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(it)
            }

            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
            hybridApplicationViewBinding.recoverFloatingWidgets.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    hybridApplicationViewBinding.recoverFloatingWidgets.visibility = View.INVISIBLE
                }

                override fun onAnimationStart(animation: Animation?) {

                }

            })
        }

        hybridApplicationViewBinding.actionButton.setOnLongClickListener {
            Handler().postDelayed({
                val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@ApplicationsView, hybridApplicationViewBinding.actionButton, "transition")
                Intent().let {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    it.setClass(this@ApplicationsView, PreferencesActivity::class.java)
                    startActivity(it, activityOptionsCompat.toBundle())
                }
            }, 113)

            true
        }

        hybridApplicationViewBinding.switchCategories.setOnLongClickListener {
            if (!hybridApplicationViewBinding.recoverFloatingCategories.isShown) {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_show)
                hybridApplicationViewBinding.recoverFloatingCategories.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        hybridApplicationViewBinding.recoverFloatingCategories.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
                hybridApplicationViewBinding.recoverFloatingCategories.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }
                    override fun onAnimationEnd(animation: Animation) {
                        hybridApplicationViewBinding.recoverFloatingCategories.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }

            true
        }
        hybridApplicationViewBinding.switchWidgets.setOnLongClickListener {
            if (!hybridApplicationViewBinding.recoverFloatingWidgets.isShown) {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_show)
                hybridApplicationViewBinding.recoverFloatingWidgets.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        hybridApplicationViewBinding.recoverFloatingWidgets.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
                hybridApplicationViewBinding.recoverFloatingWidgets.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        hybridApplicationViewBinding.recoverFloatingWidgets.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }

            true
        }

        //In-App Billing
        PurchasesCheckpoint(this@ApplicationsView).trigger()

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

        if (!getFileStreamPath(".License").exists() && functionsClass.networkConnection()) {
            startService(Intent(applicationContext, LicenseValidator::class.java))

            val intentFilter = IntentFilter()
            intentFilter.addAction(getString(R.string.license))
            val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == getString(R.string.license)) {
                        functionsClass.dialogueLicense(this@ApplicationsView)

                        Handler().postDelayed({
                            stopService(Intent(applicationContext, LicenseValidator::class.java))
                        }, 1000)

                        unregisterReceiver(this)
                    }
                }
            }
            registerReceiver(broadcastReceiver, intentFilter)
        }

        if (functionsClass.networkConnection()
                && functionsClass.readPreference(".UserInformation", "userEmail", null) == null
                && firebaseAuth.currentUser == null) {

            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.webClientId))
                    .requestEmail()
                    .build()

            val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
            googleSignInClient.signInIntent.run {
                startActivityForResult(this, 666)
            }

            ViewModelProvider(this@ApplicationsView).get(WaitingDialogueLiveData::class.java).run {
                this.dialogueTitle.value = getString(R.string.signinTitle)
                this.dialogueMessage.value = getString(R.string.signinMessage)

                waitingDialogue = WaitingDialogue().initShow(this@ApplicationsView)
            }
        }

        val drawableShare: LayerDrawable? = getDrawable(R.drawable.draw_share) as LayerDrawable
        val backgroundShare = drawableShare!!.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundShare.setTint(PublicVariable.primaryColor)

        hybridApplicationViewBinding.shareIt.setImageDrawable(drawableShare)
        hybridApplicationViewBinding.shareIt.setOnClickListener {
            functionsClass.doVibrate(50)

            val shareText = getString(R.string.shareTitle) +
                    "\n" + getString(R.string.shareSummary) +
                    "\n" + getString(R.string.play_store_link) + packageName
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.putExtra(Intent.EXTRA_TEXT, shareText)
                this.type = "text/plain"
            }
            startActivity(sharingIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        PublicVariable.inMemory = true

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnSuccessListener {
                    firebaseRemoteConfig.activate().addOnSuccessListener {
                        if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(packageName)) {
                            val layerDrawableNewUpdate = getDrawable(R.drawable.ic_update) as LayerDrawable?
                            val gradientDrawableNewUpdate = layerDrawableNewUpdate!!.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable
                            gradientDrawableNewUpdate.setTint(PublicVariable.primaryColor)

                            hybridApplicationViewBinding.newUpdate.setImageDrawable(layerDrawableNewUpdate)
                            hybridApplicationViewBinding.newUpdate.visibility = View.VISIBLE
                            hybridApplicationViewBinding.newUpdate.setOnClickListener {
                                functionsClass.upcomingChangeLog(
                                        this@ApplicationsView,
                                        firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()), firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()).toString())
                            }
                            functionsClass.notificationCreator(
                                    getString(R.string.updateAvailable),
                                    firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogSummaryConfigKey()),
                                    firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()).toInt()
                            )
                            val inAppUpdateTriggeredTime =
                                    (Calendar.getInstance()[Calendar.YEAR].toString() + Calendar.getInstance()[Calendar.MONTH].toString() + Calendar.getInstance()[Calendar.DATE].toString())
                                            .toInt()
                            if (firebaseAuth.currentUser != null
                                    && functionsClass.readPreference("InAppUpdate", "TriggeredDate", 0) < inAppUpdateTriggeredTime) {
                                startActivity(Intent(applicationContext, InAppUpdateProcess::class.java)
                                        .putExtra("UPDATE_CHANGE_LOG", firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()))
                                        .putExtra("UPDATE_VERSION", firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()).toString())
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                        ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
                            }
                        } else {

                        }
                    }
                }

        functionsClassSecurity.resetAuthAppValues()
    }

    override fun onPause() {
        super.onPause()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.reload()?.addOnCompleteListener {
            firebaseAuth.addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user == null) {
                    functionsClass.savePreference(".UserInformation", "userEmail", null)

                    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.webClientId))
                            .requestEmail()
                            .build()

                    val googleSignInClient = GoogleSignIn.getClient(this@ApplicationsView, googleSignInOptions)
                    try {
                        googleSignInClient.signOut()
                        googleSignInClient.revokeAccess()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {

                }
            }
        }

        functionsClass.addAppShortcuts()
        functionsClass.savePreference("LoadView", "LoadViewPosition", recyclerViewLayoutManager.findFirstVisibleItemPosition())
        if (PublicVariable.actionCenter) {
            functionsClass.closeActionMenuOption(this@ApplicationsView, hybridApplicationViewBinding.fullActionViews, hybridApplicationViewBinding.actionButton)
        }

        functionsClass.savePreference("OpenMode", "openClassName", this.javaClass.simpleName)
    }

    override fun onDestroy() {
        super.onDestroy()
        PublicVariable.inMemory = false
    }

    override fun onBackPressed() {
        val homeScreen = Intent(Intent.ACTION_MAIN).apply {
            this.addCategory(Intent.CATEGORY_HOME)
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeScreen, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

        functionsClass.CheckSystemRAM(this@ApplicationsView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        return false
    }

    override fun onClick(view: View?) {
        if (view is ImageView) {
            val position = view.id

            functionsClassRunServices.runUnlimitedShortcutsServiceFrequently(frequentlyUsedAppsList[position])
        }
    }

    override fun onLongClick(view: View?): Boolean {
        if (view is ImageView) {
            val position = view.id
            if (functionsClassSecurity.isAppLocked(frequentlyUsedAppsList[position])) {
                authComponentName = frequentlyUsedAppsList[position]

                authPositionX = functionsClass.displayX() / 2
                authPositionY = functionsClass.displayY() / 2
                authHW = PublicVariable.HW

                functionsClassSecurity.openAuthInvocation()
            } else {
                functionsClass.appsLaunchPad(frequentlyUsedAppsList[position])
            }
        }
        return true
    }

    override fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {
        super.onSwipeGesture(gestureConstants, downMotionEvent, moveMotionEvent, initVelocityX, initVelocityY)

        when (gestureConstants) {
            is GestureConstants.SwipeHorizontal -> {
                when (gestureConstants.horizontalDirection) {
                    GestureListenerConstants.SWIPE_RIGHT -> {
                        if (functionsClass.floatingWidgetsPurchased()) {
                            functionsClass.navigateToClass(this@ApplicationsView, WidgetConfigurations::class.java,
                                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
                        } else {
                            InAppBilling.ItemIAB = BillingManager.iapFloatingWidgets

                            functionsClass.navigateToClass(this@ApplicationsView, InAppBilling::class.java,
                                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
                        }
                    }
                    GestureListenerConstants.SWIPE_LEFT -> {
                        functionsClass.navigateToClass(this@ApplicationsView, FoldersConfigurations::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        swipeGestureListener.onTouchEvent(motionEvent)

        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                666 -> {
                    val googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val googleSignInAccount = googleSignInAccountTask.getResult(ApiException::class.java)

                    val authCredential = GoogleAuthProvider.getCredential(googleSignInAccount?.idToken, null)
                    firebaseAuth.signInWithCredential(authCredential).addOnSuccessListener {
                        val firebaseUser = firebaseAuth.currentUser
                        if (firebaseUser != null) {
                            functionsClass.savePreference(".UserInformation", "userEmail", firebaseUser.email)

                            try {
                                val betaFile = File("/data/data/$packageName/shared_prefs/.UserInformation.xml")
                                val uriBetaFile = Uri.fromFile(betaFile)
                                val firebaseStorage = FirebaseStorage.getInstance()
                                val storageReference = firebaseStorage.getReference("/Users/" + "API" + functionsClass.returnAPI() + "/" +
                                        functionsClass.readPreference(".UserInformation", "userEmail", null))
                                val uploadTask = storageReference.putFile(uriBetaFile)
                                uploadTask.addOnFailureListener { exception ->
                                    exception.printStackTrace()

                                    ViewModelProvider(this@ApplicationsView).get(WaitingDialogueLiveData::class.java).run {
                                        this.dialogueTitle.value = getString(R.string.error)
                                        this.dialogueMessage.value = exception.message
                                    }
                                }.addOnSuccessListener {
                                    PrintDebug("Firebase Activities Done Successfully")
                                    functionsClass.Toast(getString(R.string.signinFinished), Gravity.TOP)

                                    waitingDialogue.dismiss()
                                }
                            } finally {
                                functionsClassSecurity.downloadLockedAppsData()
                            }
                        }
                    }
                }
            }
        } else {
            ViewModelProvider(this@ApplicationsView).get(WaitingDialogueLiveData::class.java).run {
                this.dialogueTitle.value = getString(R.string.error)
                this.dialogueMessage.value = Activity.RESULT_CANCELED.toString()
            }
        }
    }

    private fun initiateLoadingProcessAll() {
        if (functionsClass.appThemeTransparent()) {
            hybridApplicationViewBinding.loadingSplash.setBackgroundColor(Color.TRANSPARENT)
        } else {
            hybridApplicationViewBinding.loadingSplash.setBackgroundColor(window.navigationBarColor)
        }

        if (PublicVariable.themeLightDark) {
            hybridApplicationViewBinding.loadingProgress.indeterminateDrawable.colorFilter = PorterDuffColorFilter(PublicVariable.darkMutedColor, PorterDuff.Mode.MULTIPLY)
        } else if (!PublicVariable.themeLightDark) {
            hybridApplicationViewBinding.loadingProgress.indeterminateDrawable.colorFilter = PorterDuffColorFilter(PublicVariable.vibrantColor, PorterDuff.Mode.MULTIPLY)
        }

        val layerDrawableLoadLogo: LayerDrawable = getDrawable(R.drawable.ic_launcher_layer) as LayerDrawable
        val gradientDrawableLoadLogo: BitmapDrawable = layerDrawableLoadLogo!!.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable
        gradientDrawableLoadLogo.setTint(PublicVariable.primaryColor)
        hybridApplicationViewBinding.loadingLogo.setImageDrawable(layerDrawableLoadLogo)

        loadApplicationsData()
    }

    private fun loadApplicationsData() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
        hybridApplicationViewBinding.indexView.removeAllViews()

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons.load()
            PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIconsNumber())
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
                    hybridApplicationViewBinding.loadingSplash.startAnimation(splashAnimation)
                    splashAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                            if (loadFreq == true) {
                                launch {
                                    loadFrequentlyUsedApplications().await()
                                }
                            }
                            hybridApplicationViewBinding.switchFloating.visibility = View.VISIBLE
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            hybridApplicationViewBinding.loadingSplash.visibility = View.INVISIBLE
                        }
                    })
                }
                .withIndex().collect {
                    try {
                        installedPackageName = it.value.activityInfo.packageName
                        installedClassName = it.value.activityInfo.name
                        installedAppName = functionsClass.activityLabel(it.value.activityInfo)

                        newChar = installedAppName!!.substring(0, 1).toUpperCase(Locale.getDefault())

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
                            loadCustomIcons.getDrawableIconForPackage(installedPackageName, functionsClass.shapedAppIcon(it.value.activityInfo))
                        } else {
                            functionsClass.shapedAppIcon(it.value.activityInfo)
                        }

                        applicationsAdapterItems.add(AdapterItemsApplications(installedAppName!!, installedPackageName!!, installedClassName!!, installedAppIcon!!,
                                SearchResultType.SearchShortcuts))
                    } finally {
                        indexList.add(newChar)
                        indexItems[newChar] = itemOfIndex++

                        hybridItem += 1

                        lastIntentItem = it.index
                        oldChar = installedAppName!!.substring(0, 1).toUpperCase(Locale.getDefault())
                    }
                }

        recyclerViewAdapter = CardHybridAdapter(this@ApplicationsView, applicationContext, applicationsAdapterItems)

        if (intent.getStringArrayExtra("freq") != null) {
            frequentlyUsedAppsList = intent.getStringArrayExtra("freq")
            val freqLength = intent.getIntExtra("num", -1)
            PublicVariable.frequentlyUsedApps = frequentlyUsedAppsList
            PublicVariable.freqLength = freqLength
            if (freqLength > 1) {
                loadFreq = true
            }
        }

        if (!loadFreq) {
            hybridApplicationViewBinding.MainView.removeView(hybridApplicationViewBinding.freqList)
            val layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            )
            hybridApplicationViewBinding.nestedScrollView.layoutParams = layoutParams
        } else {
            val layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.freqList)
            hybridApplicationViewBinding.scrollRelativeLayout.setPadding(0, hybridApplicationViewBinding.scrollRelativeLayout.paddingTop, 0, 0)
            hybridApplicationViewBinding.nestedScrollView.layoutParams = layoutParams
            hybridApplicationViewBinding.nestedIndexScrollView.setPadding(0, 0, 0, functionsClass.DpToInteger(66))
        }

        recyclerViewAdapter.notifyDataSetChanged()
        val sectionsData = arrayOfNulls<HybridSectionedGridRecyclerViewAdapter.Section>(sections.size)
        val hybridSectionedGridRecyclerViewAdapter = HybridSectionedGridRecyclerViewAdapter(
                applicationContext,
                R.layout.hybrid_sections,
                R.id.section_text,
                hybridApplicationViewBinding.applicationsListView,
                recyclerViewAdapter
        )

        hybridSectionedGridRecyclerViewAdapter.setSections(sections.toArray(sectionsData))
        hybridApplicationViewBinding.applicationsListView.adapter = hybridSectionedGridRecyclerViewAdapter

        recyclerViewLayoutManager.scrollToPosition(0)
        hybridApplicationViewBinding.nestedScrollView.scrollTo(0, 0)

        loadApplicationsIndex().await()

        loadInstalledCustomIconPackages().await()

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

    private fun loadFrequentlyUsedApplications() = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {
        hybridApplicationViewBinding.freqItem.removeAllViews()

        frequentlyUsedAppsCounter = IntArray(25)
        frequentlyUsedAppsList = intent.getStringArrayExtra("freq")
        val freqLength = intent.getIntExtra("num", -1)
        if (getFileStreamPath("Frequently").exists()) {
            functionsClass.removeLine(".categoryInfo", "Frequently")
            deleteFile("Frequently")
        }
        functionsClass.saveFileAppendLine(".categoryInfo", "Frequently")

        hybridApplicationViewBinding.freqList.visibility = View.VISIBLE

        for (i in 0 until freqLength) {
            val freqLayout = layoutInflater.inflate(R.layout.freq_item, null) as RelativeLayout
            val shapesImage = functionsClass.initShapesImage(freqLayout, R.id.freqItems)
            shapesImage.id = i
            shapesImage.setOnClickListener(this@ApplicationsView)
            shapesImage.setOnLongClickListener(this@ApplicationsView)
            shapesImage.setImageDrawable(if (functionsClass.loadCustomIcons()) loadCustomIcons.getDrawableIconForPackage(frequentlyUsedAppsList[i], functionsClass.shapedAppIcon(frequentlyUsedAppsList[i])) else functionsClass.shapedAppIcon(frequentlyUsedAppsList[i]))
            hybridApplicationViewBinding.freqItem.addView(freqLayout)

            functionsClass.saveFileAppendLine("Frequently", frequentlyUsedAppsList[i])
            functionsClass.saveFile(frequentlyUsedAppsList[i] + "Frequently", frequentlyUsedAppsList[i])
        }

        functionsClass.addAppShortcuts()
    }

    private fun loadInstalledCustomIconPackages() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
        val packageManager = applicationContext.packageManager
        //ACTION: com.novalauncher.THEME
        //CATEGORY: com.novalauncher.category.CUSTOM_ICON_PICKER
        val intentCustomIcons = Intent()
        intentCustomIcons.action = "com.novalauncher.THEME"
        intentCustomIcons.addCategory("com.novalauncher.category.CUSTOM_ICON_PICKER")
        val resolveInfos = packageManager.queryIntentActivities(intentCustomIcons, 0)

        PublicVariable.customIconsPackages.clear()
        for (resolveInfo in resolveInfos) {
            PrintDebug("CustomIconPackages ::: " + resolveInfo.activityInfo.packageName)
            PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName)
        }
    }

    /*Indexing*/
    private fun loadApplicationsIndex() = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {
        hybridApplicationViewBinding.indexView.removeAllViews()

        withContext(Dispatchers.Default) {
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
        }

        val indexListFinal: List<String> = ArrayList(mapIndexFirstItem.keys)
        indexListFinal.forEach { indexText ->
            val sideIndexItem = layoutInflater.inflate(R.layout.side_index_item, null) as TextView
            sideIndexItem.text = indexText.toUpperCase(Locale.getDefault())
            sideIndexItem.setTextColor(PublicVariable.colorLightDarkOpposite)
            hybridApplicationViewBinding.indexView.addView(sideIndexItem)
        }

        val sideIndexItem = layoutInflater.inflate(R.layout.side_index_item, null) as TextView
        Handler().postDelayed({
            var upperRange = (hybridApplicationViewBinding.indexView.y - sideIndexItem.height).toInt()
            for (number in 0 until hybridApplicationViewBinding.indexView.childCount) {
                val indexText = (hybridApplicationViewBinding.indexView.getChildAt(number) as TextView).text.toString()
                val indexRange = (hybridApplicationViewBinding.indexView.getChildAt(number).y + hybridApplicationViewBinding.indexView.y + sideIndexItem.height).toInt()
                for (jRange in upperRange..indexRange) {
                    mapRangeIndex[jRange] = indexText
                }

                upperRange = indexRange
            }

            setupFastScrollingIndexing()
        },700)
    }

    private fun setupFastScrollingIndexing() {
        val popupIndexBackground = getDrawable(R.drawable.ic_launcher_balloon)!!.mutate()
        popupIndexBackground.setTint(PublicVariable.primaryColorOpposite)
        hybridApplicationViewBinding.popupIndex.background = popupIndexBackground

        hybridApplicationViewBinding.nestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
        hybridApplicationViewBinding.nestedIndexScrollView.visibility = View.VISIBLE

        val popupIndexOffsetY = (PublicVariable.statusBarHeight + PublicVariable.actionBarHeight + if (functionsClass.UsageStatsEnabled()) functionsClass.DpToInteger(7) else functionsClass.DpToInteger(7)).toFloat()
        hybridApplicationViewBinding.nestedIndexScrollView.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    if (!functionsClass.litePreferencesEnabled()) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            hybridApplicationViewBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                            hybridApplicationViewBinding.popupIndex.text = indexText
                            hybridApplicationViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                            hybridApplicationViewBinding.popupIndex.visibility = View.VISIBLE
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!functionsClass.litePreferencesEnabled()) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            if (!hybridApplicationViewBinding.popupIndex.isShown) {
                                hybridApplicationViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                                hybridApplicationViewBinding.popupIndex.visibility = View.VISIBLE
                            }
                            hybridApplicationViewBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                            hybridApplicationViewBinding.popupIndex.text = indexText
                            try {
                                hybridApplicationViewBinding.nestedScrollView.smoothScrollTo(
                                        0,
                                        hybridApplicationViewBinding.applicationsListView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            if (hybridApplicationViewBinding.popupIndex.isShown) {
                                hybridApplicationViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                                hybridApplicationViewBinding.popupIndex.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (functionsClass.litePreferencesEnabled()) {
                        try {
                            hybridApplicationViewBinding.nestedScrollView.smoothScrollTo(
                                    0,
                                    hybridApplicationViewBinding.applicationsListView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        if (hybridApplicationViewBinding.popupIndex.isShown) {
                            try {
                                hybridApplicationViewBinding.nestedScrollView.smoothScrollTo(
                                        0,
                                        hybridApplicationViewBinding.applicationsListView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            hybridApplicationViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                            hybridApplicationViewBinding.popupIndex.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            true
        }
    }
    /*Indexing*/

    /*Search Engine*/
    private fun loadSearchEngineData() = CoroutineScope(SupervisorJob() + Dispatchers.Default).async {
        var searchAdapterItems: ArrayList<AdapterItemsSearchEngine> = ArrayList<AdapterItemsSearchEngine>()

        if (SearchEngineAdapter.allSearchData.isEmpty()) {

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
                            installedClassName= it.activityInfo.name
                            installedAppName = functionsClass.activityLabel(it.activityInfo)

                            installedAppIcon = if (functionsClass.loadCustomIcons()) {
                                loadCustomIcons.getDrawableIconForPackage(installedPackageName, functionsClass.shapedAppIcon(it.activityInfo))
                            } else {
                                functionsClass.shapedAppIcon(it.activityInfo)
                            }

                            searchAdapterItems.add(AdapterItemsSearchEngine(installedAppName, installedPackageName, installedClassName, installedAppIcon, SearchResultType.SearchShortcuts))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {

                        }
                    }

            //Loading Folders
            try {
                getFileStreamPath(".categoryInfo").readLines().forEach {
                    try {
                        searchAdapterItems.add(AdapterItemsSearchEngine(it, functionsClass.readFileLine(it), SearchResultType.SearchFolders))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //Loading Widgets
            if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
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
                val widgetDataModels = widgetDataInterface.initDataAccessObject().getAllWidgetDataSuspend()

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

                                searchAdapterItems.add(AdapterItemsSearchEngine(
                                        newAppName,
                                        packageName,
                                        className,
                                        configClassName,
                                        widgetDataModel.WidgetLabel,
                                        appIcon,
                                        appWidgetProviderInfo,
                                        appWidgetId,
                                        SearchResultType.SearchWidgets
                                ))
                            } else {
                                widgetDataInterface.initDataAccessObject().deleteByWidgetClassNameProviderWidgetSuspend(packageName, className)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            val searchRecyclerViewAdapter = SearchEngineAdapter(applicationContext, searchAdapterItems)

            withContext(Dispatchers.Main) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

                if (searchAdapterItems.size > 0) {
                    setupSearchView(searchRecyclerViewAdapter)
                }
            }
        } else {
            searchAdapterItems = SearchEngineAdapter.allSearchData

            val searchRecyclerViewAdapter = SearchEngineAdapter(applicationContext, searchAdapterItems)

            withContext(Dispatchers.Main) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

                if (searchAdapterItems.size > 0) {
                    setupSearchView(searchRecyclerViewAdapter)
                }
            }
        }
    }

    private fun setupSearchView(searchRecyclerViewAdapter: SearchEngineAdapter) {
        if (loadFreq) {
            val layoutParamsAbove = hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.layoutParams as RelativeLayout.LayoutParams
            layoutParamsAbove.addRule(RelativeLayout.ABOVE, R.id.freqList)

            hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.layoutParams = layoutParamsAbove
            hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.bringToFront()

            hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.layoutParams = layoutParamsAbove
            hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.bringToFront()

            val layoutParamsAlignEnd = hybridApplicationViewBinding.searchEngineViewInclude.searchFloatIt.layoutParams as RelativeLayout.LayoutParams
            layoutParamsAlignEnd.addRule(RelativeLayout.END_OF, R.id.textInputSearchView)
            layoutParamsAlignEnd.addRule(RelativeLayout.ABOVE, R.id.freqList)

            hybridApplicationViewBinding.searchEngineViewInclude.searchFloatIt.layoutParams = layoutParamsAlignEnd
            hybridApplicationViewBinding.searchEngineViewInclude.searchFloatIt.bringToFront()

            val layoutParamsAlignStart = hybridApplicationViewBinding.searchEngineViewInclude.searchClose.layoutParams as RelativeLayout.LayoutParams
            layoutParamsAlignStart.addRule(RelativeLayout.START_OF, R.id.textInputSearchView)
            layoutParamsAlignStart.addRule(RelativeLayout.ABOVE, R.id.freqList)

            hybridApplicationViewBinding.searchEngineViewInclude.searchClose.layoutParams = layoutParamsAlignStart
            hybridApplicationViewBinding.searchEngineViewInclude.searchClose.bringToFront()
        }

        hybridApplicationViewBinding.searchEngineViewInclude.searchView.setAdapter(searchRecyclerViewAdapter)

        hybridApplicationViewBinding.searchEngineViewInclude.searchView.setDropDownBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        hybridApplicationViewBinding.searchEngineViewInclude.searchView.isVerticalScrollBarEnabled = false
        hybridApplicationViewBinding.searchEngineViewInclude.searchView.scrollBarSize = 0

        hybridApplicationViewBinding.searchEngineViewInclude.searchView.setTextColor(PublicVariable.colorLightDarkOpposite)
        hybridApplicationViewBinding.searchEngineViewInclude.searchView.compoundDrawableTintList = ColorStateList.valueOf(functionsClass.setColorAlpha(PublicVariable.colorLightDarkOpposite, 175f))

        val layerDrawableSearchIcon = getDrawable(R.drawable.search_icon) as RippleDrawable?
        val backgroundTemporarySearchIcon = layerDrawableSearchIcon!!.findDrawableByLayerId(R.id.backgroundTemporary)
        val frontTemporarySearchIcon = layerDrawableSearchIcon.findDrawableByLayerId(R.id.frontTemporary)
        val frontDrawableSearchIcon = layerDrawableSearchIcon.findDrawableByLayerId(R.id.frontDrawable)
        backgroundTemporarySearchIcon.setTint(PublicVariable.colorLightDarkOpposite)
        frontTemporarySearchIcon.setTint(PublicVariable.colorLightDark)
        frontDrawableSearchIcon.setTint(if (PublicVariable.themeLightDark) functionsClass.manipulateColor(PublicVariable.primaryColor, 0.90f) else functionsClass.manipulateColor(PublicVariable.primaryColor, 3.00f))

        layerDrawableSearchIcon.setLayerInset(2,
                functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13))

        hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.setImageDrawable(layerDrawableSearchIcon)
        hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
        hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.visibility = View.VISIBLE

        hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.hintTextColor = ColorStateList.valueOf(PublicVariable.primaryColorOpposite)
        hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.boxStrokeColor = PublicVariable.primaryColor

        var backgroundTemporaryInput = GradientDrawable()
        try {
            val layerDrawableBackgroundInput = getDrawable(R.drawable.background_search_input) as LayerDrawable?
            backgroundTemporaryInput = layerDrawableBackgroundInput!!.findDrawableByLayerId(R.id.backgroundTemporary) as GradientDrawable
            backgroundTemporaryInput.setTint(PublicVariable.colorLightDark)
            hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.background = layerDrawableBackgroundInput
        } catch (e: Exception) {
            e.printStackTrace()
            hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.background = null
        }

        val finalBackgroundTemporaryInput = backgroundTemporaryInput
        hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.setOnClickListener {
            val bundleSearchEngineUsed = Bundle()
            bundleSearchEngineUsed.putParcelable("USER_USED_SEARCH_ENGINE", firebaseAuth.currentUser)
            bundleSearchEngineUsed.putInt("TYPE_USED_SEARCH_ENGINE", SearchResultType.SearchFolders)

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
                            } catch (e: Exception) {
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

    private fun performSearchEngine(finalBackgroundTemporaryInput: GradientDrawable) = CoroutineScope(Dispatchers.Main).launch {
        delay(99)

        if (functionsClass.searchEngineSubscribed()) {
            hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
            hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.visibility = View.VISIBLE

            hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
            hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.visibility = View.INVISIBLE

            val valueAnimatorCornerDown = ValueAnimator.ofInt(functionsClass.DpToInteger(51), functionsClass.DpToInteger(7))
            valueAnimatorCornerDown.duration = 777
            valueAnimatorCornerDown.addUpdateListener { animator ->
                val animatorValue = animator.animatedValue as Int

                hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
            }
            valueAnimatorCornerDown.start()

            val valueAnimatorScalesUp = ValueAnimator.ofInt(functionsClass.DpToInteger(51), hybridApplicationViewBinding.switchWidgets.width)
            valueAnimatorScalesUp.duration = 777
            valueAnimatorScalesUp.addUpdateListener { animator ->
                val animatorValue = animator.animatedValue as Int
                hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.layoutParams.width = animatorValue
                hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.requestLayout()
            }
            valueAnimatorScalesUp.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    hybridApplicationViewBinding.searchEngineViewInclude.searchView.requestFocus()

                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(hybridApplicationViewBinding.searchEngineViewInclude.searchView, InputMethodManager.SHOW_IMPLICIT)

                    Handler().postDelayed({
                        hybridApplicationViewBinding.searchEngineViewInclude.searchFloatIt.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_up_bounce_interpolator))
                        hybridApplicationViewBinding.searchEngineViewInclude.searchFloatIt.visibility = View.VISIBLE

                        hybridApplicationViewBinding.searchEngineViewInclude.searchClose.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_up_bounce_interpolator))
                        hybridApplicationViewBinding.searchEngineViewInclude.searchClose.visibility = View.VISIBLE
                    }, 555)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            valueAnimatorScalesUp.start()

            hybridApplicationViewBinding.searchEngineViewInclude.searchFloatIt.setOnClickListener {
                if (hybridApplicationViewBinding.searchEngineViewInclude.searchView.text.toString().isNotEmpty() && SearchEngineAdapter.allSearchResults.size > 0 && hybridApplicationViewBinding.searchEngineViewInclude.searchView.text.toString().length >= 2) {
                    SearchEngineAdapter.allSearchResults.forEach { searchResultItem ->
                        when (searchResultItem.searchResultType) {
                            SearchResultType.SearchShortcuts -> {
                                functionsClassRunServices.runUnlimitedShortcutsService(searchResultItem.PackageName!!, searchResultItem.ClassName!!)
                            }
                            SearchResultType.SearchFolders -> {
                                functionsClass.runUnlimitedFolderService(searchResultItem.folderName)
                            }
                            SearchResultType.SearchWidgets -> {
                                functionsClass
                                        .runUnlimitedWidgetService(searchResultItem.appWidgetId!!,
                                                searchResultItem.widgetLabel)
                            }
                        }
                    }
                }
            }

            hybridApplicationViewBinding.searchEngineViewInclude.searchView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                }
                override fun afterTextChanged(s: Editable) {

                }
            })

            hybridApplicationViewBinding.searchEngineViewInclude.searchView.setOnEditorActionListener { textView, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (SearchEngineAdapter.allSearchResults.size == 1 && !hybridApplicationViewBinding.searchEngineViewInclude.searchView.text.toString().isEmpty() && hybridApplicationViewBinding.searchEngineViewInclude.searchView.text.toString().length >= 2) {
                        when (SearchEngineAdapter.allSearchResults[0].searchResultType) {
                            SearchResultType.SearchShortcuts -> {
                                functionsClassRunServices.runUnlimitedShortcutsService(SearchEngineAdapter.allSearchResults[0].PackageName!!, SearchEngineAdapter.allSearchResults[0].ClassName!!)
                            }
                            SearchResultType.SearchFolders -> {
                                functionsClass.runUnlimitedFolderService(SearchEngineAdapter.allSearchResults[0].folderName)
                            }
                            SearchResultType.SearchWidgets -> {
                                functionsClass
                                        .runUnlimitedWidgetService(SearchEngineAdapter.allSearchResults[0].appWidgetId!!,
                                                SearchEngineAdapter.allSearchResults[0].widgetLabel)
                            }
                        }

                        hybridApplicationViewBinding.searchEngineViewInclude.searchView.setText("")

                        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(hybridApplicationViewBinding.searchEngineViewInclude.searchView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                        val valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51))
                        valueAnimatorCornerUp.duration = 777
                        valueAnimatorCornerUp.addUpdateListener { animator ->
                            val animatorValue = animator.animatedValue as Int
                            hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                            finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
                        }
                        valueAnimatorCornerUp.start()

                        val valueAnimatorScales = ValueAnimator.ofInt(hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.width, functionsClass.DpToInteger(51))
                        valueAnimatorScales.duration = 777
                        valueAnimatorScales.addUpdateListener { animator ->
                            val animatorValue = animator.animatedValue as Int
                            hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.layoutParams.width = animatorValue
                            hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.requestLayout()
                        }
                        valueAnimatorScales.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {

                            }

                            override fun onAnimationEnd(animation: Animator) {
                                hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                                hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.visibility = View.INVISIBLE

                                hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                                hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.visibility = View.VISIBLE

                                hybridApplicationViewBinding.searchEngineViewInclude.searchFloatIt.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                                hybridApplicationViewBinding.searchEngineViewInclude.searchFloatIt.visibility = View.INVISIBLE

                                hybridApplicationViewBinding.searchEngineViewInclude.searchClose.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                                hybridApplicationViewBinding.searchEngineViewInclude.searchClose.visibility = View.INVISIBLE
                            }

                            override fun onAnimationCancel(animation: Animator) {

                            }

                            override fun onAnimationRepeat(animation: Animator) {

                            }
                        })
                        valueAnimatorScales.start()
                    } else {
                        if (SearchEngineAdapter.allSearchResults.size > 0 && hybridApplicationViewBinding.searchEngineViewInclude.searchView.text.toString().length >= 2) {
                            hybridApplicationViewBinding.searchEngineViewInclude.searchView.showDropDown()
                        }
                    }
                }

                false
            }

            hybridApplicationViewBinding.searchEngineViewInclude.searchClose.setOnClickListener {
                hybridApplicationViewBinding.searchEngineViewInclude.searchView.setText("")

                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(hybridApplicationViewBinding.searchEngineViewInclude.searchView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                val valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51))
                valueAnimatorCornerUp.duration = 777
                valueAnimatorCornerUp.addUpdateListener { animator ->
                    val animatorValue = animator.animatedValue as Int
                    hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                    finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
                }
                valueAnimatorCornerUp.start()

                val valueAnimatorScales = ValueAnimator.ofInt(hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.width, functionsClass.DpToInteger(51))
                valueAnimatorScales.duration = 777
                valueAnimatorScales.addUpdateListener { animator ->
                    val animatorValue = animator.animatedValue as Int
                    hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.layoutParams.width = animatorValue
                    hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.requestLayout()
                }
                valueAnimatorScales.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                        hybridApplicationViewBinding.searchEngineViewInclude.textInputSearchView.visibility = View.INVISIBLE

                        hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                        hybridApplicationViewBinding.searchEngineViewInclude.searchIcon.visibility = View.VISIBLE

                        hybridApplicationViewBinding.searchEngineViewInclude.searchFloatIt.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                        hybridApplicationViewBinding.searchEngineViewInclude.searchFloatIt.visibility = View.INVISIBLE

                        hybridApplicationViewBinding.searchEngineViewInclude.searchClose.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                        hybridApplicationViewBinding.searchEngineViewInclude.searchClose.visibility = View.INVISIBLE
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

    }
    /*Search Engine*/
}