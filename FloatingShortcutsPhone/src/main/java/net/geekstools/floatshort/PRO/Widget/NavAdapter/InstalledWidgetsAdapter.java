package net.geekstools.floatshort.PRO.Widget.NavAdapter;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.NavAdapter.NavDrawerItem;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class InstalledWidgetsAdapter extends RecyclerView.Adapter<InstalledWidgetsAdapter.ViewHolder> {

    Activity activity;
    Context context;

    FunctionsClass functionsClass;

    ArrayList<NavDrawerItem> navDrawerItems;

    View view;
    ViewHolder viewHolder;

    public static final int WIDGET_CONFIGURATION_REQUEST = 666;

    public static String pickedWidgetPackageName;
    public static AppWidgetProviderInfo pickedAppWidgetProviderInfo;
    public static int pickedWidgetId;
    public static String pickedWidgetLabel;

    AppWidgetHost appWidgetHost;

    public InstalledWidgetsAdapter(Activity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems, AppWidgetHost appWidgetHost) {
        this.context = context;
        this.activity = activity;

        this.navDrawerItems = navDrawerItems;

        this.appWidgetHost = appWidgetHost;

        functionsClass = new FunctionsClass(context, activity);
    }

    @Override
    public InstalledWidgetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.widget_installed_items, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolderBinder, final int position) {

        viewHolder.widgetPreview.setImageDrawable(navDrawerItems.get(position).getWidgetPreview());
        viewHolder.widgetLabel.setText(navDrawerItems.get(position).getWidgetLabel());
        viewHolder.widgetLabel.setTextColor(PublicVariable.themeLightDark ? context.getColor(R.color.dark) : context.getColor(R.color.light));

        viewHolder.widgetitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        viewHolder.widgetitem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                functionsClass.doVibrate(77);

                pickedWidgetPackageName = navDrawerItems.get(position).getPackageName();
                pickedAppWidgetProviderInfo = navDrawerItems.get(position).getAppWidgetProviderInfo();
                pickedWidgetId = appWidgetHost.allocateAppWidgetId();
                pickedWidgetLabel = navDrawerItems.get(position).getWidgetLabel();

                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pickedWidgetId);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, pickedAppWidgetProviderInfo.provider);
                activity.startActivityForResult(intent, WIDGET_CONFIGURATION_REQUEST);


                if (pickedAppWidgetProviderInfo.configure != null) {
                    Intent intentWidgetConfiguration = new Intent();
                    //intentWidgetConfiguration.setAction(AppWidgetManager.ACTION_APPWIDGET_BIND);
                    intentWidgetConfiguration.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
                    intentWidgetConfiguration.setComponent(pickedAppWidgetProviderInfo.configure);
                    intentWidgetConfiguration.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pickedWidgetId);
                    intentWidgetConfiguration.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, pickedAppWidgetProviderInfo.provider);
                    intentWidgetConfiguration.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivityForResult(intentWidgetConfiguration, WIDGET_CONFIGURATION_REQUEST);
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return navDrawerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout widgetitem;
        ImageView widgetPreview;
        TextView widgetLabel;

        public ViewHolder(View view) {
            super(view);
            widgetitem = (RelativeLayout) view.findViewById(R.id.widgetItem);
            widgetPreview = (ImageView) view.findViewById(R.id.widgetPreview);
            widgetLabel = (TextView) view.findViewById(R.id.widgetLabel);
        }
    }
}