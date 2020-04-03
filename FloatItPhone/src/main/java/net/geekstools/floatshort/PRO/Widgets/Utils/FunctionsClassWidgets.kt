package net.geekstools.floatshort.PRO.Widgets.Utils

import android.content.Context
import android.content.SharedPreferences

import android.graphics.PixelFormat
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager

class FunctionsClassWidgets (private val context: Context) {

    fun handleOrientationWidgetLandscape(widgetIdentifier: String): WindowManager.LayoutParams {
        val layoutParams: WindowManager.LayoutParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT)
        } else {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT)
        }

        val initWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 213f, context.resources.displayMetrics).toInt()
        val initHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 133f, context.resources.displayMetrics).toInt()

        val sharedPrefPosition: SharedPreferences = context.getSharedPreferences(widgetIdentifier, Context.MODE_PRIVATE)

        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.width = sharedPrefPosition.getInt("WidgetWidth", initWidth)
        layoutParams.height = sharedPrefPosition.getInt("WidgetHeight", initHeight)
        layoutParams.x = sharedPrefPosition.getInt("Y", 0)
        layoutParams.y = sharedPrefPosition.getInt("X", 0)
        layoutParams.windowAnimations = android.R.style.Animation_Dialog

        val editor = sharedPrefPosition.edit()
        editor.putInt("X", layoutParams.x)
        editor.putInt("Y", layoutParams.y)
        editor.apply()

        return layoutParams
    }

    fun handleOrientationWidgetPortrait(widgetIdentifier: String): WindowManager.LayoutParams {
        val layoutParams: WindowManager.LayoutParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT)
        } else {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT)
        }

        val initWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 213f, context.resources.displayMetrics).toInt()
        val initHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 133f, context.resources.displayMetrics).toInt()

        val sharedPrefPosition: SharedPreferences = context.getSharedPreferences(widgetIdentifier, Context.MODE_PRIVATE)

        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.width = sharedPrefPosition.getInt("WidgetWidth", initWidth)
        layoutParams.height = sharedPrefPosition.getInt("WidgetHeight", initHeight)
        layoutParams.x = sharedPrefPosition.getInt("Y", 0)
        layoutParams.y = sharedPrefPosition.getInt("X", 0)
        layoutParams.windowAnimations = android.R.style.Animation_Dialog

        val editor = sharedPrefPosition.edit()
        editor.putInt("X", layoutParams.x)
        editor.putInt("Y", layoutParams.y)
        editor.apply()

        return layoutParams
    }
}