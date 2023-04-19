/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 12/6/20 7:16 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders

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
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener
import com.google.firebase.inappmessaging.model.Action
import com.google.firebase.inappmessaging.model.InAppMessage
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.BindServices
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.FoldersListAdapter
import net.geekstools.floatshort.PRO.Folders.FoldersApplicationsSelectionProcess.AppSelectionList
import net.geekstools.floatshort.PRO.Preferences.PreferencesActivity
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchResultType
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.Shortcuts.ApplicationsViewPhone
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.Debug
import net.geekstools.floatshort.PRO.Utils.Functions.Dialogues
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Utils.PurchasesCheckpoint
import net.geekstools.floatshort.PRO.Utils.InAppUpdate.InAppUpdateProcess
import net.geekstools.floatshort.PRO.Utils.RemoteProcess.CloudMessageHandler
import net.geekstools.floatshort.PRO.Utils.RemoteProcess.LicenseValidator
import net.geekstools.floatshort.PRO.Utils.RemoteTask.Create.RecoveryShortcuts
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.SwipeGestureListener
import net.geekstools.floatshort.PRO.Utils.UI.PopupDialogue.WaitingDialogue
import net.geekstools.floatshort.PRO.Utils.UI.PopupDialogue.WaitingDialogueLiveData
import net.geekstools.floatshort.PRO.Widgets.WidgetConfigurations
import net.geekstools.floatshort.PRO.databinding.FoldersConfigurationViewBinding
import java.nio.charset.Charset
import java.util.Calendar

class FoldersConfigurations : AppCompatActivity(),
        View.OnClickListener, View.OnLongClickListener,
        GestureListenerInterface,
        FirebaseInAppMessagingClickListener {

    private val foldersConfigurationsDependencyInjection: FoldersConfigurationsDependencyInjection by lazy {
        FoldersConfigurationsDependencyInjection(applicationContext)
    }

    private val dialogues: Dialogues by lazy {
        Dialogues(this@FoldersConfigurations, foldersConfigurationsDependencyInjection.functionsClassLegacy)
    }

    private lateinit var foldersListAdapter: RecyclerView.Adapter<FoldersListAdapter.ViewHolder>
    private val folderAdapterItems: ArrayList<AdapterItems> = ArrayList<AdapterItems>()

    private val loadCustomIcons: LoadCustomIcons by lazy {
        LoadCustomIcons(applicationContext, foldersConfigurationsDependencyInjection.functionsClassLegacy.customIconPackageName())
    }

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(applicationContext, this@FoldersConfigurations)
    }

    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var waitingDialogue: Dialog

    private lateinit var waitingDialogueLiveData: WaitingDialogueLiveData

    private var billingClient: BillingClient? = null

    private lateinit var foldersConfigurationViewBinding: FoldersConfigurationViewBinding

    private object Google {
        const val SignInRequest: Int = 666
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foldersConfigurationViewBinding = FoldersConfigurationViewBinding.inflate(layoutInflater)
        setContentView(foldersConfigurationViewBinding.root)

        foldersConfigurationsDependencyInjection.functionsClassLegacy.loadSavedColor()
        foldersConfigurationsDependencyInjection.functionsClassLegacy.checkLightDarkTheme()

        foldersConfigurationsDependencyInjection.applicationThemeController.setThemeColorFloating(this@FoldersConfigurations, foldersConfigurationViewBinding.wholeCategory, foldersConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent())
        dialogues.changeLog()

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
        if (PublicVariable.themeLightDark /*light*/ && foldersConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent() /*transparent*/) {
            foldersConfigurationViewBinding.switchWidgets.setTextColor(getColor(R.color.dark))
            foldersConfigurationViewBinding.switchApps.setTextColor(getColor(R.color.dark))
        }

        foldersConfigurationViewBinding.switchApps.setBackgroundColor(if (foldersConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) foldersConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        foldersConfigurationViewBinding.switchApps.rippleColor = ColorStateList.valueOf(if (foldersConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) foldersConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        foldersConfigurationViewBinding.switchWidgets.setBackgroundColor(if (foldersConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) foldersConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(PublicVariable.primaryColor, 51f) else PublicVariable.primaryColor)
        foldersConfigurationViewBinding.switchWidgets.rippleColor = ColorStateList.valueOf(if (foldersConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) foldersConfigurationsDependencyInjection.functionsClassLegacy.setColorAlpha(PublicVariable.primaryColorOpposite, 51f) else PublicVariable.primaryColorOpposite)

        foldersConfigurationViewBinding.recoveryAction.setBackgroundColor(PublicVariable.primaryColorOpposite)
        foldersConfigurationViewBinding.recoveryAction.rippleColor = ColorStateList.valueOf(PublicVariable.primaryColor)

        foldersConfigurationViewBinding.actionButton.setOnClickListener { preferencesView ->
            foldersConfigurationsDependencyInjection.functionsClassLegacy.doVibrate(33)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@FoldersConfigurations,
                preferencesView,
                "transition"
            )

            val intent = Intent(this@FoldersConfigurations, PreferencesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent, options.toBundle())

        }
        foldersConfigurationViewBinding.switchApps.setOnClickListener {

            foldersConfigurationsDependencyInjection.functionsClassLegacy.navigateToClass(this@FoldersConfigurations, ApplicationsViewPhone::class.java,
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
        }
        foldersConfigurationViewBinding.switchWidgets.setOnClickListener {
            if (foldersConfigurationsDependencyInjection.networkCheckpoint.networkConnection() && firebaseAuth.currentUser != null) {
                if (foldersConfigurationsDependencyInjection.functionsClassLegacy.floatingWidgetsPurchased()) {
                    foldersConfigurationsDependencyInjection.functionsClassLegacy.navigateToClass(this@FoldersConfigurations, WidgetConfigurations::class.java,
                            ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left))
                } else {

                    startActivity(Intent(applicationContext, InitializeInAppBilling::class.java).apply {
                        putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.OneTimePurchase)
                        putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemFloatingWidgets)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.down_up, android.R.anim.fade_out).toBundle())

                }
            } else {
                if (foldersConfigurationsDependencyInjection.networkCheckpoint.networkConnection()) {
                    Toast.makeText(applicationContext, getString(R.string.internetError), Toast.LENGTH_LONG).show()
                }
                if (firebaseAuth.currentUser == null) {
                    Toast.makeText(applicationContext, getString(R.string.authError), Toast.LENGTH_LONG).show()
                }
            }
        }

        foldersConfigurationViewBinding.recoveryAction.setOnClickListener {

            Intent(applicationContext, RecoveryShortcuts::class.java).let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(it)
                } else {
                    startService(it)
                }
            }

            if (PublicVariable.allFloatingCounter == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(Intent(applicationContext, BindServices::class.java))
                } else {
                    startService(Intent(applicationContext, BindServices::class.java))
                }
            }

        }

        foldersConfigurationViewBinding.actionButton.setOnLongClickListener {

            Handler(Looper.getMainLooper()).postDelayed({

                startActivity(Intent(this@FoldersConfigurations, InitializeInAppBilling::class.java)
                    .putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.OneTimePurchase)
                    .putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemDonation)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    ActivityOptions.makeCustomAnimation(this@FoldersConfigurations, R.anim.down_up, android.R.anim.fade_out).toBundle())

            }, 113)

            true
        }
        foldersConfigurationViewBinding.switchApps.setOnLongClickListener {

            true
        }
        foldersConfigurationViewBinding.switchWidgets.setOnLongClickListener {

            true
        }

        foldersConfigurationViewBinding.addNewFolder.setOnClickListener {

            PublicVariable.folderName = "FloatingFolder"

            startActivity(Intent(this@FoldersConfigurations, AppSelectionList::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

        }

        //In-App Billing
        billingClient = PurchasesCheckpoint(this@FoldersConfigurations).trigger()

        if (BuildConfig.VERSION_NAME.contains("[BETA]")
                && !foldersConfigurationsDependencyInjection.preferencesIO.readPreference(".UserInformation", "SubscribeToBeta", false)) {
            FirebaseMessaging.getInstance().subscribeToTopic("BETA")
                    .addOnSuccessListener {
                        foldersConfigurationsDependencyInjection.preferencesIO.savePreference(".UserInformation", "SubscribeToBeta", true) }
                    .addOnFailureListener {

                    }
        }
    }

    override fun onStart() {
        super.onStart()

        if (!getFileStreamPath(".License").exists() && foldersConfigurationsDependencyInjection.networkCheckpoint.networkConnection()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(applicationContext, LicenseValidator::class.java))
            } else {
                startService(Intent(applicationContext, LicenseValidator::class.java))
            }

            val intentFilter = IntentFilter()
            intentFilter.addAction(getString(R.string.license))
            val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == getString(R.string.license)) {
                        foldersConfigurationsDependencyInjection.functionsClassLegacy.dialogueLicense(this@FoldersConfigurations)

                        Handler(Looper.getMainLooper()).postDelayed({
                            stopService(Intent(applicationContext, LicenseValidator::class.java))
                        }, 1000)

                        unregisterReceiver(this)
                    }
                }
            }
            registerReceiver(broadcastReceiver, intentFilter)
        }

        if (foldersConfigurationsDependencyInjection.networkCheckpoint.networkConnection()
                && foldersConfigurationsDependencyInjection.preferencesIO.readPreference(".UserInformation", "userEmail", null) == null) {

            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.webClientId))
                    .requestEmail()
                    .build()

            val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
            googleSignInClient.signInIntent.run {
                startActivityForResult(this, Google.SignInRequest)
            }

            waitingDialogueLiveData = ViewModelProvider(this@FoldersConfigurations).get(WaitingDialogueLiveData::class.java)
            waitingDialogueLiveData.run {
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

            Firebase.analytics.logEvent("ShareIt", Bundle().apply {
                putString("Email Address", Firebase.auth.currentUser?.email)
            })

            foldersConfigurationsDependencyInjection.functionsClassLegacy.doVibrate(50)

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

                        if (firebaseRemoteConfig.getLong(foldersConfigurationsDependencyInjection.functionsClassLegacy.versionCodeRemoteConfigKey()) > foldersConfigurationsDependencyInjection.functionsClassLegacy.applicationVersionCode(packageName)) {

                            val layerDrawableNewUpdate = getDrawable(R.drawable.ic_update) as LayerDrawable?
                            val gradientDrawableNewUpdate = layerDrawableNewUpdate?.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable?
                            gradientDrawableNewUpdate?.setTint(PublicVariable.primaryColor)

                            foldersConfigurationViewBinding.newUpdate.setImageDrawable(layerDrawableNewUpdate)
                            foldersConfigurationViewBinding.newUpdate.visibility = View.VISIBLE

                            foldersConfigurationViewBinding.newUpdate.setOnClickListener {
                                foldersConfigurationsDependencyInjection.functionsClassLegacy.upcomingChangeLog(
                                        this@FoldersConfigurations,
                                        firebaseRemoteConfig.getString(foldersConfigurationsDependencyInjection.functionsClassLegacy.upcomingChangeLogRemoteConfigKey()), firebaseRemoteConfig.getLong(foldersConfigurationsDependencyInjection.functionsClassLegacy.versionCodeRemoteConfigKey()).toString())
                            }

                            foldersConfigurationsDependencyInjection.functionsClassLegacy.notificationCreator(
                                    getString(R.string.updateAvailable),
                                    firebaseRemoteConfig.getString(foldersConfigurationsDependencyInjection.functionsClassLegacy.upcomingChangeLogSummaryConfigKey()),
                                    firebaseRemoteConfig.getLong(foldersConfigurationsDependencyInjection.functionsClassLegacy.versionCodeRemoteConfigKey()).toInt()
                            )

                            val inAppUpdateTriggeredTime =
                                    (Calendar.getInstance()[Calendar.YEAR].toString() + Calendar.getInstance()[Calendar.MONTH].toString() + Calendar.getInstance()[Calendar.DATE].toString())
                                            .toInt()

                            if (firebaseAuth.currentUser != null
                                    && foldersConfigurationsDependencyInjection.preferencesIO.readPreference("InAppUpdate", "TriggeredDate", 0) < inAppUpdateTriggeredTime) {

                                startActivity(Intent(applicationContext, InAppUpdateProcess::class.java)
                                        .putExtra("UPDATE_CHANGE_LOG", firebaseRemoteConfig.getString(foldersConfigurationsDependencyInjection.functionsClassLegacy.upcomingChangeLogRemoteConfigKey()))
                                        .putExtra("UPDATE_VERSION", firebaseRemoteConfig.getLong(foldersConfigurationsDependencyInjection.functionsClassLegacy.versionCodeRemoteConfigKey()).toString())
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                        ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
                            }
                        }
                    }
                }

        foldersConfigurationsDependencyInjection.popupApplicationShortcuts.addPopupApplicationShortcuts()
    }

    override fun onPause() {
        super.onPause()

        if (PublicVariable.actionCenter) {
            foldersConfigurationsDependencyInjection.functionsClassLegacy.closeActionMenuOption(this@FoldersConfigurations, foldersConfigurationViewBinding.fullActionViews, foldersConfigurationViewBinding.actionButton)
        }

        foldersConfigurationsDependencyInjection.preferencesIO.savePreference("OpenMode", "openClassName", this.javaClass.simpleName)

        billingClient?.endConnection()

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

        foldersConfigurationsDependencyInjection.functionsClassLegacy.CheckSystemRAM(this@FoldersConfigurations)
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

                        foldersConfigurationsDependencyInjection.functionsClassLegacy.navigateToClass(this@FoldersConfigurations, ApplicationsViewPhone::class.java,
                                ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))
                    }
                    GestureListenerConstants.SWIPE_LEFT -> {

                        if (foldersConfigurationsDependencyInjection.networkCheckpoint.networkConnection() && firebaseAuth.currentUser != null) {

                            if (foldersConfigurationsDependencyInjection.functionsClassLegacy.floatingWidgetsPurchased()) {

                                foldersConfigurationsDependencyInjection.functionsClassLegacy.navigateToClass(this@FoldersConfigurations, WidgetConfigurations::class.java,
                                        ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_left, R.anim.slide_to_right))

                            } else {

                                startActivity(Intent(applicationContext, InitializeInAppBilling::class.java).apply {
                                    putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.OneTimePurchase)
                                    putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemFloatingWidgets)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.down_up, android.R.anim.fade_out).toBundle())

                            }
                        } else {
                            if (foldersConfigurationsDependencyInjection.networkCheckpoint.networkConnection()) {
                                Toast.makeText(applicationContext, getString(R.string.internetError), Toast.LENGTH_LONG).show()
                            }

                            if (firebaseAuth.currentUser == null) {
                                Toast.makeText(applicationContext, getString(R.string.authError), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        motionEvent?.let {
            swipeGestureListener.onTouchEvent(it)
        }

        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Google.SignInRequest -> {

                if (resultCode == Activity.RESULT_OK) {

                    data?.let {

                        GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener { googleSignInAccountTask ->

                            val authCredential = GoogleAuthProvider.getCredential(googleSignInAccountTask?.idToken, null)
                            firebaseAuth.signInWithCredential(authCredential)
                                .addOnSuccessListener {
                                    val firebaseUser = firebaseAuth.currentUser
                                    if (firebaseUser != null) {
                                        Debug.PrintDebug("Firebase Activities Done Successfully")

                                        foldersConfigurationsDependencyInjection.preferencesIO.savePreference(".UserInformation", "userEmail", firebaseUser.email)

                                        foldersConfigurationsDependencyInjection.functionsClassLegacy.Toast(getString(R.string.signinFinished), Gravity.TOP)

                                        foldersConfigurationsDependencyInjection.securityFunctions.downloadLockedAppsData()

                                        waitingDialogue.dismiss()
                                    }
                                }.addOnFailureListener { exception ->

                                    waitingDialogueLiveData.run {
                                        this.dialogueTitle.value = getString(R.string.error)
                                        this.dialogueMessage.value = exception.message
                                    }
                                }

                        }

                    }

                } else {

                    waitingDialogueLiveData.run {
                        this.dialogueTitle.value = getString(R.string.error)
                        this.dialogueMessage.value = Activity.RESULT_CANCELED.toString()
                    }

                }

            }
        }

    }

    override fun messageClicked(inAppMessage: InAppMessage, action: Action) {

        CloudMessageHandler()
                .extractData(inAppMessage, action)
    }

    fun loadFolders() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

        if (foldersConfigurationsDependencyInjection.functionsClassLegacy.appThemeTransparent()) {
            foldersConfigurationViewBinding.loadingSplash.setBackgroundColor(Color.TRANSPARENT)
        } else {
            foldersConfigurationViewBinding.loadingSplash.setBackgroundColor(window.navigationBarColor)
        }

        val typeface = Typeface.createFromAsset(assets, "ubuntu.ttf")
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

        if (foldersConfigurationsDependencyInjection.functionsClassLegacy.customIconsEnable()) {
            loadCustomIcons.load()
            Debug.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.totalIconsNumber)
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
                                functionsClassLegacy = foldersConfigurationsDependencyInjection.functionsClassLegacy,
                                fileIO = foldersConfigurationsDependencyInjection.fileIO,
                                floatingServices = foldersConfigurationsDependencyInjection.floatingServices,
                                customIcons = loadCustomIcons,
                                firebaseAuth = firebaseAuth).apply {

                            this.initializeSearchEngineData()
                        }
                        /*Search Engine*/
                    }
                    .withIndex().collect { folderInformation ->

                        folderAdapterItems.add(AdapterItems(folderInformation.value,
                                foldersConfigurationsDependencyInjection.fileIO.readFileLinesAsArray(folderInformation.value), SearchResultType.SearchFolders))

                    }

            foldersListAdapter = FoldersListAdapter(this@FoldersConfigurations, applicationContext, folderAdapterItems)

        } else {

            folderAdapterItems.clear()

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
            Debug.PrintDebug("CustomIconPackages ::: " + resolveInfo.activityInfo.packageName)
            PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName)
        }
    }
}