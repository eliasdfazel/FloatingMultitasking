/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 2:32 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.UI;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;

import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;

public class AppsConfirmButton extends androidx.appcompat.widget.AppCompatButton
        implements SimpleGestureFilterAdvance.SimpleGestureListener {

    FunctionsClass functionsClass;

    Context context;

    SimpleGestureFilterAdvance detector;
    BroadcastReceiver visibilityReceiver;

    LayerDrawable drawShow, drawDismiss;

    public AppsConfirmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        functionsClass = new FunctionsClass(context);

        initConfirmButton();
    }

    public AppsConfirmButton(Context context) {
        super(context);
    }

    public void initConfirmButton() {
        detector = new SimpleGestureFilterAdvance(context, this);

        drawShow = (LayerDrawable) context.getDrawable(R.drawable.draw_saved_show);
        Drawable backShow = drawShow.findDrawableByLayerId(R.id.backgroundTemporary);
        backShow.setTint(PublicVariable.primaryColorOpposite);

        drawDismiss = (LayerDrawable) context.getDrawable(R.drawable.draw_saved_dismiss);
        Drawable backDismiss = drawDismiss.findDrawableByLayerId(R.id.backgroundTemporary);
        backDismiss.setTint(PublicVariable.primaryColor);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.visibilityActionAdvance));
        intentFilter.addAction(context.getString(R.string.animtaionActionAdvance));
        intentFilter.addAction(context.getString(R.string.setDismissAdvance));
        visibilityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.visibilityActionAdvance))) {
                    AppsConfirmButton.this.setBackground(drawShow);
                    if (!AppsConfirmButton.this.isShown()) {
                        AppsConfirmButton.this.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
                        AppsConfirmButton.this.setVisibility(VISIBLE);
                    }
                } else if (intent.getAction().equals(context.getString(R.string.animtaionActionAdvance))) {
                    AppsConfirmButton.this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_confirm_button));
                } else if (intent.getAction().equals(context.getString(R.string.setDismissAdvance))) {
                    AppsConfirmButton.this.setBackground(drawDismiss);
                }
            }
        };
        context.registerReceiver(visibilityReceiver, intentFilter);
        PublicVariable.confirmButtonX = this.getX();
        PublicVariable.confirmButtonY = this.getY();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(visibilityReceiver);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        this.detector.onTouchEvent(me);

        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilterAdvance.SWIPE_DOWN:

                break;
            case SimpleGestureFilterAdvance.SWIPE_LEFT:
                context.sendBroadcast(new Intent(context.getString(R.string.savedActionAdvance)));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                            AppsConfirmButton.this.setBackground(drawDismiss);
                        }
                    }
                }, 200);

                break;
            case SimpleGestureFilterAdvance.SWIPE_RIGHT:
                context.sendBroadcast(new Intent(context.getString(R.string.savedActionAdvance)));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                            AppsConfirmButton.this.setBackground(drawDismiss);
                        }
                    }
                }, 200);

                break;
            case SimpleGestureFilterAdvance.SWIPE_UP:
                context.sendBroadcast(new Intent(context.getString(R.string.savedActionAdvance)));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                            AppsConfirmButton.this.setBackground(drawDismiss);
                        }
                    }
                }, 200);

                break;
        }
    }

    @Override
    public void onSingleTapUp() {
        functionsClass.navigateToClass(FoldersConfigurations.class, (Activity) getContext());
    }
}
