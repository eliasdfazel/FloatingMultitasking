/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:18 PM
 * Last modified 11/11/19 7:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.loadingspin.sprite;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by ybq.
 */
public class RingSprite extends ShapeSprite {


    @Override
    public void drawShape(Canvas canvas, Paint paint) {
        if (getDrawBounds() != null) {
            paint.setStyle(Paint.Style.STROKE);
            int radius = Math.min(getDrawBounds().width(), getDrawBounds().height()) / 2;
            paint.setStrokeWidth(radius / 12);
            canvas.drawCircle(getDrawBounds().centerX(),
                    getDrawBounds().centerY(),
                    radius, paint);
        }
    }

    @Override
    public ValueAnimator onCreateAnimation() {
        return null;
    }
}
