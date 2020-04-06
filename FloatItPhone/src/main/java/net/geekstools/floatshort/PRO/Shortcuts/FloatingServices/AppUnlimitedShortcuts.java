/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/28/20 12:48 PM
 * Last modified 3/28/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts.FloatingServices;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.preference.PreferenceManager;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.Utils.FunctionsClassSecurity;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Utils.UI.Splash.FloatingSplash;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class AppUnlimitedShortcuts extends Service {

    FunctionsClass functionsClass;
    FunctionsClassSecurity functionsClassSecurity;

    WindowManager windowManager;

    WindowManager.LayoutParams[] layoutParams;
    WindowManager.LayoutParams[] stickyEdgeParams;
    WindowManager.LayoutParams moveDetection;

    int array;
    int xPosition, yPosition, xInitial = 13, yInitial = 13, xMove, yMove;

    ActivityInfo[] activityInformation;

    String[] packageNames, classNames;
    Drawable[] appIcons;
    int[] iconColors;
    boolean[] movePermit, removePermit, touchingDelay, stickedToEdge, openPermit;

    ViewGroup[] floatingView;
    ShapesImage[] shapedIcons, controlIcons, notificationDots;

    Runnable delayRunnable = null, getBackRunnable = null, runnablePressHold = null;
    Handler delayHandler = new Handler(), getBackHandler = new Handler(), handlerPressHold = new Handler();

    BroadcastReceiver broadcastReceiver;
    SharedPreferences sharedPrefPosition;

    Map<String, Integer> mapPackageNameStartId;

    LoadCustomIcons loadCustomIcons;

    GestureDetector.SimpleOnGestureListener[] simpleOnGestureListener;
    GestureDetector[] gestureDetector;

    FlingAnimation[] flingAnimationX, flingAnimationY;

    float flingPositionX = 0, flingPositionY = 0;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT: {
                for (int J = 1; J <= floatingView.length; J++) {
                    if (floatingView != null) {
                        if (floatingView[J].isShown()) {
                            layoutParams[J] = functionsClass.handleOrientationPortrait(classNames[J], layoutParams[J].height);
                            windowManager.updateViewLayout(floatingView[J], layoutParams[J]);
                        }
                    }
                }

                break;
            }
            case Configuration.ORIENTATION_LANDSCAPE: {
                for (int J = 1; J <= floatingView.length; J++) {
                    if (floatingView != null) {
                        if (floatingView[J].isShown()) {
                            layoutParams[J] = functionsClass.handleOrientationLandscape(classNames[J], layoutParams[J].height);
                            windowManager.updateViewLayout(floatingView[J], layoutParams[J]);
                        }
                    }
                }

                break;
            }
            default: {

                break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, final int serviceStartId) {

        if (functionsClass.customIconsEnable()) {
            if (loadCustomIcons == null) {
                loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
            }
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            packageNames[serviceStartId] = intent.getStringExtra("PackageName");
            if (!packageNames[serviceStartId].equals(getString(R.string.remove_all_floatings))) {
                classNames[serviceStartId] = intent.getStringExtra("ClassName");

                activityInformation[serviceStartId] = getPackageManager().getActivityInfo(new ComponentName(packageNames[serviceStartId], classNames[serviceStartId]), 0);

                floatingView[serviceStartId] = (ViewGroup) layoutInflater.inflate(R.layout.floating_shortcuts, null, false);
                controlIcons[serviceStartId] = functionsClass.initShapesImage(floatingView[serviceStartId], R.id.controlIcon);
                shapedIcons[serviceStartId] = functionsClass.initShapesImage(floatingView[serviceStartId], R.id.shapedIcon);
                notificationDots[serviceStartId] = functionsClass.initShapesImage(floatingView[serviceStartId],
                        functionsClass.checkStickyEdge() ? R.id.notificationDotEnd : R.id.notificationDotStart);

                movePermit[serviceStartId] = true;

                touchingDelay[serviceStartId] = false;
                stickedToEdge[serviceStartId] = false;
                openPermit[serviceStartId] = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Service.START_NOT_STICKY;
        }

        if (packageNames[serviceStartId].equals(getString(R.string.remove_all_floatings))) {
            for (int r = 1; r < serviceStartId; r++) {
                try {
                    if (floatingView != null) {
                        if (floatingView[r].isShown()) {
                            try {
                                windowManager.removeView(floatingView[r]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                PublicVariable.floatingCounter = PublicVariable.floatingCounter - 1;

                                if (PublicVariable.floatingCounter == 0) {
                                    if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                            .getBoolean("stable", true)) {
                                        stopService(new Intent(getApplicationContext(), BindServices.class));
                                    }
                                }
                            }
                        } else if (PublicVariable.floatingCounter == 0) {
                            if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .getBoolean("stable", true)) {
                                stopService(new Intent(getApplicationContext(), BindServices.class));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            PublicVariable.FloatingShortcuts.clear();
            PublicVariable.shortcutsCounter = -1;
            try {
                if (broadcastReceiver != null) {
                    try {
                        unregisterReceiver(broadcastReceiver);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopSelf();
            return START_NOT_STICKY;
        }

        mapPackageNameStartId.put(classNames[serviceStartId], serviceStartId);
        if (functionsClass.appIsInstalled(packageNames[serviceStartId]) == false) {
            return START_NOT_STICKY;
        }
        functionsClass.saveUnlimitedShortcutsService(packageNames[serviceStartId]);
        functionsClass.updateRecoverShortcuts();

        appIcons[serviceStartId] = functionsClass.shapedAppIcon(activityInformation[serviceStartId]);
        iconColors[serviceStartId] = functionsClass.extractDominantColor(functionsClass.appIcon(activityInformation[serviceStartId]));
        shapedIcons[serviceStartId].setImageDrawable(functionsClass.customIconsEnable() ?
                loadCustomIcons.getDrawableIconForPackage(packageNames[serviceStartId], functionsClass.shapedAppIcon(activityInformation[serviceStartId]))
                :
                functionsClass.shapedAppIcon(activityInformation[serviceStartId]));

        sharedPrefPosition = getSharedPreferences((classNames[serviceStartId]), MODE_PRIVATE);

        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);
        PublicVariable.HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size, getApplicationContext().getResources().getDisplayMetrics());

        xInitial = xInitial + 13;
        yInitial = yInitial + 13;
        xPosition = sharedPrefPosition.getInt("X", xInitial);
        yPosition = sharedPrefPosition.getInt("Y", yInitial);
        layoutParams[serviceStartId] = functionsClass.normalLayoutParams(PublicVariable.HW, xPosition, yPosition);
        try {
            floatingView[serviceStartId].setTag(serviceStartId);
            windowManager.addView(floatingView[serviceStartId], layoutParams[serviceStartId]);
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }

        xMove = xPosition;
        yMove = yPosition;

        shapedIcons[serviceStartId].setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));

        if (!functionsClass.litePreferencesEnabled()) {
            flingAnimationX[serviceStartId] = new FlingAnimation(new FloatValueHolder())
                    .setFriction(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f));
            flingAnimationY[serviceStartId] = new FlingAnimation(new FloatValueHolder())
                    .setFriction(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f));

            flingAnimationX[serviceStartId].setMaxValue(functionsClass.displayX() - PublicVariable.HW);
            flingAnimationX[serviceStartId].setMinValue(0);

            flingAnimationY[serviceStartId].setMaxValue(functionsClass.displayY() - PublicVariable.HW);
            flingAnimationY[serviceStartId].setMinValue(0);

            simpleOnGestureListener[serviceStartId] = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent motionEventFirst, MotionEvent motionEventLast, float velocityX, float velocityY) {

                    if (movePermit[serviceStartId]) {
                        flingAnimationX[serviceStartId].setStartVelocity(velocityX);
                        flingAnimationY[serviceStartId].setStartVelocity(velocityY);

                        flingAnimationX[serviceStartId].setStartValue(flingPositionX);
                        flingAnimationY[serviceStartId].setStartValue(flingPositionY);

                        try {
                            flingAnimationX[serviceStartId].addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                                @Override
                                public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                                    if (floatingView[serviceStartId].isShown()) {
                                        layoutParams[serviceStartId].x = (int) value;     // X movePoint
                                        windowManager.updateViewLayout(floatingView[serviceStartId], layoutParams[serviceStartId]);

                                        xMove = (int) value;
                                    }
                                }
                            });
                            flingAnimationY[serviceStartId].addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                                @Override
                                public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                                    if (floatingView[serviceStartId].isShown()) {
                                        layoutParams[serviceStartId].y = (int) value;     // Y movePoint
                                        windowManager.updateViewLayout(floatingView[serviceStartId], layoutParams[serviceStartId]);

                                        yMove = (int) value;
                                    }
                                }
                            });

                            flingAnimationX[serviceStartId].start();
                            flingAnimationY[serviceStartId].start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        openPermit[serviceStartId] = false;
                    }

                    return false;
                }
            };

            flingAnimationX[serviceStartId].addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    openPermit[serviceStartId] = true;
                }
            });

            flingAnimationY[serviceStartId].addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    openPermit[serviceStartId] = true;
                }
            });

            gestureDetector[serviceStartId] = new GestureDetector(getApplicationContext(), simpleOnGestureListener[serviceStartId]);
        }

        floatingView[serviceStartId].setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                try {
                    flingAnimationX[serviceStartId].cancel();
                    flingAnimationY[serviceStartId].cancel();
                    gestureDetector[serviceStartId].onTouchEvent(motionEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                WindowManager.LayoutParams layoutParamsOnTouch;
                if (stickedToEdge[serviceStartId] == true) {
                    layoutParamsOnTouch = stickyEdgeParams[serviceStartId];
                    layoutParamsOnTouch.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                } else {
                    layoutParamsOnTouch = layoutParams[serviceStartId];
                }

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = layoutParamsOnTouch.x;
                        initialY = layoutParamsOnTouch.y;

                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();

                        xMove = Math.round(initialTouchX);
                        yMove = Math.round(initialTouchY);

                        touchingDelay[serviceStartId] = true;
                        delayRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (touchingDelay[serviceStartId] == true) {
                                    removePermit[serviceStartId] = true;
                                    LayerDrawable drawClose = (LayerDrawable) getDrawable(R.drawable.draw_close_service);
                                    Drawable backPref = drawClose.findDrawableByLayerId(R.id.backgroundTemporary);
                                    backPref.setTint(iconColors[serviceStartId]);
                                    controlIcons[serviceStartId].setImageDrawable(drawClose);

                                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    vibrator.vibrate(100);
                                    sendBroadcast(new Intent("Hide_PopupListView_Shortcuts"));

                                    getBackRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (removePermit[serviceStartId] == true) {
                                                removePermit[serviceStartId] = false;
                                                controlIcons[serviceStartId].setImageDrawable(null);
                                            }
                                        }
                                    };
                                    getBackHandler.postDelayed(getBackRunnable, 3333 + functionsClass.readDefaultPreference("delayPressHold", 333));

                                }
                            }
                        };
                        delayHandler.postDelayed(delayRunnable, 3333 + functionsClass.readDefaultPreference("delayPressHold", 333));

                        runnablePressHold = new Runnable() {
                            @Override
                            public void run() {
                                if (touchingDelay[serviceStartId] == true) {
                                    functionsClass.PopupOptionShortcuts(
                                            floatingView[serviceStartId],
                                            packageNames[serviceStartId],
                                            classNames[serviceStartId],
                                            AppUnlimitedShortcuts.class.getSimpleName(),
                                            serviceStartId,
                                            initialX,
                                            initialY
                                    );

                                    openPermit[serviceStartId] = false;
                                }
                            }
                        };
                        handlerPressHold.postDelayed(runnablePressHold, functionsClass.readDefaultPreference("delayPressHold", 333));
                        break;
                    case MotionEvent.ACTION_UP:
                        touchingDelay[serviceStartId] = false;
                        delayHandler.removeCallbacks(delayRunnable);
                        handlerPressHold.removeCallbacks(runnablePressHold);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openPermit[serviceStartId] = true;
                            }
                        }, 113);

                        if (movePermit[serviceStartId] == true) {
                            layoutParamsOnTouch.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                            layoutParamsOnTouch.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                            FunctionsClassDebug.Companion.PrintDebug("X :: " + layoutParamsOnTouch.x + "\n" + " Y :: " + layoutParamsOnTouch.y);

                            xMove = Math.round(layoutParamsOnTouch.x);
                            yMove = Math.round(layoutParamsOnTouch.y);

                            SharedPreferences sharedPrefPosition = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            try {
                                sharedPrefPosition = getSharedPreferences((classNames[serviceStartId]), MODE_PRIVATE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            SharedPreferences.Editor editor = sharedPrefPosition.edit();
                            editor.putInt("X", layoutParamsOnTouch.x);
                            editor.putInt("Y", layoutParamsOnTouch.y);
                            editor.apply();
                        } else {
                            if (!functionsClass.litePreferencesEnabled()) {
                                float initialTouchXBoundBack = getSharedPreferences((classNames[serviceStartId]), MODE_PRIVATE).getInt("X", 0);
                                if (initialTouchXBoundBack < 0) {
                                    initialTouchXBoundBack = 0;
                                } else if (initialTouchXBoundBack > functionsClass.displayX()) {
                                    initialTouchXBoundBack = functionsClass.displayX();
                                }

                                float initialTouchYBoundBack = getSharedPreferences((classNames[serviceStartId]), MODE_PRIVATE).getInt("Y", 0);
                                if (initialTouchYBoundBack < 0) {
                                    initialTouchYBoundBack = 0;
                                } else if (initialTouchYBoundBack > functionsClass.displayY()) {
                                    initialTouchYBoundBack = functionsClass.displayY();
                                }

                                SpringForce springForceX = new SpringForce()
                                        .setFinalPosition(initialTouchXBoundBack)//EDIT HERE
                                        .setStiffness(SpringForce.STIFFNESS_MEDIUM)
                                        .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);

                                SpringForce springForceY = new SpringForce()
                                        .setFinalPosition(initialTouchYBoundBack)//EDIT HERE
                                        .setStiffness(SpringForce.STIFFNESS_MEDIUM)
                                        .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);

                                SpringAnimation springAnimationX = new SpringAnimation(new FloatValueHolder())
                                        .setMinValue(0)
                                        .setSpring(springForceX);

                                SpringAnimation springAnimationY = new SpringAnimation(new FloatValueHolder())
                                        .setMinValue(0)
                                        .setSpring(springForceY);

                                float springStartValueX = motionEvent.getRawX();
                                if (springStartValueX < 0f) {
                                    springStartValueX = 0;
                                } else if (springStartValueX > functionsClass.displayX()) {
                                    springStartValueX = functionsClass.displayX();
                                }

                                float springStartValueY = motionEvent.getRawY();
                                if (springStartValueY < 0f) {
                                    springStartValueY = 0;
                                } else if (springStartValueY > functionsClass.displayY()) {
                                    springStartValueY = functionsClass.displayY();
                                }

                                springAnimationX.setStartValue(springStartValueX);
                                springAnimationX.setStartVelocity(-0f);
                                springAnimationX.setMaxValue(functionsClass.displayX());

                                springAnimationY.setStartValue(springStartValueY);
                                springAnimationY.setStartVelocity(-0f);
                                springAnimationY.setMaxValue(functionsClass.displayY());

                                springAnimationX.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                                        if (floatingView[serviceStartId].isShown()) {
                                            layoutParamsOnTouch.x = (int) value;     // X movePoint
                                            windowManager.updateViewLayout(floatingView[serviceStartId], layoutParamsOnTouch);
                                        }
                                    }
                                });
                                springAnimationY.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                                        if (floatingView[serviceStartId].isShown()) {
                                            layoutParamsOnTouch.y = (int) value;     // Y movePoint
                                            windowManager.updateViewLayout(floatingView[serviceStartId], layoutParamsOnTouch);
                                        }
                                    }
                                });

                                springAnimationX.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                                        openPermit[serviceStartId] = true;
                                    }
                                });
                                springAnimationY.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                                        openPermit[serviceStartId] = true;
                                    }
                                });

                                springAnimationX.start();
                                springAnimationY.start();
                            }
                        }

                        moveDetection = layoutParamsOnTouch;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (movePermit[serviceStartId] == true) {
                            layoutParamsOnTouch.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                            layoutParamsOnTouch.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(floatingView[serviceStartId], layoutParamsOnTouch);
                            moveDetection = layoutParamsOnTouch;

                            int difMoveX = (int) (layoutParamsOnTouch.x - initialTouchX);
                            int difMoveY = (int) (layoutParamsOnTouch.y - initialTouchY);
                            if (Math.abs(difMoveX) > Math.abs(PublicVariable.HW + ((PublicVariable.HW * 70) / 100))
                                    || Math.abs(difMoveY) > Math.abs(PublicVariable.HW + ((PublicVariable.HW * 70) / 100))) {
                                sendBroadcast(new Intent("Hide_PopupListView_Shortcuts"));

                                openPermit[serviceStartId] = false;
                                touchingDelay[serviceStartId] = false;
                                delayHandler.removeCallbacks(delayRunnable);
                                handlerPressHold.removeCallbacks(runnablePressHold);
                            }

                            flingPositionX = layoutParamsOnTouch.x;
                            flingPositionY = layoutParamsOnTouch.y;
                        } else {
                            if (!functionsClass.litePreferencesEnabled()) {
                                layoutParamsOnTouch.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);     // X movePoint
                                layoutParamsOnTouch.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);     // Y movePoint
                                windowManager.updateViewLayout(floatingView[serviceStartId], layoutParamsOnTouch);

                                int difMoveX = (int) (layoutParamsOnTouch.x - initialTouchX);
                                int difMoveY = (int) (layoutParamsOnTouch.y - initialTouchY);
                                if (Math.abs(difMoveX) > Math.abs(PublicVariable.HW + ((PublicVariable.HW * 70) / 100))
                                        || Math.abs(difMoveY) > Math.abs(PublicVariable.HW + ((PublicVariable.HW * 70) / 100))) {
                                    sendBroadcast(new Intent("Hide_PopupListView_Shortcuts"));

                                    openPermit[serviceStartId] = false;
                                    touchingDelay[serviceStartId] = false;
                                    delayHandler.removeCallbacks(delayRunnable);
                                    handlerPressHold.removeCallbacks(runnablePressHold);
                                }
                            }
                        }
                        break;
                }
                return false;
            }
        });
        floatingView[serviceStartId].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (removePermit[serviceStartId] == true) {
                    if (floatingView[serviceStartId] == null) {
                        return;
                    }
                    if (floatingView[serviceStartId].isShown()) {
                        try {
                            windowManager.removeView(floatingView[serviceStartId]);
                            getBackHandler.removeCallbacks(getBackRunnable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            PublicVariable.FloatingShortcuts.remove(packageNames[serviceStartId]);
                            PublicVariable.floatingCounter = PublicVariable.floatingCounter - 1;
                            PublicVariable.shortcutsCounter = PublicVariable.shortcutsCounter - 1;

                            if (PublicVariable.floatingCounter == 0) {
                                if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                        .getBoolean("stable", true)) {
                                    stopService(new Intent(getApplicationContext(), BindServices.class));
                                }
                                if (broadcastReceiver != null) {
                                    try {
                                        unregisterReceiver(broadcastReceiver);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                                stopSelf();
                            }
                        }
                    }
                } else {
                    if (openPermit[serviceStartId]) {
                        if (functionsClassSecurity.isAppLocked(packageNames[serviceStartId])) {
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthFloatingShortcuts(true);

                            FunctionsClassSecurity.AuthOpenAppValues.setAuthComponentName(packageNames[serviceStartId]);
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthSecondComponentName(classNames[serviceStartId]);

                            if (moveDetection != null) {
                                FunctionsClassSecurity.AuthOpenAppValues.setAuthPositionX(moveDetection.x);
                                FunctionsClassSecurity.AuthOpenAppValues.setAuthPositionY(moveDetection.y);
                            } else {
                                FunctionsClassSecurity.AuthOpenAppValues.setAuthPositionX(layoutParams[serviceStartId].x);
                                FunctionsClassSecurity.AuthOpenAppValues.setAuthPositionY(layoutParams[serviceStartId].y);
                            }
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthHW(layoutParams[serviceStartId].width);

                            functionsClassSecurity.openAuthInvocation();
                        } else {
                            if (functionsClass.splashReveal()) {
                                Intent splashReveal = new Intent(getApplicationContext(), FloatingSplash.class);
                                splashReveal.putExtra("packageName", packageNames[serviceStartId]);
                                splashReveal.putExtra("className", classNames[serviceStartId]);
                                if (moveDetection != null) {
                                    splashReveal.putExtra("X", moveDetection.x);
                                    splashReveal.putExtra("Y", moveDetection.y);
                                } else {
                                    splashReveal.putExtra("X", layoutParams[serviceStartId].x);
                                    splashReveal.putExtra("Y", layoutParams[serviceStartId].y);
                                }
                                splashReveal.putExtra("HW", layoutParams[serviceStartId].width);
                                startService(splashReveal);
                            } else {
                                if (functionsClass.FreeForm()) {
                                    functionsClass.openApplicationFreeForm(packageNames[serviceStartId],
                                            classNames[serviceStartId],
                                            layoutParams[serviceStartId].x,
                                            (functionsClass.displayX() / 2),
                                            layoutParams[serviceStartId].y,
                                            (functionsClass.displayY() / 2)
                                    );
                                } else {
                                    functionsClass.appsLaunchPad(packageNames[serviceStartId], classNames[serviceStartId]);
                                }
                            }
                        }
                    } else {

                    }
                }
            }
        });

        notificationDots[serviceStartId].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.PopupNotificationShortcuts(
                        floatingView[serviceStartId],
                        packageNames[serviceStartId],
                        AppUnlimitedShortcuts.class.getSimpleName(),
                        serviceStartId,
                        iconColors[serviceStartId],
                        xMove,
                        yMove,
                        layoutParams[serviceStartId].width
                );
            }
        });
        notificationDots[serviceStartId].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (functionsClass.AccessibilityServiceEnabled() && functionsClass.SettingServiceRunning(InteractionObserver.class)) {
                    functionsClass.sendInteractionObserverEvent(view, packageNames[serviceStartId], AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, 66666);
                } else {
                    try {
                        Object sbservice = getSystemService("statusbar");
                        Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                        Method showsb = statusbarManager.getMethod("expandNotificationsPanel");//expandNotificationsPanel
                        showsb.invoke(sbservice);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Object sbservice = getSystemService("statusbar");
                            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                            Method showsb = statusbarManager.getMethod("expand");//expandNotificationsPanel
                            showsb.invoke(sbservice);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return true;
            }
        });

        final String className = AppUnlimitedShortcuts.class.getSimpleName();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Split_Apps_Single_" + className);
        intentFilter.addAction("Pin_App_" + className);
        intentFilter.addAction("Unpin_App_" + className);
        intentFilter.addAction("Float_It_" + className);
        intentFilter.addAction("Remove_App_" + className);
        intentFilter.addAction("Sticky_Edge");
        intentFilter.addAction("Sticky_Edge_No");
        intentFilter.addAction("Notification_Dot");
        intentFilter.addAction("Notification_Dot_No");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                if (intent.getAction().equals("Split_Apps_Single_" + className) && PublicVariable.splitScreen == true) {
                    FunctionsClassDebug.Companion.PrintDebug("Split Apps Single");
                    PublicVariable.splitScreen = false;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent splitSingle = new Intent();
                                if (PublicVariable.splitSingleClassName != null) {
                                    splitSingle.setClassName(PublicVariable.splitSinglePackage, PublicVariable.splitSingleClassName);
                                } else {
                                    splitSingle = getPackageManager().getLaunchIntentForPackage(PublicVariable.splitSinglePackage);
                                }
                                splitSingle.setFlags(
                                        Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                startActivity(splitSingle);
                                PublicVariable.splitScreen = true;

                                functionsClass.Toast(functionsClass.appName(packageNames[intent.getIntExtra("startId", 1)]), Gravity.TOP);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                } else if (intent.getAction().equals("Pin_App_" + className)) {
                    FunctionsClassDebug.Companion.PrintDebug(functionsClass.appName(packageNames[intent.getIntExtra("startId", 1)]));
                    movePermit[intent.getIntExtra("startId", 1)] = false;

                    Drawable pinDrawable = null;
                    if (functionsClass.customIconsEnable()) {
                        pinDrawable = functionsClass.getAppIconDrawableCustomIcon(packageNames[intent.getIntExtra("startId", 1)]).mutate();
                    } else {
                        switch (functionsClass.shapesImageId()) {
                            case 1:
                                pinDrawable = getDrawable(R.drawable.pin_droplet_icon);
                                controlIcons[intent.getIntExtra("startId", 1)].setPadding(-3, -3, -3, -3);
                                break;
                            case 2:
                                pinDrawable = getDrawable(R.drawable.pin_circle_icon);
                                break;
                            case 3:
                                pinDrawable = getDrawable(R.drawable.pin_square_icon);
                                break;
                            case 4:
                                pinDrawable = getDrawable(R.drawable.pin_squircle_icon);
                                break;
                            case 0:
                                pinDrawable = functionsClass.appIcon(activityInformation[intent.getIntExtra("startId", 1)]).mutate();
                                break;
                        }
                    }
                    pinDrawable.setTint(functionsClass.setColorAlpha(Color.RED, 175));
                    if (functionsClass.returnAPI() >= 26) {
                        pinDrawable.setAlpha(175);

                        pinDrawable.setTint(Color.RED);
                        controlIcons[intent.getIntExtra("startId", 1)].setAlpha(0.50f);
                    }
                    controlIcons[intent.getIntExtra("startId", 1)].setImageDrawable(pinDrawable);
                } else if (intent.getAction().equals("Unpin_App_" + className)) {
                    FunctionsClassDebug.Companion.PrintDebug(functionsClass.appName(packageNames[intent.getIntExtra("startId", 1)]));
                    movePermit[intent.getIntExtra("startId", 1)] = true;
                    controlIcons[intent.getIntExtra("startId", 1)].setImageDrawable(null);
                } else if (intent.getAction().equals("Float_It_" + className)) {
                    if (functionsClassSecurity.isAppLocked(packageNames[intent.getIntExtra("startId", 1)])) {
                        FunctionsClassSecurity.AuthOpenAppValues.setAuthFloatingShortcuts(true);

                        FunctionsClassSecurity.AuthOpenAppValues.setAuthComponentName(packageNames[intent.getIntExtra("startId", 1)]);
                        FunctionsClassSecurity.AuthOpenAppValues.setAuthSecondComponentName(classNames[intent.getIntExtra("startId", 1)]);

                        if (moveDetection != null) {
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthPositionX(moveDetection.x);
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthPositionY(moveDetection.y);
                        } else {
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthPositionX(layoutParams[intent.getIntExtra("startId", 1)].x);
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthPositionY(layoutParams[intent.getIntExtra("startId", 1)].y);
                        }
                        FunctionsClassSecurity.AuthOpenAppValues.setAuthHW(layoutParams[intent.getIntExtra("startId", 1)].width);

                        functionsClassSecurity.openAuthInvocation();
                    } else {
                        if (functionsClass.splashReveal()) {
                            if (!functionsClass.FreeForm()) {
                                functionsClass.saveDefaultPreference("freeForm", true);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        functionsClass.saveDefaultPreference("freeForm", false);
                                    }
                                }, 1000);
                            }

                            Intent splashReveal = new Intent(getApplicationContext(), FloatingSplash.class);
                            splashReveal.putExtra("packageName", packageNames[intent.getIntExtra("startId", 1)]);
                            splashReveal.putExtra("className", classNames[intent.getIntExtra("startId", 1)]);
                            if (moveDetection != null) {
                                splashReveal.putExtra("X", moveDetection.x);
                                splashReveal.putExtra("Y", moveDetection.y);
                            } else {
                                splashReveal.putExtra("X", layoutParams[intent.getIntExtra("startId", 1)].x);
                                splashReveal.putExtra("Y", layoutParams[intent.getIntExtra("startId", 1)].y);
                            }
                            splashReveal.putExtra("HW", layoutParams[intent.getIntExtra("startId", 1)].width);
                            startService(splashReveal);
                        } else {
                            functionsClass.openApplicationFreeForm(packageNames[intent.getIntExtra("startId", 1)],
                                    classNames[intent.getIntExtra("startId", 1)],
                                    layoutParams[intent.getIntExtra("startId", 1)].x,
                                    (functionsClass.displayX() / 2),
                                    layoutParams[intent.getIntExtra("startId", 1)].y,
                                    (functionsClass.displayY() / 2)
                            );
                        }
                    }
                } else if (intent.getAction().equals("Remove_App_" + className)) {
                    if (floatingView[intent.getIntExtra("startId", 1)] == null) {
                        return;
                    }
                    if (floatingView[intent.getIntExtra("startId", 1)].isShown()) {
                        try {
                            windowManager.removeView(floatingView[intent.getIntExtra("startId", 1)]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            PublicVariable.FloatingShortcuts.remove(packageNames[intent.getIntExtra("startId", 1)]);
                            PublicVariable.floatingCounter = PublicVariable.floatingCounter - 1;
                            PublicVariable.shortcutsCounter = PublicVariable.shortcutsCounter - 1;

                            if (PublicVariable.floatingCounter == 0) {
                                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                        .getBoolean("stable", true) == false) {
                                    stopService(new Intent(getApplicationContext(), BindServices.class));
                                }
                                if (broadcastReceiver != null) {
                                    try {
                                        unregisterReceiver(broadcastReceiver);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                                stopSelf();
                            }
                        }
                    }
                } else if (intent.getAction().equals("Sticky_Edge")) {
                    for (int r = 1; r <= serviceStartId; r++) {
                        try {
                            if (floatingView != null) {
                                if (floatingView[r].isShown()) {
                                    try {
                                        stickedToEdge[r] = true;
                                        stickyEdgeParams[r] = functionsClass.moveToEdge(AppUnlimitedShortcuts.this.classNames[r], layoutParams[r].height);
                                        windowManager.updateViewLayout(floatingView[r], stickyEdgeParams[r]);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (intent.getAction().equals("Sticky_Edge_No")) {
                    for (int r = 1; r <= serviceStartId; r++) {
                        try {
                            if (floatingView != null) {
                                if (floatingView[r].isShown()) {
                                    try {
                                        try {
                                            sharedPrefPosition = getSharedPreferences((AppUnlimitedShortcuts.this.classNames[r]), MODE_PRIVATE);

                                            stickedToEdge[r] = false;
                                            xPosition = sharedPrefPosition.getInt("X", xInitial);
                                            yPosition = sharedPrefPosition.getInt("Y", yInitial);
                                            windowManager.updateViewLayout(floatingView[r], functionsClass.backFromEdge(layoutParams[r].height, xPosition, yPosition));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (intent.getAction().equals("Notification_Dot")) {
                    try {
                        String notificationPackage = intent.getStringExtra("NotificationPackage");
                        int StartIdNotification = mapPackageNameStartId.get(notificationPackage);
                        if (floatingView[StartIdNotification] != null) {
                            if (floatingView[StartIdNotification].isShown()) {
                                /*add dot*/
                                Drawable dotDrawable = null;
                                if (functionsClass.customIconsEnable()) {
                                    dotDrawable = functionsClass.getAppIconDrawableCustomIcon(packageNames[StartIdNotification]).mutate();
                                } else {
                                    switch (functionsClass.shapesImageId()) {
                                        case 1:
                                            dotDrawable = getDrawable(R.drawable.dot_droplet_icon);
                                            break;
                                        case 2:
                                            dotDrawable = getDrawable(R.drawable.dot_circle_icon);
                                            break;
                                        case 3:
                                            dotDrawable = getDrawable(R.drawable.dot_square_icon);
                                            break;
                                        case 4:
                                            dotDrawable = getDrawable(R.drawable.dot_squircle_icon);
                                            break;
                                        case 0:
                                            dotDrawable = functionsClass.appIcon(packageNames[StartIdNotification]).mutate();
                                            break;
                                    }
                                }
                                if (PublicVariable.themeLightDark) {
                                    dotDrawable.setTint(functionsClass.manipulateColor(functionsClass.extractVibrantColor(functionsClass.appIcon(packageNames[StartIdNotification])), 1.30f));
                                } else {
                                    dotDrawable.setTint(functionsClass.manipulateColor(functionsClass.extractVibrantColor(functionsClass.appIcon(packageNames[StartIdNotification])), 0.50f));
                                }
                                notificationDots[StartIdNotification].setImageDrawable(dotDrawable);
                                notificationDots[StartIdNotification].setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (intent.getAction().equals("Notification_Dot_No")) {
                    try {
                        String notificationPackage = intent.getStringExtra("NotificationPackage");
                        int StartIdNotification = mapPackageNameStartId.get(notificationPackage);
                        if (floatingView[StartIdNotification] != null) {
                            if (floatingView[StartIdNotification].isShown()) {
                                /*remove dot*/
                                notificationDots[StartIdNotification].setVisibility(View.INVISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        if (getFileStreamPath(packageNames[serviceStartId] + "_" + "Notification" + "Package").exists()) {
            sendBroadcast(new Intent("Notification_Dot").putExtra("NotificationPackage", packageNames[serviceStartId]));
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClass = new FunctionsClass(getApplicationContext());
        functionsClassSecurity = new FunctionsClassSecurity(getApplicationContext());

        array = getApplicationContext().getPackageManager().getInstalledApplications(0).size() * 2;
        layoutParams = new WindowManager.LayoutParams[array];
        stickyEdgeParams = new WindowManager.LayoutParams[array];
        packageNames = new String[array];
        classNames = new String[array];
        appIcons = new Drawable[array];
        iconColors = new int[array];
        floatingView = new ViewGroup[array];
        controlIcons = new ShapesImage[array];
        shapedIcons = new ShapesImage[array];
        notificationDots = new ShapesImage[array];
        movePermit = new boolean[array];
        removePermit = new boolean[array];
        touchingDelay = new boolean[array];
        stickedToEdge = new boolean[array];
        openPermit = new boolean[array];
        if (!functionsClass.litePreferencesEnabled()) {
            simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener[array];
            gestureDetector = new GestureDetector[array];

            flingAnimationX = new FlingAnimation[array];
            flingAnimationY = new FlingAnimation[array];
        }

        activityInformation = new ActivityInfo[array];

        mapPackageNameStartId = new LinkedHashMap<String, Integer>();

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }
    }
}
