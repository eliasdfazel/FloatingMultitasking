/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 10:39 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FloatingServices;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.imageview.customshapes.ShapesImage;

public class FloatingFolders extends Service {

    public static boolean running = false;

    FunctionsClass functionsClass;

    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;

    ViewGroup floatingView;
    RelativeLayout wholeCategoryFloating;
    ShapesImage one, two, three, four;
    ShapesImage controlIcons;

    int xPos, yPos, xInit = 19, yInit = 19, xMove, yMove;

    String folderNames;
    boolean allowMove, touchingDelay, openIt;
    BroadcastReceiver broadcastReceiver;
    SharedPreferences sharedPrefPosition;
    Runnable runnablePressHold = null;
    Handler handlerPressHold = new Handler();

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        try {
            folderNames = intent.getStringExtra("FolderName");

            openIt = true;
            allowMove = true;
        } catch (Exception e) {
            e.printStackTrace();
            return Service.START_NOT_STICKY;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        floatingView = (ViewGroup) layoutInflater.inflate(R.layout.category_floating, null, false);

        wholeCategoryFloating = (RelativeLayout) floatingView.findViewById(R.id.wholeCategoryFloating);
        one = functionsClass.initShapesImage(floatingView, R.id.one);
        two = functionsClass.initShapesImage(floatingView, R.id.two);
        three = functionsClass.initShapesImage(floatingView, R.id.three);
        four = functionsClass.initShapesImage(floatingView, R.id.four);
        controlIcons = functionsClass.initShapesImage(floatingView, R.id.pin);

        int categoryFloatingColor;
        if (PublicVariable.transparencyEnabled == true) {
            categoryFloatingColor = functionsClass.setColorAlpha(PublicVariable.primaryColor, 50);
        } else {
            categoryFloatingColor = PublicVariable.primaryColor;
        }

        Drawable drawableBack = null;
        switch (functionsClass.shapesImageId()) {
            case 1:
                drawableBack = getDrawable(R.drawable.category_droplet_icon);
                drawableBack.setTint(categoryFloatingColor);
                break;
            case 2:
                drawableBack = getDrawable(R.drawable.category_circle_icon);
                drawableBack.setTint(categoryFloatingColor);
                break;
            case 3:
                drawableBack = getDrawable(R.drawable.category_square_icon);
                drawableBack.setTint(categoryFloatingColor);
                break;
            case 4:
                drawableBack = getDrawable(R.drawable.category_squircle_icon);
                drawableBack.setTint(categoryFloatingColor);
                break;
            case 5:
                drawableBack = getDrawable(R.drawable.category_cut_circle_icon);
                drawableBack.setTint(categoryFloatingColor);
                break;
            case 0:
                drawableBack = null;
                break;
        }
        wholeCategoryFloating.setBackground(drawableBack);

        String[] appsInFolder = functionsClass.readFileLine(".uFile");
        if (appsInFolder != null) {
            try {
                one.setImageDrawable(functionsClass.shapedAppIcon(appsInFolder[0]));
                one.setImageAlpha(PublicVariable.transparencyEnabled ? 157 : 250);
            } catch (Exception e) {
                one.setImageDrawable(null);
            }
            try {
                two.setImageDrawable(functionsClass.shapedAppIcon(appsInFolder[1]));
                two.setImageAlpha(PublicVariable.transparencyEnabled ? 157 : 250);
            } catch (Exception e) {
                two.setImageDrawable(null);
            }
            try {
                three.setImageDrawable(functionsClass.shapedAppIcon(appsInFolder[2]));
                three.setImageAlpha(PublicVariable.transparencyEnabled ? 157 : 250);
            } catch (Exception e) {
                three.setImageDrawable(null);
            }
            try {
                four.setImageDrawable(functionsClass.shapedAppIcon(appsInFolder[3]));
                four.setImageAlpha(PublicVariable.transparencyEnabled ? 157 : 250);
            } catch (Exception e) {
                four.setImageDrawable(null);
            }
        }


        int HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 41, this.getResources().getDisplayMetrics());

        String nameForPosition = folderNames;
        sharedPrefPosition = getSharedPreferences(nameForPosition, MODE_PRIVATE);
        xInit = xInit + 13;
        yInit = yInit + 13;
        xPos = sharedPrefPosition.getInt("X", xInit);
        yPos = sharedPrefPosition.getInt("Y", yInit);

        layoutParams = functionsClass.normalLayoutParams(HW, xPos, yPos);
        windowManager.addView(floatingView, layoutParams);

        final String className = FloatingFolders.class.getSimpleName();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Split_Apps_Pair_" + className);
        intentFilter.addAction("Split_Apps_Single_" + className);
        intentFilter.addAction("Pin_App_" + className);
        intentFilter.addAction("Unpin_App_" + className);
        intentFilter.addAction("Remove_Category_" + className);
        intentFilter.addAction("Sticky_Edge");
        intentFilter.addAction("Sticky_Edge_No");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("Pin_App_" + className)) {
                    allowMove = false;
                    Drawable drawableBack = null;
                    switch (functionsClass.shapesImageId()) {
                        case 1:
                            drawableBack = getDrawable(R.drawable.pin_droplet_icon);
                            drawableBack.setTint(context.getColor(R.color.red_trans));
                            break;
                        case 2:
                            drawableBack = getDrawable(R.drawable.pin_circle_icon);
                            drawableBack.setTint(context.getColor(R.color.red_trans));
                            break;
                        case 3:
                            drawableBack = getDrawable(R.drawable.pin_square_icon);
                            drawableBack.setTint(context.getColor(R.color.red_trans));
                            break;
                        case 4:
                            drawableBack = getDrawable(R.drawable.pin_squircle_icon);
                            drawableBack.setTint(context.getColor(R.color.red_trans));
                            break;
                        case 5:
                            drawableBack = getDrawable(R.drawable.pin_cut_circle_icon);
                            drawableBack.setTint(context.getColor(R.color.red_trans));
                            break;
                        case 0:
                            drawableBack = getDrawable(R.drawable.pin_noshap);
                            drawableBack.setTint(context.getColor(R.color.red_trans));
                            break;
                    }
                    controlIcons.setImageDrawable(drawableBack);
                } else if (intent.getAction().equals("Unpin_App_" + className)) {

                    allowMove = true;
                    controlIcons.setImageDrawable(null);

                } else if (intent.getAction().equals("Remove_Category_" + className)) {
                    try {
                        if (floatingView != null) {
                            if (floatingView == null) {

                                return;
                            }

                            if (floatingView.isShown()) {
                                try {
                                    windowManager.removeView(floatingView);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    PublicVariable.floatingFoldersList.remove(folderNames);
                                    PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter - 1;
                                    PublicVariable.floatingFolderCounter_Folder = PublicVariable.floatingFolderCounter_Folder - 1;
                                    PublicVariable.floatingFolderCounter = PublicVariable.floatingFolderCounter - 1;

                                    if (PublicVariable.allFloatingCounter == 0) {
                                        stopService(new Intent(getApplicationContext(), BindServices.class));
                                    }
                                    if (PublicVariable.floatingFolderCounter_Folder == 0) {
                                        if (broadcastReceiver != null) {
                                            unregisterReceiver(broadcastReceiver);
                                        }
                                        running = false;
                                        stopSelf();
                                    }
                                }
                            } else if (PublicVariable.allFloatingCounter == 0) {
                                stopService(new Intent(getApplicationContext(), BindServices.class));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        floatingView.setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY;
            float initialTouchX;
            float initialTouchY;

            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                WindowManager.LayoutParams paramsF;
                paramsF = layoutParams;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        initialX = paramsF.x;
                        initialY = paramsF.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        xMove = paramsF.x;
                        yMove = paramsF.y;

                        touchingDelay = true;
                        runnablePressHold = new Runnable() {
                            @Override
                            public void run() {
                                if (touchingDelay == true) {
                                    functionsClass.PopupOptionCategory(
                                            view,
                                            ".uFile",
                                            className,
                                            startId,
                                            initialX,
                                            initialY
                                    );
                                    openIt = false;
                                }
                            }
                        };
                        handlerPressHold.postDelayed(runnablePressHold, 555);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        touchingDelay = false;
                        handlerPressHold.removeCallbacks(runnablePressHold);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openIt = true;
                            }
                        }, 113);

                        if (allowMove == true) {
                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            System.out.println("X :: " + paramsF.x + "\n" + " Y :: " + paramsF.y);

                            String nameForPosition = folderNames;
                            SharedPreferences sharedPrefPosition = getSharedPreferences(nameForPosition, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPrefPosition.edit();
                            editor.putInt("X", paramsF.x);
                            editor.putInt("Y", paramsF.y);
                            editor.apply();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (allowMove == true) {
                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(floatingView, paramsF);
                            xMove = paramsF.x;
                            yMove = paramsF.y;

                            int difMoveX = (int) (paramsF.x - initialTouchX);
                            int difMoveY = (int) (paramsF.y - initialTouchY);
                            if (Math.abs(difMoveX) > Math.abs(PublicVariable.floatingViewsHW + ((PublicVariable.floatingViewsHW * 70) / 100))
                                    || Math.abs(difMoveY) > Math.abs(PublicVariable.floatingViewsHW + ((PublicVariable.floatingViewsHW * 70) / 100))) {
                                openIt = false;
                                touchingDelay = false;
                                handlerPressHold.removeCallbacks(runnablePressHold);
                            }
                        }
                        break;
                    }
                }
                return false;
            }
        });
        floatingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (openIt) {
                    functionsClass.PopupListCategory(
                            view,
                            ".uFile",
                            functionsClass.readFileLine(".uFile"),
                            className,
                            startId,
                            xMove,
                            yMove,
                            layoutParams.width
                    );
                }
            }
        });

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClass = new FunctionsClass(getApplicationContext());

        running = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
