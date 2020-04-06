/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 2:32 PM
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
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class AppsConfirmButton : AppCompatButton, SimpleGestureFilterAdvance.SimpleGestureListener, ConfirmButtonViewInterface {

    private lateinit var activity: Activity

    lateinit var functionsClass: FunctionsClass

    private lateinit var confirmButtonProcessInterface: ConfirmButtonProcessInterface
    private lateinit var simpleGestureFilterAdvance: SimpleGestureFilterAdvance

    private lateinit var dismissDrawable: LayerDrawable

    constructor(activity: Activity, context: Context,
                functionsClass: FunctionsClass,
                confirmButtonProcessInterface: ConfirmButtonProcessInterface) : super(context) {

        this.activity = activity


        this.functionsClass = functionsClass
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
        simpleGestureFilterAdvance = SimpleGestureFilterAdvance(context, this)

        dismissDrawable = context.getDrawable(R.drawable.draw_saved_dismiss) as LayerDrawable
        val backgroundTemporary = dismissDrawable.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary.setTint(PublicVariable.primaryColor)

        PublicVariable.confirmButtonX = this.x
        PublicVariable.confirmButtonY = this.y
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        simpleGestureFilterAdvance.onTouchEvent(motionEvent)
        return super.dispatchTouchEvent(motionEvent)
    }

    override fun onSwipe(direction: Int) {
        when (direction) {
            SimpleGestureFilterAdvance.SWIPE_DOWN -> {

            }
            SimpleGestureFilterAdvance.SWIPE_LEFT -> {
                confirmButtonProcessInterface.showSavedShortcutList()

                if (functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                    this@AppsConfirmButton.background = dismissDrawable
                }
            }
            SimpleGestureFilterAdvance.SWIPE_RIGHT -> {
                confirmButtonProcessInterface.showSavedShortcutList()

                if (functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                    this@AppsConfirmButton.background = dismissDrawable
                }
            }
            SimpleGestureFilterAdvance.SWIPE_UP -> {
                confirmButtonProcessInterface.showSavedShortcutList()

                if (functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                    this@AppsConfirmButton.background = dismissDrawable
                }
            }
        }
    }

    override fun onSingleTapUp() {

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