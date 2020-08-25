/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/25/20 5:18 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.PreferenceManager
import net.geekstools.floatshort.PRO.R
import net.geekstools.imageview.customshapes.ShapesImage

class ShapingBitmap(private val context: Context) {

    private val colorsExtractor: ColorsExtractor = ColorsExtractor(context)

    fun applicationIcon(packageName: String?): Drawable? {
        var icon: Drawable? = null
        try {
            val packManager = context.packageManager
            icon = packManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (icon == null) {
                try {
                    val packManager = context.packageManager
                    icon = packManager.defaultActivityIcon
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return icon
    }

    fun applicationIcon(activityInfo: ActivityInfo): Drawable? {
        var icon: Drawable? = null
        try {
            icon = activityInfo.loadIcon(context.packageManager)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (icon == null) {
                try {
                    val packManager = context.packageManager
                    icon = packManager.defaultActivityIcon
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return icon
    }

    fun shapesImageId(): Int {
        var shapesImageId = 0
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        when (sharedPreferences.getInt("iconShape", 0)) {
            1 -> shapesImageId = 1
            2 -> shapesImageId = 2
            3 -> shapesImageId = 3
            4 -> shapesImageId = 4
            5 -> shapesImageId = 5
            0 -> shapesImageId = 0
        }
        return shapesImageId
    }

    fun shapedDrablesResourceId(): Int {
        var shapeDrawable: Int = R.drawable.droplet_icon
        when (shapesImageId()) {
            1 -> shapeDrawable = R.drawable.droplet_icon
            2 -> shapeDrawable = R.drawable.circle_icon
            3 -> shapeDrawable = R.drawable.square_icon
            4 -> shapeDrawable = R.drawable.squircle_icon
            0 -> shapeDrawable = R.drawable.droplet_icon
        }
        return shapeDrawable
    }

    fun shapesDrawables(): Drawable? {
        var shapeDrawable: Drawable? = null
        when (shapesImageId()) {
            1 -> shapeDrawable = context.getDrawable(R.drawable.droplet_icon)
            2 -> shapeDrawable = context.getDrawable(R.drawable.circle_icon)
            3 -> shapeDrawable = context.getDrawable(R.drawable.square_icon)
            4 -> shapeDrawable = context.getDrawable(R.drawable.squircle_icon)
            0 -> shapeDrawable = null
        }
        return shapeDrawable
    }

    fun shapesDrawablesCategory(categoryChar: TextView): Drawable? {
        var shapeDrawable: Drawable? = null
        when (shapesImageId()) {
            1 -> shapeDrawable = context.getDrawable(R.drawable.category_droplet_icon)!!.mutate()
            2 -> shapeDrawable = context.getDrawable(R.drawable.category_circle_icon)!!.mutate()
            3 -> shapeDrawable = context.getDrawable(R.drawable.category_square_icon)!!.mutate()
            4 -> shapeDrawable = context.getDrawable(R.drawable.category_squircle_icon)!!.mutate()
            0 -> {
                shapeDrawable = context.getDrawable(R.drawable.brilliant)!!.mutate()
                shapeDrawable.alpha = 0
                categoryChar.setTextColor(PublicVariable.colorLightDarkOpposite)
            }
        }
        return shapeDrawable
    }

    fun initShapesImage(viewGroup: ViewGroup, viewId: Int): ShapesImage? {
        val shapesImage: ShapesImage = viewGroup.findViewById(viewId)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        when (sharedPreferences.getInt("iconShape", 0)) {
            1 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.droplet_icon))
            2 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.circle_icon))
            3 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.square_icon))
            4 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.squircle_icon))
            0 -> shapesImage.setShapeDrawable(null)
        }
        return shapesImage
    }

    fun initShapesImage(view: View, viewId: Int): ShapesImage? {
        val shapesImage: ShapesImage = view.findViewById(viewId)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        when (sharedPreferences.getInt("iconShape", 0)) {
            1 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.droplet_icon))
            2 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.circle_icon))
            3 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.square_icon))
            4 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.squircle_icon))
            0 -> shapesImage.setShapeDrawable(null)
        }
        return shapesImage
    }

    fun initShapesImage(activity: Activity, viewId: Int): ShapesImage? {
        val shapesImage: ShapesImage = activity.findViewById(viewId)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        when (sharedPreferences.getInt("iconShape", 0)) {
            1 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.droplet_icon))
            2 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.circle_icon))
            3 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.square_icon))
            4 -> shapesImage.setShapeDrawable(context.getDrawable(R.drawable.squircle_icon))
            0 -> shapesImage.setShapeDrawable(null)
        }
        return shapesImage
    }

    fun initShapesImage(shapesImageView: ShapesImage): ShapesImage? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        when (sharedPreferences.getInt("iconShape", 0)) {
            1 -> shapesImageView.setShapeDrawable(context.getDrawable(R.drawable.droplet_icon))
            2 -> shapesImageView.setShapeDrawable(context.getDrawable(R.drawable.circle_icon))
            3 -> shapesImageView.setShapeDrawable(context.getDrawable(R.drawable.square_icon))
            4 -> shapesImageView.setShapeDrawable(context.getDrawable(R.drawable.squircle_icon))
            0 -> shapesImageView.setShapeDrawable(null)
        }
        return shapesImageView
    }

    fun shapedNotificationUser(drawable: Drawable): Drawable? {
        val drawableBack: Drawable = ColorDrawable(colorsExtractor.extractDominantColor(drawable))
        val shapedDrawable = LayerDrawable(arrayOf(drawableBack, drawable))
        shapedDrawable.paddingMode = LayerDrawable.PADDING_MODE_NEST
        shapedDrawable.setLayerInset(1, 7, 7, 7, 7)
        return shapedDrawable
    }

    fun shapedAppIcon(packageName: String?): Drawable? {
        var appIconDrawable: Drawable? = null
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (sharedPreferences.getInt("iconShape", 0) == 1 || sharedPreferences.getInt("iconShape", 0) == 2 || sharedPreferences.getInt("iconShape", 0) == 3 || sharedPreferences.getInt("iconShape", 0) == 4 || sharedPreferences.getInt("iconShape", 0) == 5) {
            var drawableBack: Drawable? = null
            var drawableFront: Drawable? = null
            var layerDrawable: LayerDrawable? = null
            if (Build.VERSION.SDK_INT >= 26) {
                var adaptiveIconDrawable: AdaptiveIconDrawable? = null
                try {
                    val tempAppIcon = applicationIcon(packageName)
                    if (tempAppIcon is AdaptiveIconDrawable) {
                        adaptiveIconDrawable = tempAppIcon
                        drawableBack = adaptiveIconDrawable!!.background
                        drawableFront = adaptiveIconDrawable.foreground
                        layerDrawable = LayerDrawable(arrayOf<Drawable?>(drawableBack, drawableFront))
                        appIconDrawable = layerDrawable
                    } else {
                        drawableBack = ColorDrawable(colorsExtractor.extractDominantColor(tempAppIcon))
                        drawableFront = tempAppIcon
                        layerDrawable = LayerDrawable(arrayOf(drawableBack, drawableFront))
                        layerDrawable.paddingMode = LayerDrawable.PADDING_MODE_NEST
                        layerDrawable.setLayerInset(1, 35, 35, 35, 35)
                        appIconDrawable = layerDrawable
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val tempAppIcon = applicationIcon(packageName)
                drawableBack = ColorDrawable(colorsExtractor.extractDominantColor(tempAppIcon))
                drawableFront = tempAppIcon
                layerDrawable = LayerDrawable(arrayOf(drawableBack, drawableFront))
                layerDrawable.paddingMode = LayerDrawable.PADDING_MODE_NEST
                layerDrawable.setLayerInset(1, 35, 35, 35, 35)
                appIconDrawable = layerDrawable
            }
        } else if (sharedPreferences.getInt("iconShape", 0) == 0) {
            appIconDrawable = applicationIcon(packageName)
        }
        return appIconDrawable
    }

    fun shapedAppIcon(activityInfo: ActivityInfo): Drawable? {
        var appIconDrawable: Drawable? = null
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (sharedPreferences.getInt("iconShape", 0) == 1 || sharedPreferences.getInt("iconShape", 0) == 2 || sharedPreferences.getInt("iconShape", 0) == 3 || sharedPreferences.getInt("iconShape", 0) == 4 || sharedPreferences.getInt("iconShape", 0) == 5) {
            var drawableBack: Drawable? = null
            var drawableFront: Drawable? = null
            var layerDrawable: LayerDrawable? = null
            if (Build.VERSION.SDK_INT >= 26) {
                var adaptiveIconDrawable: AdaptiveIconDrawable? = null
                try {
                    val tempAppIcon = applicationIcon(activityInfo)
                    if (tempAppIcon is AdaptiveIconDrawable) {
                        adaptiveIconDrawable = tempAppIcon
                        drawableBack = adaptiveIconDrawable!!.background
                        drawableFront = adaptiveIconDrawable.foreground
                        layerDrawable = LayerDrawable(arrayOf<Drawable?>(drawableBack, drawableFront))
                        appIconDrawable = layerDrawable
                    } else {
                        drawableBack = ColorDrawable(colorsExtractor.extractDominantColor(tempAppIcon))
                        drawableFront = tempAppIcon
                        layerDrawable = LayerDrawable(arrayOf(drawableBack, drawableFront))
                        layerDrawable.paddingMode = LayerDrawable.PADDING_MODE_NEST
                        layerDrawable.setLayerInset(1, 35, 35, 35, 35)
                        appIconDrawable = layerDrawable
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val tempAppIcon = applicationIcon(activityInfo)
                drawableBack = ColorDrawable(colorsExtractor.extractDominantColor(tempAppIcon))
                drawableFront = tempAppIcon
                layerDrawable = LayerDrawable(arrayOf(drawableBack, drawableFront))
                layerDrawable.paddingMode = LayerDrawable.PADDING_MODE_NEST
                layerDrawable.setLayerInset(1, 35, 35, 35, 35)
                appIconDrawable = layerDrawable
            }
        } else if (sharedPreferences.getInt("iconShape", 0) == 0) {
            appIconDrawable = applicationIcon(activityInfo)
        }
        return appIconDrawable
    }

}