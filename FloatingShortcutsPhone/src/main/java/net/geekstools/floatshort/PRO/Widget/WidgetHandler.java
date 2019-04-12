package net.geekstools.floatshort.PRO.Widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;

import java.util.List;

public class WidgetHandler extends Activity {

    FunctionsClass functionsClass;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.widget_handler);

        functionsClass = new FunctionsClass(getApplicationContext(), WidgetHandler.this);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        List<AppWidgetProviderInfo> widgetProviderInfoList = appWidgetManager.getInstalledProviders();

        int widgetIndex = 0;
        for (AppWidgetProviderInfo appWidgetProviderInfo : widgetProviderInfoList) {


            System.out.println(widgetIndex + " >>>>> " + appWidgetProviderInfo.configure + " | " + appWidgetProviderInfo.provider + " <<<<<");

            widgetIndex++;
        }


        ImageView widgetPreview = (ImageView) findViewById(R.id.widgetPreview);
        try {
            //com.google.android.keep/com.google.android.apps.keep.ui.homescreenwidget.WidgetConfigureActivity
            widgetPreview.setImageDrawable(widgetProviderInfoList.get(21).loadPreviewImage(getApplicationContext(), DisplayMetrics.DENSITY_HIGH));
            widgetPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.setAction("android.appwidget.action.APPWIDGET_CONFIGURE");
                    intent.setComponent(widgetProviderInfoList.get(21).configure);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetProviderInfoList.get(21).autoAdvanceViewId);

//                    intent.setPackage("com.google.android.keep");
                    /*intent.setClassName(*//*widgetProviderInfoList.get(17).configure.getPackageName()*//*"com.google.android.keep",
                     *//*widgetProviderInfoList.get(17).configure.getClassName()*//*"com.google.android.apps.keep.ui.homescreenwidget.WidgetConfigureActivity");*/
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
