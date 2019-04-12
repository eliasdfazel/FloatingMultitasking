package net.geekstools.floatshort.PRO.Widget;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;

import java.util.List;

public class WidgetHandler extends Activity {

    FunctionsClass functionsClass;

    public static final int WIDGET_CONFIGURATION_REQUEST = 666;
    AppWidgetManager appWidgetManager;
    AppWidgetHost appWidgetHostView;
    AppWidgetProviderInfo pickedAppWidgetProviderInfo;
    int pickedWidgetId = -1;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.widget_handler);

        functionsClass = new FunctionsClass(getApplicationContext(), WidgetHandler.this);

        appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetHostView = new AppWidgetHost(getApplicationContext(), 666);

        List<AppWidgetProviderInfo> widgetProviderInfoList = appWidgetManager.getInstalledProviders();

        int widgetIndex = 0;
        for (AppWidgetProviderInfo appWidgetProviderInfo : widgetProviderInfoList) {


            System.out.println(widgetIndex + " >>>>> " + appWidgetProviderInfo.configure + " | " + appWidgetProviderInfo.provider + " <<<<<");

            widgetIndex++;
        }

        ImageView widgetPreview = (ImageView) findViewById(R.id.widgetPreview);
        try {
            int indexWidget = 22;

            //com.google.android.keep/com.google.android.apps.keep.ui.homescreenwidget.WidgetConfigureActivity
            widgetPreview.setImageDrawable(widgetProviderInfoList.get(indexWidget).loadPreviewImage(getApplicationContext(), DisplayMetrics.DENSITY_HIGH));
            widgetPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    pickedAppWidgetProviderInfo = widgetProviderInfoList.get(indexWidget);
                    pickedWidgetId = appWidgetHostView.allocateAppWidgetId();

                    Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pickedWidgetId);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, pickedAppWidgetProviderInfo.provider);
                    startActivityForResult(intent, 333);

                    System.out.println("WidgetId === " + pickedWidgetId);

                    if (pickedAppWidgetProviderInfo.configure != null) {

                        Intent intentWidgetConfiguration = new Intent();
//                        intentWidgetConfiguration.setAction(AppWidgetManager.ACTION_APPWIDGET_BIND);
                        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
                        intentWidgetConfiguration.setComponent(pickedAppWidgetProviderInfo.configure);
                        intentWidgetConfiguration.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pickedWidgetId);
                        intentWidgetConfiguration.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, pickedAppWidgetProviderInfo.provider);
                        intentWidgetConfiguration.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intentWidgetConfiguration, WIDGET_CONFIGURATION_REQUEST);

                    } else {

                        createWidgetByID(((RelativeLayout) findViewById(R.id.widgetHost)), appWidgetHostView, pickedAppWidgetProviderInfo, pickedWidgetId);

                    }


                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (pickedWidgetId != -1) {
            System.out.println(pickedWidgetId);

            createWidgetByID(((RelativeLayout) findViewById(R.id.widgetHost)), appWidgetHostView, pickedAppWidgetProviderInfo, pickedWidgetId);

        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case WIDGET_CONFIGURATION_REQUEST: {
                    Bundle extras = data.getExtras();
                    int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);


                    System.out.println(">>> onActivityResult <<< " + appWidgetId);

                    break;
                }
                case 333: {
                    System.out.println(">>> 333 <<<");

                    break;
                }
            }
        }
    }

    public void createWidgetByID(ViewGroup widgetView, AppWidgetHost appWidgetHostView, AppWidgetProviderInfo appWidgetProviderInfo, int widgetId) {
        try {
            widgetView.removeAllViews();

            appWidgetHostView.startListening();

            AppWidgetHostView hostView = appWidgetHostView.createView(getApplicationContext(), widgetId, appWidgetProviderInfo);
            hostView.setAppWidget(widgetId, appWidgetProviderInfo);

            int widgetWidth = 0, widgetHeight = 0;

            System.out.println("widget id >> " + widgetId);

            widgetWidth = appWidgetProviderInfo.minWidth;
            widgetHeight = appWidgetProviderInfo.minHeight;

            hostView.setMinimumWidth(widgetWidth);
            hostView.setMinimumHeight(widgetHeight);

            widgetView.addView(hostView);

            Bundle bundle = new Bundle();
            appWidgetManager.bindAppWidgetIdIfAllowed(widgetId, appWidgetProviderInfo.provider, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
