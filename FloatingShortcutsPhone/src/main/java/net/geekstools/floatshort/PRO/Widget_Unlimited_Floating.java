package net.geekstools.floatshort.PRO;

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
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.util.LinkedHashMap;
import java.util.Map;

public class Widget_Unlimited_Floating extends Service {

    FunctionsClass functionsClass;
    WindowManager windowManager;
    WindowManager.LayoutParams[] layoutParams;
    WindowManager.LayoutParams[] StickyEdgeParams;

    ViewGroup[] floatingView;
    RelativeLayout wholeCategoryFloating;
    ShapesImage one, two, three, four;
    ShapesImage[] pin, notificationDot;

    int xPos, yPos, xInit = 19, yInit = 19, xMove, yMove;

    int array, categorySize;
    String[] categoryName;
    boolean[] allowMove, touchingDelay, trans, StickyEdge, openIt;

    String notificationPackage;
    boolean showNotificationDot = false;

    BroadcastReceiver broadcastReceiver;
    SharedPreferences sharedPrefPosition;

    Map<String, String> mapContentCategoryName;
    Map<String, Integer> mapCategoryNameStartId;

    Runnable runnablePressHold = null;
    Handler handlerPressHold = new Handler();

    LoadCustomIcons loadCustomIcons;

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
                                layoutParams[J] = functionsClass.handleOrientationPortrait(categoryName[J], layoutParams[J].height);
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
                                layoutParams[J] = functionsClass.handleOrientationLandscape(categoryName[J], layoutParams[J].height);
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
        System.out.println(this.getClass().getSimpleName() + " ::: StartId ::: " + startId);
        startIdCounter = startId;

        if (functionsClass.loadCustomIcons()) {
            if (loadCustomIcons == null) {
                loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
            }
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        try {
            categoryName[startId] = intent.getStringExtra("categoryName");

            touchingDelay[startId] = false;
            StickyEdge[startId] = false;
            allowMove[startId] = true;
            openIt[startId] = true;
        } catch (Exception e) {
            e.printStackTrace();
            return Service.START_NOT_STICKY;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        floatingView[startId] = (ViewGroup) layoutInflater.inflate(R.layout.floating_category_medium, null, false);
        if (PublicVariable.size == 13 || PublicVariable.size == 26) {
            floatingView[startId] = (ViewGroup) layoutInflater.inflate(R.layout.floating_category_small, null, false);
            categorySize = 24;
        } else if (PublicVariable.size == 39 || PublicVariable.size == 52) {
            floatingView[startId] = (ViewGroup) layoutInflater.inflate(R.layout.floating_category_medium, null, false);
            categorySize = 36;
        } else if (PublicVariable.size == 65 || PublicVariable.size == 78) {
            floatingView[startId] = (ViewGroup) layoutInflater.inflate(R.layout.floating_category_large, null, false);
            categorySize = 48;
        }

        wholeCategoryFloating = floatingView[startId].findViewById(R.id.wholeCategoryFloating);
        one = functionsClass.initShapesImage(floatingView[startId], R.id.one);
        two = functionsClass.initShapesImage(floatingView[startId], R.id.two);
        three = functionsClass.initShapesImage(floatingView[startId], R.id.three);
        four = functionsClass.initShapesImage(floatingView[startId], R.id.four);
        pin[startId] = functionsClass.initShapesImage(floatingView[startId], R.id.pin);
        notificationDot[startId] = functionsClass.initShapesImage(floatingView[startId],
                functionsClass.checkStickyEdge() ? R.id.notificationDotEnd : R.id.notificationDotStart);

        Drawable drawableBack = null;
        switch (functionsClass.shapesImageId()) {
            case 1:
                drawableBack = getDrawable(R.drawable.category_droplet_icon);
                break;
            case 2:
                drawableBack = getDrawable(R.drawable.category_circle_icon);
                break;
            case 3:
                drawableBack = getDrawable(R.drawable.category_square_icon);
                break;
            case 4:
                drawableBack = getDrawable(R.drawable.category_squircle_icon);
                break;
            case 0:
                drawableBack = null;
                break;
        }
        if (drawableBack != null) {
            drawableBack.setTint(PublicVariable.primaryColor);
            drawableBack.setAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
        }
        wholeCategoryFloating.setBackground(drawableBack);

        if (categoryName[startId].equals(getString(R.string.remove_all_shortcuts))) {
            for (int r = 1; r < startId; r++) {
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
                                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                            .getBoolean("stable", true) == false) {
                                        stopService(new Intent(getApplicationContext(), BindServices.class));
                                    }
                                }
                            }
                        } else if (PublicVariable.floatingCounter == 0) {
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
            PublicVariable.FloatingCategories.clear();
            PublicVariable.categoriesCounter = -1;
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
        mapCategoryNameStartId.put(categoryName[startId], startId);
        String[] categoryApps = functionsClass.readFileLine(categoryName[startId]);
        if (categoryApps != null) {
            if (categoryApps.length > 0) {
                for (int i = 0; i < categoryApps.length; i++) {
                    mapContentCategoryName.put(categoryApps[i], categoryName[startId]);
                    if (getFileStreamPath(categoryApps[i] + "_" + "Notification" + "Package").exists()) {
                        showNotificationDot = true;
                        notificationPackage = categoryApps[i];
                    }
                }
            }
            try {
                one.setImageDrawable(functionsClass.loadCustomIcons() ?
                        loadCustomIcons.getDrawableIconForPackage(categoryApps[0], functionsClass.shapedAppIcon(categoryApps[0]))
                        :
                        functionsClass.shapedAppIcon(categoryApps[0]));
                one.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
            } catch (Exception e) {
                one.setImageDrawable(null);
            }
            try {
                two.setImageDrawable(functionsClass.loadCustomIcons() ?
                        loadCustomIcons.getDrawableIconForPackage(categoryApps[1], functionsClass.shapedAppIcon(categoryApps[1]))
                        :
                        functionsClass.shapedAppIcon(categoryApps[1]));
                two.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
            } catch (Exception e) {
                two.setImageDrawable(null);
            }
            try {
                three.setImageDrawable(functionsClass.loadCustomIcons() ?
                        loadCustomIcons.getDrawableIconForPackage(categoryApps[2], functionsClass.shapedAppIcon(categoryApps[2]))
                        :
                        functionsClass.shapedAppIcon(categoryApps[2]));
                three.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
            } catch (Exception e) {
                three.setImageDrawable(null);
            }
            try {
                four.setImageDrawable(functionsClass.loadCustomIcons() ?
                        loadCustomIcons.getDrawableIconForPackage(categoryApps[3], functionsClass.shapedAppIcon(categoryApps[3]))
                        :
                        functionsClass.shapedAppIcon(categoryApps[3]));
                four.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
            } catch (Exception e) {
                four.setImageDrawable(null);
            }
        }

        int HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, categorySize * 2, this.getResources().getDisplayMetrics());
        String nameForPosition = categoryName[startId];
        sharedPrefPosition = getSharedPreferences(nameForPosition, MODE_PRIVATE);
        xInit = xInit + 13;
        yInit = yInit + 13;
        xPos = sharedPrefPosition.getInt("X", xInit);
        yPos = sharedPrefPosition.getInt("Y", yInit);

        layoutParams[startId] = functionsClass.normalLayoutParams(HW, xPos, yPos);
        windowManager.addView(floatingView[startId], layoutParams[startId]);

        final String className = Widget_Unlimited_Floating.class.getSimpleName();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Split_Apps_Pair_" + className);
        intentFilter.addAction("Split_Apps_Single_" + className);
        intentFilter.addAction("Pin_App_" + className);
        intentFilter.addAction("Unpin_App_" + className);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("Pin_App_" + className)) {
                    allowMove[intent.getIntExtra("startId", 1)] = false;
                    Drawable drawableBack = null;
                    switch (functionsClass.shapesImageId()) {
                        case 1:
                            drawableBack = getDrawable(R.drawable.pin_droplet_icon);
                            drawableBack.setTint(context.getResources().getColor(R.color.red_transparent));
                            break;
                        case 2:
                            drawableBack = getDrawable(R.drawable.pin_circle_icon);
                            drawableBack.setTint(context.getResources().getColor(R.color.red_transparent));
                            break;
                        case 3:
                            drawableBack = getDrawable(R.drawable.pin_square_icon);
                            drawableBack.setTint(context.getResources().getColor(R.color.red_transparent));
                            break;
                        case 4:
                            drawableBack = getDrawable(R.drawable.pin_squircle_icon);
                            drawableBack.setTint(context.getResources().getColor(R.color.red_transparent));
                            break;
                        case 0:
                            drawableBack = getDrawable(R.drawable.pin_noshap);
                            drawableBack.setTint(context.getResources().getColor(R.color.red_transparent));
                            break;
                    }
                    pin[intent.getIntExtra("startId", 1)].setImageDrawable(drawableBack);
                } else if (intent.getAction().equals("Unpin_App_" + className)) {
                    allowMove[intent.getIntExtra("startId", 1)] = true;
                    pin[intent.getIntExtra("startId", 1)].setImageDrawable(null);
                } else if (intent.getAction().equals("Sticky_Edge")) {
                    for (int r = 1; r <= startId; r++) {
                        try {
                            if (floatingView != null) {
                                if (floatingView[r].isShown()) {
                                    try {
                                        StickyEdge[r] = true;
                                        StickyEdgeParams[r] = functionsClass.moveToEdge(categoryName[r], layoutParams[r].height);
                                        windowManager.updateViewLayout(floatingView[r], StickyEdgeParams[r]);
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
                                            sharedPrefPosition = getSharedPreferences((categoryName[r]), MODE_PRIVATE);

                                            StickyEdge[r] = false;
                                            xPos = sharedPrefPosition.getInt("X", xInit);
                                            yPos = sharedPrefPosition.getInt("Y", yInit);
                                            windowManager.updateViewLayout(floatingView[r], functionsClass.backFromEdge(layoutParams[r].height, xPos, yPos));
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
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);


        return functionsClass.serviceMode();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClass = new FunctionsClass(getApplicationContext());

        array = getApplicationContext().getPackageManager().getInstalledApplications(0).size() * 2;
        layoutParams = new WindowManager.LayoutParams[array];
        StickyEdgeParams = new WindowManager.LayoutParams[array];
        floatingView = new ViewGroup[array];
        categoryName = new String[array];
        pin = new ShapesImage[array];
        notificationDot = new ShapesImage[array];
        allowMove = new boolean[array];
        touchingDelay = new boolean[array];
        trans = new boolean[array];
        StickyEdge = new boolean[array];
        openIt = new boolean[array];

        mapContentCategoryName = new LinkedHashMap<String, String>();
        mapCategoryNameStartId = new LinkedHashMap<String, Integer>();

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
