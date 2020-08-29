/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.UI.Splash

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy

class FloatingSplashRemoval: AppCompatImageView, FloatingSplashInterface {

    private val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(context)
    }

    private val paint = Paint()

    constructor(context: Context) : super(context) {

        paint.style = Paint.Style.FILL
        paint.color = Color.TRANSPARENT

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {


            canvas.drawPaint(paint)

            canvas.drawCircle(functionsClassLegacy.displayX().toFloat(),
                    functionsClassLegacy.displayY().toFloat(),
                    functionsClassLegacy.displayY() * 2.toFloat(),
                    paint)
        }
    }

    override fun floatingSplashRemoval() {

        this@FloatingSplashRemoval.visibility = View.VISIBLE

        functionsClassLegacy.circularRevealSplashScreenRemoval(
                this@FloatingSplashRemoval,
                FloatingSplash.xPositionRemoval + FloatingSplash.HeightWidthRemoval / 2,
                FloatingSplash.yPositionRemoval + FloatingSplash.HeightWidthRemoval / 2
        )
    }
}