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
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations;
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;

public class AppsConfirmButton extends androidx.appcompat.widget.AppCompatButton
        implements SimpleGestureFilterAdvance.SimpleGestureListener {

    Activity activity;
    Context context;

    FunctionsClass functionsClass;

    ConfirmButtonProcessInterface confirmButtonProcessInterface;

    SimpleGestureFilterAdvance simpleGestureFilterAdvance;


    LayerDrawable drawDismiss;

    public AppsConfirmButton(Activity activity, Context context,
                             FunctionsClass functionsClass,
                             ConfirmButtonProcessInterface confirmButtonProcessInterface) {

        super(context);

        this.activity = activity;
        this.context = context;

        this.functionsClass = functionsClass;

        this.confirmButtonProcessInterface = confirmButtonProcessInterface;

        initializeConfirmButton();
    }

    public AppsConfirmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        initializeConfirmButton();
    }

    public AppsConfirmButton(Context context) {
        super(context);
        this.context = context;

        initializeConfirmButton();
    }

    public void initializeConfirmButton() {
        simpleGestureFilterAdvance = new SimpleGestureFilterAdvance(context, this);

        drawDismiss = (LayerDrawable) context.getDrawable(R.drawable.draw_saved_dismiss);
        Drawable backDismiss = drawDismiss.findDrawableByLayerId(R.id.backgroundTemporary);
        backDismiss.setTint(PublicVariable.primaryColor);

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(context.getString(R.string.visibilityActionAdvance));
//        intentFilter.addAction(context.getString(R.string.animtaionActionAdvance));
//        intentFilter.addAction(context.getString(R.string.setDismissAdvance));

        PublicVariable.confirmButtonX = this.getX();
        PublicVariable.confirmButtonY = this.getY();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.simpleGestureFilterAdvance.onTouchEvent(motionEvent);

        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilterAdvance.SWIPE_DOWN:

                break;
            case SimpleGestureFilterAdvance.SWIPE_LEFT:
                confirmButtonProcessInterface.showSavedShortcutList();

                if (functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                    AppsConfirmButton.this.setBackground(drawDismiss);
                }

                break;
            case SimpleGestureFilterAdvance.SWIPE_RIGHT:
                confirmButtonProcessInterface.showSavedShortcutList();

                if (functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                    AppsConfirmButton.this.setBackground(drawDismiss);
                }

                break;
            case SimpleGestureFilterAdvance.SWIPE_UP:
                confirmButtonProcessInterface.showSavedShortcutList();

                if (functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                    AppsConfirmButton.this.setBackground(drawDismiss);
                }

                break;
        }
    }

    @Override
    public void onSingleTapUp() {

        functionsClass.navigateToClass(FoldersConfigurations.class, activity);
    }
}
