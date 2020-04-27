/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 5:59 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts

import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.support.wearable.activity.WearableActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.geekstools.floatshort.PRO.Automation.RecoveryShortcuts
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.Folders.FloatingServices.FloatingFolders
import net.geekstools.floatshort.PRO.Preferences.PreferencesUI
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter.ApplicationsViewAdapter
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsApplications
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassIO
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.RemoteProcess.LicenseValidator
import net.geekstools.floatshort.PRO.databinding.ApplicationsViewWatchBinding
import java.util.*

class ApplicationsViewWatch : WearableActivity() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val functionsClassIO: FunctionsClassIO by lazy {
        FunctionsClassIO(applicationContext)
    }


    private lateinit var applicationsInformationList: List<ApplicationInfo>
    private val adapterItemsApplications: ArrayList<AdapterItemsApplications> = ArrayList<AdapterItemsApplications>()

    lateinit var applicationsViewAdapter: RecyclerView.Adapter<ApplicationsViewAdapter.ViewHolder>
    lateinit var recyclerViewLayoutManager: LinearLayoutManager


    var loadViewPosition: Int = 0

    lateinit var applicationsViewWatchBinding: ApplicationsViewWatchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationsViewWatchBinding = ApplicationsViewWatchBinding.inflate(layoutInflater)
        setContentView(applicationsViewWatchBinding.root)
        setAmbientEnabled()

        recyclerViewLayoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        applicationsViewWatchBinding.applicationsListView.layoutManager = recyclerViewLayoutManager

        initiateLoadingProcessAll()

        if (BuildConfig.VERSION_NAME.contains("[BETA]")
                && !functionsClass.readPreference(".UserInformation", "SubscribeToBeta", false)) {

            FirebaseMessaging.getInstance().subscribeToTopic("BETA").addOnSuccessListener {

                functionsClass.savePreference(".UserInformation", "SubscribeToBeta", true)

            }.addOnFailureListener {

            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (!getFileStreamPath(".License").exists()
                && functionsClass.networkConnection()) {

            val intentFilter = IntentFilter()
            intentFilter.addAction(getString(R.string.license))
            val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

                override fun onReceive(context: Context, intent: Intent) {

                    if (intent.action == getString(R.string.license)) {
                        functionsClass.dialogueLicense(this@ApplicationsViewWatch)

                        Handler().postDelayed({
                            stopService(Intent(applicationContext, LicenseValidator::class.java))
                        }, 1000)
                    }
                }
            }
            registerReceiver(broadcastReceiver, intentFilter)

            if (!BuildConfig.DEBUG) {

                startService(Intent(applicationContext, LicenseValidator::class.java))
            }
        }

        applicationsViewWatchBinding.settingGUI.setOnClickListener {

            startActivity(Intent(applicationContext, PreferencesUI::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_from_right, R.anim.slide_to_left).toBundle())
        }

        applicationsViewWatchBinding.floatingShortcutsRecovery.setOnClickListener {

            startService(Intent(applicationContext, RecoveryShortcuts::class.java))
        }

        applicationsViewWatchBinding.floatingShortcutsRecovery.setOnLongClickListener {
            functionsClass.doVibrate(70)

            if (!FloatingFolders.running) {
                functionsClassIO.readFileLines(".uFile")?.let {

                    if (it.isNotEmpty()) {
                        functionsClass.runUnlimitedCategoryService(getString(R.string.group), it)
                    }
                }
            }

            false
        }

        applicationsViewWatchBinding.applicationsListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                loadViewPosition = recyclerViewLayoutManager.findFirstVisibleItemPosition()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)

                .addOnCompleteListener(this@ApplicationsViewWatch, OnCompleteListener<Void?> { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {

                            if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(packageName)) {

                                Toast.makeText(applicationContext,
                                        getString(R.string.updateAvailable),
                                        Toast.LENGTH_LONG).show()

                                val layerDrawableNewUpdate = getDrawable(R.drawable.ic_update) as LayerDrawable?
                                val gradientDrawableNewUpdate = layerDrawableNewUpdate?.findDrawableByLayerId(R.id.ic_launcher_back_layer) as BitmapDrawable?
                                gradientDrawableNewUpdate?.setTint(PublicVariable.primaryColor)

                                applicationsViewWatchBinding.newUpdate.setImageDrawable(layerDrawableNewUpdate)
                                applicationsViewWatchBinding.newUpdate.visibility = View.VISIBLE
                            }
                        }
                    }
                })
    }

    override fun onPause() {
        super.onPause()

        functionsClass.savePreference("LoadView", "LoadViewPosition", recyclerViewLayoutManager.findFirstVisibleItemPosition())

        this@ApplicationsViewWatch.finish()
    }


    private fun initiateLoadingProcessAll() {
        adapterItemsApplications.clear()

        if (functionsClass.checkThemeLightDark()) {
            applicationsViewWatchBinding.root.setBackgroundColor(getColor(R.color.light_trans))
        } else {
            applicationsViewWatchBinding.root.setBackgroundColor(getColor(R.color.trans_black))
        }

        applicationsViewWatchBinding.loadingProgress.indeterminateDrawable.colorFilter = PorterDuffColorFilter(PublicVariable.primaryColor, PorterDuff.Mode.MULTIPLY)

        loadApplicationsData()
    }

    private fun loadApplicationsData() = CoroutineScope(Dispatchers.Default).launch {

        applicationsInformationList = applicationContext.packageManager.getInstalledApplications(0)
        val applicationInfoListSorted = applicationsInformationList.sortedWith(ApplicationInfo.DisplayNameComparator(packageManager))

        applicationInfoListSorted.asFlow()
                .onEach {

                }
                .filter {

                    (packageManager.getLaunchIntentForPackage(it.packageName) != null)
                }
                .map {

                    it
                }
                .onCompletion {

                    applicationsViewAdapter = ApplicationsViewAdapter(applicationContext, adapterItemsApplications)

                    withContext(SupervisorJob() + Dispatchers.Main) {
                        applicationsViewWatchBinding.applicationsListView.adapter = applicationsViewAdapter

                        val fadeOutAnimation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
                        applicationsViewWatchBinding.loadingSplash.startAnimation(fadeOutAnimation)
                        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {

                            override fun onAnimationStart(animation: Animation) {

                            }

                            override fun onAnimationEnd(animation: Animation) {
                                applicationsViewWatchBinding.loadingSplash.visibility = View.GONE
                            }

                            override fun onAnimationRepeat(animation: Animation) {

                            }
                        })
                    }
                }
                .withIndex().collect {
                    val packageName: String = it.value.packageName
                    val appName = functionsClass.appName(packageName)
                    val appIcon = functionsClass.shapedAppIcon(packageName)

                    adapterItemsApplications.add(
                            AdapterItemsApplications(appName,
                                    packageName,
                                    appIcon,
                                    functionsClass.extractDominantColor(appIcon))
                    )
                }
    }
}