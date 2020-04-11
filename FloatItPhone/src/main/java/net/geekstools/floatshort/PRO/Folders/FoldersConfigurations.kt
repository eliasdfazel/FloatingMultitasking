/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 7:35 PM
 * Last modified 3/26/20 7:25 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders

import android.animation.Animator
import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Automation.Folders.FolderAutoFeatures
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.FoldersListAdapter
import net.geekstools.floatshort.PRO.Preferences.PreferencesActivity
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchResultType
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.Utils.FunctionsClassSecurity
import net.geekstools.floatshort.PRO.Shortcuts.ApplicationsView
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
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
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.SwipeGestureListener
import net.geekstools.floatshort.PRO.Utils.UI.PopupDialogue.WaitingDialogue
import net.geekstools.floatshort.PRO.Utils.UI.PopupDialogue.WaitingDialogueLiveData
import net.geekstools.floatshort.PRO.Widgets.WidgetConfigurations
import net.geekstools.floatshort.PRO.databinding.FoldersConfigurationViewBinding
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.math.hypot
import kotlin.math.roundToInt

class FoldersConfigurations : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener, GestureListenerInterface {

    private val functionsClassDataActivity: FunctionsClassDataActivity by lazy {
        FunctionsClassDataActivity(this@FoldersConfigurations)
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

    private lateinit var foldersListAdapter: RecyclerView.Adapter<FoldersListAdapter.ViewHolder>
    private val folderAdapterItems: ArrayList<AdapterItems> = ArrayList<AdapterItems>()

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, functionsClass.customIconPackageName())
    }

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(applicationContext, this@FoldersConfigurations)
    }

    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var waitingDialogue: Dialog

    private lateinit var foldersConfigurationViewBinding: FoldersConfigurationViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foldersConfigurationViewBinding = FoldersConfigurationViewBinding.inflate(layoutInflater)
        setContentView(foldersConfigurationViewBinding.root)

        functionsClass.loadSavedColor()
        functionsClass.checkLightDarkTheme()

        functionsClass.setThemeColorFloating(this@FoldersConfigurations, foldersConfigurationViewBinding.wholeCategory, functionsClass.appThemeTransparent())
        functionsClassDialogues.changeLog()

        val recyclerViewLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        foldersConfigurationViewBinding.foldersList.layoutManager = recyclerViewLayoutManager

        val drawFloatingLogo = getDrawable(R.drawable.draw_floating_logo) as LayerDrawable?
        val backFloatingLogo = drawFloatingLogo?.findDrawableByLayerId(R.id.backgroundTemporary)
        backFloatingLogo?.setTint(PublicVariable.primaryColorOpposite)
        foldersConfigurationViewBinding.loadingLogo.setImageDrawable(drawFloatingLogo)

        loadFolders()

        val drawPreferenceAction = getDrawable(R.drawable.draw_pref_action) as LayerDrawable?
        val backgroundTemporary = drawPreferenceAction?.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary?.setTint(PublicVariable.primaryColorOpposite)
        foldersConfigurationViewBinding.actionButton.setImageDrawable(drawPreferenceAction)

        foldersConfigurationViewBinding.switchWidgets.setTextColor(getColor(R.color.light))
        foldersConfigurationViewBinding.switchApps.setTextColor(getColor(R.color.light))
        if (PublicVariable.themeLightDark /*light*/ && functionsClass.appThemeTransparent() /*transparent*/) {
            foldersConfigurationViewBinding.switchWidgets.setTextColor(getColor(R.color.dark))
            foldersConfigurationViewBinding.switchApps.setTextColor(getColor(R.color.dark))
        }

        foldersConfigurationViewBinding.switchApps.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        foldersConfigurationViewBinding.switchApps.rippleColor = ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        foldersConfigurationViewBinding.switchWidgets.setBackgroundColor(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        foldersConfigurationViewBinding.switchWidgets.rippleColor = ColorStateList.valueOf(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        foldersConfigurationViewBinding.recoveryAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        foldersConfigurationViewBinding.recoveryAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        foldersConfigurationViewBinding.automationAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        foldersConfigurationViewBinding.automationAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        val drawRecoverFloatingCategories = getDrawable(R.drawable.draw_recovery)?.mutate() as LayerDrawable?
        val backRecoverFloatingCategories = drawRecoverFloatingCategories?.findDrawableByLayerId(R.id.backgroundTemporary)?.mutate()
        backRecoverFloatingCategories?.setTint(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)

        val drawRecoverFloatingWidgets = getDrawable(R.drawable.draw_recovery_widgets)?.mutate() as LayerDrawable?
        val backRecoverFloatingWidgets = drawRecoverFloatingWidgets?.findDrawableByLayerId(R.id.backgroundTemporary)?.mutate()
        backRecoverFloatingWidgets?.setTint(if (functionsClass.appThemeTransparent()) functionsClass.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)

        foldersConfigurationViewBinding.recoverFloatingApps.setImageDrawable(drawRecoverFloatingCategories)
        foldersConfigurationViewBinding.recoverFloatingWidgets.setImageDrawable(drawRecoverFloatingWidgets)

        foldersConfigurationViewBinding.actionButton.setOnClickListener {
            functionsClass.doVibrate(33)

            if (!PublicVariable.actionCenter) {
                val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(foldersConfigurationViewBinding.recoveryAction,
                        foldersConfigurationViewBinding.actionButton.x.roundToInt(), foldersConfigurationViewBinding.actionButton.y.roundToInt(),
                        finalRadius.toFloat(), functionsClass.DpToInteger(13).toFloat())
                circularReveal.duration = 777
                circularReveal.interpolator = AccelerateInterpolator()
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        foldersConfigurationViewBinding.recoveryAction.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                functionsClass.openActionMenuOption(this@FoldersConfigurations, foldersConfigurationViewBinding.fullActionViews, foldersConfigurationViewBinding.actionButton, foldersConfigurationViewBinding.fullActionViews.isShown)
            } else {
                foldersConfigurationViewBinding.recoveryAction.visibility = View.VISIBLE

                val finalRadius = hypot(functionsClass.displayX().toDouble(), functionsClass.displayY().toDouble()).toInt()
                val circularReveal = ViewAnimationUtils.createCircularReveal(foldersConfigurationViewBinding.recoveryAction,
                        foldersConfigurationViewBinding.actionButton.x.roundToInt(), foldersConfigurationViewBinding.actionButton.y.roundToInt(),
                        functionsClass.DpToInteger(13).toFloat(), finalRadius.toFloat())
                circularReveal.duration = 1300
                circularReveal.interpolator = AccelerateInterpolator()
                circularReveal.start()
                circularReveal.addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        foldersConfigurationViewBinding.recoveryAction.visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                functionsClass.closeActionMenuOption(this@FoldersConfigurations, foldersConfigurationViewBinding.fullActionViews, foldersConfigurationViewBinding.actionButton)
            }
        }
        foldersConfigurationViewBinding.switchApps.setOnClickListener {

            functionsClass.navigateToClass(this@FoldersConfigurations, ApplicationsView::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
        }
        foldersConfigurationViewBinding.switchWidgets.setOnClickListener {
            if (functionsClass.networkConnection() && firebaseAuth.currentUser != null) {
                if (functionsClass.floatingWidgetsPurchased()) {
                    functionsClass.navigateToClass(this@FoldersConfigurations, WidgetConfigurations::class.java,
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
        foldersConfigurationViewBinding.automationAction.setOnClickListener {
            functionsClass.doVibrate(50)

            Intent(applicationContext, FolderAutoFeatures::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this,
                        ActivityOptions.makeCustomAnimation(applicationContext, R.anim.up_down, android.R.anim.fade_out).toBundle())
            }
        }
        foldersConfigurationViewBinding.recoveryAction.setOnClickListener {

            Intent(applicationContext, RecoveryFolders::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(this)
            }
        }
        foldersConfigurationViewBinding.recoverFloatingApps.setOnClickListener {
            Intent(applicationContext, RecoveryShortcuts::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(this)
            }

            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
            foldersConfigurationViewBinding.recoverFloatingApps.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    foldersConfigurationViewBinding.recoverFloatingApps.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }
        foldersConfigurationViewBinding.recoverFloatingWidgets.setOnClickListener {
            Intent(applicationContext, RecoveryWidgets::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startService(this)
            }

            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
            foldersConfigurationViewBinding.recoverFloatingWidgets.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    foldersConfigurationViewBinding.recoverFloatingWidgets.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }

        foldersConfigurationViewBinding.actionButton.setOnLongClickListener {

            val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@FoldersConfigurations, foldersConfigurationViewBinding.actionButton, "transition")

            Intent().apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.setClass(this@FoldersConfigurations, PreferencesActivity::class.java)
                startActivity(this, activityOptionsCompat.toBundle())
            }

            true
        }
        foldersConfigurationViewBinding.switchApps.setOnLongClickListener {

            if (!foldersConfigurationViewBinding.recoverFloatingApps.isShown) {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_show)
                foldersConfigurationViewBinding.recoverFloatingApps.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        foldersConfigurationViewBinding.recoverFloatingApps.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
                foldersConfigurationViewBinding.recoverFloatingApps.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        foldersConfigurationViewBinding.recoverFloatingApps.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }

            true
        }
        foldersConfigurationViewBinding.switchWidgets.setOnLongClickListener {

            if (!foldersConfigurationViewBinding.recoverFloatingWidgets.isShown) {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_show)
                foldersConfigurationViewBinding.recoverFloatingWidgets.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        foldersConfigurationViewBinding.recoverFloatingWidgets.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.recovery_actions_hide)
                foldersConfigurationViewBinding.recoverFloatingWidgets.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        foldersConfigurationViewBinding.recoverFloatingWidgets.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }
            true
        }

        //In-App Billing
        PurchasesCheckpoint(this@FoldersConfigurations).trigger()

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
                        functionsClass.dialogueLicense(this@FoldersConfigurations)

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

            ViewModelProvider(this@FoldersConfigurations).get(WaitingDialogueLiveData::class.java).run {
                this.dialogueTitle.value = getString(R.string.signinTitle)
                this.dialogueMessage.value = getString(R.string.signinMessage)

                waitingDialogue = WaitingDialogue().initShow(this@FoldersConfigurations)
            }
        }

        val drawableShare: LayerDrawable? = getDrawable(R.drawable.draw_share) as LayerDrawable
        val backgroundShare = drawableShare!!.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundShare.setTint(PublicVariable.primaryColor)

        foldersConfigurationViewBinding.shareIt.setImageDrawable(drawableShare)
        foldersConfigurationViewBinding.shareIt.setOnClickListener {
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
                                        this@FoldersConfigurations,
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

                    val googleSignInClient = GoogleSignIn.getClient(this@FoldersConfigurations, googleSignInOptions)
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
            functionsClass.closeActionMenuOption(this@FoldersConfigurations, foldersConfigurationViewBinding.fullActionViews, foldersConfigurationViewBinding.actionButton)
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

        functionsClass.CheckSystemRAM(this@FoldersConfigurations)
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

                        functionsClass.navigateToClass(this@FoldersConfigurations, ApplicationsView::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
                    }
                    GestureListenerConstants.SWIPE_LEFT -> {

                        if (functionsClass.networkConnection() && firebaseAuth.currentUser != null) {

                            if (functionsClass.floatingWidgetsPurchased()) {

                                functionsClass.navigateToClass(this@FoldersConfigurations, WidgetConfigurations::class.java,
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

                                    ViewModelProvider(this@FoldersConfigurations).get(WaitingDialogueLiveData::class.java).run {
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
            ViewModelProvider(this@FoldersConfigurations).get(WaitingDialogueLiveData::class.java).run {
                this.dialogueTitle.value = getString(R.string.error)
                this.dialogueMessage.value = Activity.RESULT_CANCELED.toString()
            }
        }
    }

    fun loadFolders() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
        if (functionsClass.appThemeTransparent()) {
            foldersConfigurationViewBinding.loadingSplash.setBackgroundColor(Color.TRANSPARENT)
        } else {
            foldersConfigurationViewBinding.loadingSplash.setBackgroundColor(window.navigationBarColor)
        }

        val typeface = Typeface.createFromAsset(assets, "upcil.ttf")
        foldersConfigurationViewBinding.loadingText.typeface = typeface

        if (PublicVariable.themeLightDark) {
            foldersConfigurationViewBinding.loadingProgress.indeterminateDrawable.setTint(PublicVariable.darkMutedColor)
            foldersConfigurationViewBinding.loadingText.setTextColor(getColor(R.color.dark))
        } else {
            foldersConfigurationViewBinding.loadingProgress.indeterminateDrawable.setTint(PublicVariable.vibrantColor)
            foldersConfigurationViewBinding.loadingText.setTextColor(getColor(R.color.light))
        }

        if (!getFileStreamPath(".categoryInfo").exists()) {
            val animation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            foldersConfigurationViewBinding.loadingSplash.visibility = View.INVISIBLE
            foldersConfigurationViewBinding.loadingSplash.startAnimation(animation)
        }

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons.load()
            FunctionsClassDebug.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.totalIconsNumber)
        }

        if (getFileStreamPath(".categoryInfo").exists()) {
            folderAdapterItems.clear()

            getFileStreamPath(".categoryInfo")
                    .readLines(Charset.defaultCharset()).asFlow()
                    .onCompletion {

                        loadInstalledCustomIcons()

                        /*Search Engine*/
                        SearchEngine(activity = this@FoldersConfigurations, context = applicationContext,
                                searchEngineViewBinding = foldersConfigurationViewBinding.searchEngineViewInclude,
                                functionsClass = functionsClass,
                                functionsClassRunServices = functionsClassRunServices,
                                functionsClassSecurity = functionsClassSecurity,
                                customIcons = loadCustomIcons,
                                firebaseAuth = firebaseAuth).apply {

                            this.initializeSearchEngineData()
                        }
                        /*Search Engine*/
                    }
                    .withIndex().collect { folderInformation ->

                        folderAdapterItems.add(AdapterItems(folderInformation.value,
                                functionsClass.readFileLine(folderInformation.value), SearchResultType.SearchFolders))
                    }

            folderAdapterItems.add(AdapterItems(packageName, arrayOf(packageName), SearchResultType.SearchFolders))
            foldersListAdapter = FoldersListAdapter(this@FoldersConfigurations, applicationContext, folderAdapterItems)

        } else {
            folderAdapterItems.clear()

            folderAdapterItems.add(AdapterItems(packageName, arrayOf(packageName), SearchResultType.SearchFolders))
            foldersListAdapter = FoldersListAdapter(this@FoldersConfigurations, applicationContext, folderAdapterItems)
        }

        foldersConfigurationViewBinding.foldersList.adapter = foldersListAdapter

        val animation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
        foldersConfigurationViewBinding.loadingSplash.visibility = View.INVISIBLE
        foldersConfigurationViewBinding.loadingSplash.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                foldersConfigurationViewBinding.foldersList.scrollTo(0, 0)

                foldersConfigurationViewBinding.switchFloating.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    private fun loadInstalledCustomIcons() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        val packageManager = applicationContext.packageManager
        //ACTION: com.novalauncher.THEME
        //CATEGORY: com.novalauncher.category.CUSTOM_ICON_PICKER
        val intentCustomIcons = Intent()
        intentCustomIcons.action = "com.novalauncher.THEME"
        intentCustomIcons.addCategory("com.novalauncher.category.CUSTOM_ICON_PICKER")
        val resolveInfos = packageManager.queryIntentActivities(intentCustomIcons, 0)

        PublicVariable.customIconsPackages.clear()
        for (resolveInfo in resolveInfos) {
            FunctionsClassDebug.PrintDebug("CustomIconPackages ::: " + resolveInfo.activityInfo.packageName)
            PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName)
        }
    }
}