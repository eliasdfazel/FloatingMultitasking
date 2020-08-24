/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 6:17 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Folders.UI

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatButton
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonViewInterface
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerConstants
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerInterface
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.SwipeGestureListener

class AppsConfirmButton : AppCompatButton, GestureListenerInterface,
        ConfirmButtonViewInterface {

    private lateinit var activity: Activity

    lateinit var functionsClass: FunctionsClass
    lateinit var fileIO: FileIO

    private lateinit var confirmButtonProcessInterface: ConfirmButtonProcessInterface

    private val swipeGestureListener: SwipeGestureListener by lazy {
        SwipeGestureListener(context, this@AppsConfirmButton)
    }

    private lateinit var dismissDrawable: LayerDrawable

    constructor(activity: Activity, context: Context,
                functionsClass: FunctionsClass,
                fileIO: FileIO,
                confirmButtonProcessInterface: ConfirmButtonProcessInterface) : super(context) {

        this.activity = activity


        this.functionsClass = functionsClass
        this.fileIO = fileIO

        this.confirmButtonProcessInterface = confirmButtonProcessInterface

        initializeConfirmButton()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        initializeConfirmButton()
    }

    constructor(context: Context) : super(context) {

        initializeConfirmButton()
    }

    private fun initializeConfirmButton() {
        swipeGestureListener.swipeMinDistance = 100

        dismissDrawable = context.getDrawable(R.drawable.draw_saved_dismiss) as LayerDrawable
        val backgroundTemporary = dismissDrawable.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary.setTint(PublicVariable.primaryColor)

        PublicVariable.confirmButtonX = this.x
        PublicVariable.confirmButtonY = this.y
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        motionEvent?.let {
            swipeGestureListener.onTouchEvent(it)
        }

        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {
        super.onSwipeGesture(gestureConstants, downMotionEvent, moveMotionEvent, initVelocityX, initVelocityY)

        when (gestureConstants) {
            is GestureConstants.SwipeHorizontal -> {
                when (gestureConstants.horizontalDirection) {
                    GestureListenerConstants.SWIPE_RIGHT -> {
                        confirmButtonProcessInterface.showSavedShortcutList()

                        if (fileIO.fileLinesCounter(PublicVariable.folderName) > 0) {
                            this@AppsConfirmButton.background = dismissDrawable
                        }
                    }
                    GestureListenerConstants.SWIPE_LEFT -> {
                        confirmButtonProcessInterface.showSavedShortcutList()

                        if (fileIO.fileLinesCounter(PublicVariable.folderName) > 0) {
                            this@AppsConfirmButton.background = dismissDrawable
                        }
                    }
                }
            }
            is GestureConstants.SwipeVertical -> {
                when (gestureConstants.verticallDirection) {
                    GestureListenerConstants.SWIPE_UP -> {
                        confirmButtonProcessInterface.showSavedShortcutList()

                        if (fileIO.fileLinesCounter(PublicVariable.folderName) > 0) {
                            this@AppsConfirmButton.background = dismissDrawable
                        }
                    }
                    GestureListenerConstants.SWIPE_DOWN -> {

                    }
                }
            }
        }
    }

    override fun onSingleTapUp(motionEvent: MotionEvent) {
        super.onSingleTapUp(motionEvent)

        functionsClass.navigateToClass(FoldersConfigurations::class.java, activity)
    }

    /*ConfirmButtonViewInterface*/
    override fun makeItVisible() {
        val confirmDrawable = context.getDrawable(R.drawable.draw_saved_show) as LayerDrawable
        val backgroundTemporary  = confirmDrawable.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary.setTint(PublicVariable.primaryColorOpposite)
        this@AppsConfirmButton.background = confirmDrawable

        if (!this@AppsConfirmButton.isShown) {
            this@AppsConfirmButton.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            this@AppsConfirmButton.visibility = View.VISIBLE
        }
    }

    override fun startCustomAnimation(animation: Animation?) {
        if (animation == null) {

            this@AppsConfirmButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_confirm_button))

        } else {

            this@AppsConfirmButton.startAnimation(animation)

        }
    }

    override fun setDismissBackground() {
        val drawDismiss = context.getDrawable(R.drawable.draw_saved_dismiss) as LayerDrawable
        val backgroundTemporary: Drawable = drawDismiss.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary.setTint(PublicVariable.primaryColor)

        this@AppsConfirmButton.background = drawDismiss
    }
    /*ConfirmButtonViewInterface*/
}