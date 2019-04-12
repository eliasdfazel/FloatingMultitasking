package net.geekstools.floatshort.PRO.Widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        List<AppWidgetProviderInfo> widgetProviderInfoList = manager.getInstalledProviders();

        for (AppWidgetProviderInfo appWidgetProviderInfo : widgetProviderInfoList) {


            System.out.println(">>>>> " + appWidgetProviderInfo.label + " | " + appWidgetProviderInfo.provider + " <<<<<");


        }


        ImageView widgetPreview = (ImageView) findViewById(R.id.widgetPreview);
        try {
            widgetPreview.setImageDrawable(widgetProviderInfoList.get(widgetProviderInfoList.size() - 1).loadPreviewImage(getApplicationContext(), DisplayMetrics.DENSITY_MEDIUM));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
