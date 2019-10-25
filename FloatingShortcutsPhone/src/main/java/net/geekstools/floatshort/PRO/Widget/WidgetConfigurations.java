package net.geekstools.floatshort.PRO.Widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import net.geekstools.floatshort.PRO.Automation.Apps.AppAutoFeatures;
import net.geekstools.floatshort.PRO.Folders.FoldersHandler;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Shortcuts.HybridAppsList;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.IAP.InAppBilling;
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingManager;
import net.geekstools.floatshort.PRO.Util.NavAdapter.AdapterItems;
import net.geekstools.floatshort.PRO.Util.NavAdapter.RecycleViewSmoothLayoutGrid;
import net.geekstools.floatshort.PRO.Util.RemoteTask.RecoveryFolders;
import net.geekstools.floatshort.PRO.Util.RemoteTask.RecoveryShortcuts;
import net.geekstools.floatshort.PRO.Util.RemoteTask.RecoveryWidgets;
import net.geekstools.floatshort.PRO.Util.SearchEngine.SearchEngineAdapter;
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.PinPassword.HandlePinPassword;
import net.geekstools.floatshort.PRO.Util.SettingGUI.SettingGUI;
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Util.UI.SimpleGestureFilterSwitch;
import net.geekstools.floatshort.PRO.Widget.NavAdapter.ConfiguredWidgetsAdapter;
import net.geekstools.floatshort.PRO.Widget.NavAdapter.InstalledWidgetsAdapter;
import net.geekstools.floatshort.PRO.Widget.NavAdapter.WidgetSectionedGridRecyclerViewAdapter;
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataInterface;
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataModel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class WidgetConfigurations extends Activity implements SimpleGestureFilterSwitch.SimpleGestureListener {

    FunctionsClass functionsClass;
    FunctionsClassSecurity functionsClassSecurity;

    RelativeLayout wholeWidget, fullActionViews;
    ImageView addWidget,
            actionButton, recoverFloatingCategories, recoverFloatingApps;
    MaterialButton switchApps, switchCategories, recoveryAction, automationAction,
            reconfigure;

    RecyclerView installedWidgetsLoadView, configuredWidgetsLoadView;
    ScrollView installedWidgetsNestedScrollView, configuredWidgetsNestedScrollView,
            nestedIndexScrollView, installedNestedIndexScrollView;

    LinearLayout indexView, indexViewInstalled;
    TextView popupIndex;

    /*Search Engine*/
    SearchEngineAdapter searchRecyclerViewAdapter;
    ArrayList<AdapterItems> searchAdapterItems;
    TextInputLayout textInputSearchView;
    AppCompatAutoCompleteTextView searchView;
    ImageView searchIcon, searchFloatIt,
            searchClose;
    /*Search Engine*/

    Map<String, Integer> mapIndexFirstItem, mapIndexLastItem,
            mapIndexFirstItemInstalled, mapIndexLastItemInstalled;
    Map<Integer, String> mapRangeIndex,
            mapRangeIndexInstalled;
    NavigableMap<String, Integer> indexItems, indexItemsInstalled;
    List<String> indexListConfigured, indexListInstalled;


    List<WidgetSectionedGridRecyclerViewAdapter.Section> installedWidgetsSections, configuredWidgetsSections;
    RecyclerView.Adapter installedWidgetsRecyclerViewAdapter, configuredWidgetsRecyclerViewAdapter;
    WidgetSectionedGridRecyclerViewAdapter configuredWidgetsSectionedGridRecyclerViewAdapter;
    GridLayoutManager installedWidgetsRecyclerViewLayoutManager, configuredWidgetsRecyclerViewLayoutManager;

    RelativeLayout loadingSplash;
    ProgressBar loadingBarLTR;
    TextView gx;

    List<AppWidgetProviderInfo> widgetProviderInfoList;
    ArrayList<AdapterItems> installedWidgetsAdapterItems, configuredWidgetsAdapterItems;

    AppWidgetManager appWidgetManager;
    AppWidgetHost appWidgetHost;

    LoadCustomIcons loadCustomIcons;

    SimpleGestureFilterSwitch simpleGestureFilterSwitch;

    FirebaseRemoteConfig firebaseRemoteConfig;

    boolean installedWidgetsLoaded = false, configuredWidgetAvailable = false;

    public static boolean alreadyAuthenticated = false;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.widget_configurations_views);

        functionsClass = new FunctionsClass(getApplicationContext(), WidgetConfigurations.this);
        functionsClassSecurity = new FunctionsClassSecurity(WidgetConfigurations.this, getApplicationContext());

        if (!functionsClass.readPreference("WidgetsInformation", "Reallocated", true) && getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
            startActivity(new Intent(getApplicationContext(), WidgetsReallocationProcess.class),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());

            finish();
            return;
        }

        wholeWidget = (RelativeLayout) findViewById(R.id.wholeWidget);
        fullActionViews = (RelativeLayout) findViewById(R.id.fullActionViews);

        installedWidgetsNestedScrollView = (ScrollView) findViewById(R.id.installedNestedScrollView);
        installedWidgetsLoadView = (RecyclerView) findViewById(R.id.installedWidgetList);

        configuredWidgetsNestedScrollView = (ScrollView) findViewById(R.id.configuredWidgetNestedScrollView);
        configuredWidgetsLoadView = (RecyclerView) findViewById(R.id.configuredWidgetList);

        popupIndex = (TextView) findViewById(R.id.popupIndex);

        addWidget = (ImageView) findViewById(R.id.addWidget);
        actionButton = (ImageView) findViewById(R.id.actionButton);
        switchApps = (MaterialButton) findViewById(R.id.switchApps);
        switchCategories = (MaterialButton) findViewById(R.id.switchCategories);
        recoveryAction = (MaterialButton) findViewById(R.id.recoveryAction);
        automationAction = (MaterialButton) findViewById(R.id.automationAction);
        recoverFloatingCategories = (ImageView) findViewById(R.id.recoverFloatingCategories);
        recoverFloatingApps = (ImageView) findViewById(R.id.recoverFloatingApps);
        reconfigure = (MaterialButton) findViewById(R.id.reconfigure);

        nestedIndexScrollView = (ScrollView) findViewById(R.id.nestedIndexScrollView);
        installedNestedIndexScrollView = (ScrollView) findViewById(R.id.installedNestedIndexScrollView);

        indexView = (LinearLayout) findViewById(R.id.side_index);
        indexViewInstalled = (LinearLayout) findViewById(R.id.installed_side_index);

        /*Search Engine*/
        textInputSearchView = (TextInputLayout) findViewById(R.id.textInputSearchView);
        searchView = (AppCompatAutoCompleteTextView) findViewById(R.id.searchView);
        searchIcon = (ImageView) findViewById(R.id.searchIcon);
        searchFloatIt = (ImageView) findViewById(R.id.searchFloatIt);
        searchClose = (ImageView) findViewById(R.id.searchClose);
        /*Search Engine*/

        simpleGestureFilterSwitch = new SimpleGestureFilterSwitch(getApplicationContext(), this);

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
            functionsClass.setThemeColorFloating(wholeWidget, true);
        } else {
            functionsClass.setThemeColorFloating(wholeWidget, false);
        }

        appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetHost = new AppWidgetHost(getApplicationContext(), (int) System.currentTimeMillis());

        installedWidgetsAdapterItems = new ArrayList<AdapterItems>();
        configuredWidgetsAdapterItems = new ArrayList<AdapterItems>();
        indexListConfigured = new ArrayList<String>();
        indexListInstalled = new ArrayList<String>();
        mapIndexFirstItem = new LinkedHashMap<String, Integer>();
        mapIndexFirstItemInstalled = new LinkedHashMap<String, Integer>();
        mapIndexLastItem = new LinkedHashMap<String, Integer>();
        mapIndexLastItemInstalled = new LinkedHashMap<String, Integer>();
        mapRangeIndex = new LinkedHashMap<Integer, String>();
        mapRangeIndexInstalled = new LinkedHashMap<Integer, String>();
        indexItems = new TreeMap<String, Integer>();
        indexItemsInstalled = new TreeMap<String, Integer>();

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }

        if (getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
            LoadConfiguredWidgets loadConfiguredWidgets = new LoadConfiguredWidgets();
            loadConfiguredWidgets.execute();
        } else {
            reconfigure.setVisibility(View.INVISIBLE);

            actionButton.bringToFront();
            addWidget.bringToFront();

            addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener);

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

        LayerDrawable drawAddWidget = (LayerDrawable) getDrawable(R.drawable.draw_pref_add_widget);
        Drawable backAddWidget = drawAddWidget.findDrawableByLayerId(R.id.backtemp);
        Drawable frontAddWidget = drawAddWidget.findDrawableByLayerId(R.id.frontTemp).mutate();
        backAddWidget.setTint(PublicVariable.primaryColor);
        frontAddWidget.setTint(getColor(R.color.light));
        addWidget.setImageDrawable(drawAddWidget);

        LayerDrawable drawPreferenceAction = (LayerDrawable) getDrawable(R.drawable.draw_pref_action);
        Drawable backPreferenceAction = drawPreferenceAction.findDrawableByLayerId(R.id.backtemp);
        backPreferenceAction.setTint(PublicVariable.primaryColorOpposite);
        actionButton.setImageDrawable(drawPreferenceAction);

        switchApps.setTextColor(getColor(R.color.light));
        switchCategories.setTextColor(getColor(R.color.light));
        if (PublicVariable.themeLightDark /*light*/ && functionsClass.appThemeTransparent() /*transparent*/) {
            switchApps.setTextColor(getColor(R.color.dark));
            switchCategories.setTextColor(getColor(R.color.dark));
        }

        switchCategories.setBackgroundColor(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColor, 51) : PublicVariable.primaryColor);
        switchCategories.setRippleColor(ColorStateList.valueOf(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51) : PublicVariable.primaryColorOpposite));

        switchApps.setBackgroundColor(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColor, 51) : PublicVariable.primaryColor);
        switchApps.setRippleColor(ColorStateList.valueOf(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51) : PublicVariable.primaryColorOpposite));

        recoveryAction.setBackgroundColor(PublicVariable.primaryColorOpposite);
        recoveryAction.setRippleColor(ColorStateList.valueOf(PublicVariable.primaryColor));

        automationAction.setBackgroundColor(PublicVariable.primaryColorOpposite);
        automationAction.setRippleColor(ColorStateList.valueOf(PublicVariable.primaryColor));

        try {
            LayerDrawable drawRecoverFloatingCategories = (LayerDrawable) getDrawable(R.drawable.draw_recovery).mutate();
            Drawable backRecoverFloatingCategories = drawRecoverFloatingCategories.findDrawableByLayerId(R.id.backtemp).mutate();
            backRecoverFloatingCategories.setTint(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColor, 51) : PublicVariable.primaryColor);

            recoverFloatingCategories.setImageDrawable(drawRecoverFloatingCategories);
            recoverFloatingApps.setImageDrawable(drawRecoverFloatingCategories);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.doVibrate(33);

                if (PublicVariable.actionCenter == false) {
                    if (installedWidgetsNestedScrollView.isShown()) {
                        installedNestedIndexScrollView.setVisibility(View.INVISIBLE);

                        if (!configuredWidgetAvailable) {
                            addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener);
                        }

                        ViewCompat.animate(addWidget)
                                .rotation(0.0F)
                                .withLayer()
                                .setDuration(300L)
                                .setInterpolator(new OvershootInterpolator(3.0f))
                                .start();

                        int xPosition = (int) (addWidget.getX() + (addWidget.getWidth() / 2));
                        int yPosition = (int) (addWidget.getY() + (addWidget.getHeight() / 2));

                        int startRadius = 0;
                        int endRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());

                        Animator circularReveal = ViewAnimationUtils.createCircularReveal(installedWidgetsNestedScrollView, xPosition, yPosition, endRadius, startRadius);
                        circularReveal.setDuration(864);
                        circularReveal.start();
                        circularReveal.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                installedWidgetsNestedScrollView.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });

                        if (functionsClass.appThemeTransparent() == true) {
                            final Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            if (PublicVariable.themeLightDark) {
                                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                                if (functionsClass.returnAPI() > 25) {
                                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                                }
                            }

                            ValueAnimator valueAnimator = ValueAnimator
                                    .ofArgb(getWindow().getNavigationBarColor(), functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180));
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    window.setStatusBarColor((Integer) animator.getAnimatedValue());
                                    window.setNavigationBarColor((Integer) animator.getAnimatedValue());
                                }
                            });
                            valueAnimator.start();
                        } else {
                            final Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            if (PublicVariable.themeLightDark) {
                                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                                if (functionsClass.returnAPI() > 25) {
                                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                                }
                            }

                            ValueAnimator colorAnimation = ValueAnimator
                                    .ofArgb(getWindow().getNavigationBarColor(), PublicVariable.colorLightDark);
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    getWindow().setNavigationBarColor((Integer) animator.getAnimatedValue());
                                    getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                                }
                            });
                            colorAnimation.start();
                        }
                    }

                    int finalRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());
                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(recoveryAction, (int) actionButton.getX(), (int) actionButton.getY(), finalRadius, functionsClass.DpToInteger(13));
                    circularReveal.setDuration(777);
                    circularReveal.setInterpolator(new AccelerateInterpolator());
                    circularReveal.start();
                    circularReveal.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            recoveryAction.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    functionsClass.openActionMenuOption(fullActionViews, actionButton, fullActionViews.isShown());
                } else {
                    recoveryAction.setVisibility(View.VISIBLE);

                    int finalRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());
                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(recoveryAction, (int) actionButton.getX(), (int) actionButton.getY(), functionsClass.DpToInteger(13), finalRadius);
                    circularReveal.setDuration(1300);
                    circularReveal.setInterpolator(new AccelerateInterpolator());
                    circularReveal.start();
                    circularReveal.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            recoveryAction.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    functionsClass.closeActionMenuOption(fullActionViews, actionButton);
                }
            }
        });
        switchCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.navigateToClass(FoldersHandler.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        switchApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HybridAppsList.class),
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left).toBundle());
            }
        });
        automationAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.doVibrate(50);

                Intent intent = new Intent(getApplicationContext(), AppAutoFeatures.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.up_down, android.R.anim.fade_out).toBundle());
            }
        });
        recoveryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecoveryWidgets.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);
            }
        });
        recoverFloatingCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecoveryFolders.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_hide);
                recoverFloatingCategories.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        recoverFloatingCategories.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
        recoverFloatingApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecoveryShortcuts.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_hide);
                recoverFloatingApps.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        recoverFloatingApps.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

        actionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(WidgetConfigurations.this, actionButton, "transition");
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(WidgetConfigurations.this, SettingGUI.class);
                        startActivity(intent, activityOptionsCompat.toBundle());
                    }
                }, 113);

                return true;
            }
        });
        switchCategories.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!recoverFloatingCategories.isShown()) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_show);
                    recoverFloatingCategories.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            recoverFloatingCategories.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_hide);
                    recoverFloatingCategories.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            recoverFloatingCategories.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

                return true;
            }
        });
        switchApps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!recoverFloatingApps.isShown()) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_show);
                    recoverFloatingApps.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            recoverFloatingApps.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_hide);
                    recoverFloatingApps.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            recoverFloatingApps.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

                return true;
            }
        });

        ImageView floatingLogo = (ImageView) findViewById(R.id.loadLogo);
        LayerDrawable drawFloatingLogo = (LayerDrawable) getDrawable(R.drawable.draw_floating_widgets);
        Drawable backFloatingLogo = drawFloatingLogo.findDrawableByLayerId(R.id.backtemp);
        backFloatingLogo.setTint(PublicVariable.primaryColorOpposite);
        floatingLogo.setImageDrawable(drawFloatingLogo);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("FORCE_RELOAD");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("FORCE_RELOAD")) {
                    try {
                        configuredWidgetsAdapterItems.clear();
                        configuredWidgetsSections.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    LoadConfiguredWidgets loadConfiguredWidgets = new LoadConfiguredWidgets();
                    loadConfiguredWidgets.execute();
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        /*Search Engine*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        /*Search Engine*/
    }

    @Override
    public void onStart() {
        super.onStart();

        reconfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), WidgetsReallocationProcess.class),
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());

                finish();
            }
        });

        addWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.doVibrate(77);

                if (installedWidgetsNestedScrollView.isShown()) {
                    installedNestedIndexScrollView.setVisibility(View.INVISIBLE);

                    if (!configuredWidgetAvailable) {
                        addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener);
                    }

                    ViewCompat.animate(addWidget)
                            .rotation(0.0F)
                            .withLayer()
                            .setDuration(300L)
                            .setInterpolator(new OvershootInterpolator(3.0f))
                            .start();

                    int xPosition = (int) (addWidget.getX() + (addWidget.getWidth() / 2));
                    int yPosition = (int) (addWidget.getY() + (addWidget.getHeight() / 2));

                    int startRadius = 0;
                    int endRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());

                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(installedWidgetsNestedScrollView, xPosition, yPosition, endRadius, startRadius);
                    circularReveal.setDuration(864);
                    circularReveal.start();
                    circularReveal.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            installedWidgetsNestedScrollView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

                    if (functionsClass.appThemeTransparent() == true) {
                        final Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        if (PublicVariable.themeLightDark) {
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                            if (functionsClass.returnAPI() > 25) {
                                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                            }
                        }

                        ValueAnimator valueAnimator = ValueAnimator
                                .ofArgb(getWindow().getNavigationBarColor(), functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180));
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                window.setStatusBarColor((Integer) animator.getAnimatedValue());
                                window.setNavigationBarColor((Integer) animator.getAnimatedValue());
                            }
                        });
                        valueAnimator.start();
                    } else {
                        final Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        if (PublicVariable.themeLightDark) {
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                            if (functionsClass.returnAPI() > 25) {
                                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                            }
                        }

                        ValueAnimator colorAnimation = ValueAnimator
                                .ofArgb(getWindow().getNavigationBarColor(), PublicVariable.colorLightDark);
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                getWindow().setNavigationBarColor((Integer) animator.getAnimatedValue());
                                getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                            }
                        });
                        colorAnimation.start();
                    }
                } else {
                    if (PublicVariable.actionCenter) {
                        recoveryAction.setVisibility(View.VISIBLE);

                        int finalRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());
                        Animator circularReveal = ViewAnimationUtils.createCircularReveal(recoveryAction, (int) actionButton.getX(), (int) actionButton.getY(), functionsClass.DpToInteger(13), finalRadius);
                        circularReveal.setDuration(1300);
                        circularReveal.setInterpolator(new AccelerateInterpolator());
                        circularReveal.start();
                        circularReveal.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                recoveryAction.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        functionsClass.closeActionMenuOption(fullActionViews, actionButton);
                    }

                    LoadInstalledWidgets loadInstalledWidgets = new LoadInstalledWidgets();
                    loadInstalledWidgets.execute();
                }
            }
        });

        addWidget.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                int appWidgetId = appWidgetHost.allocateAppWidgetId();
                Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
                pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                ArrayList<AppWidgetProviderInfo> appWidgetProviderInfos = new ArrayList<AppWidgetProviderInfo>();
                pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, appWidgetProviderInfos);
                ArrayList<Bundle> bundleArrayList = new ArrayList<Bundle>();
                pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, bundleArrayList);
                startActivityForResult(pickIntent, InstalledWidgetsAdapter.SYSTEM_WIDGET_PICKER);

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default);
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(WidgetConfigurations.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(getPackageName())) {
                                        functionsClass.notificationCreator(
                                                getString(R.string.updateAvailable),
                                                firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogSummaryConfigKey()),
                                                (int) firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey())
                                        );
                                    } else {

                                    }
                                }
                            });
                        } else {

                        }
                    }
                });

        if (functionsClass.readPreference(".Password", "Pin", "0").equals("0") && functionsClass.securityServicesSubscribed()) {
            startActivity(new Intent(getApplicationContext(), HandlePinPassword.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
        } else {
            if (!alreadyAuthenticated) {
                if (functionsClass.securityServicesSubscribed()) {
                    FunctionsClassSecurity.AuthOpenAppValues.setAuthComponentName(getString(R.string.securityServices));
                    FunctionsClassSecurity.AuthOpenAppValues.setAuthSecondComponentName(getPackageName());
                    FunctionsClassSecurity.AuthOpenAppValues.setAuthWidgetConfigurations(true);

                    functionsClassSecurity.openAuthInvocation();
                }
            }
        }

        /*Search Engine*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        /*Search Engine*/
    }

    @Override
    public void onPause() {
        super.onPause();
        if (PublicVariable.actionCenter == true) {
            functionsClass.closeActionMenuOption(fullActionViews, actionButton);
        }
    }

    @Override
    public void onBackPressed() {
        if (installedWidgetsNestedScrollView.isShown()) {
            functionsClass.doVibrate(77);
            installedNestedIndexScrollView.setVisibility(View.INVISIBLE);

            if (!configuredWidgetAvailable) {
                addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener);
            }

            ViewCompat.animate(addWidget)
                    .rotation(0.0F)
                    .withLayer()
                    .setDuration(300L)
                    .setInterpolator(new OvershootInterpolator(3.0f))
                    .start();

            int xPosition = (int) (addWidget.getX() + (addWidget.getWidth() / 2));
            int yPosition = (int) (addWidget.getY() + (addWidget.getHeight() / 2));

            int startRadius = 0;
            int endRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());

            Animator circularReveal = ViewAnimationUtils.createCircularReveal(installedWidgetsNestedScrollView, xPosition, yPosition, endRadius, startRadius);
            circularReveal.setDuration(864);
            circularReveal.start();
            circularReveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    installedWidgetsNestedScrollView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            if (functionsClass.appThemeTransparent() == true) {
                final Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if (PublicVariable.themeLightDark) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    if (functionsClass.returnAPI() > 25) {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                    }
                }

                ValueAnimator valueAnimator = ValueAnimator
                        .ofArgb(getWindow().getNavigationBarColor(), functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180));
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        window.setStatusBarColor((Integer) animator.getAnimatedValue());
                        window.setNavigationBarColor((Integer) animator.getAnimatedValue());
                    }
                });
                valueAnimator.start();
            } else {
                final Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if (PublicVariable.themeLightDark) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    if (functionsClass.returnAPI() > 25) {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                    }
                }

                ValueAnimator colorAnimation = ValueAnimator
                        .ofArgb(getWindow().getNavigationBarColor(), PublicVariable.colorLightDark);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        getWindow().setNavigationBarColor((Integer) animator.getAnimatedValue());
                        getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
            }
        } else {
            try {
                functionsClass.overrideBackPressToMain(WidgetConfigurations.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilterSwitch.SWIPE_RIGHT: {
                try {
                    functionsClass.navigateToClass(FoldersHandler.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case SimpleGestureFilterSwitch.SWIPE_LEFT: {
                try {
                    functionsClass.navigateToClass(HybridAppsList.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.simpleGestureFilterSwitch.onTouchEvent(motionEvent);

        return super.dispatchTouchEvent(motionEvent);
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
                                    System.currentTimeMillis(),
                                    appWidgetId,
                                    InstalledWidgetsAdapter.pickedWidgetPackageName,
                                    InstalledWidgetsAdapter.pickedWidgetClassNameProvider,
                                    InstalledWidgetsAdapter.pickedWidgetConfigClassName,
                                    functionsClass.appName(InstalledWidgetsAdapter.pickedWidgetPackageName),
                                    InstalledWidgetsAdapter.pickedWidgetLabel,
                                    false
                            );

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
                case InstalledWidgetsAdapter.SYSTEM_WIDGET_PICKER: {

                    Bundle extras = data.getExtras();
                    int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                    AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
                    if (appWidgetInfo.configure != null) {

                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
                        intent.setComponent(appWidgetInfo.configure);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        startActivityForResult(intent, InstalledWidgetsAdapter.SYSTEM_WIDGET_PICKER_CONFIGURATION);

                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                WidgetDataModel widgetDataModel = new WidgetDataModel(
                                        System.currentTimeMillis(),
                                        appWidgetId,
                                        appWidgetInfo.provider.getPackageName(),
                                        InstalledWidgetsAdapter.pickedWidgetClassNameProvider,
                                        InstalledWidgetsAdapter.pickedWidgetConfigClassName,
                                        functionsClass.appName(appWidgetInfo.provider.getPackageName()),
                                        appWidgetInfo.loadLabel(getPackageManager()),
                                        false
                                );

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
                    }

                    break;
                }
                case InstalledWidgetsAdapter.SYSTEM_WIDGET_PICKER_CONFIGURATION: {

                    Bundle extras = data.getExtras();
                    int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                    AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            WidgetDataModel widgetDataModel = new WidgetDataModel(
                                    System.currentTimeMillis(),
                                    appWidgetId,
                                    appWidgetInfo.provider.getPackageName(),
                                    InstalledWidgetsAdapter.pickedWidgetClassNameProvider,
                                    InstalledWidgetsAdapter.pickedWidgetConfigClassName,
                                    functionsClass.appName(appWidgetInfo.provider.getPackageName()),
                                    appWidgetInfo.loadLabel(getPackageManager()),
                                    false
                            );

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

    public class LoadConfiguredWidgets extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                configuredWidgetsAdapterItems.clear();
                configuredWidgetsSections.clear();
                configuredWidgetsLoadView.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
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
                gx.setTextColor(getColor(R.color.dark));
            } else if (!PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeColor, android.graphics.PorterDuff.Mode.MULTIPLY);
                gx.setTextColor(getColor(R.color.light));
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            configuredWidgetsAdapterItems.clear();
            configuredWidgetsSections.clear();

            configuredWidgetAvailable = false;
            try {
                if (functionsClass.loadCustomIcons()) {
                    loadCustomIcons.load();
                    FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                }

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
                if (widgetDataModels.size() > 0) {
                    configuredWidgetAvailable = true;
                } else {
                    return false;
                }

                String oldAppName = "";
                int widgetIndex = 0;
                for (WidgetDataModel widgetDataModel : widgetDataModels) {
                    try {
                        int appWidgetId = widgetDataModel.getWidgetId();
                        String packageName = widgetDataModel.getPackageName();
                        String className = widgetDataModel.getClassNameProvider();
                        String configClassName = widgetDataModel.getConfigClassName();

                        FunctionsClassDebug.Companion.PrintDebug("*** " + appWidgetId + " *** PackageName: " + packageName + " - ClassName: " + className + " - Configure: " + configClassName + " ***");

                        if (functionsClass.appIsInstalled(packageName)) {
                            AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
                            String newAppName = functionsClass.appName(packageName);
                            Drawable appIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.shapedAppIcon(packageName)) : functionsClass.shapedAppIcon(packageName);

                            if (widgetIndex == 0) {
                                configuredWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, appIcon));
                                indexListConfigured.add(newAppName.substring(0, 1).toUpperCase());
                            } else {
                                if (!oldAppName.equals(newAppName)) {
                                    configuredWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, appIcon));
                                    indexListConfigured.add(newAppName.substring(0, 1).toUpperCase());
                                }
                            }

                            oldAppName = functionsClass.appName(packageName);

                            indexListConfigured.add(newAppName.substring(0, 1).toUpperCase());
                            configuredWidgetsAdapterItems.add(new AdapterItems(
                                    newAppName,
                                    packageName,
                                    className,
                                    configClassName,
                                    widgetDataModel.getWidgetLabel(),
                                    appIcon,
                                    appWidgetProviderInfo,
                                    appWidgetId,
                                    widgetDataModel.getRecovery(),
                                    SearchEngineAdapter.SearchResultType.SearchWidgets
                            ));

                            widgetIndex++;
                        } else {
                            widgetDataInterface.initDataAccessObject().deleteByWidgetClassNameProviderWidget(packageName, className);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (configuredWidgetsAdapterItems.size() > 0) {
                    configuredWidgetAvailable = true;
                } else {
                    return false;
                }

                configuredWidgetsRecyclerViewAdapter = new ConfiguredWidgetsAdapter(WidgetConfigurations.this, getApplicationContext(), configuredWidgetsAdapterItems, appWidgetManager, appWidgetHost);

                widgetDataInterface.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }

            return configuredWidgetAvailable;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                reconfigure.setVisibility(View.VISIBLE);

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
                        configuredWidgetsNestedScrollView.scrollTo(0, 0);

                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                        loadingSplash.setVisibility(View.INVISIBLE);
                        loadingSplash.startAnimation(animation);
                    }
                }, 333);
            } else {
                reconfigure.setVisibility(View.INVISIBLE);

                installedWidgetsLoaded = false;
                addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener);
                loadingSplash.setVisibility(View.VISIBLE);
                try {
                    configuredWidgetsRecyclerViewAdapter.notifyDataSetChanged();
                    configuredWidgetsSectionedGridRecyclerViewAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            LoadApplicationsIndexConfigured loadApplicationsIndexConfigured = new LoadApplicationsIndexConfigured();
            loadApplicationsIndexConfigured.execute();

            LoadSearchEngineData loadSearchEngineData = new LoadSearchEngineData();
            loadSearchEngineData.execute();
        }
    }

    public class LoadInstalledWidgets extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            installedWidgetsNestedScrollView.setBackgroundColor(PublicVariable.themeLightDark ? getColor(R.color.transparent_light_high_twice) : getColor(R.color.transparent_dark_high_twice));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            installedWidgetsAdapterItems.clear();
            installedWidgetsSections.clear();

            try {
                widgetProviderInfoList = appWidgetManager.getInstalledProviders();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    widgetProviderInfoList.sort(new Comparator<AppWidgetProviderInfo>() {
                        @Override
                        public int compare(AppWidgetProviderInfo appWidgetProviderInfoLeft, AppWidgetProviderInfo appWidgetProviderInfoRight) {
                            return functionsClass.appName(appWidgetProviderInfoLeft.provider.getPackageName())
                                    .compareTo(functionsClass.appName(appWidgetProviderInfoRight.provider.getPackageName()));
                        }
                    });
                }


                if (functionsClass.loadCustomIcons()) {
                    loadCustomIcons.load();
                    FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                }

                String oldAppName = "";
                int widgetIndex = 0;
                for (AppWidgetProviderInfo appWidgetProviderInfo : widgetProviderInfoList) {
                    FunctionsClassDebug.Companion.PrintDebug("*** Provider = " + appWidgetProviderInfo.provider + " | Config = " + appWidgetProviderInfo.configure + " ***");
                    try {
                        String packageName = appWidgetProviderInfo.provider.getPackageName();
                        String className = appWidgetProviderInfo.provider.getClassName();
                        ComponentName componentNameConfiguration = appWidgetProviderInfo.configure;

                        if (componentNameConfiguration != null) {
                            if (getPackageManager().getActivityInfo(componentNameConfiguration, PackageManager.GET_META_DATA).exported) {
                                if (!packageName.isEmpty() && !className.isEmpty()) {
                                    String newAppName = functionsClass.appName(packageName);
                                    Drawable newAppIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.shapedAppIcon(packageName)) : functionsClass.shapedAppIcon(packageName);

                                    if (widgetIndex == 0) {
                                        installedWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, newAppIcon));
                                        indexListInstalled.add(newAppName.substring(0, 1).toUpperCase());
                                    } else {
                                        if (!oldAppName.equals(newAppName)) {
                                            installedWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, newAppIcon));
                                            indexListInstalled.add(newAppName.substring(0, 1).toUpperCase());
                                        }
                                    }

                                    oldAppName = functionsClass.appName(appWidgetProviderInfo.provider.getPackageName());

                                    Drawable widgetPreviewDrawable = appWidgetProviderInfo.loadPreviewImage(getApplicationContext(), DisplayMetrics.DENSITY_HIGH);
                                    String widgetLabel = appWidgetProviderInfo.loadLabel(getPackageManager());

                                    indexListInstalled.add(newAppName.substring(0, 1).toUpperCase());
                                    installedWidgetsAdapterItems.add(new AdapterItems(functionsClass.appName(appWidgetProviderInfo.provider.getPackageName()),
                                            appWidgetProviderInfo.provider.getPackageName(),
                                            appWidgetProviderInfo.provider.getClassName(),
                                            componentNameConfiguration.getClassName(),
                                            (widgetLabel != null) ? widgetLabel : newAppName,
                                            newAppIcon,
                                            (widgetPreviewDrawable != null) ? widgetPreviewDrawable : appWidgetProviderInfo.loadIcon(getApplicationContext(), DisplayMetrics.DENSITY_HIGH),
                                            appWidgetProviderInfo
                                    ));
                                }
                            }
                        } else {
                            if (!packageName.isEmpty() && !className.isEmpty()) {
                                String newAppName = functionsClass.appName(packageName);
                                Drawable newAppIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.shapedAppIcon(packageName)) : functionsClass.shapedAppIcon(packageName);

                                if (widgetIndex == 0) {
                                    installedWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, newAppIcon));
                                    indexListInstalled.add(newAppName.substring(0, 1).toUpperCase());
                                } else {
                                    if (!oldAppName.equals(newAppName)) {
                                        installedWidgetsSections.add(new WidgetSectionedGridRecyclerViewAdapter.Section(widgetIndex, newAppName, newAppIcon));
                                        indexListInstalled.add(newAppName.substring(0, 1).toUpperCase());
                                    }
                                }

                                oldAppName = functionsClass.appName(appWidgetProviderInfo.provider.getPackageName());

                                Drawable widgetPreviewDrawable = appWidgetProviderInfo.loadPreviewImage(getApplicationContext(), DisplayMetrics.DENSITY_HIGH);
                                String widgetLabel = appWidgetProviderInfo.loadLabel(getPackageManager());

                                indexListInstalled.add(newAppName.substring(0, 1).toUpperCase());
                                installedWidgetsAdapterItems.add(new AdapterItems(functionsClass.appName(appWidgetProviderInfo.provider.getPackageName()),
                                        appWidgetProviderInfo.provider.getPackageName(),
                                        appWidgetProviderInfo.provider.getClassName(),
                                        null,
                                        (widgetLabel != null) ? widgetLabel : newAppName,
                                        newAppIcon,
                                        (widgetPreviewDrawable != null) ? widgetPreviewDrawable : appWidgetProviderInfo.loadIcon(getApplicationContext(), DisplayMetrics.DENSITY_HIGH),
                                        appWidgetProviderInfo
                                ));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    widgetIndex++;
                }

                installedWidgetsRecyclerViewAdapter = new InstalledWidgetsAdapter(WidgetConfigurations.this, getApplicationContext(), installedWidgetsAdapterItems, appWidgetHost);
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return configuredWidgetAvailable;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            installedWidgetsLoaded = true;

            ViewPropertyAnimator viewPropertyAnimator = addWidget.animate()
                    .rotation(135.0F)
                    .withLayer()
                    .setDuration(500L)
                    .setInterpolator(new OvershootInterpolator(13.0f));
            viewPropertyAnimator.start();

            int xPosition = (int) (addWidget.getX() + (addWidget.getWidth() / 2));
            int yPosition = (int) (addWidget.getY() + (addWidget.getHeight() / 2));

            int startRadius = 0;
            int endRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());

            installedWidgetsNestedScrollView.setBackgroundColor(PublicVariable.themeLightDark ? getColor(R.color.transparent_light) : getColor(R.color.dark_transparent));
            installedWidgetsNestedScrollView.setVisibility(View.VISIBLE);
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(installedWidgetsNestedScrollView, xPosition, yPosition, startRadius, endRadius);
            circularReveal.setDuration(864);
            circularReveal.start();
            circularReveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    installedWidgetsNestedScrollView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            final Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            if (functionsClass.appThemeTransparent()) {
                ValueAnimator colorAnimation = ValueAnimator
                        .ofArgb(getWindow().getNavigationBarColor(), PublicVariable.themeLightDark ? getColor(R.color.fifty_light_twice) : getColor(R.color.transparent_dark_high_twice));
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        window.setStatusBarColor((Integer) animator.getAnimatedValue());
                        window.setNavigationBarColor((Integer) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
            } else {
                if (PublicVariable.themeLightDark) {
                    installedWidgetsNestedScrollView.setBackground(new ColorDrawable(getColor(R.color.transparent_light)));
                    if (PublicVariable.themeLightDark) {
                        if (functionsClass.returnAPI() > 25) {
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                        }
                    }

                    ValueAnimator colorAnimation = ValueAnimator
                            .ofArgb(getWindow().getNavigationBarColor(), functionsClass.mixColors(getColor(R.color.light), getWindow().getNavigationBarColor(), 0.70f));
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            getWindow().setNavigationBarColor((Integer) animator.getAnimatedValue());
                            getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                        }
                    });
                    colorAnimation.start();
                } else if (!PublicVariable.themeLightDark) {
                    installedWidgetsNestedScrollView.setBackground(new ColorDrawable(getColor(R.color.dark_transparent)));

                    ValueAnimator colorAnimation = ValueAnimator
                            .ofArgb(getWindow().getNavigationBarColor(), functionsClass.mixColors(getColor(R.color.dark), getWindow().getNavigationBarColor(), 0.70f));
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            getWindow().setNavigationBarColor((Integer) animator.getAnimatedValue());
                            getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                        }
                    });
                    colorAnimation.start();
                }
            }

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

            if (!getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists() || !result) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                        loadingSplash.setVisibility(View.INVISIBLE);
                        loadingSplash.startAnimation(animation);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                ((LinearLayout) findViewById(R.id.switchFloating)).setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }, 200);
            }

            LoadApplicationsIndexInstalled loadApplicationsIndexInstalled = new LoadApplicationsIndexInstalled();
            loadApplicationsIndexInstalled.execute();
        }
    }

    private class LoadApplicationsIndexConfigured extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indexView.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            int indexCount = indexListConfigured.size();
            for (int navItem = 0; navItem < indexCount; navItem++) {
                try {
                    String indexText = indexListConfigured.get(navItem);
                    if (mapIndexFirstItem.get(indexText) == null/*avoid duplication*/) {
                        mapIndexFirstItem.put(indexText, navItem);
                    }

                    mapIndexLastItem.put(indexText, navItem);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            TextView textView = (TextView) getLayoutInflater()
                    .inflate(R.layout.side_index_item, null);
            List<String> indexListFinal = new ArrayList<String>(mapIndexFirstItem.keySet());
            for (String index : indexListFinal) {
                textView = (TextView) getLayoutInflater()
                        .inflate(R.layout.side_index_item, null);
                textView.setText(index.toUpperCase());
                textView.setTextColor(PublicVariable.colorLightDarkOpposite);
                indexView.addView(textView);
            }

            TextView finalTextView = textView;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int upperRange = (int) (indexView.getY() - finalTextView.getHeight());
                    for (int i = 0; i < indexView.getChildCount(); i++) {
                        String indexText = ((TextView) indexView.getChildAt(i)).getText().toString();
                        int indexRange = (int) (indexView.getChildAt(i).getY() + indexView.getY() + finalTextView.getHeight());
                        for (int jRange = upperRange; jRange <= (indexRange); jRange++) {
                            mapRangeIndex.put(jRange, indexText);
                        }

                        upperRange = indexRange;
                    }

                    setupFastScrollingIndexingConfigured();
                }
            }, 700);
        }
    }

    private class LoadApplicationsIndexInstalled extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indexViewInstalled.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            int indexCount = indexListInstalled.size();
            for (int navItem = 0; navItem < indexCount; navItem++) {
                try {
                    String indexText = indexListInstalled.get(navItem);
                    if (mapIndexFirstItemInstalled.get(indexText) == null/*avoid duplication*/) {
                        mapIndexFirstItemInstalled.put(indexText, navItem);
                    }

                    mapIndexLastItemInstalled.put(indexText, navItem);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            TextView textView = (TextView) getLayoutInflater()
                    .inflate(R.layout.side_index_item, null);
            List<String> indexListFinal = new ArrayList<String>(mapIndexFirstItemInstalled.keySet());
            for (String index : indexListFinal) {
                textView = (TextView) getLayoutInflater()
                        .inflate(R.layout.side_index_item, null);
                textView.setText(index.toUpperCase());
                textView.setTextColor(PublicVariable.colorLightDarkOpposite);
                indexViewInstalled.addView(textView);
            }

            TextView finalTextView = textView;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int upperRange = (int) (indexViewInstalled.getY() - finalTextView.getHeight());
                    for (int i = 0; i < indexViewInstalled.getChildCount(); i++) {
                        String indexText = ((TextView) indexViewInstalled.getChildAt(i)).getText().toString();
                        int indexRange = (int) (indexViewInstalled.getChildAt(i).getY() + indexViewInstalled.getY() + finalTextView.getHeight());
                        for (int jRange = upperRange; jRange <= (indexRange); jRange++) {
                            mapRangeIndexInstalled.put(jRange, indexText);
                        }

                        upperRange = indexRange;
                    }

                    setupFastScrollingIndexingInstalled();
                }
            }, 700);
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

    Animator.AnimatorListener scaleDownListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!installedWidgetsLoaded) {
                addWidget.animate().scaleXBy(0.23f).scaleYBy(0.23f).setDuration(223).setListener(scaleUpListener);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }
    };

    Animator.AnimatorListener scaleUpListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            addWidget.animate().scaleXBy(-0.23f).scaleYBy(-0.23f).setDuration(323).setListener(scaleDownListener);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public void setupFastScrollingIndexingConfigured() {
        Drawable popupIndexBackground = getDrawable(R.drawable.ic_launcher_balloon).mutate();
        popupIndexBackground.setTint(PublicVariable.primaryColorOpposite);
        popupIndex.setBackground(popupIndexBackground);

        nestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        nestedIndexScrollView.setVisibility(View.VISIBLE);

        float popupIndexOffsetY = PublicVariable.statusBarHeight + PublicVariable.actionBarHeight + (functionsClass.UsageStatsEnabled() ? functionsClass.DpToInteger(7) : functionsClass.DpToInteger(7));
        nestedIndexScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (functionsClass.litePreferencesEnabled()) {

                        } else {
                            String indexText = mapRangeIndex.get(((int) motionEvent.getY()));

                            if (indexText != null) {
                                popupIndex.setY(motionEvent.getRawY() - popupIndexOffsetY);
                                popupIndex.setText(indexText);
                                popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                popupIndex.setVisibility(View.VISIBLE);
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (functionsClass.litePreferencesEnabled()) {

                        } else {
                            String indexText = mapRangeIndex.get(((int) motionEvent.getY()));

                            if (indexText != null) {
                                if (!popupIndex.isShown()) {
                                    popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                    popupIndex.setVisibility(View.VISIBLE);
                                }
                                popupIndex.setY(motionEvent.getRawY() - popupIndexOffsetY);
                                popupIndex.setText(indexText);

                                try {
                                    configuredWidgetsNestedScrollView.smoothScrollTo(
                                            0,
                                            ((int) configuredWidgetsLoadView.getChildAt(mapIndexFirstItem.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY())
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (popupIndex.isShown()) {
                                    popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                    popupIndex.setVisibility(View.INVISIBLE);
                                }
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (functionsClass.litePreferencesEnabled()) {
                            try {
                                configuredWidgetsNestedScrollView.smoothScrollTo(
                                        0,
                                        ((int) configuredWidgetsLoadView.getChildAt(mapIndexFirstItem.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY())
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (popupIndex.isShown()) {
                                try {
                                    configuredWidgetsNestedScrollView.smoothScrollTo(
                                            0,
                                            ((int) configuredWidgetsLoadView.getChildAt(mapIndexFirstItem.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY())
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                popupIndex.setVisibility(View.INVISIBLE);
                            }
                        }

                        break;
                    }
                }
                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupFastScrollingIndexingInstalled() {
        Drawable popupIndexBackground = getDrawable(R.drawable.ic_launcher_balloon).mutate();
        popupIndexBackground.setTint(PublicVariable.primaryColorOpposite);
        popupIndex.setBackground(popupIndexBackground);

        installedNestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        installedNestedIndexScrollView.setVisibility(View.VISIBLE);

        float popupIndexOffsetY = PublicVariable.statusBarHeight + PublicVariable.actionBarHeight + (functionsClass.UsageStatsEnabled() ? functionsClass.DpToInteger(7) : functionsClass.DpToInteger(7));
        installedNestedIndexScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (functionsClass.litePreferencesEnabled()) {

                        } else {
                            String indexText = mapRangeIndexInstalled.get(((int) motionEvent.getY()));

                            if (indexText != null) {
                                popupIndex.setY(motionEvent.getRawY() - popupIndexOffsetY);
                                popupIndex.setText(indexText);
                                popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                popupIndex.setVisibility(View.VISIBLE);
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (functionsClass.litePreferencesEnabled()) {

                        } else {
                            String indexText = mapRangeIndexInstalled.get(((int) motionEvent.getY()));

                            if (indexText != null) {
                                if (!popupIndex.isShown()) {
                                    popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                    popupIndex.setVisibility(View.VISIBLE);
                                }
                                popupIndex.setY(motionEvent.getRawY() - popupIndexOffsetY);
                                popupIndex.setText(indexText);

                                try {
                                    installedWidgetsNestedScrollView.smoothScrollTo(
                                            0,
                                            ((int) installedWidgetsLoadView.getChildAt(mapIndexFirstItemInstalled.get(mapRangeIndexInstalled.get(((int) motionEvent.getY())))).getY())
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (popupIndex.isShown()) {
                                    popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                    popupIndex.setVisibility(View.INVISIBLE);
                                }
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (functionsClass.litePreferencesEnabled()) {
                            try {
                                installedWidgetsNestedScrollView.smoothScrollTo(
                                        0,
                                        ((int) installedWidgetsLoadView.getChildAt(mapIndexFirstItemInstalled.get(mapRangeIndexInstalled.get(((int) motionEvent.getY())))).getY())
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (popupIndex.isShown()) {
                                try {
                                    installedWidgetsNestedScrollView.smoothScrollTo(
                                            0,
                                            ((int) installedWidgetsLoadView.getChildAt(mapIndexFirstItemInstalled.get(mapRangeIndexInstalled.get(((int) motionEvent.getY())))).getY())
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                popupIndex.setVisibility(View.INVISIBLE);
                            }
                        }

                        break;
                    }
                }
                return true;
            }
        });
    }

    /*Search Engine*/
    private class LoadSearchEngineData extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            /*
             * Search Engine
             */
            if (SearchEngineAdapter.allSearchResultItems.isEmpty()) {
                searchAdapterItems = new ArrayList<AdapterItems>();

                //Loading Folders
                if (getFileStreamPath(".categoryInfo").exists()) {
                    try {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(getFileStreamPath(".categoryInfo"));
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                            String folderData = "";
                            while ((folderData = bufferedReader.readLine()) != null) {
                                try {
                                    searchAdapterItems.add(new AdapterItems(folderData,
                                            functionsClass.readFileLine(folderData), SearchEngineAdapter.SearchResultType.SearchFolders));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            fileInputStream.close();
                            bufferedReader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.cancel(true);
                    } finally {

                    }
                }

                //Loading Shortcuts
                try {
                    List<ApplicationInfo> applicationInfoList = getApplicationContext().getPackageManager().getInstalledApplications(0);
                    Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(getPackageManager()));

                    for (int appInfo = 0; appInfo < applicationInfoList.size(); appInfo++) {
                        if (getPackageManager().getLaunchIntentForPackage(applicationInfoList.get(appInfo).packageName) != null) {
                            try {


                                String PackageName = applicationInfoList.get(appInfo).packageName;
                                String AppName = functionsClass.appName(PackageName);
                                Drawable AppIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(PackageName, functionsClass.shapedAppIcon(PackageName)) : functionsClass.shapedAppIcon(PackageName);

                                searchAdapterItems.add(new AdapterItems(AppName, PackageName, AppIcon, SearchEngineAdapter.SearchResultType.SearchShortcuts));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Loading Widgets
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WidgetConfigurations.this);
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
                if (widgetDataModels.size() > 0) {
                    for (WidgetDataModel widgetDataModel : widgetDataModels) {
                        try {
                            int appWidgetId = widgetDataModel.getWidgetId();
                            String packageName = widgetDataModel.getPackageName();
                            String className = widgetDataModel.getClassNameProvider();
                            String configClassName = widgetDataModel.getConfigClassName();

                            FunctionsClassDebug.Companion.PrintDebug("*** " + appWidgetId + " *** PackageName: " + packageName + " - ClassName: " + className + " - Configure: " + configClassName + " ***");

                            if (functionsClass.appIsInstalled(packageName)) {
                                AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
                                String newAppName = functionsClass.appName(packageName);
                                Drawable appIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.shapedAppIcon(packageName)) : functionsClass.shapedAppIcon(packageName);


                                searchAdapterItems.add(new AdapterItems(
                                        newAppName,
                                        packageName,
                                        className,
                                        configClassName,
                                        widgetDataModel.getWidgetLabel(),
                                        appIcon,
                                        appWidgetProviderInfo,
                                        appWidgetId,
                                        widgetDataModel.getRecovery(),
                                        SearchEngineAdapter.SearchResultType.SearchWidgets
                                ));

                            } else {
                                widgetDataInterface.initDataAccessObject().deleteByWidgetClassNameProviderWidget(packageName, className);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                }
                searchRecyclerViewAdapter = new SearchEngineAdapter(getApplicationContext(), searchAdapterItems);
            } else {
                searchAdapterItems = SearchEngineAdapter.allSearchResultItems;

                searchRecyclerViewAdapter = new SearchEngineAdapter(getApplicationContext(), searchAdapterItems);
            }
            /*
             * Search Engine
             */
            return (searchAdapterItems.size() > 0);
        }

        @Override
        protected void onPostExecute(Boolean results) {
            super.onPostExecute(results);

            if (results) {
                setupSearchView();
            }
        }
    }

    public void setupSearchView() {
        searchView.setAdapter(searchRecyclerViewAdapter);

        searchView.setDropDownBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        searchView.setVerticalScrollBarEnabled(false);
        searchView.setScrollBarSize(0);

        searchView.setTextColor(PublicVariable.colorLightDarkOpposite);
        searchView.setCompoundDrawableTintList(ColorStateList.valueOf(functionsClass.setColorAlpha(PublicVariable.colorLightDarkOpposite, 175)));

        RippleDrawable layerDrawableSearchIcon = (RippleDrawable) getDrawable(R.drawable.search_icon);
        Drawable backgroundTemporarySearchIcon = layerDrawableSearchIcon.findDrawableByLayerId(R.id.backgroundTemporary);
        Drawable frontTemporarySearchIcon = layerDrawableSearchIcon.findDrawableByLayerId(R.id.frontTemporary);
        Drawable frontDrawableSearchIcon = layerDrawableSearchIcon.findDrawableByLayerId(R.id.frontDrawable);
        backgroundTemporarySearchIcon.setTint(PublicVariable.colorLightDarkOpposite);
        frontTemporarySearchIcon.setTint(PublicVariable.colorLightDark);
        frontDrawableSearchIcon.setTint(PublicVariable.primaryColor);

        layerDrawableSearchIcon.setLayerInset(2,
                functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13));

        searchIcon.setImageDrawable(layerDrawableSearchIcon);
        searchIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        searchIcon.setVisibility(View.VISIBLE);

        textInputSearchView.setHintTextColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
        textInputSearchView.setBoxStrokeColor(PublicVariable.primaryColor);

        GradientDrawable backgroundTemporaryInput = new GradientDrawable();
        try {
            LayerDrawable layerDrawableBackgroundInput = (LayerDrawable) getDrawable(R.drawable.background_search_input);
            backgroundTemporaryInput = (GradientDrawable) layerDrawableBackgroundInput.findDrawableByLayerId(R.id.backgroundTemporary);
            backgroundTemporaryInput.setTint(PublicVariable.colorLightDark);
            textInputSearchView.setBackground(layerDrawableBackgroundInput);
        } catch (Exception e) {
            e.printStackTrace();

            textInputSearchView.setBackground(null);
        }

        GradientDrawable finalBackgroundTemporaryInput = backgroundTemporaryInput;
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.searchEnginePurchased()) {
                            textInputSearchView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                            textInputSearchView.setVisibility(View.VISIBLE);

                            searchIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                            searchIcon.setVisibility(View.INVISIBLE);

                            ValueAnimator valueAnimatorCornerDown = ValueAnimator.ofInt(functionsClass.DpToInteger(51), functionsClass.DpToInteger(7));
                            valueAnimatorCornerDown.setDuration(777);
                            valueAnimatorCornerDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    int animatorValue = (int) animator.getAnimatedValue();

                                    textInputSearchView.setBoxCornerRadii(animatorValue, animatorValue, animatorValue, animatorValue);
                                    finalBackgroundTemporaryInput.setCornerRadius(animatorValue);
                                }
                            });
                            valueAnimatorCornerDown.start();

                            ValueAnimator valueAnimatorScalesUp = ValueAnimator.ofInt(functionsClass.DpToInteger(51), switchApps.getWidth());
                            valueAnimatorScalesUp.setDuration(777);
                            valueAnimatorScalesUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    int animatorValue = (int) animator.getAnimatedValue();

                                    textInputSearchView.getLayoutParams().width = animatorValue;
                                    textInputSearchView.requestLayout();
                                }
                            });
                            valueAnimatorScalesUp.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    searchView.requestFocus();

                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            searchFloatIt.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_up_bounce_interpolator));
                                            searchFloatIt.setVisibility(View.VISIBLE);

                                            searchClose.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_up_bounce_interpolator));
                                            searchClose.setVisibility(View.VISIBLE);
                                        }
                                    }, 555);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            valueAnimatorScalesUp.start();

                            searchFloatIt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!searchView.getText().toString().isEmpty() && (SearchEngineAdapter.allSearchResultItems.size() > 0) && (searchView.getText().toString().length() >= 2)) {
                                        for (AdapterItems searchResultItem : SearchEngineAdapter.allSearchResultItems) {
                                            switch (searchResultItem.getSearchResultType()) {
                                                case SearchEngineAdapter.SearchResultType.SearchShortcuts: {
                                                    functionsClass.runUnlimitedShortcutsService(searchResultItem.getPackageName());

                                                    break;
                                                }
                                                case SearchEngineAdapter.SearchResultType.SearchFolders: {
                                                    functionsClass.runUnlimitedFolderService(searchResultItem.getCategory());

                                                    break;
                                                }
                                                case SearchEngineAdapter.SearchResultType.SearchWidgets: {
                                                    functionsClass
                                                            .runUnlimitedWidgetService(searchResultItem.getAppWidgetId(),
                                                                    searchResultItem.getWidgetLabel());

                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            });

                            searchView.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                        if (SearchEngineAdapter.allSearchResultItems.size() == 1 && !searchView.getText().toString().isEmpty() && (searchView.getText().toString().length() >= 2)) {
                                            switch (SearchEngineAdapter.allSearchResultItems.get(0).getSearchResultType()) {
                                                case SearchEngineAdapter.SearchResultType.SearchShortcuts: {
                                                    functionsClass.runUnlimitedShortcutsService(SearchEngineAdapter.allSearchResultItems.get(0).getPackageName());

                                                    break;
                                                }
                                                case SearchEngineAdapter.SearchResultType.SearchFolders: {
                                                    functionsClass.runUnlimitedFolderService(SearchEngineAdapter.allSearchResultItems.get(0).getCategory());

                                                    break;
                                                }
                                                case SearchEngineAdapter.SearchResultType.SearchWidgets: {
                                                    functionsClass
                                                            .runUnlimitedWidgetService(SearchEngineAdapter.allSearchResultItems.get(0).getAppWidgetId(),
                                                                    SearchEngineAdapter.allSearchResultItems.get(0).getWidgetLabel());

                                                    break;
                                                }
                                            }

                                            searchView.setText("");

                                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                                            ValueAnimator valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51));
                                            valueAnimatorCornerUp.setDuration(777);
                                            valueAnimatorCornerUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                @Override
                                                public void onAnimationUpdate(ValueAnimator animator) {
                                                    int animatorValue = (int) animator.getAnimatedValue();

                                                    textInputSearchView.setBoxCornerRadii(animatorValue, animatorValue, animatorValue, animatorValue);
                                                    finalBackgroundTemporaryInput.setCornerRadius(animatorValue);
                                                }
                                            });
                                            valueAnimatorCornerUp.start();

                                            ValueAnimator valueAnimatorScales = ValueAnimator.ofInt(textInputSearchView.getWidth(), functionsClass.DpToInteger(51));
                                            valueAnimatorScales.setDuration(777);
                                            valueAnimatorScales.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                @Override
                                                public void onAnimationUpdate(ValueAnimator animator) {
                                                    int animatorValue = (int) animator.getAnimatedValue();

                                                    textInputSearchView.getLayoutParams().width = animatorValue;
                                                    textInputSearchView.requestLayout();
                                                }
                                            });
                                            valueAnimatorScales.addListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    textInputSearchView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                                    textInputSearchView.setVisibility(View.INVISIBLE);

                                                    searchIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                                    searchIcon.setVisibility(View.VISIBLE);

                                                    searchFloatIt.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down_zero));
                                                    searchFloatIt.setVisibility(View.INVISIBLE);

                                                    searchClose.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down_zero));
                                                    searchClose.setVisibility(View.INVISIBLE);
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            });
                                            valueAnimatorScales.start();
                                        } else {
                                            if (SearchEngineAdapter.allSearchResultItems.size() > 0 && (searchView.getText().toString().length() >= 2)) {
                                                searchView.showDropDown();
                                            }
                                        }
                                    }
                                    return false;
                                }
                            });

                            searchClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    searchView.setText("");

                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                                    ValueAnimator valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51));
                                    valueAnimatorCornerUp.setDuration(777);
                                    valueAnimatorCornerUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animator) {
                                            int animatorValue = (int) animator.getAnimatedValue();

                                            textInputSearchView.setBoxCornerRadii(animatorValue, animatorValue, animatorValue, animatorValue);
                                            finalBackgroundTemporaryInput.setCornerRadius(animatorValue);
                                        }
                                    });
                                    valueAnimatorCornerUp.start();

                                    ValueAnimator valueAnimatorScales = ValueAnimator.ofInt(textInputSearchView.getWidth(), functionsClass.DpToInteger(51));
                                    valueAnimatorScales.setDuration(777);
                                    valueAnimatorScales.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animator) {
                                            int animatorValue = (int) animator.getAnimatedValue();

                                            textInputSearchView.getLayoutParams().width = animatorValue;
                                            textInputSearchView.requestLayout();
                                        }
                                    });
                                    valueAnimatorScales.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            textInputSearchView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                            textInputSearchView.setVisibility(View.INVISIBLE);

                                            searchIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                            searchIcon.setVisibility(View.VISIBLE);

                                            searchFloatIt.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down_zero));
                                            searchFloatIt.setVisibility(View.INVISIBLE);

                                            searchClose.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down_zero));
                                            searchClose.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                    valueAnimatorScales.start();
                                }
                            });
                        } else {
                            InAppBilling.ItemIAB = BillingManager.iapSearchEngine;

                            startActivity(new Intent(getApplicationContext(), InAppBilling.class),
                                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                        }
                    }
                }, 99);
            }
        });
    }
    /*Search Engine*/
}