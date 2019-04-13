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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.BuildConfig;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.floatshort.PRO.Util.NavAdapter.RecycleViewSmoothLayoutGrid;
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Widget.NavAdapter.WidgetSectionedGridRecyclerViewAdapter;
import net.geekstools.floatshort.PRO.Widget.NavAdapter.WidgetsAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

public class WidgetHandler extends Activity {

    FunctionsClass functionsClass;

    RelativeLayout wholeWidget;

    RecyclerView loadView;
    ScrollView nestedScrollView;

    List<WidgetSectionedGridRecyclerViewAdapter.Section> sections;
    RecyclerView.Adapter recyclerViewAdapter;
    GridLayoutManager recyclerViewLayoutManager;

    RelativeLayout loadingSplash;
    ProgressBar loadingBarLTR;
    TextView gx;

    Button addWidget;

    List<AppWidgetProviderInfo> widgetProviderInfoList;
    ArrayList<NavDrawerItem> navDrawerItems;

    public static final int WIDGET_CREATING_REQUEST = 333;
    AppWidgetManager appWidgetManager;
    AppWidgetHost appWidgetHostView;

    public static final int WIDGET_CONFIGURATION_REQUEST = 666;
    AppWidgetProviderInfo pickedAppWidgetProviderInfo;

    int pickedWidgetId = -1;

    LoadCustomIcons loadCustomIcons;

    public static void createWidget(Context context, ViewGroup widgetView, AppWidgetManager appWidgetManager, AppWidgetHost appWidgetHostView, AppWidgetProviderInfo appWidgetProviderInfo, int widgetId) {
        try {
            widgetView.removeAllViews();

            appWidgetHostView.startListening();

            AppWidgetHostView hostView = appWidgetHostView.createView(context, widgetId, appWidgetProviderInfo);
            hostView.setAppWidget(widgetId, appWidgetProviderInfo);

            int widgetWidth = 0, widgetHeight = 0;

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

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.widget_handler);

        functionsClass = new FunctionsClass(getApplicationContext(), WidgetHandler.this);

        wholeWidget = (RelativeLayout) findViewById(R.id.wholeWidget);
        nestedScrollView = (ScrollView) findViewById(R.id.nestedScrollView);
        loadView = (RecyclerView) findViewById(R.id.list);

        addWidget = (Button) findViewById(R.id.addWidget);

        recyclerViewLayoutManager = new RecycleViewSmoothLayoutGrid(getApplicationContext(), functionsClass.columnCount(190), OrientationHelper.VERTICAL, false);
        loadView.setLayoutManager(recyclerViewLayoutManager);

        sections = new ArrayList<WidgetSectionedGridRecyclerViewAdapter.Section>();


        if (functionsClass.appThemeTransparent() == true) {
            functionsClass.setThemeColorAutomationFeature(wholeWidget, true);
        } else {
            functionsClass.setThemeColorAutomationFeature(wholeWidget, false);
        }

        appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetHostView = new AppWidgetHost(getApplicationContext(), (int) System.currentTimeMillis());

        navDrawerItems = new ArrayList<NavDrawerItem>();

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }

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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onStart() {
        super.onStart();

        addWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nestedScrollView.isShown()) {

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.go_down);
                    nestedScrollView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            nestedScrollView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                } else {

                    LoadConfiguredWidgets loadConfiguredWidgets = new LoadConfiguredWidgets();
                    loadConfiguredWidgets.execute();

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case WidgetsAdapter.WIDGET_CONFIGURATION_REQUEST: {

                    Bundle dataExtras = data.getExtras();
                    int appWidgetId = dataExtras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

                    System.out.println(">>> onActivityResult WIDGET_CONFIGURATION_REQUEST <<< " + appWidgetId);

                    createWidget(getApplicationContext(), ((RelativeLayout) findViewById(R.id.widgetTemp)), appWidgetManager, appWidgetHostView, WidgetsAdapter.pickedAppWidgetProviderInfo, appWidgetId);

                    break;
                }
            }
        }

    }

    public class LoadConfiguredWidgets extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            navDrawerItems.clear();
            sections.clear();


            /*loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
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
                }*/

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
                            sections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, newAppIcon));

                        } else {
                            if (!oldAppName.equals(newAppName)) {

                                sections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, newAppIcon));

                            }
                        }

                        oldAppName = functionsClass.appName(appWidgetProviderInfo.provider.getPackageName());

                        Drawable widgetPreviewDrawable = appWidgetProviderInfo.loadPreviewImage(getApplicationContext(), DisplayMetrics.DENSITY_HIGH);
                        String widgetLabel = appWidgetProviderInfo.loadLabel(getPackageManager());

                        navDrawerItems.add(new NavDrawerItem(functionsClass.appName(appWidgetProviderInfo.provider.getPackageName()),
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
                    sections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(i, "", null));
                }

                recyclerViewAdapter = new WidgetsAdapter(WidgetHandler.this, getApplicationContext(), navDrawerItems, appWidgetHostView);


            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.go_up);
            nestedScrollView.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    nestedScrollView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            recyclerViewAdapter.notifyDataSetChanged();
            WidgetSectionedGridRecyclerViewAdapter.Section[] sectionsData = new WidgetSectionedGridRecyclerViewAdapter.Section[sections.size()];
            WidgetSectionedGridRecyclerViewAdapter widgetSectionedGridRecyclerViewAdapter = new WidgetSectionedGridRecyclerViewAdapter(
                    getApplicationContext(),
                    R.layout.widgets_sections,
                    loadView,
                    recyclerViewAdapter
            );
            widgetSectionedGridRecyclerViewAdapter.setSections(sections.toArray(sectionsData));
            loadView.setAdapter(widgetSectionedGridRecyclerViewAdapter);

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
