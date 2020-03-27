/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 7:00 PM
 * Last modified 3/26/20 6:58 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders

import android.animation.Animator
import android.animation.ValueAnimator
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
import android.graphics.Typeface
import android.graphics.drawable.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
import net.geeksempire.primepuzzles.GameView.UI.SwipeGestureListener
import net.geekstools.floatshort.PRO.Automation.Folders.FolderAutoFeatures
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.FoldersListAdapter
import net.geekstools.floatshort.PRO.Preferences.PreferencesActivity
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.SearchEngineAdapter
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.PinPassword.HandlePinPassword
import net.geekstools.floatshort.PRO.Shortcuts.ApplicationsView
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsSearchEngine
import net.geekstools.floatshort.PRO.Utils.Functions.*
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
import net.geekstools.floatshort.PRO.Utils.UI.PopupDialogue.WaitingDialogue
import net.geekstools.floatshort.PRO.Utils.UI.PopupDialogue.WaitingDialogueLiveData
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations
import net.geekstools.floatshort.PRO.databinding.CategoryHandlerBinding
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.math.hypot
import kotlin.math.roundToInt

class FoldersConfigurationsXYZ : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener, GestureListenerInterface {

    private val functionsClassDataActivity: FunctionsClassDataActivity by lazy {
        FunctionsClassDataActivity(this@FoldersConfigurationsXYZ)
    }

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }
    private val functionsClassSecurity: FunctionsClassSecurity by lazy {
        FunctionsClassSecurity(applicationContext)
    }
    private val functionsClassDialogues: FunctionsClassDialogues by lazy {
        FunctionsClassDialogues(functionsClassDataActivity, functionsClass)
    }
    private val functionsClassRunServices: FunctionsClassRunServices by lazy {
        FunctionsClassRunServices(applicationContext)
    }

    /*Search Engine*/
    private lateinit var searchAdapterItems: ArrayList<AdapterItemsSearchEngine>
    /*Search Engine*/

    private lateinit var foldersListAdapter: RecyclerView.Adapter<FoldersListAdapter.ViewHolder>
    private val folderAdapterItems: ArrayList<AdapterItems> = ArrayList<AdapterItems>()

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(applicationContext, this@FoldersConfigurationsXYZ)
    }

    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var waitingDialogue: Dialog

    private lateinit var categoryHandlerBinding: CategoryHandlerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryHandlerBinding = CategoryHandlerBinding.inflate(layoutInflater)
        setContentView(categoryHandlerBinding.root)

        functionsClass.loadSavedColor()
        functionsClass.checkLightDarkTheme()

        functionsClass.setThemeColorFloating(this@FoldersConfigurationsXYZ, categoryHandlerBinding.wholeCategory, functionsClass.appThemeTransparent())
        functionsClassDialogues.changeLog()

        val recyclerViewLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        categoryHandlerBinding.foldersList.layoutManager = recyclerViewLayoutManager

        val drawFloatingLogo = getDrawable(R.drawable.draw_floating_logo) as LayerDrawable?
        val backFloatingLogo = drawFloatingLogo?.findDrawableByLayerId(R.id.backgroundTemporary)
        backFloatingLogo?.setTint(PublicVariable.primaryColorOpposite)
        categoryHandlerBinding.loadingLogo.setImageDrawable(drawFloatingLogo)

        loadFolders()

        val drawPreferenceAction = getDrawable(R.drawable.draw_pref_action) as LayerDrawable?
        val backgroundTemporary = drawPreferenceAction?.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary?.setTint(PublicVariable.primaryColorOpposite)
        categoryHandlerBinding.actionButton.setImageDrawable(drawPreferenceAction)

        categoryHandlerBinding.switchWidgets.setTextColor(getColor(R.color.light))
        categoryHandlerBinding.switchApps.setTextColor(getColor(R.color.light))
        if (PublicVariable.themeLightDark /*light*/ && functionsClass.appThemeTransparent() /*transparent*/) {
            categoryHandlerBinding.switchWidgets.setTextColor(getColor(R.color.dark))
            categoryHandlerBinding.switchApps.setTextColor(getColor(R.color.dark))
        }

        categoryHandlerBinding.switchApps.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        categoryHandlerBinding.switchApps.rippleColor = ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        categoryHandlerBinding.switchWidgets.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        categoryHandlerBinding.switchWidgets.rippleColor = ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        categoryHandlerBinding.recoveryAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        categoryHandlerBinding.recoveryAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        categoryHandlerBinding.automationAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        categoryHandlerBinding.automationAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        val drawRecoverFloatingCategories = getDrawable(R.drawable.draw_recovery)?.mutate() as LayerDrawable?
        val backRecoverFloatingCategories = drawRecoverFloatingCategories?.findDrawableByLayerId(R.id.backgroundTemporary)?.mutate()
        backRecoverFloatingCategories?.setTint(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)

        val drawRecoverFloatingWidgets = getDrawable(R.drawable.draw_recovery_widgets)?.mutate() as LayerDrawable?
        val backRecoverFloatingWidgets = drawRecoverFloatingWidgets?.findDrawableByLayerId(R.id.backgroundTemporary)?.mutate()
        backRecoverFloatingWidgets?.setTint(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)

        categoryHandlerBinding.recoverFloatingApps.setImageDrawable(drawRecoverFloatingCategories)
        categoryHandlerBinding.recoverFloatingWidgets.setImageDrawable(drawRecoverFloatingWidgets)

        categoryHandlerBinding.actionButton.setOnClickListener {
            functionsClass.doVibrate(33)

            if (!PublicVariable.actionCenter) {
                val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(categoryHandlerBinding.recoveryAction,
                        categoryHandlerBinding.actionButton.x.roundToInt(), categoryHandlerBinding.actionButton.y.roundToInt(),
                        finalRadius.toFloat(), functionsClass.DpToInteger(13).toFloat())
                circularReveal.duration = 777
                circularReveal.interpolator = AccelerateInterpolator()
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        categoryHandlerBinding.recoveryAction.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                functionsClass.openActionMenuOption(this@FoldersConfigurationsXYZ, categoryHandlerBinding.fullActionViews, categoryHandlerBinding.actionButton, categoryHandlerBinding.fullActionViews.isShown)
            } else {
                categoryHandlerBinding.recoveryAction.visibility = View.VISIBLE

                val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(categoryHandlerBinding.recoveryAction,
                        categoryHandlerBinding.actionButton.x.roundToInt(), categoryHandlerBinding.actionButton.y.roundToInt(),
                        functionsClass.DpToInteger(13).toFloat(), finalRadius.toFloat())
                circularReveal.duration = 1300
                circularReveal.interpolator = AccelerateInterpolator()
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        categoryHandlerBinding.recoveryAction.visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                functionsClass.closeActionMenuOption(this@FoldersConfigurationsXYZ, categoryHandlerBinding.fullActionViews, categoryHandlerBinding.actionButton)
            }
        }
        categoryHandlerBinding.switchApps.setOnClickListener {

            functionsClass.navigateToClass(this@FoldersConfigurationsXYZ, ApplicationsView::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
        }
        categoryHandlerBinding.switchWidgets.setOnClickListener {
            if (functionsClass.networkConnection() && firebaseAuth.currentUser != null) {
                if (functionsClass.floatingWidgetsPurchased()) {
                    functionsClass.navigateToClass(this@FoldersConfigurationsXYZ, WidgetConfigurations::class.java,
                            ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
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
        categoryHandlerBinding.automationAction.setOnClickListener {
            functionsClass.doVibrate(50)

            Intent(applicationContext, FolderAutoFeatures::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this,
                        ActivityOptions.makeCustomAnimation(applicationContext, R.anim.up_down, android.R.anim.fade_out).toBundle())
            }
        }
        categoryHandlerBinding.recoveryAction.setOnClickListener {

            Intent(applicationContext, RecoveryFolders::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(this)
            }
        }
        categoryHandlerBinding.recoverFloatingApps.setOnClickListener {
            Intent(applicationContext, RecoveryShortcuts::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(this)
            }

            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
            categoryHandlerBinding.recoverFloatingApps.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    categoryHandlerBinding.recoverFloatingApps.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }
        categoryHandlerBinding.recoverFloatingWidgets.setOnClickListener {
            Intent(applicationContext, RecoveryWidgets::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(this)
            }

            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
            categoryHandlerBinding.recoverFloatingWidgets.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    categoryHandlerBinding.recoverFloatingWidgets.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }

        categoryHandlerBinding.actionButton.setOnLongClickListener {

            val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@FoldersConfigurationsXYZ, categoryHandlerBinding.actionButton, "transition")

            Intent().apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.setClass(this@FoldersConfigurationsXYZ, PreferencesActivity::class.java)
                startActivity(this, activityOptionsCompat.toBundle())
            }

            true
        }
        categoryHandlerBinding.switchApps.setOnLongClickListener {

            if (!categoryHandlerBinding.recoverFloatingApps.isShown) {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_show)
                categoryHandlerBinding.recoverFloatingApps.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        categoryHandlerBinding.recoverFloatingApps.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
                categoryHandlerBinding.recoverFloatingApps.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        categoryHandlerBinding.recoverFloatingApps.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }

            true
        }
        categoryHandlerBinding.switchWidgets.setOnLongClickListener {

            if (!categoryHandlerBinding.recoverFloatingWidgets.isShown) {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_show)
                categoryHandlerBinding.recoverFloatingWidgets.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        categoryHandlerBinding.recoverFloatingWidgets.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
                categoryHandlerBinding.recoverFloatingWidgets.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        categoryHandlerBinding.recoverFloatingWidgets.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }
            true
        }

        //In-App Billing
        PurchasesCheckpoint(this@FoldersConfigurationsXYZ).trigger()

        if (BuildConfig.VERSION_NAME.contains("[BETA]")
                && !functionsClass.readPreference(".UserInformation", "SubscribeToBeta", false)) {
            FirebaseMessaging.getInstance().subscribeToTopic("BETA")
                    .addOnSuccessListener {
                        functionsClass.savePreference(".UserInformation", "SubscribeToBeta", true) }
                    .addOnFailureListener {

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
                        functionsClass.dialogueLicense(this@FoldersConfigurationsXYZ)

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

            ViewModelProvider(this@FoldersConfigurationsXYZ).get(WaitingDialogueLiveData::class.java).run {
                this.dialogueTitle.value = getString(R.string.signinTitle)
                this.dialogueMessage.value = getString(R.string.signinMessage)

                waitingDialogue = WaitingDialogue().initShow(this@FoldersConfigurationsXYZ)
            }
        }

        val drawableShare: LayerDrawable? = getDrawable(R.drawable.draw_share) as LayerDrawable
        val backgroundShare = drawableShare!!.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundShare.setTint(PublicVariable.primaryColor)

        categoryHandlerBinding.shareIt.setImageDrawable(drawableShare)
        categoryHandlerBinding.shareIt.setOnClickListener {
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

        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnSuccessListener {
                    firebaseRemoteConfig.activate().addOnSuccessListener {
                        if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(packageName)) {
                            val layerDrawableNewUpdate = getDrawable(R.drawable.ic_update) as LayerDrawable?
                            val gradientDrawableNewUpdate = layerDrawableNewUpdate!!.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable
                            gradientDrawableNewUpdate.setTint(PublicVariable.primaryColor)
                            val newUpdate = findViewById<View>(R.id.newUpdate) as ImageView
                            newUpdate.setImageDrawable(layerDrawableNewUpdate)
                            newUpdate.visibility = View.VISIBLE
                            newUpdate.setOnClickListener {
                                functionsClass.upcomingChangeLog(
                                        this@FoldersConfigurationsXYZ,
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

        functionsClass.addAppShortcuts()
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

                    val googleSignInClient = GoogleSignIn.getClient(this@FoldersConfigurationsXYZ, googleSignInOptions)
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

        if (PublicVariable.actionCenter) {
            functionsClass.closeActionMenuOption(this@FoldersConfigurationsXYZ, categoryHandlerBinding.fullActionViews, categoryHandlerBinding.actionButton)
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

        functionsClass.CheckSystemRAM(this@FoldersConfigurationsXYZ)
    }

    override fun onClick(view: View?) {

    }

    override fun onLongClick(view: View?): Boolean {

        return true
    }

    override fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {
        super.onSwipeGesture(gestureConstants, downMotionEvent, moveMotionEvent, initVelocityX, initVelocityY)

        when (gestureConstants) {
            is GestureConstants.SwipeHorizontal -> {
                when (gestureConstants.horizontalDirection) {
                    GestureListenerConstants.SWIPE_RIGHT -> {

                        functionsClass.navigateToClass(this@FoldersConfigurationsXYZ, ApplicationsView::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
                    }
                    GestureListenerConstants.SWIPE_LEFT -> {

                        if (functionsClass.floatingWidgetsPurchased()) {
                            functionsClass.navigateToClass(this@FoldersConfigurationsXYZ, WidgetConfigurations::class.java,
                                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
                        } else {
                            InAppBilling.ItemIAB = BillingManager.iapFloatingWidgets

                            functionsClass.navigateToClass(this@FoldersConfigurationsXYZ, InAppBilling::class.java,
                                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
                        }
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

                                    ViewModelProvider(this@FoldersConfigurationsXYZ).get(WaitingDialogueLiveData::class.java).run {
                                        this.dialogueTitle.value = getString(R.string.error)
                                        this.dialogueMessage.value = exception.message
                                    }
                                }.addOnSuccessListener {
                                    FunctionsClassDebug.PrintDebug("Firebase Activities Done Successfully")
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
            ViewModelProvider(this@FoldersConfigurationsXYZ).get(WaitingDialogueLiveData::class.java).run {
                this.dialogueTitle.value = getString(R.string.error)
                this.dialogueMessage.value = Activity.RESULT_CANCELED.toString()
            }
        }
    }

    fun loadFolders() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
        if (functionsClass.appThemeTransparent()) {
            categoryHandlerBinding.loadingSplash.setBackgroundColor(Color.TRANSPARENT)
        } else {
            categoryHandlerBinding.loadingSplash.setBackgroundColor(window.navigationBarColor)
        }

        val typeface = Typeface.createFromAsset(assets, "upcil.ttf")
        categoryHandlerBinding.loadingText.typeface = typeface

        if (PublicVariable.themeLightDark) {
            categoryHandlerBinding.loadingProgress.indeterminateDrawable.setTint(PublicVariable.darkMutedColor)
            categoryHandlerBinding.loadingText.setTextColor(getColor(R.color.dark))
        } else {
            categoryHandlerBinding.loadingProgress.indeterminateDrawable.setTint(PublicVariable.vibrantColor)
            categoryHandlerBinding.loadingText.setTextColor(getColor(R.color.light))
        }

        if (!getFileStreamPath(".categoryInfo").exists()) {
            val animation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            categoryHandlerBinding.loadingSplash.visibility = View.INVISIBLE
            categoryHandlerBinding.loadingSplash.startAnimation(animation)
        }

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons.load()
            FunctionsClassDebug.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.totalIconsNumber)
        }

        if (getFileStreamPath(".categoryInfo").exists()) {
            folderAdapterItems.clear()

            getFileStreamPath(".categoryInfo")
                    .readLines(Charset.defaultCharset()).asFlow()
                    .onCompletion {

                    }
                    .withIndex().collect { folderInformation ->

                        folderAdapterItems.add(AdapterItems(folderInformation.value,
                                functionsClass.readFileLine(folderInformation.value), SearchEngineAdapter.SearchResultType.SearchFolders))
                    }

            folderAdapterItems.add(AdapterItems(packageName, arrayOf(packageName), SearchEngineAdapter.SearchResultType.SearchFolders))
            foldersListAdapter = FoldersListAdapter(this@FoldersConfigurationsXYZ, applicationContext, folderAdapterItems)

            categoryHandlerBinding.foldersList.adapter = foldersListAdapter

            val animation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            categoryHandlerBinding.loadingSplash.visibility = View.INVISIBLE
            categoryHandlerBinding.loadingSplash.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    categoryHandlerBinding.foldersList.scrollTo(0, 0)

                    categoryHandlerBinding.switchFloating.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }

        loadInstalledCustomIcons()

        loadSearchEngineData().await()
    }

    private fun loadInstalledCustomIcons() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    }

    /*Search Engine*/
    private fun loadSearchEngineData() = CoroutineScope(SupervisorJob() + Dispatchers.Default).async {
        if (SearchEngineAdapter.allSearchResultItems.isEmpty()) {
            searchAdapterItems = ArrayList<AdapterItemsSearchEngine>()

            //Loading Applications
            val applicationInfoList = packageManager.queryIntentActivities(Intent().apply {
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
                            val installedPackageName = it.activityInfo.packageName
                            val installedClassName= it.activityInfo.name
                            val installedAppName = functionsClass.activityLabel(it.activityInfo)

                            val installedAppIcon = if (functionsClass.loadCustomIcons()) {
                                loadCustomIcons.getDrawableIconForPackage(installedPackageName, functionsClass.shapedAppIcon(it.activityInfo))
                            } else {
                                functionsClass.shapedAppIcon(it.activityInfo)
                            }

                            searchAdapterItems.add(AdapterItemsSearchEngine(installedAppName, installedPackageName, installedClassName, installedAppIcon, SearchEngineAdapter.SearchResultType.SearchShortcuts))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {

                        }
                    }

            //Loading Folders
            try {
                getFileStreamPath(".categoryInfo").readLines().forEach {
                    try {
                        searchAdapterItems.add(AdapterItemsSearchEngine(it, functionsClass.readFileLine(it), SearchEngineAdapter.SearchResultType.SearchFolders))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //Loading Widgets
            if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
                val appWidgetManager = AppWidgetManager.getInstance(this@FoldersConfigurationsXYZ)
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

                            FunctionsClassDebug.PrintDebug("*** $appWidgetId *** PackageName: $packageName - ClassName: $className - Configure: $configClassName ***")

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
            }

            val searchRecyclerViewAdapter = SearchEngineAdapter(applicationContext, searchAdapterItems)

            withContext(Dispatchers.Main) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

                if (searchAdapterItems.size > 0) {
                    setupSearchView(searchRecyclerViewAdapter)
                }
            }
        } else {
            searchAdapterItems = SearchEngineAdapter.allSearchResultItems

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
        categoryHandlerBinding.searchView.setAdapter(searchRecyclerViewAdapter)

        categoryHandlerBinding.searchView.setDropDownBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        categoryHandlerBinding.searchView.isVerticalScrollBarEnabled = false
        categoryHandlerBinding.searchView.scrollBarSize = 0

        categoryHandlerBinding.searchView.setTextColor(PublicVariable.colorLightDarkOpposite)
        categoryHandlerBinding.searchView.compoundDrawableTintList = ColorStateList.valueOf(functionsClass.setColorAlpha(PublicVariable.colorLightDarkOpposite, 175f))

        val layerDrawableSearchIcon = getDrawable(R.drawable.search_icon) as RippleDrawable?
        val backgroundTemporarySearchIcon = layerDrawableSearchIcon?.findDrawableByLayerId(R.id.backgroundTemporary)
        val frontTemporarySearchIcon = layerDrawableSearchIcon?.findDrawableByLayerId(R.id.frontTemporary)
        val frontDrawableSearchIcon = layerDrawableSearchIcon?.findDrawableByLayerId(R.id.frontDrawable)
        backgroundTemporarySearchIcon?.setTint(PublicVariable.colorLightDarkOpposite)
        frontTemporarySearchIcon?.setTint(PublicVariable.colorLightDark)
        frontDrawableSearchIcon?.setTint(if (PublicVariable.themeLightDark) functionsClass.manipulateColor(PublicVariable.primaryColor, 0.90f) else functionsClass.manipulateColor(PublicVariable.primaryColor, 3.00f))

        layerDrawableSearchIcon?.setLayerInset(2,
                functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13))

        categoryHandlerBinding.searchIcon.setImageDrawable(layerDrawableSearchIcon)
        categoryHandlerBinding.searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
        categoryHandlerBinding.searchIcon.visibility = View.VISIBLE

        categoryHandlerBinding.textInputSearchView.hintTextColor = ColorStateList.valueOf(PublicVariable.primaryColorOpposite)
        categoryHandlerBinding.textInputSearchView.boxStrokeColor = PublicVariable.primaryColor

        var backgroundTemporaryInput = GradientDrawable()
        try {
            val layerDrawableBackgroundInput = getDrawable(R.drawable.background_search_input) as LayerDrawable?
            backgroundTemporaryInput = layerDrawableBackgroundInput!!.findDrawableByLayerId(R.id.backgroundTemporary) as GradientDrawable
            backgroundTemporaryInput.setTint(PublicVariable.colorLightDark)
            categoryHandlerBinding.textInputSearchView.background = layerDrawableBackgroundInput
        } catch (e: Exception) {
            e.printStackTrace()
            categoryHandlerBinding.textInputSearchView.background = null
        }

        val finalBackgroundTemporaryInput = backgroundTemporaryInput
        categoryHandlerBinding.searchIcon.setOnClickListener {
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

    private fun performSearchEngine(finalBackgroundTemporaryInput: GradientDrawable)  = CoroutineScope(Dispatchers.Main).launch {
        delay(90)

        if (functionsClass.searchEngineSubscribed()) {
            categoryHandlerBinding.textInputSearchView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
            categoryHandlerBinding.textInputSearchView.visibility = View.VISIBLE

            categoryHandlerBinding.searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
            categoryHandlerBinding.searchIcon.visibility = View.INVISIBLE

            val valueAnimatorCornerDown = ValueAnimator.ofInt(functionsClass.DpToInteger(51), functionsClass.DpToInteger(7))
            valueAnimatorCornerDown.duration = 777
            valueAnimatorCornerDown.addUpdateListener { animator ->
                val animatorValue = animator.animatedValue as Int

                categoryHandlerBinding.textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
            }
            valueAnimatorCornerDown.start()

            val valueAnimatorScalesUp = ValueAnimator.ofInt(functionsClass.DpToInteger(51), categoryHandlerBinding.switchApps.width)
            valueAnimatorScalesUp.duration = 777
            valueAnimatorScalesUp.addUpdateListener { animator ->
                val animatorValue = animator.animatedValue as Int
                categoryHandlerBinding.textInputSearchView.layoutParams.width = animatorValue
                categoryHandlerBinding.textInputSearchView.requestLayout()
            }
            valueAnimatorScalesUp.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    categoryHandlerBinding.searchView.requestFocus()

                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(categoryHandlerBinding.searchView, InputMethodManager.SHOW_IMPLICIT)
                    Handler().postDelayed({
                        categoryHandlerBinding.searchFloatIt.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_up_bounce_interpolator))
                        categoryHandlerBinding.searchFloatIt.visibility = View.VISIBLE
                        categoryHandlerBinding.searchClose.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_up_bounce_interpolator))
                        categoryHandlerBinding.searchClose.visibility = View.VISIBLE
                    }, 555)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            valueAnimatorScalesUp.start()

            categoryHandlerBinding.searchFloatIt.setOnClickListener {
                if (!categoryHandlerBinding.searchView.text.toString().isEmpty() && SearchEngineAdapter.allSearchResultItems.size > 0
                        && categoryHandlerBinding.searchView.text.toString().length >= 2) {
                    SearchEngineAdapter.allSearchResultItems.forEach { searchResultItem ->
                        when (searchResultItem.searchResultType) {
                            SearchEngineAdapter.SearchResultType.SearchShortcuts -> {
                                functionsClassRunServices.runUnlimitedShortcutsService(searchResultItem.PackageName!!, searchResultItem.ClassName!!)
                            }
                            SearchEngineAdapter.SearchResultType.SearchFolders -> {
                                functionsClass.runUnlimitedFolderService(searchResultItem.folderName)
                            }
                            SearchEngineAdapter.SearchResultType.SearchWidgets -> {
                                functionsClass
                                        .runUnlimitedWidgetService(searchResultItem.appWidgetId!!,
                                                searchResultItem.widgetLabel)
                            }
                        }
                    }
                }
            }

            categoryHandlerBinding.searchView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                }
                override fun afterTextChanged(s: Editable) {

                }
            })

            categoryHandlerBinding.searchView.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (SearchEngineAdapter.allSearchResultItems.size == 1
                                && !categoryHandlerBinding.searchView.text.toString().isEmpty()
                                && categoryHandlerBinding.searchView.text.toString().length >= 2) {
                            when (SearchEngineAdapter.allSearchResultItems[0].searchResultType) {
                                SearchEngineAdapter.SearchResultType.SearchShortcuts -> {
                                    functionsClassRunServices.runUnlimitedShortcutsService(SearchEngineAdapter.allSearchResultItems[0].PackageName!!, SearchEngineAdapter.allSearchResultItems[0].ClassName!!)
                                }
                                SearchEngineAdapter.SearchResultType.SearchFolders -> {
                                    functionsClass.runUnlimitedFolderService(SearchEngineAdapter.allSearchResultItems[0].folderName)
                                }
                                SearchEngineAdapter.SearchResultType.SearchWidgets -> {
                                    functionsClass
                                            .runUnlimitedWidgetService(SearchEngineAdapter.allSearchResultItems[0].appWidgetId!!,
                                                    SearchEngineAdapter.allSearchResultItems[0].widgetLabel)
                                }
                            }

                            categoryHandlerBinding.searchView.setText("")

                            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(categoryHandlerBinding.searchView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                            val valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51))
                            valueAnimatorCornerUp.duration = 777
                            valueAnimatorCornerUp.addUpdateListener { animator ->
                                val animatorValue = animator.animatedValue as Int
                                categoryHandlerBinding.textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                                finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
                            }
                            valueAnimatorCornerUp.start()

                            val valueAnimatorScales = ValueAnimator.ofInt(categoryHandlerBinding.textInputSearchView.width, functionsClass.DpToInteger(51))
                            valueAnimatorScales.duration = 777
                            valueAnimatorScales.addUpdateListener { animator ->
                                val animatorValue = animator.animatedValue as Int
                                categoryHandlerBinding.textInputSearchView.layoutParams.width = animatorValue
                                categoryHandlerBinding.textInputSearchView.requestLayout()
                            }
                            valueAnimatorScales.addListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {

                                }

                                override fun onAnimationEnd(animation: Animator) {
                                    categoryHandlerBinding.textInputSearchView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                                    categoryHandlerBinding.textInputSearchView.visibility = View.INVISIBLE

                                    categoryHandlerBinding.searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                                    categoryHandlerBinding.searchIcon.visibility = View.VISIBLE

                                    categoryHandlerBinding.searchFloatIt.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                                    categoryHandlerBinding.searchFloatIt.visibility = View.INVISIBLE

                                    categoryHandlerBinding.searchClose.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                                    categoryHandlerBinding.searchClose.visibility = View.INVISIBLE
                                }

                                override fun onAnimationCancel(animation: Animator) {

                                }

                                override fun onAnimationRepeat(animation: Animator) {

                                }
                            })
                            valueAnimatorScales.start()
                        } else {
                            if (SearchEngineAdapter.allSearchResultItems.size > 0
                                    && categoryHandlerBinding.searchView.text.toString().length >= 2) {
                                categoryHandlerBinding.searchView.showDropDown()
                            }
                        }
                    }

                    return false
                }
            })

            categoryHandlerBinding.searchClose.setOnClickListener {
                categoryHandlerBinding.searchView.setText("")

                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(categoryHandlerBinding.searchView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                val valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51))
                valueAnimatorCornerUp.duration = 777
                valueAnimatorCornerUp.addUpdateListener { animator ->
                    val animatorValue = animator.animatedValue as Int
                    categoryHandlerBinding.textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                    finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
                }
                valueAnimatorCornerUp.start()

                val valueAnimatorScales = ValueAnimator.ofInt(categoryHandlerBinding.textInputSearchView.width, functionsClass.DpToInteger(51))
                valueAnimatorScales.duration = 777
                valueAnimatorScales.addUpdateListener { animator ->
                    val animatorValue = animator.animatedValue as Int
                    categoryHandlerBinding.textInputSearchView.layoutParams.width = animatorValue
                    categoryHandlerBinding.textInputSearchView.requestLayout()
                }
                valueAnimatorScales.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        categoryHandlerBinding.textInputSearchView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                        categoryHandlerBinding.textInputSearchView.visibility = View.INVISIBLE

                        categoryHandlerBinding.searchIcon.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                        categoryHandlerBinding.searchIcon.visibility = View.VISIBLE

                        categoryHandlerBinding.searchFloatIt.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                        categoryHandlerBinding.searchFloatIt.visibility = View.INVISIBLE

                        categoryHandlerBinding.searchClose.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down_zero))
                        categoryHandlerBinding.searchClose.visibility = View.INVISIBLE
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