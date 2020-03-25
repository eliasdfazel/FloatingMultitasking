/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/25/20 2:16 PM
 * Last modified 3/25/20 2:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.UI.Splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;

import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;

public class FloatingSplashRemoval extends androidx.appcompat.widget.AppCompatImageView {

    FunctionsClass functionsClass;

    public FloatingSplashRemoval(Context context) {
        super(context);
    }

    public FloatingSplashRemoval(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        functionsClass = new FunctionsClass(getContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("FloatingSplashRemoval");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("FloatingSplashRemoval")) {
                    FloatingSplashRemoval.this.setVisibility(VISIBLE);
                    try {
                        functionsClass.circularRevealSplashScreenRemoval(
                                FloatingSplashRemoval.this,
                                FloatingSplash.xPostionRemoval + (FloatingSplash.HWRemoval / 2),
                                FloatingSplash.yPositionRemoval + (FloatingSplash.HWRemoval / 2)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        try {
            getContext().registerReceiver(broadcastReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(functionsClass.displayX(), functionsClass.displayY(), functionsClass.displayY() * 2, paint);
    }
}
