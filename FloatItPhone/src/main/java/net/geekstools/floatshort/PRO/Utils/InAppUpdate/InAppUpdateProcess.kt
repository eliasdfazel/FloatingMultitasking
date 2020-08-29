/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InAppUpdate

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.Debug
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.databinding.InAppUpdateViewBinding
import java.util.*

class InAppUpdateProcess : AppCompatActivity() {

    private val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener

    companion object {
        private const val IN_APP_UPDATE_REQUEST = 333
    }

    private lateinit var inAppUpdateViewBinding: InAppUpdateViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inAppUpdateViewBinding = InAppUpdateViewBinding.inflate(layoutInflater)
        setContentView(inAppUpdateViewBinding.root)

        window.statusBarColor = functionsClassLegacy.setColorAlpha(PublicVariable.primaryColor, 77f)
        window.navigationBarColor = functionsClassLegacy.setColorAlpha(PublicVariable.primaryColor, 77f)

        inAppUpdateViewBinding.fullEmptyView.setBackgroundColor(functionsClassLegacy.setColorAlpha(PublicVariable.primaryColor, 77f))
        inAppUpdateViewBinding.inAppUpdateWaiting.setColor(PublicVariable.primaryColorOpposite)

        inAppUpdateViewBinding.textInputChangeLog.boxBackgroundColor = functionsClassLegacy.setColorAlpha(PublicVariable.primaryColor, 77f)
        inAppUpdateViewBinding.textInputChangeLog.hintTextColor = ColorStateList.valueOf(getColor(R.color.lighter))
        inAppUpdateViewBinding.textInputChangeLog.hint = "${getString(R.string.inAppUpdateAvailable)} ${intent.getStringExtra("UPDATE_VERSION")}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            inAppUpdateViewBinding.changeLog.setText(Html.fromHtml(intent.getStringExtra("UPDATE_CHANGE_LOG"), Html.FROM_HTML_MODE_LEGACY))
        } else {
            inAppUpdateViewBinding.changeLog.setText(Html.fromHtml(intent.getStringExtra("UPDATE_CHANGE_LOG")))
        }

        installStateUpdatedListener = InstallStateUpdatedListener {
            when (it.installStatus()) {
                InstallStatus.REQUIRES_UI_INTENT -> {
                    Debug.PrintDebug("*** UPDATE Requires UI Intent ***")
                }
                InstallStatus.DOWNLOADING -> {
                    Debug.PrintDebug("*** UPDATE Downloading ***")
                }
                InstallStatus.DOWNLOADED -> {
                    Debug.PrintDebug("*** UPDATE Downloaded ***")

                    showCompleteConfirmation()
                }
                InstallStatus.INSTALLING -> {
                    Debug.PrintDebug("*** UPDATE Installing ***")

                }
                InstallStatus.INSTALLED -> {
                    Debug.PrintDebug("*** UPDATE Installed ***")

                    appUpdateManager.unregisterListener(installStateUpdatedListener)
                }
                InstallStatus.CANCELED -> {
                    Debug.PrintDebug("*** UPDATE Canceled ***")

                    val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
                    functionsClassLegacy.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)

                    this@InAppUpdateProcess.finish()
                }
                InstallStatus.FAILED -> {
                    Debug.PrintDebug("*** UPDATE Failed ***")

                    val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
                    functionsClassLegacy.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)

                    this@InAppUpdateProcess.finish()
                }
                InstallStatus.PENDING -> {
                    Debug.PrintDebug("*** UPDATE Pending ***")

                }
                InstallStatus.UNKNOWN -> {
                    Debug.PrintDebug("*** UPDATE Unknown Stage ***")

                }
            }
        }
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        appUpdateManager.registerListener(installStateUpdatedListener)

        val appUpdateInfo: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo
        appUpdateInfo.addOnSuccessListener { updateInfo ->
            Debug.PrintDebug("*** Update Availability == ${updateInfo.updateAvailability()} ||| Available Version Code == ${updateInfo.availableVersionCode()} ***")

            if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                if (!this@InAppUpdateProcess.isFinishing) {

                    appUpdateManager.startUpdateFlowForResult(
                            updateInfo,
                            AppUpdateType.FLEXIBLE,
                            this@InAppUpdateProcess,
                            IN_APP_UPDATE_REQUEST
                    )
                }

            } else {
                val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
                functionsClassLegacy.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)

                this@InAppUpdateProcess.finish()
            }

        }.addOnFailureListener {
            Debug.PrintDebug("*** Exception Error ${it} ***")

            val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
            functionsClassLegacy.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)
        }

        appUpdateManager.unregisterListener {
            Debug.PrintDebug("*** Unregister Listener ${it} ***")

            this@InAppUpdateProcess.finish()
        }
    }

    override fun onStart() {
        super.onStart()

        inAppUpdateViewBinding.rateFloatIt.setOnClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_link) + packageName)),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }

        inAppUpdateViewBinding.pageFloatIt.setOnClickListener {

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook_app))),
                    ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }

        inAppUpdateViewBinding.cancelInAppUpdateNow.setOnLongClickListener {

            val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
            functionsClassLegacy.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)

            appUpdateManager.unregisterListener {

            }

            this@InAppUpdateProcess.finish()

            false
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {

                if (!this@InAppUpdateProcess.isFinishing) {

                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            this@InAppUpdateProcess,
                            IN_APP_UPDATE_REQUEST
                    )
                }

            }

            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showCompleteConfirmation()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        try {

            appUpdateManager
                    .unregisterListener(installStateUpdatedListener)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onBackPressed() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == IN_APP_UPDATE_REQUEST) {
            when (resultCode) {
                RESULT_CANCELED -> {
                    Debug.PrintDebug("*** RESULT CANCELED ***")

                    val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
                    functionsClassLegacy.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)

                    appUpdateManager.unregisterListener(installStateUpdatedListener)
                    this@InAppUpdateProcess.finish()

                }
                RESULT_OK -> {
                    Debug.PrintDebug("*** RESULT OK ***")

                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Debug.PrintDebug("*** RESULT IN APP UPDATE FAILED ***")

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showCompleteConfirmation() {
        Debug.PrintDebug("*** Complete Confirmation ***")

        val snackbar = Snackbar.make(findViewById<RelativeLayout>(R.id.fullEmptyView),
                getString(R.string.inAppUpdateDescription),
                Snackbar.LENGTH_INDEFINITE)
        snackbar.setBackgroundTint(PublicVariable.colorLightDark)
        snackbar.setTextColor(PublicVariable.colorLightDarkOpposite)
        snackbar.setActionTextColor(PublicVariable.primaryColor)
        snackbar.setAction(Html.fromHtml(getString(R.string.inAppUpdateAction))) { view ->
            appUpdateManager.completeUpdate().addOnSuccessListener {
                Debug.PrintDebug("*** Complete Update Success Listener ***")

            }.addOnFailureListener {
                Debug.PrintDebug("*** Complete Update Failure Listener | ${it} ***")

                val inAppUpdateTriggeredTime: Int = "${Calendar.getInstance().get(Calendar.YEAR)}${Calendar.getInstance().get(Calendar.MONTH)}${Calendar.getInstance().get(Calendar.DATE)}".toInt()
                functionsClassLegacy.savePreference("InAppUpdate", "TriggeredDate", inAppUpdateTriggeredTime)
            }
        }

        val view = snackbar.view
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.BOTTOM
        view.layoutParams = layoutParams

        snackbar.show()
    }
}