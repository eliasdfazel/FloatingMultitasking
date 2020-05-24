/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/23/20 9:27 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.R

class FunctionsClassTheme (private val context: Context) {

    private val functionsClass: FunctionsClass = FunctionsClass(context)

    fun setThemeColorFloating(instanceOfActivity: AppCompatActivity, rootView: View, applyTransparency: Boolean) {

        if (applyTransparency) {

            rootView.setBackgroundColor(functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f))

            instanceOfActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            instanceOfActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (PublicVariable.themeLightDark) {
                instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT > 25) {
                    instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }

            instanceOfActivity.window.statusBarColor = functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f)
            instanceOfActivity.window.navigationBarColor = functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180f)

        } else {

            rootView.setBackgroundColor(PublicVariable.colorLightDark)

            instanceOfActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            instanceOfActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (PublicVariable.themeLightDark) {
                instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT > 25) {
                    instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }

            instanceOfActivity.window.statusBarColor = PublicVariable.colorLightDark
            instanceOfActivity.window.navigationBarColor = PublicVariable.colorLightDark

        }
    }

    fun setThemeColorAutomationFeature(instanceOfActivity: AppCompatActivity, rootView: View, applyTransparency: Boolean) {

        if (applyTransparency) {

            if (Utils().wallpaperStaticLive()) {
                Utils().setBackgroundTheme(instanceOfActivity)
            }

            rootView.setBackgroundColor(functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), if (Utils().wallpaperStaticLive()) {
                180.toFloat()
            } else {
                80.toFloat()
            }))

            instanceOfActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            instanceOfActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (PublicVariable.themeLightDark) {
                instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT > 25) {
                    instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }

            instanceOfActivity.window.statusBarColor = functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.75f), if (Utils().wallpaperStaticLive()) {
                245.toFloat()
            } else {
                113.toFloat()
            })
            instanceOfActivity.window.navigationBarColor = functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), if (Utils().wallpaperStaticLive()) 180.toFloat() else 80.toFloat())

        } else {

            rootView.setBackgroundColor(PublicVariable.colorLightDark)

            instanceOfActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            instanceOfActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (PublicVariable.themeLightDark) {
                instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT > 25) {
                    instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }

            instanceOfActivity.window.statusBarColor = PublicVariable.primaryColor
            instanceOfActivity.window.navigationBarColor = PublicVariable.colorLightDark
        }

    }

    fun setThemeColorPreferences(instanceOfActivity: FragmentActivity, rootView: View, preferencesToolbar: Toolbar, applyTransparency: Boolean, title: String, subTitle: String) {

        if (applyTransparency) {

            if (Utils().wallpaperStaticLive()) {
                Utils().setBackgroundTheme(instanceOfActivity)
            }

            rootView.setBackgroundColor(functionsClass.setColorAlpha(PublicVariable.colorLightDark, if (Utils().wallpaperStaticLive()) {
                180.toFloat()
            } else {
                80.toFloat()
            }))

            preferencesToolbar.setBackgroundColor(PublicVariable.primaryColor)
            if (PublicVariable.themeLightDark) {
                (preferencesToolbar.findViewById(R.id.titlePreferences) as TextView).setTextColor(context.getColor(R.color.dark))
                (preferencesToolbar.findViewById(R.id.summaryPreferences) as TextView).setTextColor(context.getColor(R.color.dark))
            } else {
                (preferencesToolbar.findViewById(R.id.titlePreferences) as TextView).setTextColor(context.getColor(R.color.light))
                (preferencesToolbar.findViewById(R.id.summaryPreferences) as TextView).setTextColor(context.getColor(R.color.light))
            }
            (preferencesToolbar.findViewById(R.id.titlePreferences) as TextView).setText(title)
            (preferencesToolbar.findViewById(R.id.summaryPreferences) as TextView).setText(subTitle)

            instanceOfActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            instanceOfActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (PublicVariable.themeLightDark) {
                instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT > 25) {
                    instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }

            instanceOfActivity.window.statusBarColor = PublicVariable.primaryColor
            instanceOfActivity.window.navigationBarColor = functionsClass.setColorAlpha(PublicVariable.colorLightDark, if (Utils().wallpaperStaticLive()) {
                180.toFloat()
            } else {
                80.toFloat()
            })

        } else {

            rootView.setBackgroundColor(PublicVariable.colorLightDark)

            preferencesToolbar.setBackgroundColor(PublicVariable.primaryColor)
            (preferencesToolbar.findViewById(R.id.titlePreferences) as TextView).text = Html.fromHtml("<font color='" + context.getColor(R.color.light) + "'>" + title + "</font>")
            (preferencesToolbar.findViewById(R.id.summaryPreferences) as TextView).text = Html.fromHtml("<small><font color='" + context.getColor(R.color.light) + "'>" + subTitle + "</font></small>")

            instanceOfActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            instanceOfActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (PublicVariable.themeLightDark) {
                instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT > 25) {
                    instanceOfActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }

            instanceOfActivity.window.statusBarColor = PublicVariable.primaryColor
            instanceOfActivity.window.navigationBarColor = PublicVariable.colorLightDark

        }

    }

    inner class Utils {

        fun setAppThemeBlur(instanceOfActivity: Activity) {

            if (appThemeBlurry()) {

                val wallpaperManager = WallpaperManager.getInstance(context)
                val wallpaper = wallpaperManager.drawable as BitmapDrawable
                val bitmapWallpaper = wallpaper.bitmap

                val inputBitmap: Bitmap = if (bitmapWallpaper.width < functionsClass.displayX() || bitmapWallpaper.height < functionsClass.displayY()) {
                    Bitmap.createScaledBitmap(bitmapWallpaper, functionsClass.displayX(), functionsClass.displayY(), false)
                } else {
                    Bitmap.createBitmap(
                            bitmapWallpaper,
                            bitmapWallpaper.width / 2 - functionsClass.displayX() / 2,
                            bitmapWallpaper.height / 2 - functionsClass.displayY() / 2,
                            functionsClass.displayX(),
                            functionsClass.displayY()
                    )
                }
                val outputBitmap = Bitmap.createBitmap(inputBitmap)
                val renderScript = RenderScript.create(context)

                val intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

                val allocationIn = Allocation.createFromBitmap(renderScript, inputBitmap)
                val allocationOut = Allocation.createFromBitmap(renderScript, outputBitmap)

                intrinsicBlur.setRadius(25f)
                intrinsicBlur.setInput(allocationIn)
                intrinsicBlur.forEach(allocationOut)
                allocationOut.copyTo(outputBitmap)

                val bitmapDrawable = BitmapDrawable(context.resources, outputBitmap)

                instanceOfActivity.window.decorView.background = bitmapDrawable

            }
        }

        fun setWallpaperToBackground(instanceOfActivity: Activity) {

            val wallpaperManager = WallpaperManager.getInstance(context)
            val wallpaperManagerDrawable = wallpaperManager.drawable as BitmapDrawable

            instanceOfActivity.window.decorView.background = wallpaperManagerDrawable

        }

        fun setBackgroundTheme(instanceOfActivity: Activity) {

            if (appThemeBlurry()) {

                setAppThemeBlur(instanceOfActivity)

            } else {

                setWallpaperToBackground(instanceOfActivity)

            }
        }

        fun wallpaperStaticLive(): Boolean {
            var wallpaperMode = false

            if (WallpaperManager.getInstance(context).wallpaperInfo == null) { //static

                wallpaperMode = true

            } else if (WallpaperManager.getInstance(context).wallpaperInfo != null) { //live

                wallpaperMode = false
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("blur", false).apply()

            }

            return wallpaperMode
        }

        fun appThemeBlurry(): Boolean {

            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("blur", true)
        }

    }
}