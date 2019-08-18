package net.geekstools.floatshort.PRO.Util

import android.content.Intent
import android.os.Bundle
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
import kotlinx.android.synthetic.main.empty.*
import net.geeksempire.loadingspin.SpinKitView
import net.geeksempire.loadingspin.style.RotatingCircle
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable

class InAppUpdate : AppCompatActivity() {

    lateinit var appUpdateManager: AppUpdateManager
    lateinit var installStateUpdatedListener: InstallStateUpdatedListener

    val IN_APP_UPDATE_REQUEST = 333

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.empty)

        val spinKitView: SpinKitView = SpinKitView(applicationContext)
        spinKitView.setIndeterminateDrawable(RotatingCircle())
        spinKitView.setColor(PublicVariable.primaryColor)
        spinKitView.setPadding(7, 7, 7, 7)
        fullEmptyView.addView(spinKitView)

        installStateUpdatedListener = InstallStateUpdatedListener {
            when (it.installStatus()) {
                InstallStatus.REQUIRES_UI_INTENT -> {
                    FunctionsClassDebug.PrintDebug("*** UPDATE Requires UI Intent ***")
                }
                InstallStatus.DOWNLOADING -> {
                    FunctionsClassDebug.PrintDebug("*** UPDATE Downloading ***")
                }
                InstallStatus.DOWNLOADED -> {
                    FunctionsClassDebug.PrintDebug("*** UPDATE Downloaded ***")

//                    showCompleteConfirmation()
                    appUpdateManager.completeUpdate().addOnSuccessListener {
                        FunctionsClassDebug.PrintDebug("*** Complete Update Success Listener ***")

                    }.addOnFailureListener {
                        FunctionsClassDebug.PrintDebug("*** Complete Update Failure Listener ${it} ***")

                    }


                }
                InstallStatus.INSTALLING -> {
                    FunctionsClassDebug.PrintDebug("*** UPDATE Installing ***")
                }
                InstallStatus.INSTALLED -> {
                    FunctionsClassDebug.PrintDebug("*** UPDATE Installed ***")

//                    appUpdateManager.unregisterListener(installStateUpdatedListener)
                }
                InstallStatus.CANCELED -> {
                    FunctionsClassDebug.PrintDebug("*** UPDATE Canceled ***")

//                    this@InAppUpdate.finish()
                }
                InstallStatus.FAILED -> {
                    FunctionsClassDebug.PrintDebug("*** UPDATE Failed ***")
                }
            }
        }
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        appUpdateManager.registerListener(installStateUpdatedListener)

        val appUpdateInfo: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo
        appUpdateInfo.addOnSuccessListener { updateInfo ->
            FunctionsClassDebug.PrintDebug("*** ${updateInfo.updateAvailability()} --- ${updateInfo.availableVersionCode()} ***")

            if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                appUpdateManager.startUpdateFlowForResult(
                        updateInfo,
                        AppUpdateType.IMMEDIATE,
                        this@InAppUpdate,
                        IN_APP_UPDATE_REQUEST
                )
            }

        }.addOnFailureListener {
            FunctionsClassDebug.PrintDebug("*** Exception Error ${it} ***")
        }

//        appUpdateManager.unregisterListener {
//            FunctionsClassDebug.PrintDebug("*** Unregister Listener ${it} ***")
//
//            this@InAppUpdate.finish()
//        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.updateAvailability()
                            == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                this@InAppUpdate,
                                IN_APP_UPDATE_REQUEST
                        )
                    }

                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        showCompleteConfirmation()
                    }
                }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IN_APP_UPDATE_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                FunctionsClassDebug.PrintDebug("*** RESULT CANCELED ***")

//                appUpdateManager.unregisterListener(installStateUpdatedListener)
                this@InAppUpdate.finish()

                PublicVariable.updateCancelByUser = false
            } else if (resultCode == RESULT_OK) {
                FunctionsClassDebug.PrintDebug("*** RESULT OK ***")

            }

            if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                FunctionsClassDebug.PrintDebug("*** RESULT IN APP UPDATE FAILED ***")

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showCompleteConfirmation() {
        FunctionsClassDebug.PrintDebug("*** Complete Confirmation ***")

        val snackbar = Snackbar.make(
                findViewById<RelativeLayout>(R.id.fullEmptyView),
                "An Update has Just Been Installed.",
                Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Complete It!") { view ->
            appUpdateManager.completeUpdate().addOnSuccessListener {
                FunctionsClassDebug.PrintDebug("*** Complete Update Success Listener ***")

            }.addOnFailureListener {
                FunctionsClassDebug.PrintDebug("*** Complete Update Failure Listener ***")

            }
        }
        snackbar.setActionTextColor(PublicVariable.primaryColorOpposite)
        snackbar.show()
    }
}