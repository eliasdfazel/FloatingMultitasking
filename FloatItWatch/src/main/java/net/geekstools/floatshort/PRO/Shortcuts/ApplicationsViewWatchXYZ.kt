/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/26/20 8:51 AM
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
import android.os.Bundle
import android.os.Handler
import android.support.wearable.activity.WearableActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Automation.RecoveryShortcuts
import net.geekstools.floatshort.PRO.BuildConfig
import net.geekstools.floatshort.PRO.Folders.FloatingServices.FloatingFolders
import net.geekstools.floatshort.PRO.Preferences.PreferencesUI
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter.ApplicationsViewAdapter
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsApplications
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassIO
import net.geekstools.floatshort.PRO.Utils.RemoteProcess.LicenseValidator
import net.geekstools.floatshort.PRO.databinding.ApplicationsViewWatchBinding
import java.util.*

class ApplicationsViewWatchXYZ : WearableActivity() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private val functionsClassIO: FunctionsClassIO by lazy {
        FunctionsClassIO(applicationContext)
    }


    private val applicationsInformationList: ArrayList<ApplicationInfo> = ArrayList<ApplicationInfo>()
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

        loadApplicationsData()

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
                        functionsClass.dialogueLicense(this@ApplicationsViewWatchXYZ)

                        Handler().postDelayed({
                            stopService(Intent(applicationContext, LicenseValidator::class.java))
                        }, 1000)
                    }
                }
            }
            registerReceiver(broadcastReceiver, intentFilter)

            if (!BuildConfig.DEBUG /*|| !functionsClass.appVersionName(packageName).contains("[BETA]")*/) {

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
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun loadApplicationsData() = CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {


    }
}