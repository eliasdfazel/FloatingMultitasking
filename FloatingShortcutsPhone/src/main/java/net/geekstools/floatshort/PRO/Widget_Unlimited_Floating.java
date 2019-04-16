package net.geekstools.floatshort.PRO;

import android.app.Service;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;

public class Widget_Unlimited_Floating extends Service {

    FunctionsClass functionsClass;

    WindowManager windowManager;
    WindowManager.LayoutParams[] layoutParams;

    ViewGroup[] floatingView, widgetLayout;
    RelativeLayout[] wholeViewWidget;
    TextView[] widgetLabel;
    ImageView[] widgetMoveButton, widgetCloseButton;
    Button[] widgetResizeControl;

    AppWidgetManager appWidgetManager;
    AppWidgetProviderInfo[] appWidgetProviderInfo;
    AppWidgetHost[] appWidgetHosts;
    AppWidgetHostView[] appWidgetHostView;

    int array,
            xPos, yPos,
            xInit = 19, yInit = 19;

    int[] appWidgetId, widgetColor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        System.out.println("StartID ::: " + startId);
        if (startId >= array) {
            return START_NOT_STICKY;
        }
        appWidgetId[startId] = intent.getIntExtra("WidgetId", -1);

        appWidgetProviderInfo[startId] = appWidgetManager.getAppWidgetInfo(appWidgetId[startId]);
        appWidgetHosts[startId] = new AppWidgetHost(this, (int) System.currentTimeMillis());
        appWidgetHosts[startId].startListening();

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        floatingView[startId] = (ViewGroup) layoutInflater.inflate(R.layout.floating_widgets, null, false);

        widgetLayout[startId] = (ViewGroup) floatingView[startId].findViewById(R.id.widgetViewGroup);
        wholeViewWidget[startId] = (RelativeLayout) floatingView[startId].findViewById(R.id.whole_widget_view);
        widgetLabel[startId] = (TextView) floatingView[startId].findViewById(R.id.widgetLabel);
        widgetMoveButton[startId] = (ImageView) floatingView[startId].findViewById(R.id.widgetMoveButton);
        widgetCloseButton[startId] = (ImageView) floatingView[startId].findViewById(R.id.widgetCloseButton);
        widgetResizeControl[startId] = (Button) floatingView[startId].findViewById(R.id.widgetResizeControl);

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
                PublicVariable.FloatingWidgets.clear();
                PublicVariable.widgetsCounter = -1;

                stopSelf();
                return START_NOT_STICKY;
            }
        }

        widgetColor[startId] = functionsClass.extractVibrantColor(appWidgetProviderInfo[startId].loadPreviewImage(getApplicationContext(), DisplayMetrics.DENSITY_LOW));

        String widgetLabelText =
                appWidgetProviderInfo[startId].loadLabel(getPackageManager()) == null ?
                        getString(R.string.widgetHint) : appWidgetProviderInfo[startId].loadLabel(getPackageManager());
        widgetLabel[startId].setText(widgetLabelText);
        widgetLabel[startId].setTextColor(widgetColor[startId]);

        LayerDrawable moveLayerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.draw_move);
        GradientDrawable moveBackgroundLayerDrawable = (GradientDrawable) moveLayerDrawable.findDrawableByLayerId(R.id.backtemp);
        moveBackgroundLayerDrawable.setColor(widgetColor[startId]);

        LayerDrawable resizeLayerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.draw_resize);
        GradientDrawable resizeBackgroundLayerDrawable = (GradientDrawable) resizeLayerDrawable.findDrawableByLayerId(R.id.backtemp);
        resizeBackgroundLayerDrawable.setColor(widgetColor[startId]);

        LayerDrawable closeLayerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.draw_close_service);
        GradientDrawable closeBackgroundLayerDrawable = (GradientDrawable) closeLayerDrawable.findDrawableByLayerId(R.id.backtemp);
        closeBackgroundLayerDrawable.setColor(widgetColor[startId]);

        widgetMoveButton[startId].setBackground(moveLayerDrawable);
        widgetResizeControl[startId].setBackground(resizeLayerDrawable);
        widgetCloseButton[startId].setBackground(closeLayerDrawable);

        appWidgetHostView[startId] = appWidgetHosts[startId].createView(this, appWidgetId[startId], appWidgetProviderInfo[startId]);

        int initWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 213, this.getResources().getDisplayMetrics());
        int initHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 133, this.getResources().getDisplayMetrics());

        Bundle bundle = new Bundle();
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, initWidth);
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, initHeight);
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, functionsClass.displayX() / 2);
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, functionsClass.displayY() / 2);
        appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId[startId], appWidgetProviderInfo[startId].provider, bundle);

        appWidgetHostView[startId].setAppWidget(appWidgetId[startId], appWidgetProviderInfo[startId]);
        appWidgetHostView[startId].setMinimumHeight(initHeight);
        appWidgetHostView[startId].setMinimumWidth(initWidth);
        widgetLayout[startId].addView(appWidgetHostView[startId]);

        final RelativeLayout.LayoutParams widgetRelativeLayout = new RelativeLayout.LayoutParams(initWidth, initHeight);
        wholeViewWidget[startId].setElevation(19);
        wholeViewWidget[startId].setLayoutParams(widgetRelativeLayout);
        wholeViewWidget[startId].requestLayout();


        xInit = xInit + 13;
        yInit = yInit + 13;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams[startId] = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        layoutParams[startId].gravity = Gravity.TOP | Gravity.START;
        layoutParams[startId].x = xInit;
        layoutParams[startId].y = yInit;
        layoutParams[startId].height = initHeight;
        layoutParams[startId].width = initWidth;
        layoutParams[startId].windowAnimations = android.R.style.Animation_Dialog;

        windowManager.addView(floatingView[startId], layoutParams[startId]);

        widgetLabel[startId].setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams layoutParamsTouch = layoutParams[startId];
            int initialX;
            int initialY;
            float initialTouchX;
            float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = layoutParamsTouch.x;
                        initialY = layoutParamsTouch.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        layoutParamsTouch.x = initialX + (int) (event.getRawX() - initialTouchX);
                        layoutParamsTouch.y = initialY + (int) (event.getRawY() - initialTouchY);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        layoutParamsTouch.x = initialX + (int) (event.getRawX() - initialTouchX);
                        layoutParamsTouch.y = initialY + (int) (event.getRawY() - initialTouchY);
                        try {
                            windowManager.updateViewLayout(floatingView[startId], layoutParamsTouch);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }

                        break;
                }
                return false;
            }
        });
        widgetLabel[startId].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        widgetMoveButton[startId].setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams layoutParamsTouch = layoutParams[startId];
            int initialX;
            int initialY;
            float initialTouchX;
            float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = layoutParamsTouch.x;
                        initialY = layoutParamsTouch.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        layoutParamsTouch.x = initialX + (int) (event.getRawX() - initialTouchX);
                        layoutParamsTouch.y = initialY + (int) (event.getRawY() - initialTouchY);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        layoutParamsTouch.x = initialX + (int) (event.getRawX() - initialTouchX);
                        layoutParamsTouch.y = initialY + (int) (event.getRawY() - initialTouchY);
                        try {
                            windowManager.updateViewLayout(floatingView[startId], layoutParamsTouch);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }

                        break;
                }
                return false;
            }
        });
        widgetMoveButton[startId].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        widgetCloseButton[startId].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    windowManager.removeView(floatingView[startId]);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    PublicVariable.FloatingWidgets.remove((Object) appWidgetId[startId]);
                    PublicVariable.floatingCounter = PublicVariable.floatingCounter - 1;
                    PublicVariable.widgetsCounter = PublicVariable.widgetsCounter - 1;

                    if (PublicVariable.floatingCounter == 0) {
                        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                .getBoolean("stable", true) == false) {
                            stopService(new Intent(getApplicationContext(), BindServices.class));
                        }

                        stopSelf();
                    }
                }
            }
        });

        widgetResizeControl[startId].setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams layoutParamsTouch = layoutParams[startId];

            int initialWidth;
            int initialHeight;

            float initialTouchX;
            float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialWidth = layoutParamsTouch.width;
                        initialHeight = layoutParamsTouch.height;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        layoutParamsTouch.x = initialWidth + (int) (event.getRawX() - initialTouchX);
                        layoutParamsTouch.y = initialHeight + (int) (event.getRawY() - initialTouchY);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int xMove = initialWidth + (int) (event.getRawX() - initialTouchX);
                        int yMove = initialHeight + (int) (event.getRawY() - initialTouchY);


                        layoutParamsTouch.width += 1;
                        layoutParamsTouch.height += 1;

                        if ((xMove > initialTouchX) && (yMove > initialTouchY)) {

                            System.out.println(">>> XY Increase <<<");

                        } else if ((xMove < initialTouchX) && (yMove > initialTouchY)) {

                            System.out.println(">>> X Decrease --- Y Increase <<<");

                        } else if ((xMove > initialTouchX) && (yMove < initialTouchY)) {

                            System.out.println(">>> X Increase --- Y Decrese <<<");

                        } else if ((xMove < initialTouchX) && (yMove < initialTouchY)) {

                            System.out.println(">>> XY Decrease <<<");

                        }


                        windowManager.updateViewLayout(floatingView[startId], layoutParamsTouch);

                        break;
                }
                return false;
            }
        });

        return functionsClass.serviceMode();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        functionsClass = new FunctionsClass(getApplicationContext());

        appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        array = getApplicationContext().getPackageManager().getInstalledApplications(0).size() * 2;
        layoutParams = new WindowManager.LayoutParams[array];
        appWidgetHosts = new AppWidgetHost[array];
        appWidgetHostView = new AppWidgetHostView[array];
        appWidgetProviderInfo = new AppWidgetProviderInfo[array];
        floatingView = new ViewGroup[array];
        widgetLayout = new ViewGroup[array];
        wholeViewWidget = new RelativeLayout[array];
        widgetLabel = new TextView[array];
        widgetMoveButton = new ImageView[array];
        widgetCloseButton = new ImageView[array];
        widgetResizeControl = new Button[array];
        appWidgetId = new int[array];
        widgetColor = new int[array];
    }
}
