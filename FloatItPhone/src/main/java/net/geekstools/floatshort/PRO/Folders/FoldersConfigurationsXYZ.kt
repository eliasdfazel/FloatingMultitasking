/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 3:43 PM
 * Last modified 3/26/20 3:40 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geeksempire.primepuzzles.GameView.UI.SwipeGestureListener
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.FoldersListAdapter
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsSearchEngine
import net.geekstools.floatshort.PRO.Utils.Functions.*
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.floatshort.PRO.databinding.CategoryHandlerBinding
import java.util.*

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
    private val adapterItems: ArrayList<AdapterItems> = ArrayList<AdapterItems>()

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
        categoryHandlerBinding.categorylist.layoutManager = recyclerViewLayoutManager

        val drawFloatingLogo = getDrawable(R.drawable.draw_floating_logo) as LayerDrawable?
        val backFloatingLogo = drawFloatingLogo?.findDrawableByLayerId(R.id.backgroundTemporary)
        backFloatingLogo?.setTint(PublicVariable.primaryColorOpposite)
        categoryHandlerBinding.loadingLogo.setImageDrawable(drawFloatingLogo)

        LoadCategory()

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


    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
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

    override fun onLongClick(v: View?): Boolean {

        return true
    }

    override fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {
        super.onSwipeGesture(gestureConstants, downMotionEvent, moveMotionEvent, initVelocityX, initVelocityY)
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        swipeGestureListener.onTouchEvent(motionEvent)

        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun LoadCategory() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

    }

    fun LoadInstalledCustomIcons() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    }

    /*Search Engine*/

    /*Search Engine*/
}