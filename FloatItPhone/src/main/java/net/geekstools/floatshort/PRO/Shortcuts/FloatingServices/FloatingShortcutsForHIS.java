/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:57 AM
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
import net.geekstools.floatshort.PRO.Utils.Functions.Debug;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Utils.UI.Splash.FloatingSplash;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class FloatingShortcutsForHIS extends Service {

    FunctionsClassLegacy functionsClassLegacy;

    WindowManager windowManager;
    WindowManager.LayoutParams[] layoutParams;
    WindowManager.LayoutParams[] StickyEdgeParams;
    WindowManager.LayoutParams moveDetection;

    int array, xPos, yPos, xInit = 13, yInit = 13, xMove, yMove;

    ComponentName[] componentName;
    ActivityInfo[] activityInfo;

    String[] packageNames, classNames;
    Drawable[] appIcon;
    int[] iconColor;
    boolean[] allowMove, remove, touchingDelay, StickyEdge, openIt;

    ViewGroup[] floatingView;
    ShapesImage[] shapedIcon, controlIcon, notificationDot;

    Runnable delayRunnable = null, getbackRunnable = null, runnablePressHold = null;
    Handler delayHandler = new Handler(), getbackHandler = new Handler(), handlerPressHold = new Handler();

    BroadcastReceiver broadcastReceiver;
    SharedPreferences sharedPrefPosition;

    Map<String, Integer> mapPackageNameStartId;

    LoadCustomIcons loadCustomIcons;

    GestureDetector.SimpleOnGestureListener[] simpleOnGestureListener;
    GestureDetector[] gestureDetector;

    FlingAnimation[] flingAnimationX, flingAnimationY;

    float flingPositionX = 0, flingPositionY = 0;

    int startIdCounter = 1;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT: {
                for (int J = 1; J <= startIdCounter; J++) {
                    try {
                        if (floatingView != null) {
                            if (floatingView[J].isShown()) {
                                layoutParams[J] = functionsClassLegacy.handleOrientationPortrait(classNames[J], layoutParams[J].height);
                                windowManager.updateViewLayout(floatingView[J], layoutParams[J]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
            case Configuration.ORIENTATION_LANDSCAPE: {
                for (int J = 1; J <= startIdCounter; J++) {
                    try {
                        if (floatingView != null) {
                            if (floatingView[J].isShown()) {
                                layoutParams[J] = functionsClassLegacy.handleOrientationLandscape(classNames[J], layoutParams[J].height);
                                windowManager.updateViewLayout(floatingView[J], layoutParams[J]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
    public int onStartCommand(Intent intent, final int flags, final int startId) {
        Debug.Companion.PrintDebug(this.getClass().getSimpleName() + " ::: StartId ::: " + startId);

        startIdCounter = startId;

        if (functionsClassLegacy.customIconsEnable()) {
            if (loadCustomIcons == null) {
                loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClassLegacy.customIconPackageName());
            }
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            allowMove[startId] = true;
            packageNames[startId] = intent.getStringExtra("PackageName");

            classNames[startId] = intent.getStringExtra("ClassName");

            componentName[startId] = new ComponentName(packageNames[startId], classNames[startId]);
            activityInfo[startId] = getPackageManager().getActivityInfo(componentName[startId], 0);

            floatingView[startId] = (ViewGroup) layoutInflater.inflate(R.layout.floating_shortcuts, null, false);
            controlIcon[startId] = functionsClassLegacy.initShapesImage(floatingView[startId], R.id.controlIcon);
            shapedIcon[startId] = functionsClassLegacy.initShapesImage(floatingView[startId], R.id.shapedIcon);
            notificationDot[startId] = functionsClassLegacy.initShapesImage(floatingView[startId],
                    functionsClassLegacy.checkStickyEdge() ? R.id.notificationDotEnd : R.id.notificationDotStart);

            touchingDelay[startId] = false;
            StickyEdge[startId] = false;
            openIt[startId] = true;
        } catch (Exception e) {
            e.printStackTrace();
            return Service.START_NOT_STICKY;
        }

        if (intent.hasExtra(getString(R.string.remove_all_floatings))) {
            if (intent.getStringExtra(getString(R.string.remove_all_floatings)).equals(getString(R.string.remove_all_floatings))) {
                for (int r = 1; r < startId; r++) {
                    try {
                        if (floatingView != null) {
                            if (floatingView[r].isShown()) {
                                try {
                                    windowManager.removeView(floatingView[r]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1;

                                    if (PublicVariable.allFloatingCounter == 0) {
                                        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                                .getBoolean("stable", true) == false) {
                                            stopService(new Intent(getApplicationContext(), BindServices.class));
                                        }
                                    }
                                }
                            } else if (PublicVariable.allFloatingCounter == 0) {
                                if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                        .getBoolean("stable", true) == false) {
                                    stopService(new Intent(getApplicationContext(), BindServices.class));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
        }

        mapPackageNameStartId.put(packageNames[startId], startId);

        if (functionsClassLegacy.appIsInstalled(packageNames[startId]) == false) {
            return START_NOT_STICKY;
        }

        appIcon[startId] = functionsClassLegacy.shapedAppIcon(activityInfo[startId]);
        iconColor[startId] = functionsClassLegacy.extractDominantColor(functionsClassLegacy.applicationIcon(activityInfo[startId]));
        shapedIcon[startId].setImageDrawable(functionsClassLegacy.customIconsEnable() ?
                loadCustomIcons.getDrawableIconForPackage(packageNames[startId], functionsClassLegacy.shapedAppIcon(activityInfo[startId]))
                :
                functionsClassLegacy.shapedAppIcon(activityInfo[startId]));

        try {
            sharedPrefPosition = getSharedPreferences((classNames[startId]), MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        xInit = xInit + 13;
        yInit = yInit + 13;
        xPos = sharedPrefPosition.getInt("X", xInit);
        yPos = sharedPrefPosition.getInt("Y", yInit);
        layoutParams[startId] = functionsClassLegacy.normalLayoutParams(PublicVariable.floatingViewsHW, xPos, yPos);
        try {
            floatingView[startId].setTag(startId);
            windowManager.addView(floatingView[startId], layoutParams[startId]);
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }

        xMove = xPos;
        yMove = yPos;

        shapedIcon[startId].setImageAlpha(functionsClassLegacy.readDefaultPreference("autoTrans", 255));

        if (!functionsClassLegacy.litePreferencesEnabled()) {
            flingAnimationX[startId] = new FlingAnimation(new FloatValueHolder())
                    .setFriction(functionsClassLegacy.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f));
            flingAnimationY[startId] = new FlingAnimation(new FloatValueHolder())
                    .setFriction(functionsClassLegacy.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f));

            flingAnimationX[startId].setMaxValue(functionsClassLegacy.displayX() - PublicVariable.floatingViewsHW);
            flingAnimationX[startId].setMinValue(0);

            flingAnimationY[startId].setMaxValue(functionsClassLegacy.displayY() - PublicVariable.floatingViewsHW);
            flingAnimationY[startId].setMinValue(0);

            simpleOnGestureListener[startId] = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent motionEventFirst, MotionEvent motionEventLast, float velocityX, float velocityY) {

                    if (allowMove[startId]) {
                        flingAnimationX[startId].setStartVelocity(velocityX);
                        flingAnimationY[startId].setStartVelocity(velocityY);

                        flingAnimationX[startId].setStartValue(flingPositionX);
                        flingAnimationY[startId].setStartValue(flingPositionY);

                        try {
                            flingAnimationX[startId].addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                                @Override
                                public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                                    if (floatingView[startId].isShown()) {
                                        layoutParams[startId].x = (int) value;     // X movePoint
                                        windowManager.updateViewLayout(floatingView[startId], layoutParams[startId]);

                                        xMove = (int) value;
                                    }
                                }
                            });
                            flingAnimationY[startId].addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                                @Override
                                public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                                    if (floatingView[startId].isShown()) {
                                        layoutParams[startId].y = (int) value;     // Y movePoint
                                        windowManager.updateViewLayout(floatingView[startId], layoutParams[startId]);

                                        yMove = (int) value;
                                    }
                                }
                            });

                            flingAnimationX[startId].start();
                            flingAnimationY[startId].start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        openIt[startId] = false;
                    }

                    return false;
                }
            };

            flingAnimationX[startId].addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    openIt[startId] = true;
                }
            });

            flingAnimationY[startId].addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    openIt[startId] = true;
                }
            });

            gestureDetector[startId] = new GestureDetector(getApplicationContext(), simpleOnGestureListener[startId]);
        }

        floatingView[startId].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        floatingView[startId].setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                try {
                    flingAnimationX[startId].cancel();
                    flingAnimationY[startId].cancel();
                    gestureDetector[startId].onTouchEvent(motionEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                WindowManager.LayoutParams layoutParamsOnTouch;
                if (StickyEdge[startId] == true) {
                    layoutParamsOnTouch = StickyEdgeParams[startId];
                    layoutParamsOnTouch.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                } else {
                    layoutParamsOnTouch = layoutParams[startId];
                }

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = layoutParamsOnTouch.x;
                        initialY = layoutParamsOnTouch.y;

                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();

                        xMove = Math.round(initialTouchX);
                        yMove = Math.round(initialTouchY);

                        touchingDelay[startId] = true;
                        delayRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (touchingDelay[startId] == true) {
                                    remove[startId] = true;
                                    LayerDrawable drawClose = (LayerDrawable) getDrawable(R.drawable.draw_close_service);
                                    Drawable backPref = drawClose.findDrawableByLayerId(R.id.backgroundTemporary);
                                    backPref.setTint(iconColor[startId]);
                                    controlIcon[startId].setImageDrawable(drawClose);

                                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    vibrator.vibrate(100);
                                    sendBroadcast(new Intent("Hide_PopupListView_Shortcuts"));

                                    getbackRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (remove[startId] == true) {
                                                remove[startId] = false;
                                                controlIcon[startId].setImageDrawable(null);
                                            }
                                        }
                                    };
                                    getbackHandler.postDelayed(getbackRunnable, 3333 + functionsClassLegacy.readDefaultPreference("delayPressHold", 333));

                                }
                            }
                        };
                        delayHandler.postDelayed(delayRunnable, 3333 + functionsClassLegacy.readDefaultPreference("delayPressHold", 333));

                        runnablePressHold = new Runnable() {
                            @Override
                            public void run() {
                                if (touchingDelay[startId] == true) {
                                    functionsClassLegacy.PopupOptionShortcuts(
                                            floatingView[startId],
                                            packageNames[startId],
                                            classNames[startId],
                                            FloatingShortcutsForHIS.class.getSimpleName(),
                                            startId,
                                            initialX,
                                            initialY
                                    );

                                    openIt[startId] = false;
                                }
                            }
                        };
                        handlerPressHold.postDelayed(runnablePressHold, functionsClassLegacy.readDefaultPreference("delayPressHold", 333));
                        break;
                    case MotionEvent.ACTION_UP:
                        touchingDelay[startId] = false;
                        delayHandler.removeCallbacks(delayRunnable);
                        handlerPressHold.removeCallbacks(runnablePressHold);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openIt[startId] = true;
                            }
                        }, 113);

                        if (allowMove[startId] == true) {
                            layoutParamsOnTouch.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                            layoutParamsOnTouch.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                            Debug.Companion.PrintDebug("X :: " + layoutParamsOnTouch.x + "\n" + " Y :: " + layoutParamsOnTouch.y);

                            xMove = Math.round(layoutParamsOnTouch.x);
                            yMove = Math.round(layoutParamsOnTouch.y);

                            SharedPreferences sharedPrefPosition = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            try {
                                sharedPrefPosition = getSharedPreferences((classNames[startId]), MODE_PRIVATE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            SharedPreferences.Editor editor = sharedPrefPosition.edit();
                            editor.putInt("X", layoutParamsOnTouch.x);
                            editor.putInt("Y", layoutParamsOnTouch.y);
                            editor.apply();
                        } else {
                            if (!functionsClassLegacy.litePreferencesEnabled()) {
                                float initialTouchXBoundBack = getSharedPreferences((classNames[startId]), MODE_PRIVATE).getInt("X", 0);
                                if (initialTouchXBoundBack < 0) {
                                    initialTouchXBoundBack = 0;
                                } else if (initialTouchXBoundBack > functionsClassLegacy.displayX()) {
                                    initialTouchXBoundBack = functionsClassLegacy.displayX();
                                }

                                float initialTouchYBoundBack = getSharedPreferences((classNames[startId]), MODE_PRIVATE).getInt("Y", 0);
                                if (initialTouchYBoundBack < 0) {
                                    initialTouchYBoundBack = 0;
                                } else if (initialTouchYBoundBack > functionsClassLegacy.displayY()) {
                                    initialTouchYBoundBack = functionsClassLegacy.displayY();
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
                                } else if (springStartValueX > functionsClassLegacy.displayX()) {
                                    springStartValueX = functionsClassLegacy.displayX();
                                }

                                float springStartValueY = motionEvent.getRawY();
                                if (springStartValueY < 0f) {
                                    springStartValueY = 0;
                                } else if (springStartValueY > functionsClassLegacy.displayY()) {
                                    springStartValueY = functionsClassLegacy.displayY();
                                }

                                springAnimationX.setStartValue(springStartValueX);
                                springAnimationX.setStartVelocity(-0f);
                                springAnimationX.setMaxValue(functionsClassLegacy.displayX());

                                springAnimationY.setStartValue(springStartValueY);
                                springAnimationY.setStartVelocity(-0f);
                                springAnimationY.setMaxValue(functionsClassLegacy.displayY());

                                springAnimationX.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                                        if (floatingView[startId].isShown()) {
                                            layoutParamsOnTouch.x = (int) value;     // X movePoint
                                            windowManager.updateViewLayout(floatingView[startId], layoutParamsOnTouch);
                                        }
                                    }
                                });
                                springAnimationY.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                                        if (floatingView[startId].isShown()) {
                                            layoutParamsOnTouch.y = (int) value;     // Y movePoint
                                            windowManager.updateViewLayout(floatingView[startId], layoutParamsOnTouch);
                                        }
                                    }
                                });

                                springAnimationX.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                                        openIt[startId] = true;
                                    }
                                });
                                springAnimationY.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                                        openIt[startId] = true;
                                    }
                                });

                                springAnimationX.start();
                                springAnimationY.start();
                            }
                        }

                        moveDetection = layoutParamsOnTouch;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (allowMove[startId] == true) {
                            layoutParamsOnTouch.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                            layoutParamsOnTouch.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(floatingView[startId], layoutParamsOnTouch);
                            moveDetection = layoutParamsOnTouch;

                            int difMoveX = (int) (layoutParamsOnTouch.x - initialTouchX);
                            int difMoveY = (int) (layoutParamsOnTouch.y - initialTouchY);
                            if (Math.abs(difMoveX) > Math.abs(PublicVariable.floatingViewsHW + ((PublicVariable.floatingViewsHW * 70) / 100))
                                    || Math.abs(difMoveY) > Math.abs(PublicVariable.floatingViewsHW + ((PublicVariable.floatingViewsHW * 70) / 100))) {
                                sendBroadcast(new Intent("Hide_PopupListView_Shortcuts"));

                                openIt[startId] = false;
                                touchingDelay[startId] = false;
                                delayHandler.removeCallbacks(delayRunnable);
                                handlerPressHold.removeCallbacks(runnablePressHold);
                            }

                            flingPositionX = layoutParamsOnTouch.x;
                            flingPositionY = layoutParamsOnTouch.y;
                        } else {
                            if (!functionsClassLegacy.litePreferencesEnabled()) {
                                layoutParamsOnTouch.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);     // X movePoint
                                layoutParamsOnTouch.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);     // Y movePoint
                                windowManager.updateViewLayout(floatingView[startId], layoutParamsOnTouch);

                                int difMoveX = (int) (layoutParamsOnTouch.x - initialTouchX);
                                int difMoveY = (int) (layoutParamsOnTouch.y - initialTouchY);
                                if (Math.abs(difMoveX) > Math.abs(PublicVariable.floatingViewsHW + ((PublicVariable.floatingViewsHW * 70) / 100))
                                        || Math.abs(difMoveY) > Math.abs(PublicVariable.floatingViewsHW + ((PublicVariable.floatingViewsHW * 70) / 100))) {
                                    sendBroadcast(new Intent("Hide_PopupListView_Shortcuts"));

                                    openIt[startId] = false;
                                    touchingDelay[startId] = false;
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
        floatingView[startId].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remove[startId] == true) {
                    if (floatingView[startId] == null) {
                        return;
                    }
                    if (floatingView[startId].isShown()) {
                        try {
                            windowManager.removeView(floatingView[startId]);
                            getbackHandler.removeCallbacks(getbackRunnable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1;

                            if (PublicVariable.allFloatingCounter == 0) {
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
                } else {
                    if (openIt[startId]) {
                        if (functionsClassLegacy.splashReveal()) {
                            Intent splashReveal = new Intent(getApplicationContext(), FloatingSplash.class);
                            splashReveal.putExtra("packageName", packageNames[startId]);
                            splashReveal.putExtra("className", classNames[startId]);
                            if (moveDetection != null) {
                                splashReveal.putExtra("X", moveDetection.x);
                                splashReveal.putExtra("Y", moveDetection.y);
                            } else {
                                splashReveal.putExtra("X", layoutParams[startId].x);
                                splashReveal.putExtra("Y", layoutParams[startId].y);
                            }
                            splashReveal.putExtra("HW", layoutParams[startId].width);
                            startService(splashReveal);
                        } else {
                            if (functionsClassLegacy.FreeForm()) {
                                functionsClassLegacy.openApplicationFreeForm(packageNames[startId],
                                        classNames[startId],
                                        layoutParams[startId].x,
                                        (functionsClassLegacy.displayX() / 2),
                                        layoutParams[startId].y,
                                        (functionsClassLegacy.displayY() / 2)
                                );
                            } else {
                                functionsClassLegacy.appsLaunchPad(packageNames[startId], classNames[startId]);
                            }
                        }
                    } else {

                    }
                }
            }
        });
        notificationDot[startId].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClassLegacy.PopupNotificationShortcuts(
                        floatingView[startId],
                        packageNames[startId],
                        FloatingShortcutsForHIS.class.getSimpleName(),
                        startId,
                        iconColor[startId],
                        xMove,
                        yMove,
                        layoutParams[startId].width
                );
            }
        });
        notificationDot[startId].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (functionsClassLegacy.AccessibilityServiceEnabled() && functionsClassLegacy.SettingServiceRunning(InteractionObserver.class)) {
                    functionsClassLegacy.sendInteractionObserverEvent(view, packageNames[startId], AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, 66666);
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

        final String className = FloatingShortcutsForHIS.class.getSimpleName();
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
                    Debug.Companion.PrintDebug("Split Apps Single");
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

                                functionsClassLegacy.Toast(functionsClassLegacy.applicationName(PublicVariable.splitSinglePackage), Gravity.TOP);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                } else if (intent.getAction().equals("Pin_App_" + className)) {
                    Debug.Companion.PrintDebug(functionsClassLegacy.applicationName(packageNames[intent.getIntExtra("startId", 1)]));
                    allowMove[intent.getIntExtra("startId", 1)] = false;

                    Drawable pinDrawable = null;
                    if (functionsClassLegacy.customIconsEnable()) {
                        pinDrawable = functionsClassLegacy.getAppIconDrawableCustomIcon(packageNames[intent.getIntExtra("startId", 1)]).mutate();
                    } else {
                        switch (functionsClassLegacy.shapesImageId()) {
                            case 1:
                                pinDrawable = getDrawable(R.drawable.pin_droplet_icon);
                                controlIcon[intent.getIntExtra("startId", 1)].setPadding(-3, -3, -3, -3);
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
                                pinDrawable = functionsClassLegacy.applicationIcon(activityInfo[intent.getIntExtra("startId", 1)]).mutate();
                                break;
                        }
                    }
                    pinDrawable.setTint(functionsClassLegacy.setColorAlpha(Color.RED, 175));
                    if (functionsClassLegacy.returnAPI() >= 26) {
                        pinDrawable.setAlpha(175);

                        pinDrawable.setTint(Color.RED);
                        controlIcon[intent.getIntExtra("startId", 1)].setAlpha(0.50f);
                    }
                    controlIcon[intent.getIntExtra("startId", 1)].setImageDrawable(pinDrawable);
                } else if (intent.getAction().equals("Unpin_App_" + className)) {
                    Debug.Companion.PrintDebug(functionsClassLegacy.applicationName(packageNames[intent.getIntExtra("startId", 1)]));
                    allowMove[intent.getIntExtra("startId", 1)] = true;
                    controlIcon[intent.getIntExtra("startId", 1)].setImageDrawable(null);
                } else if (intent.getAction().equals("Float_It_" + className)) {
                    if (functionsClassLegacy.splashReveal()) {
                        if (!functionsClassLegacy.FreeForm()) {
                            functionsClassLegacy.saveDefaultPreference("freeForm", true);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    functionsClassLegacy.saveDefaultPreference("freeForm", false);
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
                        functionsClassLegacy.openApplicationFreeForm(packageNames[intent.getIntExtra("startId", 1)],
                                classNames[intent.getIntExtra("startId", 1)],
                                layoutParams[intent.getIntExtra("startId", 1)].x,
                                (functionsClassLegacy.displayX() / 2),
                                layoutParams[intent.getIntExtra("startId", 1)].y,
                                (functionsClassLegacy.displayY() / 2)
                        );
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
                            PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1;

                            if (PublicVariable.allFloatingCounter == 0) {
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
                    for (int r = 1; r <= startId; r++) {
                        try {
                            if (floatingView != null) {
                                if (floatingView[r].isShown()) {
                                    try {
                                        StickyEdge[r] = true;
                                        StickyEdgeParams[r] = functionsClassLegacy.moveToEdge(FloatingShortcutsForHIS.this.classNames[r], layoutParams[r].height);
                                        windowManager.updateViewLayout(floatingView[r], StickyEdgeParams[r]);
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
                    for (int r = 1; r <= startId; r++) {
                        try {
                            if (floatingView != null) {
                                if (floatingView[r].isShown()) {
                                    try {
                                        try {
                                            sharedPrefPosition = getSharedPreferences((FloatingShortcutsForHIS.this.classNames[r]), MODE_PRIVATE);

                                            StickyEdge[r] = false;
                                            xPos = sharedPrefPosition.getInt("X", xInit);
                                            yPos = sharedPrefPosition.getInt("Y", yInit);
                                            windowManager.updateViewLayout(floatingView[r], functionsClassLegacy.backFromEdge(layoutParams[r].height, xPos, yPos));
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
                                if (functionsClassLegacy.customIconsEnable()) {
                                    dotDrawable = functionsClassLegacy.getAppIconDrawableCustomIcon(packageNames[StartIdNotification]).mutate();
                                } else {
                                    switch (functionsClassLegacy.shapesImageId()) {
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
                                            dotDrawable = functionsClassLegacy.applicationIcon(packageNames[StartIdNotification]).mutate();
                                            break;
                                    }
                                }
                                if (PublicVariable.themeLightDark) {
                                    dotDrawable.setTint(functionsClassLegacy.manipulateColor(functionsClassLegacy.extractVibrantColor(functionsClassLegacy.applicationIcon(packageNames[StartIdNotification])), 1.30f));
                                } else {
                                    dotDrawable.setTint(functionsClassLegacy.manipulateColor(functionsClassLegacy.extractVibrantColor(functionsClassLegacy.applicationIcon(packageNames[StartIdNotification])), 0.50f));
                                }
                                notificationDot[StartIdNotification].setImageDrawable(dotDrawable);
                                notificationDot[StartIdNotification].setVisibility(View.VISIBLE);
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
                                notificationDot[StartIdNotification].setVisibility(View.INVISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        if (getFileStreamPath(packageNames[startId] + "_" + "Notification" + "Package").exists()) {
            sendBroadcast(new Intent("Notification_Dot").putExtra("NotificationPackage", packageNames[startId]));
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClassLegacy = new FunctionsClassLegacy(getApplicationContext());

        array = getApplicationContext().getPackageManager().getInstalledApplications(0).size() * 2;
        layoutParams = new WindowManager.LayoutParams[array];
        StickyEdgeParams = new WindowManager.LayoutParams[array];
        packageNames = new String[array];
        classNames = new String[array];
        appIcon = new Drawable[array];
        iconColor = new int[array];
        floatingView = new ViewGroup[array];
        controlIcon = new ShapesImage[array];
        shapedIcon = new ShapesImage[array];
        notificationDot = new ShapesImage[array];
        allowMove = new boolean[array];
        remove = new boolean[array];
        touchingDelay = new boolean[array];
        StickyEdge = new boolean[array];
        openIt = new boolean[array];
        if (!functionsClassLegacy.litePreferencesEnabled()) {
            simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener[array];
            gestureDetector = new GestureDetector[array];

            flingAnimationX = new FlingAnimation[array];
            flingAnimationY = new FlingAnimation[array];
        }

        componentName = new ComponentName[array];
        activityInfo = new ActivityInfo[array];

        mapPackageNameStartId = new LinkedHashMap<String, Integer>();

        if (functionsClassLegacy.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClassLegacy.customIconPackageName());
        }
    }
}
