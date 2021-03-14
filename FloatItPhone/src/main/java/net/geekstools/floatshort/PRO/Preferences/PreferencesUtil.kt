/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 12/19/20 4:29 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Preferences

import android.app.Dialog
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.text.Html
import android.util.TypedValue
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.Utils.AdapterDataItem.CustomIconsThemeAdapter
import net.geekstools.floatshort.PRO.Utils.AdapterDataItem.RecycleViewSmoothLayoutList
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PopupApplicationShortcuts
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable

class PreferencesUtil : ViewModel() {

    val customDialogueDismiss: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
}

data class PreferencesDataUtilShape(var activity: FragmentActivity, var sharedPreferences: SharedPreferences, var functionsClassLegacy: FunctionsClassLegacy, var shapes: Preference)

data class PreferencesDataUtilFling(var activity: FragmentActivity, var functionsClassLegacy: FunctionsClassLegacy)

fun setupShapes(preferencesDataUtilShape: PreferencesDataUtilShape) {
    val currentShape: Int = preferencesDataUtilShape.sharedPreferences.getInt("iconShape", 0)

    val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 377f, preferencesDataUtilShape.activity.resources.displayMetrics).toInt()
    val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 387f, preferencesDataUtilShape.activity.resources.displayMetrics).toInt()

    val layoutParams = WindowManager.LayoutParams()

    layoutParams.width = dialogueWidth
    layoutParams.height = dialogueHeight
    layoutParams.windowAnimations = android.R.style.Animation_Dialog
    layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
    layoutParams.dimAmount = 0.57f

    val dialog = Dialog(preferencesDataUtilShape.activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(net.geekstools.floatshort.PRO.R.layout.icons_shapes_preferences)
    dialog.window!!.attributes = layoutParams
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
    dialog.setCancelable(true)

    val drawableTeardrop: Drawable = preferencesDataUtilShape.activity.getDrawable(R.drawable.droplet_icon)!!
    drawableTeardrop.setTint(PublicVariable.primaryColor)
    val drawableCircle: Drawable = preferencesDataUtilShape.activity.getDrawable(R.drawable.circle_icon)!!
    drawableCircle.setTint(PublicVariable.primaryColor)
    val drawableSquare: Drawable = preferencesDataUtilShape.activity.getDrawable(R.drawable.square_icon)!!
    drawableSquare.setTint(PublicVariable.primaryColor)
    val drawableSquircle: Drawable = preferencesDataUtilShape.activity.getDrawable(R.drawable.squircle_icon)!!
    drawableSquircle.setTint(PublicVariable.primaryColor)


    val dialogueTitle = dialog.findViewById<View>(R.id.dialogueTitle) as TextView
    dialogueTitle.text = Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + preferencesDataUtilShape.activity.getString(R.string.shapedDesc) + "</font>")
    dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)

    val dialogueView: View = dialog.findViewById<ScrollView>(R.id.dialogueView)
    dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

    val teardropShape = dialog.findViewById<RelativeLayout>(R.id.teardropShape)
    val circleShape = dialog.findViewById<RelativeLayout>(R.id.circleShape)
    val squareShape = dialog.findViewById<RelativeLayout>(R.id.squareShape)
    val squircleShape = dialog.findViewById<RelativeLayout>(R.id.squircleShape)

    val customIconPack = dialog.findViewById<Button>(R.id.customIconPack)
    val noShape = dialog.findViewById<Button>(R.id.noShape)

    val teardropImage = dialog.findViewById<ImageView>(R.id.teardropImage)
    val circleImage = dialog.findViewById<ImageView>(R.id.circleImage)
    val squareImage = dialog.findViewById<ImageView>(R.id.squareImage)
    val squircleImage = dialog.findViewById<ImageView>(R.id.squircleImage)

    val teardropText = dialog.findViewById<TextView>(R.id.teardropText)
    val circleText = dialog.findViewById<TextView>(R.id.circleText)
    val squareText = dialog.findViewById<TextView>(R.id.squareText)
    val squircleText = dialog.findViewById<TextView>(R.id.squircleText)

    teardropImage.setImageDrawable(drawableTeardrop)
    circleImage.setImageDrawable(drawableCircle)
    squareImage.setImageDrawable(drawableSquare)
    squircleImage.setImageDrawable(drawableSquircle)

    teardropText.setTextColor(PublicVariable.colorLightDarkOpposite)
    circleText.setTextColor(PublicVariable.colorLightDarkOpposite)
    squareText.setTextColor(PublicVariable.colorLightDarkOpposite)
    squircleText.setTextColor(PublicVariable.colorLightDarkOpposite)

    customIconPack.setTextColor(PublicVariable.colorLightDarkOpposite)
    noShape.setTextColor(PublicVariable.colorLightDarkOpposite)

    teardropShape.setOnClickListener {
        preferencesDataUtilShape.functionsClassLegacy.saveDefaultPreference("customIcon", preferencesDataUtilShape.activity.packageName)

        val editor = preferencesDataUtilShape.sharedPreferences.edit()
        editor.putInt("iconShape", 1)
        editor.apply()

        val layerDrawable = LayerDrawable(arrayOf(drawableTeardrop, preferencesDataUtilShape.activity.getDrawable(R.drawable.w_pref_gui)))
        preferencesDataUtilShape.shapes.icon = layerDrawable
        preferencesDataUtilShape.shapes.summary = preferencesDataUtilShape.activity.getString(R.string.droplet)

        preferencesDataUtilShape.functionsClassLegacy.saveDefaultPreference("LitePreferences", false)

        dialog.dismiss()
    }
    circleShape.setOnClickListener {
        preferencesDataUtilShape.functionsClassLegacy.saveDefaultPreference("customIcon", preferencesDataUtilShape.activity.packageName)

        val editor = preferencesDataUtilShape.sharedPreferences.edit()
        editor.putInt("iconShape", 2)
        editor.apply()

        val layerDrawable = LayerDrawable(arrayOf(drawableCircle, preferencesDataUtilShape.activity.getDrawable(R.drawable.w_pref_gui)))
        preferencesDataUtilShape.shapes.icon = layerDrawable
        preferencesDataUtilShape.shapes.summary = preferencesDataUtilShape.activity.getString(R.string.circle)

        preferencesDataUtilShape.functionsClassLegacy.saveDefaultPreference("LitePreferences", false)

        dialog.dismiss()
    }
    squareShape.setOnClickListener {
        preferencesDataUtilShape.functionsClassLegacy.saveDefaultPreference("customIcon", preferencesDataUtilShape.activity.packageName)

        val editor = preferencesDataUtilShape.sharedPreferences.edit()
        editor.putInt("iconShape", 3)
        editor.apply()

        val layerDrawable = LayerDrawable(arrayOf(drawableSquare, preferencesDataUtilShape.activity.getDrawable(R.drawable.w_pref_gui)))
        preferencesDataUtilShape.shapes.icon = layerDrawable
        preferencesDataUtilShape.shapes.summary = preferencesDataUtilShape.activity.getString(R.string.square)

        preferencesDataUtilShape.functionsClassLegacy.saveDefaultPreference("LitePreferences", false)

        dialog.dismiss()
    }
    squircleShape.setOnClickListener {
        preferencesDataUtilShape.functionsClassLegacy.saveDefaultPreference("customIcon", preferencesDataUtilShape.activity.packageName)

        val editor = preferencesDataUtilShape.sharedPreferences.edit()
        editor.putInt("iconShape", 4)
        editor.apply()

        val layerDrawable = LayerDrawable(arrayOf(drawableSquircle, preferencesDataUtilShape.activity.getDrawable(R.drawable.w_pref_gui)))
        preferencesDataUtilShape.shapes.icon = layerDrawable
        preferencesDataUtilShape.shapes.summary = preferencesDataUtilShape.activity.getString(R.string.squircle)

        preferencesDataUtilShape.functionsClassLegacy.saveDefaultPreference("LitePreferences", false)

        dialog.dismiss()
    }

    customIconPack.setOnClickListener {
        dialog.dismiss()

        listCustomIconsPackage(preferencesDataUtilShape)
    }

    noShape.setOnClickListener {
        preferencesDataUtilShape.functionsClassLegacy.saveDefaultPreference("customIcon", preferencesDataUtilShape.activity.packageName)

        val editor = preferencesDataUtilShape.sharedPreferences.edit()
        editor.putInt("iconShape", 0)
        editor.apply()

        val drawableNoShape: Drawable = preferencesDataUtilShape.activity.getDrawable(R.drawable.w_pref_noshape) as Drawable
        drawableNoShape.setTint(PublicVariable.primaryColor)
        preferencesDataUtilShape.shapes.icon = drawableNoShape
        preferencesDataUtilShape.shapes.summary = ""

        dialog.dismiss()
    }

    dialog.setOnDismissListener {
        PopupApplicationShortcuts(preferencesDataUtilShape.activity).addPopupApplicationShortcuts()

        if (currentShape != preferencesDataUtilShape.sharedPreferences.getInt("iconShape", 0)) {
            PublicVariable.forceReload = true

            SearchEngine.clearSearchDataToForceReload()
        }
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
    dialog.show()
}

fun listCustomIconsPackage(preferencesDataUtilShape: PreferencesDataUtilShape) {
    val currentCustomIconPack: String = preferencesDataUtilShape.sharedPreferences.getString("customIcon", preferencesDataUtilShape.activity.packageName)!!

    val layoutParams = WindowManager.LayoutParams()
    val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 377f, preferencesDataUtilShape.activity.resources.displayMetrics).toInt()

    layoutParams.width = dialogueWidth
    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    layoutParams.windowAnimations = android.R.style.Animation_Dialog
    layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
    layoutParams.dimAmount = 0.57f

    val dialog = Dialog(preferencesDataUtilShape.activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.custom_icons)
    dialog.window!!.attributes = layoutParams
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
    dialog.setCancelable(true)

    val dialogueView: View = dialog.findViewById<View>(R.id.dialogueView) as RelativeLayout
    dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

    val dialogueTitle = dialog.findViewById<TextView>(R.id.dialogueTitle)
    val defaultTheme = dialog.findViewById<TextView>(R.id.setDefault)
    val customIconList = dialog.findViewById<RecyclerView>(R.id.customIconList)

    dialogueTitle.text = Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + preferencesDataUtilShape.activity.getString(R.string.customIconTitle) + "</font>")
    dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
    defaultTheme.setTextColor(PublicVariable.colorLightDarkOpposite)

    val preferencesUtil = ViewModelProvider(preferencesDataUtilShape.activity).get(PreferencesUtil::class.java)

    val recyclerViewLayoutManager = RecycleViewSmoothLayoutList(preferencesDataUtilShape.activity, OrientationHelper.VERTICAL, false)
    customIconList.layoutManager = recyclerViewLayoutManager
    customIconList.removeAllViews()
    val adapterItems = ArrayList<AdapterItems>()
    adapterItems.clear()
    for (packageName in PublicVariable.customIconsPackages) {
        adapterItems.add(AdapterItems(
                preferencesDataUtilShape.functionsClassLegacy.applicationName(packageName),
                packageName,
                preferencesDataUtilShape.functionsClassLegacy.applicationIcon(packageName)
        ))
    }
    val customIconsThemeAdapter = CustomIconsThemeAdapter(preferencesUtil, preferencesDataUtilShape.activity, adapterItems, dialog)
    customIconList.adapter = customIconsThemeAdapter

    defaultTheme.setOnClickListener {
        preferencesDataUtilShape.functionsClassLegacy.saveDefaultPreference("customIcon", preferencesDataUtilShape.activity.packageName)

        val editor = preferencesDataUtilShape.sharedPreferences.edit()
        editor.putInt("iconShape", 0)
        editor.apply()

        val drawableNoShape: Drawable = preferencesDataUtilShape.activity.getDrawable(R.drawable.w_pref_noshape) as Drawable
        drawableNoShape.setTint(PublicVariable.primaryColor)
        preferencesDataUtilShape.shapes.icon = drawableNoShape
        preferencesDataUtilShape.shapes.summary = ""

        dialog.dismiss()
    }

    dialog.setOnDismissListener {
        adapterItems.clear()

        if (currentCustomIconPack != preferencesDataUtilShape.sharedPreferences.getString("customIcon", preferencesDataUtilShape.activity.packageName)) {
            PublicVariable.forceReload = true

            SearchEngine.clearSearchDataToForceReload()
        }
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
    dialog.show()

    preferencesUtil.customDialogueDismiss.observe(preferencesDataUtilShape.activity,
            Observer<Boolean> { dialogueDismiss ->
                if (dialogueDismiss) {
                    preferencesUtil.customDialogueDismiss.postValue(false)

                    preferencesDataUtilShape.shapes.icon = preferencesDataUtilShape.functionsClassLegacy.applicationIcon(preferencesDataUtilShape.functionsClassLegacy.customIconPackageName())
                    preferencesDataUtilShape.shapes.summary = preferencesDataUtilShape.functionsClassLegacy.applicationName(preferencesDataUtilShape.functionsClassLegacy.customIconPackageName())

                    PopupApplicationShortcuts(preferencesDataUtilShape.activity).addPopupApplicationShortcuts()

                    val editor = preferencesDataUtilShape.sharedPreferences.edit()
                    editor.putInt("iconShape", 0)
                    editor.apply()
                }
            })
}

fun setupFlingSensitivity(preferencesDataUtilFling: PreferencesDataUtilFling) {
    val layoutParams = WindowManager.LayoutParams()
    val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, preferencesDataUtilFling.activity.resources.displayMetrics).toInt()
    val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400f, preferencesDataUtilFling.activity.resources.displayMetrics).toInt()

    layoutParams.width = dialogueWidth
    layoutParams.height = dialogueHeight
    layoutParams.windowAnimations = android.R.style.Animation_Dialog
    layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
    layoutParams.dimAmount = 0.57f

    val dialog = Dialog(preferencesDataUtilFling.activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.seekbar_preferences_fling)
    dialog.window!!.attributes = layoutParams
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
    dialog.setCancelable(true)

    val dialogueView: View = dialog.findViewById<RelativeLayout>(R.id.dialogueView)
    dialogueView.backgroundTintList = ColorStateList.valueOf(PublicVariable.colorLightDark)

    val flingingIcon = dialog.findViewById<ImageView>(R.id.preferenceIcon)
    val seekBarPreferences = dialog.findViewById<SeekBar>(R.id.seekBarPreferences)
    val dialogueTitle = dialog.findViewById<TextView>(R.id.dialogueTitle)
    val revertDefault = dialog.findViewById<TextView>(R.id.revertDefault)

    seekBarPreferences.thumbTintList = ColorStateList.valueOf(PublicVariable.primaryColor)
    seekBarPreferences.thumbTintMode = PorterDuff.Mode.SRC_IN
    seekBarPreferences.progressTintList = ColorStateList.valueOf(PublicVariable.primaryColorOpposite)
    seekBarPreferences.progressTintMode = PorterDuff.Mode.SRC_IN

    seekBarPreferences.max = 50
    seekBarPreferences.progress = preferencesDataUtilFling.functionsClassLegacy.readPreference("FlingSensitivity", "FlingSensitivityValueProgress", 30)
    if (seekBarPreferences.progress <= 10) {
        seekBarPreferences.progressTintList = ColorStateList.valueOf(preferencesDataUtilFling.activity.getColor(R.color.red))
    }

    var layerDrawableLoadLogo: Drawable?
    try {
        val backgroundDot: Drawable = preferencesDataUtilFling.functionsClassLegacy.shapesDrawables().mutate()
        backgroundDot.setTint(PublicVariable.primaryColorOpposite)
        layerDrawableLoadLogo = LayerDrawable(arrayOf(
                backgroundDot,
                preferencesDataUtilFling.activity.getDrawable(R.drawable.ic_launcher_dots)
        ))
    } catch (e: NullPointerException) {
        e.printStackTrace()
        layerDrawableLoadLogo = preferencesDataUtilFling.activity.getDrawable(R.drawable.ic_launcher)
    }

    val iconHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, preferencesDataUtilFling.functionsClassLegacy.readDefaultPreference("floatingSize", 39).toFloat(), preferencesDataUtilFling.activity.resources.displayMetrics).toInt()
    val layoutParamsIcon = RelativeLayout.LayoutParams(
            iconHW,
            iconHW
    )
    layoutParamsIcon.addRule(RelativeLayout.CENTER_IN_PARENT, R.id.preferenceIcon)
    layoutParamsIcon.addRule(RelativeLayout.BELOW, R.id.dialogueTitle)
    flingingIcon.layoutParams = layoutParamsIcon
    flingingIcon.setImageDrawable(layerDrawableLoadLogo)

    dialogueTitle.text = Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + preferencesDataUtilFling.activity.getString(R.string.flingSensitivityTitle) + "</font>")
    dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
    revertDefault.setTextColor(PublicVariable.colorLightDarkOpposite)

    val flingAnimationX = FlingAnimation(flingingIcon, DynamicAnimation.TRANSLATION_X)
            .setFriction(preferencesDataUtilFling.functionsClassLegacy.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f))
    val flingAnimationY = FlingAnimation(flingingIcon, DynamicAnimation.TRANSLATION_Y)
            .setFriction(preferencesDataUtilFling.functionsClassLegacy.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f))

    flingAnimationX.setMinValue(-preferencesDataUtilFling.functionsClassLegacy.DpToPixel(97f))
    flingAnimationX.setMaxValue(preferencesDataUtilFling.functionsClassLegacy.DpToPixel(97f))

    flingAnimationY.setMinValue(0f)
    flingAnimationY.setMaxValue(preferencesDataUtilFling.functionsClassLegacy.DpToPixel(197f))

    val simpleOnGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onDown(motionEvent: MotionEvent): Boolean {
            return true
        }

        override fun onFling(motionEventFirst: MotionEvent, motionEventLast: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

            flingAnimationX.setStartVelocity(velocityX)
            flingAnimationY.setStartVelocity(velocityY)

            flingAnimationX.addUpdateListener { animation, value, velocity ->

            }
            flingAnimationY.addUpdateListener { animation, value, velocity ->

            }

            flingAnimationX.start()
            flingAnimationY.start()

            return false
        }
    }

    flingAnimationX.addEndListener { animation, canceled, value, velocity ->

    }

    flingAnimationY.addEndListener { animation, canceled, value, velocity ->

    }

    val gestureDetector = GestureDetector(preferencesDataUtilFling.activity, simpleOnGestureListener)

    flingingIcon.setOnTouchListener { view, motionEvent ->
        flingAnimationX.cancel()
        flingAnimationY.cancel()

        gestureDetector.onTouchEvent(motionEvent)
    }

    seekBarPreferences.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            preferencesDataUtilFling.functionsClassLegacy.savePreference("FlingSensitivity", "FlingSensitivityValueProgress", progress)
            if (progress <= 10) {
                seekBarPreferences.progressTintList = ColorStateList.valueOf(preferencesDataUtilFling.activity.getColor(R.color.red))
                preferencesDataUtilFling.functionsClassLegacy.savePreference("FlingSensitivity", "FlingSensitivityValue", 0.5f)
                try {
                    flingAnimationX.friction = 0.5f
                    flingAnimationY.friction = 0.5f
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                seekBarPreferences.progressTintList = ColorStateList.valueOf(PublicVariable.primaryColorOpposite)
                preferencesDataUtilFling.functionsClassLegacy.savePreference("FlingSensitivity", "FlingSensitivityValue", (progress / 10).toFloat())
                try {
                    flingAnimationX.friction = preferencesDataUtilFling.functionsClassLegacy.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f)
                    flingAnimationY.friction = preferencesDataUtilFling.functionsClassLegacy.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }
        override fun onStopTrackingTouch(seekBar: SeekBar) {

        }
    })



    revertDefault.setOnClickListener {
        preferencesDataUtilFling.functionsClassLegacy.savePreference("FlingSensitivity", "FlingSensitivityValueProgress", 30)
        preferencesDataUtilFling.functionsClassLegacy.savePreference("FlingSensitivity", "FlingSensitivityValue", 3.0f)

        seekBarPreferences.progress = 30
        seekBarPreferences.progressTintList = ColorStateList.valueOf(PublicVariable.primaryColorOpposite)
    }

    dialog.setOnDismissListener {
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    dialog.show()
}