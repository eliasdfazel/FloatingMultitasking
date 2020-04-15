/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/15/20 3:29 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FloatingServices;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RelativeLayout;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.preference.PreferenceManager;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.Utils.FloatingFoldersUtils;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class FloatingFolders extends Service {

    FunctionsClass functionsClass;

    FloatingFoldersUtils floatingFoldersUtils;

    WindowManager windowManager;

    WindowManager.LayoutParams[] layoutParams;
    WindowManager.LayoutParams[] stickyEdgeParams;

    ShapesImage[] pinIndicatorView,
            notificationDotView;

    int xPosition, yPosition, xInitial = 19, yInitial = 19, xMove, yMove;

    String[] folderName;

    boolean[] movePermit, touchingDelay, stickedToEdge, openPermit;

    String notificationPackage;
    boolean showNotificationDot = false;

    Map<String, String> mapContentFolderName;
    Map<String, Integer> mapFolderNameStartId;

    Runnable runnablePressHold = null;
    Handler handlerPressHold = new Handler();

    LoadCustomIcons loadCustomIcons;

    GestureDetector.SimpleOnGestureListener[] simpleOnGestureListener;
    GestureDetector[] gestureDetector;

    FlingAnimation[] flingAnimationX, flingAnimationY;

    float flingPositionX = 0, flingPositionY = 0;

    /*delete*/
    int startIdCounter = 1;

    ViewGroup[] floatingView;
    RelativeLayout wholeFloatingFolder;
    /*delete*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT: {
                for (int J = 1; J <= startIdCounter; J++) {
                    try {
                        if (floatingView != null) {
                            if (floatingView[J].isShown()) {
                                layoutParams[J] = functionsClass.handleOrientationPortrait(folderName[J], layoutParams[J].height);
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
                                layoutParams[J] = functionsClass.handleOrientationLandscape(folderName[J], layoutParams[J].height);
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
    public int onStartCommand(Intent intent, int flags, final int startId) {

        mapFolderNameStartId.put(folderName[startId], startId);
        String[] appsInFolder = functionsClass.readFileLine(folderName[startId]);
        if (appsInFolder != null) {
            if (appsInFolder.length > 0) {
                for (int i = 0; i < appsInFolder.length; i++) {
                    mapContentFolderName.put(appsInFolder[i], folderName[startId]);
                    if (getFileStreamPath(appsInFolder[i] + "_" + "Notification" + "Package").exists()) {
                        showNotificationDot = true;
                        notificationPackage = appsInFolder[i];
                    }
                }
            }
            try {
                bottomRight.setImageDrawable(functionsClass.customIconsEnable() ?
                        loadCustomIcons.getDrawableIconForPackage(appsInFolder[0], functionsClass.shapedAppIcon(appsInFolder[0]))
                        :
                        functionsClass.shapedAppIcon(appsInFolder[0]));
                bottomRight.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
            } catch (Exception e) {
                bottomRight.setImageDrawable(null);
            }
            try {
                topLeft.setImageDrawable(functionsClass.customIconsEnable() ?
                        loadCustomIcons.getDrawableIconForPackage(appsInFolder[1], functionsClass.shapedAppIcon(appsInFolder[1]))
                        :
                        functionsClass.shapedAppIcon(appsInFolder[1]));
                topLeft.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
            } catch (Exception e) {
                topLeft.setImageDrawable(null);
            }
            try {
                bottomLeft.setImageDrawable(functionsClass.customIconsEnable() ?
                        loadCustomIcons.getDrawableIconForPackage(appsInFolder[2], functionsClass.shapedAppIcon(appsInFolder[2]))
                        :
                        functionsClass.shapedAppIcon(appsInFolder[2]));
                bottomLeft.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
            } catch (Exception e) {
                bottomLeft.setImageDrawable(null);
            }
            try {
                topRight.setImageDrawable(functionsClass.customIconsEnable() ?
                        loadCustomIcons.getDrawableIconForPackage(appsInFolder[3], functionsClass.shapedAppIcon(appsInFolder[3]))
                        :
                        functionsClass.shapedAppIcon(appsInFolder[3]));
                topRight.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
            } catch (Exception e) {
                topRight.setImageDrawable(null);
            }
        }

        int HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, folderSize * 2, this.getResources().getDisplayMetrics());
        String nameForPosition = folderName[startId];
        SharedPreferences sharedPreferencesPosition = getSharedPreferences(nameForPosition, MODE_PRIVATE);
        xInitial = xInitial + 13;
        yInitial = yInitial + 13;
        xPosition = sharedPreferencesPosition.getInt("X", xInitial);
        yPosition = sharedPreferencesPosition.getInt("Y", yInitial);

        layoutParams[startId] = functionsClass.normalLayoutParams(HW, xPosition, yPosition);
        try {
            floatingView[startId].setTag(startId);
            windowManager.addView(floatingView[startId], layoutParams[startId]);
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }

        xMove = xPosition;
        yMove = yPosition;

        final String className = FloatingFolders.this.getClass().getSimpleName();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Split_Apps_Pair_" + className);
        intentFilter.addAction("Split_Apps_Single_" + className);
        intentFilter.addAction("Pin_App_" + className);
        intentFilter.addAction("Unpin_App_" + className);
        intentFilter.addAction("Remove_Category_" + className);
        intentFilter.addAction("Sticky_Edge");
        intentFilter.addAction("Sticky_Edge_No");
        intentFilter.addAction("Notification_Dot");
        intentFilter.addAction("Notification_Dot_No");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("Split_Apps_Pair_" + className) && PublicVariable.splitScreen == true) {
                    FunctionsClassDebug.Companion.PrintDebug("Split Apps Pair");
                    PublicVariable.splitScreen = false;

                    final String packageNameSplitOne, packageNameSplitTwo;
                    if (getFileStreamPath(PublicVariable.splitPairPackage + ".SplitOne").exists()
                            && getFileStreamPath(PublicVariable.splitPairPackage + ".SplitTwo").exists()) {
                        packageNameSplitOne = functionsClass.readFile(PublicVariable.splitPairPackage + ".SplitOne");
                        packageNameSplitTwo = functionsClass.readFile(PublicVariable.splitPairPackage + ".SplitTwo");
                    } else {
                        packageNameSplitOne = functionsClass.readFileLine(PublicVariable.splitPairPackage)[0];
                        packageNameSplitTwo = functionsClass.readFileLine(PublicVariable.splitPairPackage)[1];
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent spliteOne = getPackageManager().getLaunchIntentForPackage(packageNameSplitOne);
                                spliteOne.setFlags(
                                        Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                final Intent spliteTwo = getPackageManager().getLaunchIntentForPackage(packageNameSplitTwo);
                                spliteTwo.setFlags(
                                        Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                startActivity(spliteOne);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(spliteTwo);
                                        PublicVariable.splitScreen = true;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                sendBroadcast(new Intent("split_pair_finish"));
                                            }
                                        }, 700);
                                    }
                                }, 200);

                                functionsClass.Toast(functionsClass.appName(packageNameSplitOne), Gravity.TOP);
                                functionsClass.Toast(functionsClass.appName(packageNameSplitTwo), Gravity.BOTTOM);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 700);
                } else if (intent.getAction().equals("Split_Apps_Single_" + className) && PublicVariable.splitScreen == true) {
                    FunctionsClassDebug.Companion.PrintDebug("Split Apps Single");
                    PublicVariable.splitScreen = false;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent splitSingle = getPackageManager().getLaunchIntentForPackage(PublicVariable.splitSinglePackage);
                                splitSingle.setFlags(
                                        Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                startActivity(splitSingle);
                                PublicVariable.splitScreen = true;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendBroadcast(new Intent("split_single_finish"));
                                    }
                                }, 500);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                } else if (intent.getAction().equals("Pin_App_" + className)) {
                    movePermit[intent.getIntExtra("startId", 0)] = false;
                    Drawable drawableBack = null;
                    switch (functionsClass.shapesImageId()) {
                        case 1:
                            drawableBack = getDrawable(R.drawable.pin_droplet_icon);
                            drawableBack.setTint(context.getColor(R.color.red_transparent));
                            break;
                        case 2:
                            drawableBack = getDrawable(R.drawable.pin_circle_icon);
                            drawableBack.setTint(context.getColor(R.color.red_transparent));
                            break;
                        case 3:
                            drawableBack = getDrawable(R.drawable.pin_square_icon);
                            drawableBack.setTint(context.getColor(R.color.red_transparent));
                            break;
                        case 4:
                            drawableBack = getDrawable(R.drawable.pin_squircle_icon);
                            drawableBack.setTint(context.getColor(R.color.red_transparent));
                            break;
                        case 0:
                            drawableBack = getDrawable(R.drawable.pin_noshap);
                            drawableBack.setTint(context.getColor(R.color.red_transparent));
                            break;
                    }
                    pinIndicatorView[intent.getIntExtra("startId", 0)].setImageDrawable(drawableBack);
                } else if (intent.getAction().equals("Unpin_App_" + className)) {
                    movePermit[intent.getIntExtra("startId", 0)] = true;
                    pinIndicatorView[intent.getIntExtra("startId", 0)].setImageDrawable(null);
                } else if (intent.getAction().equals("Remove_Category_" + className)) {
                    try {
                        if (floatingView != null) {
                            if (floatingView[intent.getIntExtra("startId", 0)] == null) {
                                return;
                            }
                            if (floatingView[intent.getIntExtra("startId", 0)].isShown()) {
                                try {
                                    windowManager.removeView(floatingView[intent.getIntExtra("startId", 0)]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    PublicVariable.floatingFoldersList.remove(folderName[intent.getIntExtra("startId", 0)]);
                                    PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1;
                                    PublicVariable.floatingFolderCounter = PublicVariable.floatingFolderCounter - 1;

                                    floatingFoldersUtils.floatingFoldersCounterType(FloatingFolders.this.getClass().getSimpleName());

                                    if (PublicVariable.allFloatingCounter == 0) {
                                        if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                                .getBoolean("stable", true)) {
                                            stopService(new Intent(getApplicationContext(), BindServices.class));
                                        }
                                    }
                                    if (FloatingFoldersUtils.FloatingFoldersCounterType.getFloatingFoldersCounterType().get(FloatingFolders.this.getClass().getSimpleName()) == 0) {

                                        stopSelf();
                                    }
                                }
                            } else if (PublicVariable.allFloatingCounter == 0) {
                                if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                        .getBoolean("stable", true)) {
                                    stopService(new Intent(getApplicationContext(), BindServices.class));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (intent.getAction().equals("Sticky_Edge")) {
                    for (int r = 1; r <= startId; r++) {
                        try {
                            if (floatingView != null) {
                                if (floatingView[r].isShown()) {
                                    try {
                                        stickedToEdge[r] = true;
                                        stickyEdgeParams[r] = functionsClass.moveToEdge(folderName[r], layoutParams[r].height);
                                        windowManager.updateViewLayout(floatingView[r], stickyEdgeParams[r]);
                                    } catch (Exception e) {
                                        e.printStackTrace();
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
                                            SharedPreferences sharedPreferencesPosition = getSharedPreferences((folderName[r]), MODE_PRIVATE);

                                            stickedToEdge[r] = false;
                                            xPosition = sharedPreferencesPosition.getInt("X", xInitial);
                                            yPosition = sharedPreferencesPosition.getInt("Y", yInitial);
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
                        String folderNameNotification = mapContentFolderName.get(notificationPackage);
                        int StartIdNotification = mapFolderNameStartId.get(folderNameNotification);
                        if (floatingView[StartIdNotification] != null) {
                            if (floatingView[StartIdNotification].isShown()) {
                                /*add dot*/
                                Drawable dotDrawable =
                                        functionsClass.customIconsEnable() ?
                                                loadCustomIcons.getDrawableIconForPackage(notificationPackage, functionsClass.shapedAppIcon(notificationPackage).mutate()).mutate()
                                                :
                                                functionsClass.shapedAppIcon(notificationPackage).mutate();

                                notificationDotView[StartIdNotification].setImageDrawable(dotDrawable);
                                notificationDotView[StartIdNotification].setVisibility(View.VISIBLE);
                                notificationDotView[StartIdNotification].setTag(notificationPackage);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (intent.getAction().equals("Notification_Dot_No")) {
                    try {
                        String notificationPackage = intent.getStringExtra("NotificationPackage");
                        String folderNameNotification = mapContentFolderName.get(notificationPackage);
                        int StartIdNotification = mapFolderNameStartId.get(folderNameNotification);
                        if (floatingView[StartIdNotification] != null) {
                            if (floatingView[StartIdNotification].isShown()) {
                                /*remove dot*/
                                notificationDotView[StartIdNotification].setVisibility(View.INVISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        try {
            registerReceiver(broadcastReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!functionsClass.litePreferencesEnabled()) {
            flingAnimationX[startId] = new FlingAnimation(new FloatValueHolder())
                    .setFriction(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f));
            flingAnimationY[startId] = new FlingAnimation(new FloatValueHolder())
                    .setFriction(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f));

            flingAnimationX[startId].setMaxValue(functionsClass.displayX() - folderSize);
            flingAnimationX[startId].setMinValue(0);

            flingAnimationY[startId].setMaxValue(functionsClass.displayY() - folderSize);
            flingAnimationY[startId].setMinValue(0);

            simpleOnGestureListener[startId] = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent motionEventFirst, MotionEvent motionEventLast, float velocityX, float velocityY) {

                    if (movePermit[startId]) {
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

                        openPermit[startId] = false;
                    }

                    return false;
                }
            };

            flingAnimationX[startId].addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    openPermit[startId] = true;
                }
            });

            flingAnimationY[startId].addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    openPermit[startId] = true;
                }
            });

            gestureDetector[startId] = new GestureDetector(getApplicationContext(), simpleOnGestureListener[startId]);
        }

        floatingView[startId].setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY;
            float initialTouchX;
            float initialTouchY;

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
                if (stickedToEdge[startId] == true) {
                    layoutParamsOnTouch = stickyEdgeParams[startId];
                    layoutParamsOnTouch.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                } else {
                    layoutParamsOnTouch = layoutParams[startId];
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        initialX = layoutParamsOnTouch.x;
                        initialY = layoutParamsOnTouch.y;

                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();

                        xMove = layoutParamsOnTouch.x;
                        yMove = layoutParamsOnTouch.y;

                        touchingDelay[startId] = true;
                        runnablePressHold = new Runnable() {
                            @Override
                            public void run() {
                                if (touchingDelay[startId] == true) {
                                    functionsClass.PopupOptionFolder(
                                            view,
                                            folderName[startId],
                                            className,
                                            startId,
                                            initialX,
                                            initialY + PublicVariable.statusBarHeight
                                    );
                                    openPermit[startId] = false;
                                }
                            }
                        };
                        handlerPressHold.postDelayed(runnablePressHold, functionsClass.readDefaultPreference("delayPressHold", 333));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        touchingDelay[startId] = false;
                        handlerPressHold.removeCallbacks(runnablePressHold);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openPermit[startId] = true;
                            }
                        }, 113);

                        if (movePermit[startId] == true) {
                            layoutParamsOnTouch.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                            layoutParamsOnTouch.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                            FunctionsClassDebug.Companion.PrintDebug("X :: " + layoutParamsOnTouch.x + "\n" + " Y :: " + layoutParamsOnTouch.y);

                            xMove = layoutParamsOnTouch.x;
                            yMove = layoutParamsOnTouch.y;

                            String nameForPosition = folderName[startId];
                            SharedPreferences sharedPrefPosition = getSharedPreferences(nameForPosition, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPrefPosition.edit();
                            editor.putInt("X", layoutParamsOnTouch.x);
                            editor.putInt("Y", layoutParamsOnTouch.y);
                            editor.apply();
                        } else {
                            if (!functionsClass.litePreferencesEnabled()) {
                                float initialTouchXBoundBack = getSharedPreferences((folderName[startId]), MODE_PRIVATE).getInt("X", 0);
                                if (initialTouchXBoundBack < 0) {
                                    initialTouchXBoundBack = 0;
                                } else if (initialTouchXBoundBack > functionsClass.displayX()) {
                                    initialTouchXBoundBack = functionsClass.displayX();
                                }

                                float initialTouchYBoundBack = getSharedPreferences((folderName[startId]), MODE_PRIVATE).getInt("Y", 0);
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
                                        openPermit[startId] = true;
                                    }
                                });
                                springAnimationY.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                                        openPermit[startId] = true;
                                    }
                                });

                                springAnimationX.start();
                                springAnimationY.start();
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (movePermit[startId] == true) {
                            layoutParamsOnTouch.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                            layoutParamsOnTouch.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(floatingView[startId], layoutParamsOnTouch);
                            xMove = layoutParamsOnTouch.x;
                            yMove = layoutParamsOnTouch.y;

                            int difMoveX = (int) (layoutParamsOnTouch.x - initialTouchX);
                            int difMoveY = (int) (layoutParamsOnTouch.y - initialTouchY);
                            if (Math.abs(difMoveX) > Math.abs(PublicVariable.HW + ((PublicVariable.HW * 70) / 100))
                                    || Math.abs(difMoveY) > Math.abs(PublicVariable.HW + ((PublicVariable.HW * 70) / 100))) {
                                openPermit[startId] = false;
                                touchingDelay[startId] = false;
                                handlerPressHold.removeCallbacks(runnablePressHold);
                            }

                            flingPositionX = layoutParamsOnTouch.x;
                            flingPositionY = layoutParamsOnTouch.y;
                        } else {
                            if (!functionsClass.litePreferencesEnabled()) {
                                layoutParamsOnTouch.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);     // X movePoint
                                layoutParamsOnTouch.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);     // Y movePoint
                                windowManager.updateViewLayout(floatingView[startId], layoutParamsOnTouch);

                                int difMoveX = (int) (layoutParamsOnTouch.x - initialTouchX);
                                int difMoveY = (int) (layoutParamsOnTouch.y - initialTouchY);
                                if (Math.abs(difMoveX) > Math.abs(PublicVariable.HW + ((PublicVariable.HW * 70) / 100))
                                        || Math.abs(difMoveY) > Math.abs(PublicVariable.HW + ((PublicVariable.HW * 70) / 100))) {
                                    sendBroadcast(new Intent("Hide_PopupListView_Shortcuts"));

                                    openPermit[startId] = false;
                                    touchingDelay[startId] = false;
                                    handlerPressHold.removeCallbacks(runnablePressHold);
                                }
                            }
                        }
                        break;
                    }
                }
                return false;
            }
        });
        floatingView[startId].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (openPermit[startId]) {
                    functionsClass.PopupAppListFolder(
                            view,
                            folderName[startId],
                            functionsClass.readFileLine(folderName[startId]),
                            className,
                            startId,
                            xMove,
                            yMove,
                            layoutParams[startId].width
                    );
                } else {

                }
            }
        });
        notificationDotView[startId].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.PopupNotificationShortcuts(
                        notificationDotView[startId],
                        notificationDotView[startId].getTag().toString(),
                        FloatingFolders.this.getClass().getSimpleName(),
                        startId,
                        PublicVariable.primaryColor,
                        xMove,
                        yMove,
                        layoutParams[startId].width
                );
            }
        });
        notificationDotView[startId].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (functionsClass.AccessibilityServiceEnabled() && functionsClass.SettingServiceRunning(InteractionObserver.class)) {
                    functionsClass.sendInteractionObserverEvent(view, notificationDotView[startId].getTag().toString(), AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, 66666);
                } else {
                    try {
                        @SuppressLint("WrongConstant")
                        Object statusBarService = getSystemService("statusbar");
                        Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
                        Method statuesBarInvocation = statusBarManager.getMethod("expandNotificationsPanel");//expandNotificationsPanel
                        statuesBarInvocation.invoke(statusBarService);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            @SuppressLint("WrongConstant")
                            Object statusBarService = getSystemService("statusbar");
                            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
                            Method statuesBarInvocation = statusBarManager.getMethod("expand");//expandNotificationsPanel
                            statuesBarInvocation.invoke(statusBarService);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return true;
            }
        });

        if (showNotificationDot) {
            sendBroadcast(new Intent("Notification_Dot").putExtra("NotificationPackage", notificationPackage));
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        functionsClass = new FunctionsClass(getApplicationContext());
        floatingFoldersUtils = new FloatingFoldersUtils();

        int array = getApplicationContext().getPackageManager().getInstalledApplications(0).size() * 2;
        layoutParams = new WindowManager.LayoutParams[array];
        stickyEdgeParams = new WindowManager.LayoutParams[array];
        floatingView = new ViewGroup[array];
        folderName = new String[array];
        pinIndicatorView = new ShapesImage[array];
        notificationDotView = new ShapesImage[array];
        movePermit = new boolean[array];
        touchingDelay = new boolean[array];
        stickedToEdge = new boolean[array];
        openPermit = new boolean[array];
        if (!functionsClass.litePreferencesEnabled()) {
            simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener[array];
            gestureDetector = new GestureDetector[array];

            flingAnimationX = new FlingAnimation[array];
            flingAnimationY = new FlingAnimation[array];
        }

        mapContentFolderName = new LinkedHashMap<String, String>();
        mapFolderNameStartId = new LinkedHashMap<String, Integer>();

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }
    }
}
