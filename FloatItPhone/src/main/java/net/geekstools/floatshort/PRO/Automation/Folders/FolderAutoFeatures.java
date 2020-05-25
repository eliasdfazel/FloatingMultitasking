/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/24/20 7:35 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation.Folders;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import net.geekstools.floatshort.PRO.Automation.Apps.AppAutoFeatures;
import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassIO;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassTheme;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureConstants;
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerConstants;
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.GestureListenerInterface;
import net.geekstools.floatshort.PRO.Utils.UI.Gesture.SwipeGestureListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FolderAutoFeatures extends AppCompatActivity implements View.OnClickListener, GestureListenerInterface {

    FunctionsClass functionsClass;
    FunctionsClassIO functionsClassIO;

    FunctionsClassTheme functionsClassTheme;
    FunctionsClassTheme.Utils functionsClassThemeUtils;

    RecyclerView categorylist;
    ListView actionElementsList;
    RelativeLayout fullActionButton, MainView;
    LinearLayout autoIdentifier;
    RelativeLayout loadingSplash;
    ProgressBar loadingBarLTR;
    TextView desc;
    Button wifi, bluetooth, gps, nfc, time, autoApps, autoCategories;

    String AppTime;
    ArrayList<AdapterItems> adapterItems;
    RecyclerView.Adapter categoryAutoListAdapter;
    LinearLayoutManager recyclerViewLayoutManager;

    int color, pressColor;

    SwipeGestureListener swipeGestureListener;

    LoadCustomIcons loadCustomIcons;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        setContentView(R.layout.auto_categories);


        categorylist = (RecyclerView) findViewById(R.id.recyclerListView);
        autoIdentifier = (LinearLayout) findViewById(R.id.autoid);
        autoIdentifier.bringToFront();
        MainView = (RelativeLayout) findViewById(R.id.MainView);
        fullActionButton = (RelativeLayout) findViewById(R.id.fullActionViews);
        actionElementsList = (ListView) findViewById(R.id.acttionElementsList);
        autoApps = (Button) findViewById(R.id.autoApps);
        autoApps.bringToFront();
        autoCategories = (Button) findViewById(R.id.autoCategories);
        autoCategories.bringToFront();
        wifi = (Button) findViewById(R.id.wifi);
        bluetooth = (Button) findViewById(R.id.bluetooth);
        gps = (Button) findViewById(R.id.gps);
        nfc = (Button) findViewById(R.id.nfc);
        time = (Button) findViewById(R.id.time);

        swipeGestureListener = new SwipeGestureListener(getApplicationContext(), FolderAutoFeatures.this);

        functionsClass = new FunctionsClass(getApplicationContext());
        functionsClassIO = new FunctionsClassIO(getApplicationContext());

        functionsClassTheme = new FunctionsClassTheme(getApplicationContext());
        functionsClassThemeUtils = functionsClassTheme.new Utils();

        functionsClass.loadSavedColor();
        functionsClass.checkLightDarkTheme();

        if (!getFileStreamPath(".categoryInfo").exists()
                || !(functionsClass.countLineInnerFile(".categoryInfo") > 0)) {
            startActivity(new Intent(getApplicationContext(), FoldersConfigurations.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            finish();
            return;
        }

        if (functionsClass.returnAPI() >= 26) {
            if (!functionsClass.ControlPanel()) {
                Snackbar snackbar = Snackbar.make(MainView, Html.fromHtml("<big>" + getString(R.string.enableControlPanel) + "</big>"), Snackbar.LENGTH_INDEFINITE)
                        .setAction(Html.fromHtml("<b>" + getString(R.string.enable).toUpperCase() + "</b>"), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("stable", true);
                                editor.apply();
                                startService(new Intent(getApplicationContext(), BindServices.class));
                            }
                        });
                snackbar.setActionTextColor(PublicVariable.colorLightDarkOpposite);
                snackbar.setActionTextColor(getColor(R.color.red));

                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{
                                Color.TRANSPARENT,
                                functionsClass.setColorAlpha(PublicVariable.primaryColor, 207),
                                Color.TRANSPARENT
                        });

                View view = snackbar.getView();
                view.setBackground(gradientDrawable/*functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.03f), 180)*/);

                snackbar.show();
            }
        }

        functionsClassTheme.setThemeColorAutomationFeature(FolderAutoFeatures.this, MainView, functionsClass.appThemeTransparent());

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }

        adapterItems = new ArrayList<AdapterItems>();

        recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        categorylist.setLayoutManager(recyclerViewLayoutManager);

        autoApps.setTextColor(getColor(R.color.light));
        autoCategories.setTextColor(getColor(R.color.light));
        if (PublicVariable.themeLightDark && functionsClass.appThemeTransparent()) {
            autoApps.setTextColor(getColor(R.color.dark));
            autoCategories.setTextColor(getColor(R.color.dark));
        }

        RippleDrawable rippleDrawableShortcuts = (RippleDrawable) getDrawable(R.drawable.draw_shortcuts);
        Drawable gradientDrawableShortcutsForeground = rippleDrawableShortcuts.findDrawableByLayerId(R.id.foregroundItem);
        Drawable gradientDrawableShortcutsBackground = rippleDrawableShortcuts.findDrawableByLayerId(R.id.backgroundItem);
        Drawable gradientDrawableMaskShortcuts = rippleDrawableShortcuts.findDrawableByLayerId(android.R.id.mask);

        if (functionsClass.appThemeTransparent()) {
            rippleDrawableShortcuts.setColor(ColorStateList.valueOf(PublicVariable.primaryColor));
            gradientDrawableShortcutsForeground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 255));
            if (functionsClass.returnAPI() > 21) {
                gradientDrawableShortcutsBackground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175));
            } else {
                gradientDrawableShortcutsBackground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175));
            }
            gradientDrawableMaskShortcuts.setTint(PublicVariable.primaryColor);
        } else {
            rippleDrawableShortcuts.setColor(ColorStateList.valueOf(PublicVariable.primaryColor));
            gradientDrawableShortcutsForeground.setTint(PublicVariable.primaryColorOpposite);
            gradientDrawableShortcutsBackground.setTint(PublicVariable.primaryColorOpposite);
            gradientDrawableMaskShortcuts.setTint(PublicVariable.primaryColor);
        }
        autoApps.setBackground(rippleDrawableShortcuts);

        RippleDrawable rippleDrawableCategories = (RippleDrawable) getDrawable(R.drawable.draw_categories);
        Drawable gradientDrawableCategoriesForeground = rippleDrawableCategories.findDrawableByLayerId(R.id.foregroundItem);
        Drawable gradientDrawableCategoriesBackground = rippleDrawableCategories.findDrawableByLayerId(R.id.backgroundItem);
        Drawable gradientDrawableMaskCategories = rippleDrawableCategories.findDrawableByLayerId(android.R.id.mask);

        if (functionsClass.appThemeTransparent()) {
            rippleDrawableCategories.setColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
            gradientDrawableCategoriesForeground.setTint(
                    functionsClass.setColorAlpha(
                            functionsClass.mixColors(
                                    PublicVariable.primaryColor, PublicVariable.colorLightDark,
                                    0.75f), functionsClassThemeUtils.wallpaperStaticLive() ? 245 : 113)
            );
            gradientDrawableCategoriesBackground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColor, functionsClassThemeUtils.wallpaperStaticLive() ? 150 : 155));
            gradientDrawableMaskCategories.setTint(PublicVariable.primaryColorOpposite);
        } else {
            rippleDrawableCategories.setColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
            gradientDrawableCategoriesForeground.setTint(PublicVariable.primaryColor);
            gradientDrawableCategoriesBackground.setTint(PublicVariable.primaryColor);
            gradientDrawableMaskCategories.setTint(PublicVariable.primaryColorOpposite);
        }
        autoCategories.setBackground(rippleDrawableCategories);

        ImageView floatingLogo = (ImageView) findViewById(R.id.loadingLogo);
        LayerDrawable drawFloatingLogo = (LayerDrawable) getDrawable(R.drawable.draw_floating_logo);
        Drawable backFloatingLogo = drawFloatingLogo.findDrawableByLayerId(R.id.backgroundTemporary);
        backFloatingLogo.setTint(PublicVariable.primaryColorOpposite);
        floatingLogo.setImageDrawable(drawFloatingLogo);

        loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
        if (functionsClass.appThemeTransparent() == true) {
            loadingSplash.setBackgroundColor(Color.TRANSPARENT);
        } else {
            loadingSplash.setBackgroundColor(getWindow().getNavigationBarColor());
        }

        loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgress);
        desc = (TextView) findViewById(R.id.loadingDescription);
        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        desc.setTypeface(face);

        if (PublicVariable.themeLightDark) {
            loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.darkMutedColor, android.graphics.PorterDuff.Mode.MULTIPLY);
            desc.setTextColor(getColor(R.color.dark));
        } else if (!PublicVariable.themeLightDark) {
            loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.vibrantColor, android.graphics.PorterDuff.Mode.MULTIPLY);
            desc.setTextColor(getColor(R.color.light));
        }

        autoApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicVariable.autoID = null;
                try {
                    functionsClass.navigateToClass(FolderAutoFeatures.this, AppAutoFeatures.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (PublicVariable.themeLightDark) {
            color = PublicVariable.vibrantColor;
            pressColor = PublicVariable.darkMutedColor;
        } else if (!PublicVariable.themeLightDark) {
            color = PublicVariable.darkMutedColor;
            pressColor = PublicVariable.vibrantColor;
        }

        if (PublicVariable.autoID != null) {
            final LayerDrawable drawWifi = (LayerDrawable) getDrawable(R.drawable.draw_wifi);
            final Drawable backWifi = drawWifi.findDrawableByLayerId(R.id.backgroundTemporary);

            final LayerDrawable drawBluetooth = (LayerDrawable) getDrawable(R.drawable.draw_bluetooth);
            final Drawable backBluetooth = drawBluetooth.findDrawableByLayerId(R.id.backgroundTemporary);

            final LayerDrawable drawGPS = (LayerDrawable) getDrawable(R.drawable.draw_gps);
            final Drawable backGPS = drawGPS.findDrawableByLayerId(R.id.backgroundTemporary);

            final LayerDrawable drawNfc = (LayerDrawable) getDrawable(R.drawable.draw_nfc);
            final Drawable backNfc = drawNfc.findDrawableByLayerId(R.id.backgroundTemporary);

            final LayerDrawable drawTime = (LayerDrawable) getDrawable(R.drawable.draw_time);
            final Drawable backTime = drawTime.findDrawableByLayerId(R.id.backgroundTemporary);

            wifi.setBackground(drawWifi);
            bluetooth.setBackground(drawBluetooth);
            gps.setBackground(drawGPS);
            nfc.setBackground(drawNfc);
            time.setBackground(drawTime);

            if (PublicVariable.autoID.equals(getString(R.string.wifi_folder))) {
                backWifi.setTint(pressColor);
                wifi.setBackground(drawWifi);

                autoWiFi();

                backBluetooth.setTint(color);
                backGPS.setTint(color);
                backNfc.setTint(color);
                backTime.setTint(color);

                bluetooth.setBackground(drawBluetooth);
                gps.setBackground(drawGPS);
                nfc.setBackground(drawNfc);
                time.setBackground(drawTime);
            } else if (PublicVariable.autoID.equals(getString(R.string.bluetooth_folder))) {
                backBluetooth.setTint(pressColor);
                bluetooth.setBackground(drawBluetooth);

                autoBluetooth();

                backWifi.setTint(color);
                backGPS.setTint(color);
                backNfc.setTint(color);
                backTime.setTint(color);

                wifi.setBackground(drawWifi);
                gps.setBackground(drawGPS);
                nfc.setBackground(drawNfc);
                time.setBackground(drawTime);
            } else if (PublicVariable.autoID.equals(getString(R.string.gps_folder))) {
                backGPS.setTint(pressColor);
                gps.setBackground(drawGPS);

                autoGPS();

                backWifi.setTint(color);
                backBluetooth.setTint(color);
                backNfc.setTint(color);
                backTime.setTint(color);

                wifi.setBackground(drawWifi);
                bluetooth.setBackground(drawBluetooth);
                nfc.setBackground(drawNfc);
                time.setBackground(drawTime);
            } else if (PublicVariable.autoID.equals(getString(R.string.nfc_folder))) {
                backNfc.setTint(pressColor);
                nfc.setBackground(drawNfc);

                autoNfc();

                backWifi.setTint(color);
                backBluetooth.setTint(color);
                backGPS.setTint(color);
                backTime.setTint(color);

                wifi.setBackground(drawWifi);
                bluetooth.setBackground(drawBluetooth);
                gps.setBackground(drawGPS);
                time.setBackground(drawTime);
            } else if (PublicVariable.autoID.equals(getString(R.string.time_folder))) {
                backTime.setTint(pressColor);
                time.setBackground(drawTime);

                autoTime();

                backWifi.setTint(color);
                backBluetooth.setTint(color);
                backGPS.setTint(color);
                backNfc.setTint(color);

                wifi.setBackground(drawWifi);
                bluetooth.setBackground(drawBluetooth);
                gps.setBackground(drawGPS);
                nfc.setBackground(drawNfc);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        final LayerDrawable drawWifi = (LayerDrawable) getDrawable(R.drawable.draw_wifi);
        final Drawable backWifi = drawWifi.findDrawableByLayerId(R.id.backgroundTemporary);

        final LayerDrawable drawBluetooth = (LayerDrawable) getDrawable(R.drawable.draw_bluetooth);
        final Drawable backBluetooth = drawBluetooth.findDrawableByLayerId(R.id.backgroundTemporary);

        final LayerDrawable drawGPS = (LayerDrawable) getDrawable(R.drawable.draw_gps);
        final Drawable backGPS = drawGPS.findDrawableByLayerId(R.id.backgroundTemporary);

        final LayerDrawable drawNfc = (LayerDrawable) getDrawable(R.drawable.draw_nfc);
        final Drawable backNfc = drawNfc.findDrawableByLayerId(R.id.backgroundTemporary);

        final LayerDrawable drawTime = (LayerDrawable) getDrawable(R.drawable.draw_time);
        final Drawable backTime = drawTime.findDrawableByLayerId(R.id.backgroundTemporary);

        if (PublicVariable.themeLightDark) {
            color = PublicVariable.vibrantColor;
            pressColor = PublicVariable.darkMutedColor;
        } else if (!PublicVariable.themeLightDark) {
            color = PublicVariable.darkMutedColor;
            pressColor = PublicVariable.vibrantColor;
        }

        backWifi.setTint(color);
        backBluetooth.setTint(color);
        backGPS.setTint(color);
        backNfc.setTint(color);
        backTime.setTint(color);

        wifi.setBackground(drawWifi);
        bluetooth.setBackground(drawBluetooth);
        gps.setBackground(drawGPS);
        nfc.setBackground(drawNfc);
        time.setBackground(drawTime);

        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backWifi.setTint(pressColor);
                wifi.setBackground(drawWifi);

                autoWiFi();

                backBluetooth.setTint(color);
                backGPS.setTint(color);
                backNfc.setTint(color);
                backTime.setTint(color);

                bluetooth.setBackground(drawBluetooth);
                gps.setBackground(drawGPS);
                nfc.setBackground(drawNfc);
                time.setBackground(drawTime);
            }
        });
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBluetooth.setTint(pressColor);
                bluetooth.setBackground(drawBluetooth);

                autoBluetooth();

                backWifi.setTint(color);
                backGPS.setTint(color);
                backNfc.setTint(color);
                backTime.setTint(color);

                wifi.setBackground(drawWifi);
                gps.setBackground(drawGPS);
                nfc.setBackground(drawNfc);
                time.setBackground(drawTime);
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backGPS.setTint(pressColor);
                gps.setBackground(drawGPS);

                autoGPS();

                backWifi.setTint(color);
                backBluetooth.setTint(color);
                backNfc.setTint(color);
                backTime.setTint(color);

                wifi.setBackground(drawWifi);
                bluetooth.setBackground(drawBluetooth);
                nfc.setBackground(drawNfc);
                time.setBackground(drawTime);
            }
        });
        nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backNfc.setTint(pressColor);
                nfc.setBackground(drawNfc);

                autoNfc();

                backWifi.setTint(color);
                backBluetooth.setTint(color);
                backGPS.setTint(color);
                backTime.setTint(color);

                wifi.setBackground(drawWifi);
                bluetooth.setBackground(drawBluetooth);
                gps.setBackground(drawGPS);
                time.setBackground(drawTime);
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backTime.setTint(pressColor);
                time.setBackground(drawTime);

                autoTime();

                backWifi.setTint(color);
                backBluetooth.setTint(color);
                backGPS.setTint(color);
                backNfc.setTint(color);

                wifi.setBackground(drawWifi);
                bluetooth.setBackground(drawBluetooth);
                gps.setBackground(drawGPS);
                nfc.setBackground(drawNfc);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (functionsClassIO.automationFeatureEnable()) {
            startService(new Intent(getApplicationContext(), BindServices.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PublicVariable.autoID = null;
    }

    @Override
    public void onBackPressed() {
        try {
            functionsClass.CheckSystemRAM(FolderAutoFeatures.this);

            functionsClass.overrideBackPressToMain(FolderAutoFeatures.this, FolderAutoFeatures.this);
            overridePendingTransition(android.R.anim.fade_in, R.anim.go_up);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onSwipeGesture(@NotNull GestureConstants gestureConstants, @NotNull MotionEvent downMotionEvent, @NotNull MotionEvent moveMotionEvent, float initVelocityX, float initVelocityY) {
        if (gestureConstants instanceof GestureConstants.SwipeHorizontal) {
            switch (((GestureConstants.SwipeHorizontal) gestureConstants).getHorizontalDirection()) {
                case GestureListenerConstants.SWIPE_RIGHT: {

                    functionsClass.navigateToClass(FolderAutoFeatures.this, AppAutoFeatures.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));

                    FolderAutoFeatures.this.finish();

                    break;
                }
                case GestureListenerConstants.SWIPE_LEFT: {

                    break;
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        swipeGestureListener.onTouchEvent(motionEvent);

        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void onSingleTapUp(@NotNull MotionEvent motionEvent) {

    }

    @Override
    public void onLongPress(@NotNull MotionEvent motionEvent) {

    }

    public void autoWiFi() {
        PublicVariable.autoID = getString(R.string.wifi_folder);
        int API = android.os.Build.VERSION.SDK_INT;
        if (API > 22) {
            if (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_DENIED) {
                String[] gpsPermissions = {Manifest.permission.ACCESS_WIFI_STATE};
                requestPermissions(gpsPermissions, 888);
                return;
            }
        }

        LoadAutoCategories loadAutoCategories = new LoadAutoCategories();
        loadAutoCategories.execute();
    }

    public void autoBluetooth() {
        PublicVariable.autoID = getString(R.string.bluetooth_folder);
        int API = android.os.Build.VERSION.SDK_INT;
        if (API > 22) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED) {
                String[] gpsPermissions = {Manifest.permission.BLUETOOTH};
                requestPermissions(gpsPermissions, 999);
                return;
            }
        }

        LoadAutoCategories loadAutoCategories = new LoadAutoCategories();
        loadAutoCategories.execute();
    }

    public void autoGPS() {
        PublicVariable.autoID = getString(R.string.gps_folder);
        int API = android.os.Build.VERSION.SDK_INT;
        if (API > 22) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                String[] gpsPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(gpsPermissions, 777);
                return;
            }
        }

        LoadAutoCategories loadAutoCategories = new LoadAutoCategories();
        loadAutoCategories.execute();
    }

    public void autoNfc() {
        PublicVariable.autoID = getString(R.string.nfc_folder);
        int API = android.os.Build.VERSION.SDK_INT;
        if (API > 22) {
            if (checkSelfPermission(Manifest.permission.NFC) == PackageManager.PERMISSION_DENIED) {
                String[] gpsPermissions = {Manifest.permission.NFC};
                requestPermissions(gpsPermissions, 888);
                return;
            }
        }

        LoadAutoCategories loadAutoCategories = new LoadAutoCategories();
        loadAutoCategories.execute();
    }

    public void autoTime() {
        PublicVariable.autoID = getString(R.string.time_folder);

        LoadAutoCategories loadAutoCategories = new LoadAutoCategories();
        loadAutoCategories.execute();
    }

    /******************************************/
    private class LoadAutoCategories extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
            loadingSplash.setVisibility(View.VISIBLE);
            if (functionsClass.appThemeTransparent() == true) {
                loadingSplash.setBackgroundColor(Color.TRANSPARENT);
            } else {
                loadingSplash.setBackgroundColor(getWindow().getNavigationBarColor());
            }

            loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgress);
            desc = (TextView) findViewById(R.id.loadingDescription);
            Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
            desc.setTypeface(face);

            if (PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.darkMutedColor, android.graphics.PorterDuff.Mode.MULTIPLY);
            } else if (!PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.vibrantColor, android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String[] appData = functionsClass.readFileLine(".categoryInfo");

                if (functionsClass.customIconsEnable()) {
                    loadCustomIcons.load();
                    FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIconsNumber());
                }

                adapterItems = new ArrayList<AdapterItems>();
                for (int navItem = 0; navItem < appData.length; navItem++) {
                    try {
                        if (getFileStreamPath(appData[navItem] + ".Time").exists()) {
                            AppTime = functionsClass.readFile(appData[navItem] + ".Time");
                            String tempTimeHour = AppTime.split(":")[0];
                            String tempTimeMinute = AppTime.split(":")[1];
                            if (Integer.parseInt(tempTimeMinute) < 10) {
                                AppTime = tempTimeHour + ":" + "0" + tempTimeMinute;
                            }
                        }

                        adapterItems.add(new AdapterItems(
                                appData[navItem],
                                functionsClass.readFileLine(appData[navItem]),
                                AppTime));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                categoryAutoListAdapter = new FolderAutoListAdapter(FolderAutoFeatures.this, getApplicationContext(), adapterItems);
            } catch (Exception e) {
                e.printStackTrace();
                startActivity(new Intent(getApplicationContext(), FoldersConfigurations.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            categorylist.setAdapter(categoryAutoListAdapter);
            registerForContextMenu(categorylist);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    loadingSplash.setVisibility(View.INVISIBLE);
                    loadingSplash.startAnimation(anim);
                    autoIdentifier.setVisibility(View.VISIBLE);
                }
            }, 100);
        }
    }
}
