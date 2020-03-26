/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 2:29 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widget.WidgetsAdapter;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.material.textfield.TextInputEditText;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.SearchEngine.SearchEngineAdapter;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataDAO;
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataInterface;
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ConfiguredWidgetsAdapter extends RecyclerView.Adapter<ConfiguredWidgetsAdapter.ViewHolder> {

    WidgetConfigurations widgetConfigurations;
    Context context;

    FunctionsClass functionsClass;

    ArrayList<AdapterItems> adapterItems;

    View view;
    ViewHolder viewHolder;

    AppWidgetManager appWidgetManager;
    AppWidgetHost appWidgetHost;

    public ConfiguredWidgetsAdapter(WidgetConfigurations widgetConfigurations, Context context, ArrayList<AdapterItems> adapterItems, AppWidgetManager appWidgetManager, AppWidgetHost appWidgetHost) {
        this.context = context;
        this.widgetConfigurations = widgetConfigurations;

        this.adapterItems = adapterItems;

        this.appWidgetManager = appWidgetManager;
        this.appWidgetHost = appWidgetHost;

        functionsClass = new FunctionsClass(context);
    }

    @Override
    public ConfiguredWidgetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.widget_configured_items, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolderBinder, final int position) {
        AppWidgetProviderInfo appWidgetProviderInfo = adapterItems.get(position).getAppWidgetProviderInfo();
        int appWidgetId = adapterItems.get(position).getAppWidgetId();

        widgetConfigurations.createWidget(widgetConfigurations, viewHolder.widgetPreview,
                appWidgetManager, appWidgetHost,
                appWidgetProviderInfo, appWidgetId);

        viewHolder.widgetLabel.setText(adapterItems.get(position).getAddedWidgetRecovery() ? adapterItems.get(position).getWidgetLabel() + " " + "\uD83D\uDD04" : adapterItems.get(position).getWidgetLabel());
        viewHolder.widgetLabel.setTextColor(PublicVariable.themeLightDark ? context.getColor(R.color.dark) : context.getColor(R.color.light));

        LayerDrawable drawFloatTheWidget = (LayerDrawable) context.getDrawable(R.drawable.draw_open);
        Drawable backFloatTheWidget = drawFloatTheWidget.findDrawableByLayerId(R.id.backgroundTemporary);
        backFloatTheWidget.setTint(functionsClass.extractDominantColor(functionsClass.appIcon(adapterItems.get(position).getPackageName())));
        viewHolder.floatTheWidget.setImageDrawable(drawFloatTheWidget);

        viewHolder.floatTheWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.runUnlimitedWidgetService(adapterItems.get(position).getAppWidgetId(), adapterItems.get(position).getWidgetLabel());
            }
        });
        viewHolder.floatTheWidget.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AppWidgetProviderInfo appWidgetProviderInfoLongClick = adapterItems.get(position).getAppWidgetProviderInfo();

                functionsClass.popupOptionWidget(widgetConfigurations, context, view,
                        adapterItems.get(position).getPackageName(),
                        adapterItems.get(position).getClassNameProviderWidget(),
                        adapterItems.get(position).getWidgetLabel(),
                        appWidgetProviderInfoLongClick.loadPreviewImage(context, DisplayMetrics.DENSITY_LOW) != null ?
                                appWidgetProviderInfoLongClick.loadPreviewImage(context, DisplayMetrics.DENSITY_HIGH) : appWidgetProviderInfoLongClick.loadIcon(context, DisplayMetrics.DENSITY_HIGH),
                        adapterItems.get(position).getAddedWidgetRecovery());

                functionsClass.doVibrate(77);

                return true;
            }
        });

        viewHolder.widgetLabel.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (textView.length() > 0) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {



                                WidgetDataInterface widgetDataInterface = Room.databaseBuilder(context, WidgetDataInterface.class, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                        .fallbackToDestructiveMigration()
                                        .addCallback(new RoomDatabase.Callback() {
                                            @Override
                                            public void onCreate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                                super.onCreate(supportSQLiteDatabase);
                                            }

                                            @Override
                                            public void onOpen(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                                super.onOpen(supportSQLiteDatabase);
                                            }
                                        })
                                        .build();
                                WidgetDataDAO widgetDataDAO = widgetDataInterface.initDataAccessObject();
                                widgetDataDAO.updateWidgetLabelByWidgetId(adapterItems.get(position).getAppWidgetId(), textView.getText().toString().replace("\uD83D\uDD04", ""));
                                widgetDataInterface.close();

                                widgetConfigurations.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SearchEngineAdapter.allSearchResultItems.clear();

                                        widgetConfigurations.forceLoadConfiguredWidgets();
                                    }
                                });


                            }
                        }).start();
                    }

                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return adapterItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout widgetItem, widgetPreview;
        ImageView floatTheWidget;
        TextInputEditText widgetLabel;

        public ViewHolder(View view) {
            super(view);
            widgetItem = (RelativeLayout) view.findViewById(R.id.widgetItem);
            widgetPreview = (RelativeLayout) view.findViewById(R.id.widgetPreview);
            widgetLabel = (TextInputEditText) view.findViewById(R.id.widgetLabel);
            floatTheWidget = (ImageView) view.findViewById(R.id.floatTheWidget);
        }
    }
}
