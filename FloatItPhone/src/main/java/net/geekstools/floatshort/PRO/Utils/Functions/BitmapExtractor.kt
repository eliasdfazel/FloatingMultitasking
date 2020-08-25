/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/25/20 5:17 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.app.WallpaperManager
import android.content.Context
import android.graphics.*
import android.graphics.drawable.*
import android.os.Build
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons

class BitmapExtractor(private val context: Context) {

    private val preferencesIO: PreferencesIO = PreferencesIO(context)

    private val installedApplicationInformation: InstalledApplicationInformation = InstalledApplicationInformation(context)

    private val shapingBitmap: ShapingBitmap = ShapingBitmap(context)

    fun customIconPackageName(): String {

        return preferencesIO.readDefaultPreference("customIcon", context.packageName)?:context.packageName
    }

    fun loadSavedColor() {
        val sharedPreferences = context.getSharedPreferences(".themeColor", Context.MODE_PRIVATE)
        PublicVariable.vibrantColor = sharedPreferences.getInt("vibrantColor", context.getColor(R.color.default_color)) //getVibrantColor
        PublicVariable.darkMutedColor = sharedPreferences.getInt("darkMutedColor", context.getColor(R.color.default_color)) //getDarkMutedColor
        PublicVariable.darkMutedColorString = sharedPreferences.getString("darkMutedColorString", context.getColor(R.color.default_color).toString())
        if (PublicVariable.themeLightDark) {
            PublicVariable.primaryColor = sharedPreferences.getInt("vibrantColor", context.getColor(R.color.default_color)) //getVibrantColor
            PublicVariable.primaryColorOpposite = sharedPreferences.getInt("darkMutedColor", context.getColor(R.color.default_color)) //getDarkMutedColor
            PublicVariable.colorLightDark = context.getColor(R.color.light)
            PublicVariable.colorLightDarkOpposite = context.getColor(R.color.dark)
        } else if (!PublicVariable.themeLightDark) {
            PublicVariable.primaryColor = sharedPreferences.getInt("darkMutedColor", context.getColor(R.color.default_color)) //getDarkMutedColor
            PublicVariable.primaryColorOpposite = sharedPreferences.getInt("vibrantColor", context.getColor(R.color.default_color)) //getVibrantColor
            PublicVariable.colorLightDark = context.getColor(R.color.dark)
            PublicVariable.colorLightDarkOpposite = context.getColor(R.color.light)
        }
        PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.getColor(R.color.default_color))
    }

    fun extractWallpaperColor() {
        var vibrantColor: Int
        var darkMutedColor: Int
        var dominantColor: Int
        var darkMutedColorString: String
        val currentColor: Palette
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val currentWallpaper = wallpaperManager.drawable
            val bitmap = (currentWallpaper as BitmapDrawable).bitmap
            currentColor = if (bitmap != null && !bitmap.isRecycled) {
                Palette.from(bitmap).generate()
            } else {
                val bitmapTemp = BitmapFactory.decodeResource(context.resources, R.drawable.brilliant)
                Palette.from(bitmapTemp).generate()
            }
            val defaultColor = context.getColor(R.color.default_color)
            vibrantColor = currentColor.getVibrantColor(defaultColor)
            darkMutedColor = currentColor.getDarkMutedColor(defaultColor)
            darkMutedColorString = "#" + Integer.toHexString(currentColor.getDarkMutedColor(defaultColor)).substring(2)
            dominantColor = currentColor.getDominantColor(defaultColor)
        } catch (e: Exception) {
            e.printStackTrace()
            vibrantColor = context.getColor(R.color.default_color)
            darkMutedColor = context.getColor(R.color.default_color)
            darkMutedColorString = "" + context.getColor(R.color.default_color)
            dominantColor = context.getColor(R.color.default_color)
        }
        val sharedPreferences = context.getSharedPreferences(".themeColor", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("vibrantColor", vibrantColor)
        editor.putInt("darkMutedColor", darkMutedColor)
        editor.putString("darkMutedColorString", darkMutedColorString)
        editor.putInt("dominantColor", dominantColor)
        editor.apply()
    }

    fun extractVibrantColor(drawable: Drawable?): Int {
        var VibrantColor = context.getColor(R.color.default_color)
        var bitmap: Bitmap?
        if (Build.VERSION.SDK_INT >= 26) {
            if (drawable is VectorDrawable) {
                bitmap = drawableToBitmap(drawable)
            } else if (drawable is AdaptiveIconDrawable) {
                try {
                    bitmap = (drawable.background as BitmapDrawable).bitmap
                } catch (e: Exception) {
                    try {
                        bitmap = (drawable.foreground as BitmapDrawable).bitmap
                    } catch (e1: Exception) {
                        bitmap = drawableToBitmap(drawable)
                    }
                }
            } else {
                bitmap = drawable?.let { drawableToBitmap(it) }
            }
        } else {
            bitmap = drawable?.let { drawableToBitmap(it) }
        }
        var currentColor: Palette
        try {
            if (bitmap != null && !bitmap.isRecycled) {
                currentColor = Palette.from(bitmap).generate()
                val defaultColor = context.getColor(R.color.default_color)
                VibrantColor = currentColor.getVibrantColor(defaultColor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                if (bitmap != null && !bitmap.isRecycled) {
                    currentColor = Palette.from(bitmap).generate()
                    val defaultColor = context.getColor(R.color.default_color)
                    VibrantColor = currentColor.getMutedColor(defaultColor)
                }
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        }
        return VibrantColor
    }

    fun extractDominantColor(drawable: Drawable?): Int {
        var DominanctColor = context.getColor(R.color.default_color)
        var bitmap: Bitmap?
        if (Build.VERSION.SDK_INT >= 26) {
            if (drawable is VectorDrawable) {
                bitmap = drawableToBitmap(drawable)
            } else if (drawable is AdaptiveIconDrawable) {
                try {
                    bitmap = (drawable.background as BitmapDrawable).bitmap
                } catch (e: Exception) {
                    try {
                        bitmap = (drawable.foreground as BitmapDrawable).bitmap
                    } catch (e1: Exception) {
                        bitmap = drawableToBitmap(drawable)
                    }
                }
            } else {
                bitmap = drawable?.let { drawableToBitmap(it) }
            }
        } else {
            bitmap = drawable?.let { drawableToBitmap(it) }
        }
        var currentColor: Palette
        try {
            if (bitmap != null && !bitmap.isRecycled) {
                currentColor = Palette.from(bitmap).generate()
                val defaultColor = context.getColor(R.color.default_color)
                DominanctColor = currentColor.getDominantColor(defaultColor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                if (bitmap != null && !bitmap.isRecycled) {
                    currentColor = Palette.from(bitmap).generate()
                    val defaultColor = context.getColor(R.color.default_color)
                    DominanctColor = currentColor.getMutedColor(defaultColor)
                }
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        }
        return DominanctColor
    }

    fun brightenBitmap(bitmap: Bitmap?): Bitmap? {
        val canvas = Canvas(bitmap!!)
        val paint = Paint(Color.RED)
        val filter: ColorFilter = LightingColorFilter(-0x1, 0x00222222) // lighten
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, Matrix(), paint)
        return bitmap
    }

    fun darkenBitmap(bitmap: Bitmap?): Bitmap? {
        val canvas = Canvas(bitmap!!)
        val paint = Paint(Color.RED)
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
        val filter: ColorFilter = LightingColorFilter(-0x808081, 0x00000000) // darken
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, Matrix(), paint)
        return bitmap
    }

    fun manipulateColor(color: Int, aFactor: Float): Int {
        val a = Color.alpha(color)
        val r = Math.round(Color.red(color) * aFactor)
        val g = Math.round(Color.green(color) * aFactor)
        val b = Math.round(Color.blue(color) * aFactor)
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255))
    }

    fun mixColors(color1: Int, color2: Int, ratio: Float /*0 -- 1*/): Int {
        /*ratio = 1 >> color1*/
        /*ratio = 0 >> color2*/
        val inverseRation = 1f - ratio
        val r = Color.red(color1) * ratio + Color.red(color2) * inverseRation
        val g = Color.green(color1) * ratio + Color.green(color2) * inverseRation
        val b = Color.blue(color1) * ratio + Color.blue(color2) * inverseRation
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    /**
     * 255 is Transparent.
     */
    fun setColorAlpha(color: Int, alphaValue: Float /*1 -- 255*/): Int {
        val alpha = Math.round(Color.alpha(color) * alphaValue)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    fun colorLightDarkWallpaper(): Boolean {
        var LightDark = false
        try {
            val sharedPreferences = context.getSharedPreferences(".themeColor", Context.MODE_PRIVATE)
            val vibrantColor = sharedPreferences.getInt("vibrantColor", context.getColor(R.color.default_color))
            val darkMutedColor = sharedPreferences.getInt("darkMutedColor", context.getColor(R.color.default_color))
            val dominantColor = sharedPreferences.getInt("dominantColor", context.getColor(R.color.default_color))
            val initMix = mixColors(vibrantColor, darkMutedColor, 0.50f)
            val finalMix = mixColors(dominantColor, initMix, 0.50f)
            Debug.PrintDebug("*** Vibrant ::: " + vibrantColor + " >>> " + ColorUtils.calculateLuminance(vibrantColor))
            Debug.PrintDebug("*** Dark ::: " + darkMutedColor + " >>> " + ColorUtils.calculateLuminance(darkMutedColor))
            Debug.PrintDebug("*** Dominant ::: " + dominantColor + " >>> " + ColorUtils.calculateLuminance(dominantColor))
            Debug.PrintDebug("*** initMix ::: " + initMix + " >>> " + ColorUtils.calculateLuminance(initMix))
            Debug.PrintDebug("*** finalMix ::: " + finalMix + " >>> " + ColorUtils.calculateLuminance(finalMix))
            val calculateLuminance = ColorUtils.calculateLuminance(dominantColor)
            if (calculateLuminance > 0.50) { //light
                LightDark = true
            } else if (calculateLuminance <= 0.50) { //dark
                LightDark = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return LightDark
    }

    fun isDrawableLightDark(drawable: Drawable?): Boolean {
        var isLightDark = false
        val calculateLuminance = ColorUtils.calculateLuminance(extractDominantColor(drawable))
        if (calculateLuminance > 0.66) { //light
            isLightDark = true
        } else if (calculateLuminance <= 0.50) { //dark
            isLightDark = false
        }
        return isLightDark
    }

    fun customIconsEnable(): Boolean {
        var doLoadCustomIcon = false
        if (customIconPackageName() == context.packageName) {
            doLoadCustomIcon = false
        } else if (customIconPackageName() != context.packageName) {
            doLoadCustomIcon = true
        }
        return doLoadCustomIcon
    }

    fun getAppIconBitmapCustomIcon(packageName: String): Bitmap? {
        var loadCustomIcons: LoadCustomIcons? = null
        if (customIconsEnable()) {
            loadCustomIcons = LoadCustomIcons(context, customIconPackageName())
            loadCustomIcons.load()
        }
        return if (customIconsEnable()) loadCustomIcons!!.getIconForPackage(packageName, appIconBitmap(packageName)) else appIconBitmap(packageName)
    }

    fun getAppIconDrawableCustomIcon(packageName: String): Drawable? {
        var loadCustomIcons: LoadCustomIcons? = null
        if (customIconsEnable()) {
            loadCustomIcons = LoadCustomIcons(context, customIconPackageName())
            loadCustomIcons.load()
        }
        return if (customIconsEnable()) loadCustomIcons!!.getDrawableIconForPackage(packageName, installedApplicationInformation.applicationIcon(packageName)) else installedApplicationInformation.applicationIcon(packageName)
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is VectorDrawable) {
            val vectorDrawable = drawable
            bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
        } else if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                bitmap = bitmapDrawable.bitmap
            }
        } else if (drawable is LayerDrawable) {
            val layerDrawable = drawable
            bitmap = Bitmap.createBitmap(layerDrawable.intrinsicWidth, layerDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            layerDrawable.setBounds(0, 0, canvas.width, canvas.height)
            layerDrawable.draw(canvas)
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return bitmap
    }

    fun drawableToBitmapMute(drawable: Drawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmapCopy)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmapCopy
    }

    fun genericDrawableToBitmap(drawable: Drawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth / 2, drawable.intrinsicHeight / 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun layerDrawableToBitmap(layerDrawable: LayerDrawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(layerDrawable.intrinsicWidth / 2, layerDrawable.intrinsicHeight / 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        layerDrawable.setBounds(0, 0, canvas.width, canvas.height)
        layerDrawable.draw(canvas)
        return bitmap
    }

    fun bitmapToDrawable(bitmap: Bitmap?): Drawable? {
        return BitmapDrawable(context.resources, bitmap)
    }

    fun appIconBitmap(packageName: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val drawAppShortcuts: LayerDrawable
            val drawableIcon: Drawable? = installedApplicationInformation.applicationIcon(packageName)
            var shapeTempDrawable: Drawable? = shapingBitmap.shapesDrawables()
            if (shapeTempDrawable != null) {
                shapeTempDrawable.setTint(extractDominantColor(drawableIcon))
                drawAppShortcuts = LayerDrawable(arrayOf(
                        shapeTempDrawable,
                        drawableIcon
                ))
                drawAppShortcuts.setLayerInset(1, 43, 43, 43, 43)
            } else {
                shapeTempDrawable = ColorDrawable(Color.TRANSPARENT)
                drawAppShortcuts = LayerDrawable(arrayOf(
                        shapeTempDrawable,
                        drawableIcon
                ))
                drawAppShortcuts.setLayerInset(1, 1, 1, 1, 1)
            }
            bitmap = Bitmap.createBitmap(drawAppShortcuts.intrinsicWidth, drawAppShortcuts.intrinsicHeight, Bitmap.Config.ARGB_8888)
            drawAppShortcuts.setBounds(0, 0, drawAppShortcuts.intrinsicWidth, drawAppShortcuts.intrinsicHeight)
            drawAppShortcuts.draw(Canvas(bitmap))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun resizeDrawable(drawable: Drawable, dstWidth: Int, dstHeight: Int): Drawable? {
        var resizedDrawable: Drawable? = null
        resizedDrawable = try {
            val bitmap = (drawable as BitmapDrawable).bitmap
            val bitmapResized = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false)
            BitmapDrawable(context.resources, bitmapResized).mutate()
        } catch (e: Exception) {
            e.printStackTrace()
            ColorDrawable(Color.TRANSPARENT)
        }
        return resizedDrawable
    }

}