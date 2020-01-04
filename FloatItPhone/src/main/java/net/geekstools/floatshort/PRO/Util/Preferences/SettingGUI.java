/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/4/20 12:26 AM
 * Last modified 1/4/20 12:26 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.Preferences;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.Html;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.GeneralAdapters.AdapterItems;
import net.geekstools.floatshort.PRO.Util.GeneralAdapters.CustomIconsThemeAdapter;
import net.geekstools.floatshort.PRO.Util.GeneralAdapters.RecycleViewSmoothLayoutList;
import net.geekstools.floatshort.PRO.Util.IAP.InAppBilling;
import net.geekstools.floatshort.PRO.Util.InteractionObserver.InteractionObserver;
import net.geekstools.floatshort.PRO.Util.SearchEngine.SearchEngineAdapter;
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations;

import java.util.ArrayList;

public class SettingGUI extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    FunctionsClass functionsClass;

    SharedPreferences sharedPreferences;

    ListPreference themeColor, stick;
    SwitchPreference stable, cache, themeTrans, smart, blur, observe, notification, floatingSplash, freeForm;

    Preference pinPassword, shapes, autotrans, sizes, delayPressHold, flingSensitivity, boot, lite, support, whatsnew, adApp;

    Runnable runnablePressHold = null;
    Handler handlerPressHold = new Handler();
    boolean touchingDelay = false, FromWidgetsConfigurations = false, currentTheme = false;

    View rootLayout;

    FirebaseRemoteConfig firebaseRemoteConfig;

    String betaChangeLog = "net.geekstools.floatshort.PRO", betaVersionCode = "0";

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        if (PublicVariable.themeLightDark) {
            theme.applyStyle(R.style.GeeksEmpire_Preference_Light, true);
        } else if (!PublicVariable.themeLightDark) {
            theme.applyStyle(R.style.GeeksEmpire_Preference_Dark, true);
        }
        return theme;
    }

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        addPreferencesFromResource(R.xml.preferences_screen);
        currentTheme = PublicVariable.themeLightDark;
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        functionsClass = new FunctionsClass(getApplicationContext(), SettingGUI.this);

        this.getListView().setCacheColorHint(Color.TRANSPARENT);
        this.getListView().setVerticalFadingEdgeEnabled(true);
        this.getListView().setFadingEdgeLength(functionsClass.DpToInteger(13));
        this.getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
        this.getListView().setDividerHeight((int) functionsClass.DpToPixel(3));
        this.getListView().setScrollBarSize(0);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        FromWidgetsConfigurations = getIntent().hasExtra("FromWidgetsConfigurations") ? getIntent().getBooleanExtra("FromWidgetsConfigurations", false) : false;

//        if (functionsClass.appThemeTransparent() == true) {
//            functionsClass.setThemeColorPreferences(this.getListView(), true, getString(R.string.settingTitle), functionsClass.appVersionName(getPackageName()));
//        } else {
//            functionsClass.setThemeColorPreferences(this.getListView(), false, getString(R.string.settingTitle), functionsClass.appVersionName(getPackageName()));
//        }

        rootLayout = this.getWindow().getDecorView();
        rootLayout.setVisibility(View.INVISIBLE);
        ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int finalRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());
                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, (functionsClass.displayX() / 2), (functionsClass.displayY() / 2), functionsClass.DpToInteger(55), finalRadius);
                    circularReveal.setDuration(1300);
                    circularReveal.setInterpolator(new AccelerateInterpolator());

                    rootLayout.setVisibility(View.VISIBLE);
                    circularReveal.start();
                    rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    circularReveal.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rootLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                }
            });
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }

        stable = (SwitchPreference) findPreference("stable");
        cache = (SwitchPreference) findPreference("cache");
        themeTrans = (SwitchPreference) findPreference("transparent");
        blur = (SwitchPreference) findPreference("blur");
        smart = (SwitchPreference) findPreference("smart");
        observe = (SwitchPreference) findPreference("observe");
        notification = (SwitchPreference) findPreference("notification");
        floatingSplash = (SwitchPreference) findPreference("floatingSplash");
        freeForm = (SwitchPreference) findPreference("freeForm");

        pinPassword = (Preference) findPreference("pinPassword");
        shapes = (Preference) findPreference("shapes");
        autotrans = (Preference) findPreference("hide");
        sizes = (Preference) findPreference("sizes");
        flingSensitivity = (Preference) findPreference("flingSensitivity");
        delayPressHold = (Preference) findPreference("delayPressHold");
        lite = (Preference) findPreference("lite");
        adApp = (Preference) findPreference("app");
        whatsnew = (Preference) findPreference("whatsnew");

        String sticky = sharedPreferences.getString("stick", "1");
        stick = (ListPreference) findPreference("stick");
        if (sticky.equals("1")) {
            stick.setSummary(getString(R.string.leftEdge));
        } else if (sticky.equals("2")) {
            stick.setSummary(getString(R.string.rightEdge));
        }

        String b = sharedPreferences.getString("boot", "1");
        boot = (Preference) findPreference("boot");
        if (b.equals("0")) {
            boot.setSummary(getString(R.string.boot_none));
        } else if (b.equals("1")) {
            boot.setSummary(getString(R.string.shortcuts));
        } else if (b.equals("2")) {
            boot.setSummary(getString(R.string.floatingCategory));
        } else if (b.equals("3")) {
            boot.setSummary(getString(R.string.boot_warning));
        }

        functionsClass.checkLightDarkTheme();
        String appTheme = sharedPreferences.getString("themeColor", "2");
        themeColor = (ListPreference) findPreference("themeColor");
        if (appTheme.equals("1")) {
            themeColor.setSummary(getString(R.string.light));
            PublicVariable.themeLightDark = true;
        } else if (appTheme.equals("2")) {
            themeColor.setSummary(getString(R.string.dark));
            PublicVariable.themeLightDark = false;
        } else if (appTheme.equals("3")) {
            functionsClass.checkLightDarkTheme();
            themeColor.setSummary(getString(R.string.dynamic));
        }

        delayPressHold.setSummary(functionsClass.readDefaultPreference("delayPressHold", 333) + " " + getString(R.string.millis));

        cache.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (PublicVariable.Stable) {
                    stopService(new Intent(getApplicationContext(), BindServices.class));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startService(new Intent(getApplicationContext(), BindServices.class));
                        }
                    }, 333);
                } else {
                    if (sharedPreferences.getBoolean("cache", true) == true) {
                        startService(new Intent(getApplicationContext(), BindServices.class));
                    } else if (sharedPreferences.getBoolean("cache", true) == false) {
                        if (PublicVariable.floatingCounter == 0) {
                            stopService(new Intent(getApplicationContext(), BindServices.class));
                        }

                        functionsClass.saveDefaultPreference("LitePreferences", false);
                    }
                }
                return false;
            }
        });
        stable.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (sharedPreferences.getBoolean("stable", true) == true) {
                    PublicVariable.Stable = true;
                    startService(new Intent(getApplicationContext(), BindServices.class));
                } else if (sharedPreferences.getBoolean("stable", true) == false) {
                    PublicVariable.Stable = false;
                    if (PublicVariable.floatingCounter == 0) {
                        stopService(new Intent(getApplicationContext(), BindServices.class));
                    }

                    functionsClass.saveDefaultPreference("LitePreferences", false);
                }
                return false;
            }
        });
        themeTrans.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                if (functionsClass.appThemeTransparent() == true) {
//                    functionsClass.setThemeColorPreferences(SettingGUI.this.getListView(), true, getString(R.string.settingTitle), functionsClass.appVersionName(getPackageName()));
//                } else {
//                    functionsClass.setThemeColorPreferences(SettingGUI.this.getListView(), false, getString(R.string.settingTitle), functionsClass.appVersionName(getPackageName()));
//                }

                functionsClass.saveDefaultPreference("LitePreferences", false);
                return false;
            }
        });
        blur.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                if (functionsClass.appThemeTransparent() == true) {
//                    functionsClass.setThemeColorPreferences(SettingGUI.this.getListView(), true, getString(R.string.settingTitle), functionsClass.appVersionName(getPackageName()));
//                } else {
//                    functionsClass.setThemeColorPreferences(SettingGUI.this.getListView(), false, getString(R.string.settingTitle), functionsClass.appVersionName(getPackageName()));
//                }

                functionsClass.saveDefaultPreference("LitePreferences", false);
                return false;
            }
        });

        support = (Preference) findPreference("support");
        support.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                functionsClass.ContactSupport(SettingGUI.this);
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        LayerDrawable drawSecurity = (LayerDrawable) getDrawable(R.drawable.draw_security_preferences);
        Drawable backSecurity = drawSecurity.findDrawableByLayerId(R.id.backtemp);
        backSecurity.setTint(PublicVariable.primaryColorOpposite);
        pinPassword.setIcon(drawSecurity);

        LayerDrawable drawSmart = (LayerDrawable) getDrawable(R.drawable.draw_smart);
        Drawable backSmart = drawSmart.findDrawableByLayerId(R.id.backtemp);

        LayerDrawable drawPref = (LayerDrawable) getDrawable(R.drawable.draw_pref);
        Drawable backPref = drawPref.findDrawableByLayerId(R.id.backtemp);

        LayerDrawable drawPrefAutoTrans = (LayerDrawable) getDrawable(R.drawable.draw_pref).mutate();
        Drawable backPrefAutoTrans = drawPrefAutoTrans.findDrawableByLayerId(R.id.backtemp).mutate();

        LayerDrawable drawPrefLite = (LayerDrawable) getDrawable(R.drawable.draw_pref).mutate();
        Drawable backPrefLite = drawPrefLite.findDrawableByLayerId(R.id.backtemp).mutate();
        Drawable drawablePrefLite = drawPrefLite.findDrawableByLayerId(R.id.wPref);
        backPrefLite.setTint(PublicVariable.themeLightDark ? getColor(R.color.dark) : getColor(R.color.light));
        drawablePrefLite.setTint(PublicVariable.themeLightDark ? getColor(R.color.light) : getColor(R.color.dark));
        lite.setIcon(drawPrefLite);

        LayerDrawable drawFloatIt = (LayerDrawable) getDrawable(R.drawable.draw_floatit);
        Drawable backFloatIt = drawFloatIt.findDrawableByLayerId(R.id.backtemp);

        LayerDrawable drawSupport = (LayerDrawable) getDrawable(R.drawable.draw_support);
        Drawable backSupport = drawSupport.findDrawableByLayerId(R.id.backtemp);

        backSmart.setTint(PublicVariable.primaryColor);
        backPref.setTint(PublicVariable.primaryColor);
        backPrefAutoTrans.setTint(PublicVariable.primaryColor);
        backFloatIt.setTint(PublicVariable.primaryColor);
        backPrefAutoTrans.setAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
        backSupport.setTint(PublicVariable.primaryColorOpposite);

        stable.setIcon(drawPref);
        cache.setIcon(drawPref);
        autotrans.setIcon(drawPrefAutoTrans);
        floatingSplash.setIcon(drawPref);
        themeColor.setIcon(drawPref);
        sizes.setIcon(drawPref);
        delayPressHold.setIcon(drawPref);
        flingSensitivity.setIcon(drawPref);
        themeTrans.setIcon(drawPref);
        blur.setIcon(drawPref);
        stick.setIcon(drawPref);
        notification.setIcon(drawPref);

        smart.setIcon(drawSmart);
        observe.setIcon(drawSmart);
        boot.setIcon(drawSmart);
        freeForm.setIcon(drawFloatIt);

        support.setIcon(drawSupport);

        switch (sharedPreferences.getInt("iconShape", 0)) {
            case 1:
                final Drawable drawableTeardrop = getDrawable(R.drawable.droplet_icon);
                drawableTeardrop.setTint(PublicVariable.primaryColor);
                LayerDrawable layerDrawable1 = new LayerDrawable(new Drawable[]{drawableTeardrop, getDrawable(R.drawable.w_pref_gui)});
                shapes.setIcon(layerDrawable1);
                shapes.setSummary(getString(R.string.droplet));
                break;
            case 2:
                final Drawable drawableCircle = getDrawable(R.drawable.circle_icon);
                drawableCircle.setTint(PublicVariable.primaryColor);
                LayerDrawable layerDrawable2 = new LayerDrawable(new Drawable[]{drawableCircle, getDrawable(R.drawable.w_pref_gui)});
                shapes.setIcon(layerDrawable2);
                shapes.setSummary(getString(R.string.circle));
                break;
            case 3:
                final Drawable drawableSquare = getDrawable(R.drawable.square_icon);
                drawableSquare.setTint(PublicVariable.primaryColor);
                LayerDrawable layerDrawable3 = new LayerDrawable(new Drawable[]{drawableSquare, getDrawable(R.drawable.w_pref_gui)});
                shapes.setIcon(layerDrawable3);
                shapes.setSummary(getString(R.string.square));
                break;
            case 4:
                Drawable drawableSquircle = getDrawable(R.drawable.squircle_icon);
                drawableSquircle.setTint(PublicVariable.primaryColor);
                LayerDrawable layerDrawable4 = new LayerDrawable(new Drawable[]{drawableSquircle, getDrawable(R.drawable.w_pref_gui)});
                shapes.setIcon(layerDrawable4);
                shapes.setSummary(getString(R.string.squircle));
                break;
            case 0:
                Drawable drawableNoShape = getDrawable(R.drawable.w_pref_noshape);
                drawableNoShape.setTint(PublicVariable.primaryColor);
                shapes.setIcon(drawableNoShape);
                break;
        }
        if (functionsClass.loadCustomIcons()) {
            shapes.setIcon(functionsClass.appIcon(functionsClass.customIconPackageName()));
            shapes.setSummary(functionsClass.appName(functionsClass.customIconPackageName()));
        }

        if (functionsClass.UsageStatsEnabled()) {
            PublicVariable.forceReload = true;
            smart.setChecked(true);
        } else {
            smart.setChecked(false);
        }

        if (functionsClass.returnAPI() < 24) {
            observe.setSummary(getString(R.string.observeSum));
            observe.setEnabled(false);

            freeForm.setSummary(getString(R.string.observeSum));
            freeForm.setEnabled(false);
        }
        if (functionsClass.AccessibilityServiceEnabled() && functionsClass.SettingServiceRunning(InteractionObserver.class)) {
            observe.setChecked(true);
        } else {
            observe.setChecked(false);
        }

        if (functionsClass.NotificationAccess() && functionsClass.NotificationListenerRunning()) {
            notification.setChecked(true);
        } else {
            notification.setChecked(false);
        }

        if (!functionsClass.wallpaperStaticLive()) {
            blur.setEnabled(false);
        }

        if (functionsClass.freeFormSupport(getApplicationContext()) && functionsClass.FreeForm()) {
            freeForm.setChecked(true);
        } else {
            freeForm.setChecked(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        functionsClass.loadSavedColor();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (functionsClass.SystemCache() || functionsClass.automationFeatureEnable()) {
            startService(new Intent(getApplicationContext(), BindServices.class));
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (FromWidgetsConfigurations) {
                Intent intent = new Intent(getApplicationContext(), WidgetConfigurations.class);
                startActivity(intent);
            } else {
                if (PublicVariable.forceReload) {
                    PublicVariable.forceReload = false;

                    functionsClass.overrideBackPressToMain(SettingGUI.this);
                }
            }

            float finalRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, (functionsClass.displayX() / 2), (functionsClass.displayY() / 2), finalRadius, 0);

            circularReveal.setDuration(213);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setVisibility(View.INVISIBLE);
                }
            });
            circularReveal.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SettingGUI.this.finish();
            }
        }, 700);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem facebook = menu.findItem(R.id.facebook);
        MenuItem gift = menu.findItem(R.id.donate);

        LayerDrawable drawFacebook = (LayerDrawable) getDrawable(R.drawable.draw_facebook);
        Drawable backFacebook = drawFacebook.findDrawableByLayerId(R.id.backtemp);
        backFacebook.setTint(PublicVariable.primaryColorOpposite);
        facebook.setIcon(drawFacebook);

        if (!functionsClass.alreadyDonated()) {
            LayerDrawable drawableDonate = (LayerDrawable) getDrawable(R.drawable.draw_gift);
            Drawable backgroundGift = drawableDonate.findDrawableByLayerId(R.id.backtemp);
            backgroundGift.setTint(PublicVariable.primaryColorOpposite);
            gift.setIcon(drawableDonate);
        } else {
            gift.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        switch (item.getItemId()) {
            case R.id.facebook: {
                Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook_app)));
                b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(b);

                break;
            }
            case R.id.donate: {
                startActivity(new Intent(getApplicationContext(), InAppBilling.class)
                        .putExtra("UserEmailAddress", functionsClass.readPreference(".UserInformation", "userEmail", null)));

                break;
            }
            case android.R.id.home: {
                try {
                    if (PublicVariable.forceReload) {
                        PublicVariable.forceReload = false;

                        functionsClass.overrideBackPressToMain(SettingGUI.this);
                    }

                    float finalRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());
                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                            rootLayout, (functionsClass.displayX() / 2), (functionsClass.displayY() / 2), finalRadius, 0);

                    circularReveal.setDuration(213);
                    circularReveal.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rootLayout.setVisibility(View.INVISIBLE);
                        }
                    });
                    circularReveal.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SettingGUI.this.finish();
                    }
                }, 700);
                break;
            }
            default: {
                result = super.onOptionsItemSelected(item);
                break;
            }
        }
        return result;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String sticky = sharedPreferences.getString("stick", "1");
        stick = (ListPreference) findPreference("stick");
        if (sticky.equals("1")) {
            PublicVariable.forceReload = false;
            stick.setSummary(getString(R.string.leftEdge));
        } else if (sticky.equals("2")) {
            PublicVariable.forceReload = false;
            stick.setSummary(getString(R.string.rightEdge));
        }

        functionsClass.checkLightDarkTheme();
        String appTheme = sharedPreferences.getString("themeColor", "2");
        themeColor = (ListPreference) findPreference("themeColor");
        if (appTheme.equals("1")) {
            themeColor.setSummary(getString(R.string.light));
            if (PublicVariable.themeLightDark != currentTheme) {
                PublicVariable.forceReload = true;

                PublicVariable.themeLightDark = true;
                startActivity(new Intent(getApplicationContext(), SettingGUI.class));

                functionsClass.saveDefaultPreference("LitePreferences", false);
            }
        } else if (appTheme.equals("2")) {
            themeColor.setSummary(getString(R.string.dark));
            if (PublicVariable.themeLightDark != currentTheme) {
                PublicVariable.forceReload = true;

                PublicVariable.themeLightDark = false;
                startActivity(new Intent(getApplicationContext(), SettingGUI.class));

                functionsClass.saveDefaultPreference("LitePreferences", false);
            }
        } else if (appTheme.equals("3")) {
            themeColor.setSummary(getString(R.string.dynamic));
            if (PublicVariable.themeLightDark != currentTheme) {
                PublicVariable.forceReload = true;

                functionsClass.checkLightDarkTheme();
                functionsClass.saveDefaultPreference("LitePreferences", false);

                startActivity(new Intent(getApplicationContext(), SettingGUI.class));
            }
        }

        if (sharedPreferences.getBoolean("transparent", true) == true) {
            blur.setEnabled(true);
            if (!functionsClass.wallpaperStaticLive()) {
                blur.setChecked(false);
                blur.setEnabled(false);
            }

            functionsClass.saveDefaultPreference("LitePreferences", false);
        } else if (sharedPreferences.getBoolean("transparent", true) == false) {
            blur.setChecked(false);
            blur.setEnabled(false);
        }
    }

    /*Custom Package of Shapes/Icons*/
    public void setupShapes() {
        int currentShape = sharedPreferences.getInt("iconShape", 0);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        int dialogueWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 377, getResources().getDisplayMetrics());
        int dialogueHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 387, getResources().getDisplayMetrics());


        layoutParams.width = dialogueWidth;
        layoutParams.height = dialogueHeight;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.57f;

        final Dialog dialog = new Dialog(SettingGUI.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.icons_shapes_preferences);
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialog.setCancelable(true);

        final Drawable drawableTeardrop = getDrawable(R.drawable.droplet_icon);
        drawableTeardrop.setTint(PublicVariable.primaryColor);
        final Drawable drawableCircle = getDrawable(R.drawable.circle_icon);
        drawableCircle.setTint(PublicVariable.primaryColor);
        final Drawable drawableSquare = getDrawable(R.drawable.square_icon);
        drawableSquare.setTint(PublicVariable.primaryColor);
        final Drawable drawableSquircle = getDrawable(R.drawable.squircle_icon);
        drawableSquircle.setTint(PublicVariable.primaryColor);

        TextView dialogueTitle = (TextView) dialog.findViewById(R.id.dialogueTitle);
        dialogueTitle.setText(Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + getString(R.string.shapedDesc) + "</font>"));
        dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite);

        View dialogueView = (ScrollView) dialog.findViewById(R.id.dialogueView);
        dialogueView.setBackgroundTintList(ColorStateList.valueOf(PublicVariable.colorLightDark));

        RelativeLayout teardropShape = (RelativeLayout) dialog.findViewById(R.id.teardropShape);
        RelativeLayout circleShape = (RelativeLayout) dialog.findViewById(R.id.circleShape);
        RelativeLayout squareShape = (RelativeLayout) dialog.findViewById(R.id.squareShape);
        RelativeLayout squircleShape = (RelativeLayout) dialog.findViewById(R.id.squircleShape);

        Button customIconPack = (Button) dialog.findViewById(R.id.customIconPack);
        Button noShape = (Button) dialog.findViewById(R.id.noShape);

        ImageView teardropImage = (ImageView) dialog.findViewById(R.id.teardropImage);
        ImageView circleImage = (ImageView) dialog.findViewById(R.id.circleImage);
        ImageView squareImage = (ImageView) dialog.findViewById(R.id.squareImage);
        ImageView squircleImage = (ImageView) dialog.findViewById(R.id.squircleImage);

        TextView teardropText = (TextView) dialog.findViewById(R.id.teardropText);
        TextView circleText = (TextView) dialog.findViewById(R.id.circleText);
        TextView squareText = (TextView) dialog.findViewById(R.id.squareText);
        TextView squircleText = (TextView) dialog.findViewById(R.id.squircleText);

        teardropImage.setImageDrawable(drawableTeardrop);
        circleImage.setImageDrawable(drawableCircle);
        squareImage.setImageDrawable(drawableSquare);
        squircleImage.setImageDrawable(drawableSquircle);

        teardropText.setTextColor(PublicVariable.colorLightDarkOpposite);
        circleText.setTextColor(PublicVariable.colorLightDarkOpposite);
        squareText.setTextColor(PublicVariable.colorLightDarkOpposite);
        squircleText.setTextColor(PublicVariable.colorLightDarkOpposite);

        customIconPack.setTextColor(PublicVariable.colorLightDarkOpposite);
        noShape.setTextColor(PublicVariable.colorLightDarkOpposite);

        teardropShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.saveDefaultPreference("customIcon", getPackageName());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("iconShape", 1);
                editor.apply();

                LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawableTeardrop, getDrawable(R.drawable.w_pref_gui)});
                shapes.setIcon(layerDrawable);
                shapes.setSummary(getString(R.string.droplet));

                functionsClass.saveDefaultPreference("LitePreferences", false);
                dialog.dismiss();
            }
        });
        circleShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.saveDefaultPreference("customIcon", getPackageName());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("iconShape", 2);
                editor.apply();

                LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawableCircle, getDrawable(R.drawable.w_pref_gui)});
                shapes.setIcon(layerDrawable);
                shapes.setSummary(getString(R.string.circle));

                functionsClass.saveDefaultPreference("LitePreferences", false);
                dialog.dismiss();
            }
        });
        squareShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.saveDefaultPreference("customIcon", getPackageName());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("iconShape", 3);
                editor.apply();

                LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawableSquare, getDrawable(R.drawable.w_pref_gui)});
                shapes.setIcon(layerDrawable);
                shapes.setSummary(getString(R.string.square));

                functionsClass.saveDefaultPreference("LitePreferences", false);
                dialog.dismiss();
            }
        });
        squircleShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.saveDefaultPreference("customIcon", getPackageName());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("iconShape", 4);
                editor.apply();

                LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawableSquircle, getDrawable(R.drawable.w_pref_gui)});
                shapes.setIcon(layerDrawable);
                shapes.setSummary(getString(R.string.squircle));

                functionsClass.saveDefaultPreference("LitePreferences", false);
                dialog.dismiss();
            }
        });

        customIconPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                listCustomIconsPackage();
            }
        });

        noShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.saveDefaultPreference("customIcon", getPackageName());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("iconShape", 0);
                editor.apply();

                Drawable drawableNoShape = getDrawable(R.drawable.w_pref_noshape);
                drawableNoShape.setTint(PublicVariable.primaryColor);
                shapes.setIcon(drawableNoShape);
                shapes.setSummary("");

                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                functionsClass.addAppShortcuts();

                if (currentShape != sharedPreferences.getInt("iconShape", 0)) {
                    PublicVariable.forceReload = true;

                    SearchEngineAdapter.allSearchResultItems.clear();
                }

                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });
        dialog.show();
    }

    public void listCustomIconsPackage() {
        String currentCustomIconPack = sharedPreferences.getString("customIcon", getPackageName());

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        int dialogueWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 377, getResources().getDisplayMetrics());

        layoutParams.width = dialogueWidth;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.57f;

        final Dialog dialog = new Dialog(SettingGUI.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_icons);
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialog.setCancelable(true);

        View dialogueView = (RelativeLayout) dialog.findViewById(R.id.dialogueView);
        dialogueView.setBackgroundTintList(ColorStateList.valueOf(PublicVariable.colorLightDark));

        TextView dialogueTitle = (TextView) dialog.findViewById(R.id.dialogueTitle);
        TextView defaultTheme = (TextView) dialog.findViewById(R.id.setDefault);
        RecyclerView customIconList = (RecyclerView) dialog.findViewById(R.id.customIconList);

        dialogueTitle.setText(Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + getString(R.string.customIconTitle) + "</font>"));
        dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite);
        defaultTheme.setTextColor(PublicVariable.colorLightDarkOpposite);

        RecycleViewSmoothLayoutList recyclerViewLayoutManager = new RecycleViewSmoothLayoutList(getApplicationContext(), OrientationHelper.VERTICAL, false);
        customIconList.setLayoutManager(recyclerViewLayoutManager);
        customIconList.removeAllViews();
        final ArrayList<AdapterItems> adapterItems = new ArrayList<AdapterItems>();
        adapterItems.clear();
        for (String packageName : PublicVariable.customIconsPackages) {
            adapterItems.add(new AdapterItems(
                    functionsClass.appName(packageName),
                    packageName,
                    functionsClass.appIcon(packageName)
            ));
        }
        CustomIconsThemeAdapter customIconsThemeAdapter = new CustomIconsThemeAdapter(SettingGUI.this, getApplicationContext(), adapterItems, dialog);
        customIconList.setAdapter(customIconsThemeAdapter);

        defaultTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.saveDefaultPreference("customIcon", getPackageName());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("iconShape", 0);
                editor.apply();

                Drawable drawableNoShape = getDrawable(R.drawable.w_pref_noshape);
                drawableNoShape.setTint(PublicVariable.primaryColor);
                shapes.setIcon(drawableNoShape);
                shapes.setSummary("");

                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                adapterItems.clear();

                if (!currentCustomIconPack.equals(sharedPreferences.getString("customIcon", getPackageName()))) {
                    PublicVariable.forceReload = true;

                    SearchEngineAdapter.allSearchResultItems.clear();
                }

                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });
        dialog.show();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("CUSTOM_DIALOGUE_DISMISS");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("CUSTOM_DIALOGUE_DISMISS")) {
                    shapes.setIcon(functionsClass.appIcon(functionsClass.customIconPackageName()));
                    shapes.setSummary(functionsClass.appName(functionsClass.customIconPackageName()));

                    functionsClass.addAppShortcuts();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("iconShape", 0);
                    editor.apply();

                    dialog.dismiss();
                }
            }
        };
        try {
            registerReceiver(broadcastReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Fling Sensitivity*/
    public void setupFlingSensitivity() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        int dialogueWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        int dialogueHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());

        layoutParams.width = dialogueWidth;
        layoutParams.height = dialogueHeight;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.57f;

        final Dialog dialog = new Dialog(SettingGUI.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.seekbar_preferences_fling);
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialog.setCancelable(true);

        View dialogueView = (RelativeLayout) dialog.findViewById(R.id.dialogueView);
        dialogueView.setBackgroundTintList(ColorStateList.valueOf(PublicVariable.colorLightDark));

        final ImageView flingingIcon = (ImageView) dialog.findViewById(R.id.preferenceIcon);
        final SeekBar seekBarPreferences = (SeekBar) dialog.findViewById(R.id.seekBarPreferences);
        TextView dialogueTitle = (TextView) dialog.findViewById(R.id.dialogueTitle);
        TextView revertDefault = (TextView) dialog.findViewById(R.id.revertDefault);

        seekBarPreferences.setThumbTintList(ColorStateList.valueOf(PublicVariable.primaryColor));
        seekBarPreferences.setThumbTintMode(PorterDuff.Mode.SRC_IN);
        seekBarPreferences.setProgressTintList(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
        seekBarPreferences.setProgressTintMode(PorterDuff.Mode.SRC_IN);

        seekBarPreferences.setMax(50);
        seekBarPreferences.setProgress(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValueProgress", 30));
        if (seekBarPreferences.getProgress() <= 10) {
            seekBarPreferences.setProgressTintList(ColorStateList.valueOf(getColor(R.color.red)));
        }

        Drawable layerDrawableLoadLogo;
        try {
            Drawable backgroundDot = functionsClass.shapesDrawables().mutate();
            backgroundDot.setTint(PublicVariable.primaryColorOpposite);
            layerDrawableLoadLogo = new LayerDrawable(new Drawable[]{
                    backgroundDot,
                    getDrawable(R.drawable.ic_launcher_dots)
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
            layerDrawableLoadLogo = getDrawable(R.drawable.ic_launcher);
        }

        int iconHW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, functionsClass.readDefaultPreference("floatingSize", 39), getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams layoutParamsIcon = new RelativeLayout.LayoutParams(
                iconHW,
                iconHW
        );
        layoutParamsIcon.addRule(RelativeLayout.CENTER_IN_PARENT, R.id.preferenceIcon);
        layoutParamsIcon.addRule(RelativeLayout.BELOW, R.id.dialogueTitle);
        flingingIcon.setLayoutParams(layoutParamsIcon);
        flingingIcon.setImageDrawable(layerDrawableLoadLogo);

        dialogueTitle.setText(Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + getString(R.string.flingSensitivityTitle) + "</font>"));
        dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite);
        revertDefault.setTextColor(PublicVariable.colorLightDarkOpposite);

        FlingAnimation flingAnimationX = new FlingAnimation(flingingIcon, DynamicAnimation.TRANSLATION_X)
                .setFriction(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f));
        FlingAnimation flingAnimationY = new FlingAnimation(flingingIcon, DynamicAnimation.TRANSLATION_Y)
                .setFriction(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f));

        flingAnimationX.setMinValue(-functionsClass.DpToPixel(97));
        flingAnimationX.setMaxValue(functionsClass.DpToPixel(97));

        flingAnimationY.setMinValue(0);
        flingAnimationY.setMaxValue(functionsClass.DpToPixel(197));

        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent motionEvent) {

                return true;
            }

            @Override
            public boolean onFling(MotionEvent motionEventFirst, MotionEvent motionEventLast, float velocityX, float velocityY) {

                flingAnimationX.setStartVelocity(velocityX);
                flingAnimationY.setStartVelocity(velocityY);

                flingAnimationX.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                    @Override
                    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {

                    }
                });
                flingAnimationY.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                    @Override
                    public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {

                    }
                });

                flingAnimationX.start();
                flingAnimationY.start();

                return false;
            }
        };

        flingAnimationX.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {

            }
        });

        flingAnimationY.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {

            }
        });

        GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), simpleOnGestureListener);

        flingingIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                flingAnimationX.cancel();
                flingAnimationY.cancel();

                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        seekBarPreferences.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                functionsClass.savePreference("FlingSensitivity", "FlingSensitivityValueProgress", progress);
                if (progress <= 10) {
                    seekBarPreferences.setProgressTintList(ColorStateList.valueOf(getColor(R.color.red)));

                    functionsClass.savePreference("FlingSensitivity", "FlingSensitivityValue", 0.5f);
                    try {
                        flingAnimationX.setFriction(0.5f);
                        flingAnimationY.setFriction(0.5f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    seekBarPreferences.setProgressTintList(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));

                    functionsClass.savePreference("FlingSensitivity", "FlingSensitivityValue", (float) (progress / 10));
                    try {
                        flingAnimationX.setFriction(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f));
                        flingAnimationY.setFriction(functionsClass.readPreference("FlingSensitivity", "FlingSensitivityValue", 3.0f));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        revertDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.savePreference("FlingSensitivity", "FlingSensitivityValueProgress", 30);
                functionsClass.savePreference("FlingSensitivity", "FlingSensitivityValue", 3.0f);

                seekBarPreferences.setProgress(30);
                seekBarPreferences.setProgressTintList(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });

        dialog.show();
    }
}
