package net.geekstools.floatshort.PRO.Widget.NavAdapter;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
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
import net.geekstools.floatshort.PRO.Widget.WidgetHandler;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class ConfiguredWidgetsAdapter extends RecyclerView.Adapter<ConfiguredWidgetsAdapter.ViewHolder> {

    Activity activity;
    Context context;

    FunctionsClass functionsClass;

    ArrayList<NavDrawerItem> navDrawerItems;

    View view;
    ViewHolder viewHolder;

    AppWidgetManager appWidgetManager;
    AppWidgetHost appWidgetHost;

    public ConfiguredWidgetsAdapter(Activity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems, AppWidgetManager appWidgetManager, AppWidgetHost appWidgetHost) {
        this.context = context;
        this.activity = activity;

        this.navDrawerItems = navDrawerItems;

        this.appWidgetManager = appWidgetManager;
        this.appWidgetHost = appWidgetHost;

        functionsClass = new FunctionsClass(context, activity);
    }

    @Override
    public ConfiguredWidgetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.widget_configured_items, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolderBinder, final int position) {
        AppWidgetProviderInfo appWidgetProviderInfo = navDrawerItems.get(position).getAppWidgetProviderInfo();
        int appWidgetId = navDrawerItems.get(position).getAppWidgetId();

        WidgetHandler.createWidget(activity, viewHolder.widgetPreview,
                appWidgetManager, appWidgetHost,
                appWidgetProviderInfo, appWidgetId);

        viewHolder.widgetLabel.setText(navDrawerItems.get(position).getWidgetLabel());
        viewHolder.widgetLabel.setTextColor(PublicVariable.themeLightDark ? context.getColor(R.color.dark) : context.getColor(R.color.light));

        viewHolder.floatTheWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        viewHolder.floatTheWidget.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                functionsClass.doVibrate(77);

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return navDrawerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout widgetItem, widgetPreview;
        ImageView floatTheWidget;
        TextView widgetLabel;

        public ViewHolder(View view) {
            super(view);
            widgetItem = (RelativeLayout) view.findViewById(R.id.widgetItem);
            widgetPreview = (RelativeLayout) view.findViewById(R.id.widgetPreview);
            floatTheWidget = (ImageView) view.findViewById(R.id.floatTheWidget);
            widgetLabel = (TextView) view.findViewById(R.id.widgetLabel);
        }
    }
}
