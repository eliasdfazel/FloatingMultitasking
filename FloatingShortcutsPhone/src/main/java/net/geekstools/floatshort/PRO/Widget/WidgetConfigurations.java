package net.geekstools.floatshort.PRO.Widget;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import net.geeksempire.chat.vicinity.Util.RoomSqLiteDatabase.UserInformation.WidgetDataInterface;
import net.geeksempire.chat.vicinity.Util.RoomSqLiteDatabase.UserInformation.WidgetDataModel;
import net.geekstools.floatshort.PRO.BuildConfig;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.floatshort.PRO.Util.NavAdapter.RecycleViewSmoothLayoutGrid;
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Widget.NavAdapter.ConfiguredWidgetsAdapter;
import net.geekstools.floatshort.PRO.Widget.NavAdapter.InstalledWidgetsAdapter;
import net.geekstools.floatshort.PRO.Widget.NavAdapter.WidgetSectionedGridRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class WidgetConfigurations extends Activity {

    FunctionsClass functionsClass;

    RelativeLayout wholeWidget;

    RecyclerView installedWidgetsLoadView, configuredWidgetsLoadView;
    ScrollView installedWidgetsNestedScrollView, configuredWidgetsNestedScrollView;

    List<WidgetSectionedGridRecyclerViewAdapter.Section> installedWidgetsSections, configuredWidgetsSections;
    RecyclerView.Adapter installedWidgetsRecyclerViewAdapter, configuredWidgetsRecyclerViewAdapter;
    WidgetSectionedGridRecyclerViewAdapter configuredWidgetsSectionedGridRecyclerViewAdapter;
    GridLayoutManager installedWidgetsRecyclerViewLayoutManager, configuredWidgetsRecyclerViewLayoutManager;

    RelativeLayout loadingSplash;
    ProgressBar loadingBarLTR;
    TextView gx;

    Button addWidget;

    List<AppWidgetProviderInfo> widgetProviderInfoList;
    ArrayList<NavDrawerItem> installedWidgetsNavDrawerItems, configuredWidgetsNavDrawerItems;

    AppWidgetManager appWidgetManager;
    AppWidgetHost appWidgetHost;

    LoadCustomIcons loadCustomIcons;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.widget_handler);

        functionsClass = new FunctionsClass(getApplicationContext(), WidgetConfigurations.this);

        wholeWidget = (RelativeLayout) findViewById(R.id.wholeWidget);

        installedWidgetsNestedScrollView = (ScrollView) findViewById(R.id.nestedScrollView);
        installedWidgetsLoadView = (RecyclerView) findViewById(R.id.list);

        configuredWidgetsNestedScrollView = (ScrollView) findViewById(R.id.configuredWidgetNestedScrollView);
        configuredWidgetsLoadView = (RecyclerView) findViewById(R.id.configuredWidgetList);

        addWidget = (Button) findViewById(R.id.addWidget);

        TextView widgetPickerTitle = (TextView) findViewById(R.id.widgetPickerTitle);
        widgetPickerTitle.setText(Html.fromHtml(getString(R.string.widgetPickerTitle)));
        widgetPickerTitle.setTextColor(PublicVariable.themeLightDark ? getColor(R.color.dark) : getColor(R.color.light));

        installedWidgetsRecyclerViewLayoutManager = new RecycleViewSmoothLayoutGrid(getApplicationContext(), functionsClass.columnCount(190), OrientationHelper.VERTICAL, false);
        installedWidgetsLoadView.setLayoutManager(installedWidgetsRecyclerViewLayoutManager);

        installedWidgetsSections = new ArrayList<WidgetSectionedGridRecyclerViewAdapter.Section>();

        configuredWidgetsRecyclerViewLayoutManager = new RecycleViewSmoothLayoutGrid(getApplicationContext(), functionsClass.columnCount(190), OrientationHelper.VERTICAL, false);
        configuredWidgetsLoadView.setLayoutManager(configuredWidgetsRecyclerViewLayoutManager);

        configuredWidgetsSections = new ArrayList<WidgetSectionedGridRecyclerViewAdapter.Section>();

        if (functionsClass.appThemeTransparent() == true) {
            functionsClass.setThemeColorAutomationFeature(wholeWidget, true);
        } else {
            functionsClass.setThemeColorAutomationFeature(wholeWidget, false);
        }

        appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetHost = new AppWidgetHost(getApplicationContext(), (int) System.currentTimeMillis());

        installedWidgetsNavDrawerItems = new ArrayList<NavDrawerItem>();
        configuredWidgetsNavDrawerItems = new ArrayList<NavDrawerItem>();

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }

        if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
            LoadConfiguredWidgets loadConfiguredWidgets = new LoadConfiguredWidgets();
            loadConfiguredWidgets.execute();
        } else {
            loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
            if (functionsClass.appThemeTransparent() == true) {
                loadingSplash.setBackgroundColor(Color.TRANSPARENT);
            } else {
                loadingSplash.setBackgroundColor(getWindow().getNavigationBarColor());
            }

            loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgressltr);
            gx = (TextView) findViewById(R.id.gx);
            Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
            gx.setTypeface(face);

            if (PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeTextColor, android.graphics.PorterDuff.Mode.MULTIPLY);
                gx.setTextColor(getColor(R.color.dark));
            } else if (!PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeColor, android.graphics.PorterDuff.Mode.MULTIPLY);
                gx.setTextColor(getColor(R.color.light));
            }
        }

        ImageView floatingLogo = (ImageView) findViewById(R.id.loadLogo);
        LayerDrawable drawFloatingLogo = (LayerDrawable) getDrawable(R.drawable.draw_floating_widgets);
        GradientDrawable backFloatingLogo = (GradientDrawable) drawFloatingLogo.findDrawableByLayerId(R.id.backtemp);
        backFloatingLogo.setColor(PublicVariable.primaryColorOpposite);
        floatingLogo.setImageDrawable(drawFloatingLogo);
    }

    @Override
    public void onStart() {
        super.onStart();

        addWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (installedWidgetsNestedScrollView.isShown()) {
                    ViewCompat.animate(addWidget)
                            .rotation(0.0F)
                            .withLayer()
                            .setDuration(300L)
                            .setInterpolator(new OvershootInterpolator(3.0f))
                            .start();

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.go_down);
                    installedWidgetsNestedScrollView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            installedWidgetsNestedScrollView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                } else {

                    LoadInstalledWidgets loadInstalledWidgets = new LoadInstalledWidgets();
                    loadInstalledWidgets.execute();

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, R.anim.go_down);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case InstalledWidgetsAdapter.WIDGET_CONFIGURATION_REQUEST: {

                    Bundle dataExtras = data.getExtras();
                    int appWidgetId = dataExtras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            WidgetDataModel widgetDataModel = new WidgetDataModel(
                                    appWidgetId,
                                    InstalledWidgetsAdapter.pickedWidgetPackageName,
                                    functionsClass.appName(InstalledWidgetsAdapter.pickedWidgetPackageName),
                                    InstalledWidgetsAdapter.pickedWidgetLabel
                            );

                            String newAppName = functionsClass.appName(InstalledWidgetsAdapter.pickedWidgetPackageName);

                            WidgetDataInterface widgetDataInterface = Room.databaseBuilder(getApplicationContext(), WidgetDataInterface.class, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                    .fallbackToDestructiveMigration()
                                    .addCallback(new RoomDatabase.Callback() {
                                        @Override
                                        public void onCreate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                            super.onCreate(supportSQLiteDatabase);
                                        }

                                        @Override
                                        public void onOpen(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                            super.onOpen(supportSQLiteDatabase);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    LoadConfiguredWidgets loadConfiguredWidgets = new LoadConfiguredWidgets();
                                                    loadConfiguredWidgets.execute();
                                                }
                                            });
                                        }
                                    })
                                    .build();
                            widgetDataInterface.initDataAccessObject().insertNewWidgetData(widgetDataModel);
                            widgetDataInterface.close();
                        }
                    }).start();

                    break;
                }
            }
        }

    }

    public class LoadConfiguredWidgets extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            configuredWidgetsNavDrawerItems.clear();
            configuredWidgetsSections.clear();

            loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
            if (functionsClass.appThemeTransparent() == true) {
                loadingSplash.setBackgroundColor(Color.TRANSPARENT);
            } else {
                loadingSplash.setBackgroundColor(getWindow().getNavigationBarColor());
            }

            loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgressltr);
            gx = (TextView) findViewById(R.id.gx);
            Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
            gx.setTypeface(face);

            if (PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeTextColor, android.graphics.PorterDuff.Mode.MULTIPLY);
                gx.setTextColor(getResources().getColor(R.color.dark));
            } else if (!PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeColor, android.graphics.PorterDuff.Mode.MULTIPLY);
                gx.setTextColor(getResources().getColor(R.color.light));
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {


            try {

                WidgetDataInterface widgetDataInterface = Room.databaseBuilder(getApplicationContext(), WidgetDataInterface.class, PublicVariable.WIDGET_DATA_DATABASE_NAME)
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

                List<WidgetDataModel> widgetDataModels = widgetDataInterface.initDataAccessObject().getAllWidgetData();

                String oldAppName = "";
                int widgetIndex = 0;
                for (WidgetDataModel widgetDataModel : widgetDataModels) {

                    try {
                        int appWidgetId = widgetDataModel.getWidgetId();

                        AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
                        String packageName = widgetDataModel.getPackageName();
                        String newAppName = functionsClass.appName(packageName);
                        Drawable appIcon = functionsClass.shapedAppIcon(packageName);

                        if (widgetIndex == 0) {
                            configuredWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, appIcon));
                        } else {
                            if (!oldAppName.equals(newAppName)) {
                                configuredWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, appIcon));
                            }
                        }

                        oldAppName = functionsClass.appName(packageName);

                        configuredWidgetsNavDrawerItems.add(new NavDrawerItem(
                                newAppName,
                                packageName,
                                (appWidgetProviderInfo.loadLabel(getPackageManager()) != null) ? appWidgetProviderInfo.loadLabel(getPackageManager()) : newAppName,
                                appIcon,
                                appWidgetProviderInfo,
                                appWidgetId
                        ));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    widgetIndex++;
                }


                for (int i = widgetIndex; i < (widgetIndex + 1); i++) {
                    configuredWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(i, "", null));
                }

                configuredWidgetsRecyclerViewAdapter = new ConfiguredWidgetsAdapter(WidgetConfigurations.this, getApplicationContext(), configuredWidgetsNavDrawerItems, appWidgetManager, appWidgetHost);

                widgetDataInterface.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            configuredWidgetsRecyclerViewAdapter.notifyDataSetChanged();
            WidgetSectionedGridRecyclerViewAdapter.Section[] sectionsData = new WidgetSectionedGridRecyclerViewAdapter.Section[configuredWidgetsSections.size()];
            configuredWidgetsSectionedGridRecyclerViewAdapter = new WidgetSectionedGridRecyclerViewAdapter(
                    getApplicationContext(),
                    R.layout.widgets_sections,
                    configuredWidgetsLoadView,
                    configuredWidgetsRecyclerViewAdapter
            );
            configuredWidgetsSectionedGridRecyclerViewAdapter.setSections(configuredWidgetsSections.toArray(sectionsData));
            configuredWidgetsSectionedGridRecyclerViewAdapter.notifyDataSetChanged();
            configuredWidgetsLoadView.setAdapter(configuredWidgetsSectionedGridRecyclerViewAdapter);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    configuredWidgetsNestedScrollView.smoothScrollTo(0, 0);

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    loadingSplash.setVisibility(View.INVISIBLE);
                    loadingSplash.startAnimation(animation);
                }
            }, 200);
        }
    }

    public class LoadInstalledWidgets extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            installedWidgetsNavDrawerItems.clear();
            installedWidgetsSections.clear();

            installedWidgetsNestedScrollView.setBackgroundColor(PublicVariable.themeLightDark ? getColor(R.color.transparent_light_high_twice) : getColor(R.color.transparent_dark_high_twice));
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                widgetProviderInfoList = appWidgetManager.getInstalledProviders();

                if (functionsClass.loadCustomIcons()) {
                    loadCustomIcons.load();
                    if (BuildConfig.DEBUG) {
                        System.out.println("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                    }
                }

                String oldAppName = "";
                int widgetIndex = 0;
                for (AppWidgetProviderInfo appWidgetProviderInfo : widgetProviderInfoList) {

                    try {
                        String newAppName = functionsClass.appName(appWidgetProviderInfo.provider.getPackageName());
                        Drawable newAppIcon = functionsClass.shapedAppIcon(appWidgetProviderInfo.provider.getPackageName());

                        if (widgetIndex == 0) {
                            installedWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, newAppIcon));

                        } else {
                            if (!oldAppName.equals(newAppName)) {

                                installedWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, newAppIcon));

                            }
                        }

                        oldAppName = functionsClass.appName(appWidgetProviderInfo.provider.getPackageName());

                        Drawable widgetPreviewDrawable = appWidgetProviderInfo.loadPreviewImage(getApplicationContext(), DisplayMetrics.DENSITY_HIGH);
                        String widgetLabel = appWidgetProviderInfo.loadLabel(getPackageManager());

                        installedWidgetsNavDrawerItems.add(new NavDrawerItem(functionsClass.appName(appWidgetProviderInfo.provider.getPackageName()),
                                appWidgetProviderInfo.provider.getPackageName(),
                                (widgetLabel != null) ? widgetLabel : newAppName,
                                newAppIcon,
                                (widgetPreviewDrawable != null) ? widgetPreviewDrawable : appWidgetProviderInfo.loadIcon(getApplicationContext(), DisplayMetrics.DENSITY_HIGH),
                                appWidgetProviderInfo
                        ));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    widgetIndex++;
                }

                for (int i = widgetIndex; i < (widgetIndex + 1); i++) {
                    installedWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(i, "", null));
                }

                installedWidgetsRecyclerViewAdapter = new InstalledWidgetsAdapter(WidgetConfigurations.this, getApplicationContext(), installedWidgetsNavDrawerItems, appWidgetHost);


            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ViewPropertyAnimator viewPropertyAnimator = addWidget.animate()
                    .rotation(135.0F)
                    .withLayer()
                    .setDuration(500L)
                    .setInterpolator(new OvershootInterpolator(13.0f));
            viewPropertyAnimator.start();

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_up);
            installedWidgetsNestedScrollView.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    installedWidgetsNestedScrollView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            installedWidgetsRecyclerViewAdapter.notifyDataSetChanged();
            WidgetSectionedGridRecyclerViewAdapter.Section[] sectionsData = new WidgetSectionedGridRecyclerViewAdapter.Section[installedWidgetsSections.size()];
            WidgetSectionedGridRecyclerViewAdapter widgetSectionedGridRecyclerViewAdapter = new WidgetSectionedGridRecyclerViewAdapter(
                    getApplicationContext(),
                    R.layout.widgets_sections,
                    installedWidgetsLoadView,
                    installedWidgetsRecyclerViewAdapter
            );
            widgetSectionedGridRecyclerViewAdapter.setSections(installedWidgetsSections.toArray(sectionsData));
            widgetSectionedGridRecyclerViewAdapter.notifyDataSetChanged();
            installedWidgetsLoadView.setAdapter(widgetSectionedGridRecyclerViewAdapter);

            if (!getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                        loadingSplash.setVisibility(View.INVISIBLE);
                        loadingSplash.startAnimation(animation);
                    }
                }, 200);
            }
        }
    }

    public static void createWidget(Context context, ViewGroup widgetView, AppWidgetManager appWidgetManager, AppWidgetHost appWidgetHost, AppWidgetProviderInfo appWidgetProviderInfo, int widgetId) {
        try {
            widgetView.removeAllViews();

            FunctionsClass functionsClass = new FunctionsClass(context);

            appWidgetHost.startListening();

            AppWidgetHostView hostView = appWidgetHost.createView(context, widgetId, appWidgetProviderInfo);
            hostView.setAppWidget(widgetId, appWidgetProviderInfo);

            int widgetWidth = 213, widgetHeight = 213;

            hostView.setMinimumWidth(widgetWidth);
            hostView.setMinimumHeight(widgetHeight);

            Bundle bundle = new Bundle();
            bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, widgetWidth);
            bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, widgetHeight);
            bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, functionsClass.displayX() / 2);
            bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, functionsClass.displayY() / 2);
            appWidgetManager.bindAppWidgetIdIfAllowed(widgetId, appWidgetProviderInfo.provider, bundle);

            widgetView.addView(hostView);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
