/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/3/20 1:39 AM
 * Last modified 1/3/20 1:37 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.Preferences

import android.app.Dialog
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.preference.Preference
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Util.GeneralAdapters.AdapterItems
import net.geekstools.floatshort.PRO.Util.GeneralAdapters.CustomIconsThemeAdapter
import net.geekstools.floatshort.PRO.Util.GeneralAdapters.RecycleViewSmoothLayoutList
import net.geekstools.floatshort.PRO.Util.SearchEngine.SearchEngineAdapter

class PreferencesUtil : ViewModel() {

    companion object {
        val CUSTOM_DIALOGUE_DISMISS: MutableLiveData<Boolean> by lazy {
            MutableLiveData<Boolean>()
        }
    }
}

fun setupShapes(activity: FragmentActivity, sharedPreferences: SharedPreferences, functionsClass: FunctionsClass, shapes: Preference) {
    val currentShape: Int = sharedPreferences.getInt("iconShape", 0)

    val layoutParams = WindowManager.LayoutParams()
    val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 377f, activity.resources.displayMetrics).toInt()
    val dialogueHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 387f, activity.resources.displayMetrics).toInt()

    layoutParams.width = dialogueWidth
    layoutParams.height = dialogueHeight
    layoutParams.windowAnimations = android.R.style.Animation_Dialog
    layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
    layoutParams.dimAmount = 0.57f

    val dialog = Dialog(activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(net.geekstools.floatshort.PRO.R.layout.icons_shapes_preferences)
    dialog.window!!.attributes = layoutParams
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
    dialog.setCancelable(true)

    val drawableTeardrop: Drawable = activity.applicationContext.getDrawable(R.drawable.droplet_icon)!!
    drawableTeardrop.setTint(PublicVariable.primaryColor)
    val drawableCircle: Drawable = activity.applicationContext.getDrawable(R.drawable.circle_icon)!!
    drawableCircle.setTint(PublicVariable.primaryColor)
    val drawableSquare: Drawable = activity.applicationContext.getDrawable(R.drawable.square_icon)!!
    drawableSquare.setTint(PublicVariable.primaryColor)
    val drawableSquircle: Drawable = activity.applicationContext.getDrawable(R.drawable.squircle_icon)!!
    drawableSquircle.setTint(PublicVariable.primaryColor)


    val dialogueTitle = dialog.findViewById<View>(R.id.dialogueTitle) as TextView
    dialogueTitle.text = Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + activity.applicationContext.getString(R.string.shapedDesc) + "</font>")
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
        functionsClass.saveDefaultPreference("customIcon", activity.packageName)

        val editor = sharedPreferences.edit()
        editor.putInt("iconShape", 1)
        editor.apply()

        val layerDrawable = LayerDrawable(arrayOf(drawableTeardrop, activity.applicationContext.getDrawable(R.drawable.w_pref_gui)))
        shapes.icon = layerDrawable
        shapes.summary = activity.applicationContext.getString(R.string.droplet)

        functionsClass.saveDefaultPreference("LitePreferences", false)

        dialog.dismiss()
    }
    circleShape.setOnClickListener {
        functionsClass.saveDefaultPreference("customIcon", activity.packageName)

        val editor = sharedPreferences.edit()
        editor.putInt("iconShape", 2)
        editor.apply()

        val layerDrawable = LayerDrawable(arrayOf(drawableCircle, activity.applicationContext.getDrawable(R.drawable.w_pref_gui)))
        shapes.icon = layerDrawable
        shapes.summary = activity.applicationContext.getString(R.string.circle)

        functionsClass.saveDefaultPreference("LitePreferences", false)

        dialog.dismiss()
    }
    squareShape.setOnClickListener {
        functionsClass.saveDefaultPreference("customIcon", activity.packageName)

        val editor = sharedPreferences.edit()
        editor.putInt("iconShape", 3)
        editor.apply()

        val layerDrawable = LayerDrawable(arrayOf(drawableSquare, activity.applicationContext.getDrawable(R.drawable.w_pref_gui)))
        shapes.icon = layerDrawable
        shapes.summary = activity.applicationContext.getString(R.string.square)

        functionsClass.saveDefaultPreference("LitePreferences", false)

        dialog.dismiss()
    }
    squircleShape.setOnClickListener {
        functionsClass.saveDefaultPreference("customIcon", activity.packageName)

        val editor = sharedPreferences.edit()
        editor.putInt("iconShape", 4)
        editor.apply()

        val layerDrawable = LayerDrawable(arrayOf(drawableSquircle, activity.applicationContext.getDrawable(R.drawable.w_pref_gui)))
        shapes.icon = layerDrawable
        shapes.summary = activity.applicationContext.getString(R.string.squircle)

        functionsClass.saveDefaultPreference("LitePreferences", false)

        dialog.dismiss()
    }

    customIconPack.setOnClickListener {
        dialog.dismiss()

        listCustomIconsPackage(activity, sharedPreferences, functionsClass, shapes)
    }

    noShape.setOnClickListener {
        functionsClass.saveDefaultPreference("customIcon", activity.packageName)

        val editor = sharedPreferences.edit()
        editor.putInt("iconShape", 0)
        editor.apply()

        val drawableNoShape: Drawable = activity.applicationContext.getDrawable(R.drawable.w_pref_noshape) as Drawable
        drawableNoShape.setTint(PublicVariable.primaryColor)
        shapes.icon = drawableNoShape
        shapes.summary = ""

        dialog.dismiss()
    }

    dialog.setOnDismissListener {
        functionsClass.addAppShortcuts()
        if (currentShape != sharedPreferences.getInt("iconShape", 0)) {
            PublicVariable.forceReload = true
            SearchEngineAdapter.allSearchResultItems.clear()
        }
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
    dialog.show()
}

fun listCustomIconsPackage(activity: FragmentActivity, sharedPreferences: SharedPreferences, functionsClass: FunctionsClass, shapes: Preference) {
    val currentCustomIconPack: String = sharedPreferences.getString("customIcon", activity.packageName)!!

    val layoutParams = WindowManager.LayoutParams()
    val dialogueWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 377f, activity.resources.displayMetrics).toInt()

    layoutParams.width = dialogueWidth
    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    layoutParams.windowAnimations = android.R.style.Animation_Dialog
    layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
    layoutParams.dimAmount = 0.57f

    val dialog = Dialog(activity)
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

    dialogueTitle.text = Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + activity.applicationContext.getString(R.string.customIconTitle) + "</font>")
    dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite)
    defaultTheme.setTextColor(PublicVariable.colorLightDarkOpposite)

    val recyclerViewLayoutManager = RecycleViewSmoothLayoutList(activity, OrientationHelper.VERTICAL, false)
    customIconList.layoutManager = recyclerViewLayoutManager
    customIconList.removeAllViews()
    val adapterItems = ArrayList<AdapterItems>()
    adapterItems.clear()
    for (packageName in PublicVariable.customIconsPackages) {
        adapterItems.add(AdapterItems(
                functionsClass.appName(packageName),
                packageName,
                functionsClass.appIcon(packageName)
        ))
    }
    val customIconsThemeAdapter = CustomIconsThemeAdapter(activity, activity.applicationContext, adapterItems, dialog)
    customIconList.adapter = customIconsThemeAdapter

    defaultTheme.setOnClickListener {
        functionsClass.saveDefaultPreference("customIcon", activity.packageName)

        val editor = sharedPreferences.edit()
        editor.putInt("iconShape", 0)
        editor.apply()

        val drawableNoShape: Drawable = activity.applicationContext.getDrawable(R.drawable.w_pref_noshape) as Drawable
        drawableNoShape.setTint(PublicVariable.primaryColor)
        shapes.icon = drawableNoShape
        shapes.summary = ""

        dialog.dismiss()
    }

    dialog.setOnDismissListener {
        adapterItems.clear()

        if (currentCustomIconPack != sharedPreferences.getString("customIcon", activity.packageName)) {
            PublicVariable.forceReload = true
            SearchEngineAdapter.allSearchResultItems.clear()
        }
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
    dialog.show()

    PreferencesUtil.CUSTOM_DIALOGUE_DISMISS.observe(activity,
            Observer<Boolean> { dialogueDismiss ->
                if (dialogueDismiss) {
                    PreferencesUtil.CUSTOM_DIALOGUE_DISMISS.value = false

                    shapes.icon = functionsClass.appIcon(functionsClass.customIconPackageName())
                    shapes.summary = functionsClass.appName(functionsClass.customIconPackageName())

                    functionsClass.addAppShortcuts()

                    val editor = sharedPreferences.edit()
                    editor.putInt("iconShape", 0)
                    editor.apply()
                }
            })
}