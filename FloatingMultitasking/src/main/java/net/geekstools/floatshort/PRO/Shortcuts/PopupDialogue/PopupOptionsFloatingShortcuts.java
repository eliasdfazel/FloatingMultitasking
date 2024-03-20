/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/11/20 10:51 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts.PopupDialogue;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter.FloatingShortcutsPopupOptionsAdapter;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsFloatingShortcutsPopuiOptions;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

import java.util.ArrayList;
import java.util.List;

public class PopupOptionsFloatingShortcuts extends Service {

    FunctionsClassLegacy functionsClassLegacy;
    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;

    ViewGroup viewGroup;
    RelativeLayout wholeMenuView, itemMenuView;
    ListView popupOptionsItems;

    int HW, xPosition, yPosition, startIdCommand;
    int statusBarHeight, navBarHeight;
    String packageName, className, classNameCommand;

    FloatingShortcutsPopupOptionsAdapter floatingShortcutsPopupOptionsAdapter;
    LoadCustomIcons loadCustomIcons;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, final int startId) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        navBarHeight = 0;
        int resourceIdNav = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceIdNav > 0) {
            navBarHeight = getResources().getDimensionPixelSize(resourceIdNav);
        }
        statusBarHeight = 0;
        int resourceIdStatus = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceIdStatus > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceIdStatus);
        }

        startIdCommand = intent.getIntExtra("startIdCommand", 0);
        classNameCommand = intent.getStringExtra("classNameCommand");
        packageName = intent.getStringExtra("PackageName");
        HW = intent.getIntExtra("HW", 0);
        xPosition = intent.getIntExtra("X", 0);
        yPosition = intent.getIntExtra("Y", 0);

        viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.popup_options_shortcuts, null, false);
        wholeMenuView = (RelativeLayout) viewGroup.findViewById(R.id.wholeMenuView);
        itemMenuView = (RelativeLayout) viewGroup.findViewById(R.id.itemMenuView);
        popupOptionsItems = (ListView) viewGroup.findViewById(R.id.popupOptionsItems);

        try {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(50);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            ArrayList<AdapterItemsFloatingShortcutsPopuiOptions> navDrawerItemsSaved = new ArrayList<AdapterItemsFloatingShortcutsPopuiOptions>();
            navDrawerItemsSaved.clear();

            List<String> popupItems = new ArrayList<String>();
            popupItems.clear();

            LayerDrawable popupItemsIcon = (LayerDrawable) getDrawable(R.drawable.draw_popup_shortcuts);
            Drawable popupItemsIconBack = popupItemsIcon.findDrawableByLayerId(R.id.backgroundTemporary);
            popupItemsIconBack.setTint(functionsClassLegacy.extractVibrantColor(functionsClassLegacy.applicationIcon(packageName)));

            switch (functionsClassLegacy.displaySection(xPosition, yPosition)) {
                case FunctionsClassLegacy.DisplaySection.TopLeft:
                    if (functionsClassLegacy.returnAPI() < 24) {
                        if (functionsClassLegacy.UsageStatsEnabled()) {
                            popupItems.add(getString(R.string.close));
                        }
                        popupItems.add(getString(R.string.pin));
                        popupItems.add(getString(R.string.unpin));
                        popupItems.add(getString(R.string.remove));
                    } else {
                        if (functionsClassLegacy.addFloatItItem()) {
                            popupItems.add(getString(R.string.floatIt));
                        }
                        popupItems.add(getString(R.string.splitIt));
                        if (functionsClassLegacy.UsageStatsEnabled()) {
                            popupItems.add(getString(R.string.close));
                        }
                        popupItems.add(getString(R.string.pin));
                        popupItems.add(getString(R.string.unpin));
                        popupItems.add(getString(R.string.remove));
                    }

                    itemMenuView.setX(xPosition);
                    itemMenuView.setY(yPosition + HW);

                    break;
                case FunctionsClassLegacy.DisplaySection.TopRight:
                    if (functionsClassLegacy.returnAPI() < 24) {
                        if (functionsClassLegacy.UsageStatsEnabled()) {
                            popupItems.add(getString(R.string.close));
                        }
                        popupItems.add(getString(R.string.pin));
                        popupItems.add(getString(R.string.unpin));
                        popupItems.add(getString(R.string.remove));
                    } else {
                        if (functionsClassLegacy.addFloatItItem()) {
                            popupItems.add(getString(R.string.floatIt));
                        }
                        popupItems.add(getString(R.string.splitIt));
                        if (functionsClassLegacy.UsageStatsEnabled()) {
                            popupItems.add(getString(R.string.close));
                        }
                        popupItems.add(getString(R.string.pin));
                        popupItems.add(getString(R.string.unpin));
                        popupItems.add(getString(R.string.remove));
                    }

                    itemMenuView.setX(xPosition - (functionsClassLegacy.DpToInteger(177) - HW));
                    itemMenuView.setY(yPosition + HW);

                    break;
                case FunctionsClassLegacy.DisplaySection.BottomLeft:
                    if (functionsClassLegacy.returnAPI() < 24) {
                        popupItems.add(getString(R.string.remove));
                        popupItems.add(getString(R.string.unpin));
                        popupItems.add(getString(R.string.pin));
                        if (functionsClassLegacy.UsageStatsEnabled()) {
                            popupItems.add(getString(R.string.close));
                        }
                    } else {
                        popupItems.add(getString(R.string.remove));
                        popupItems.add(getString(R.string.unpin));
                        popupItems.add(getString(R.string.pin));
                        if (functionsClassLegacy.UsageStatsEnabled()) {
                            popupItems.add(getString(R.string.close));
                        }
                        popupItems.add(getString(R.string.splitIt));
                        if (functionsClassLegacy.addFloatItItem()) {
                            popupItems.add(getString(R.string.floatIt));
                        }
                    }

                    itemMenuView.setX(xPosition);
                    itemMenuView.setY(yPosition - ((functionsClassLegacy.DpToInteger(37) * popupItems.size()) + functionsClassLegacy.DpToInteger(3)));

                    break;
                case FunctionsClassLegacy.DisplaySection.BottomRight:
                    if (functionsClassLegacy.returnAPI() < 24) {
                        popupItems.add(getString(R.string.remove));
                        popupItems.add(getString(R.string.unpin));
                        popupItems.add(getString(R.string.pin));
                        if (functionsClassLegacy.UsageStatsEnabled()) {
                            popupItems.add(getString(R.string.close));
                        }
                    } else {
                        popupItems.add(getString(R.string.remove));
                        popupItems.add(getString(R.string.unpin));
                        popupItems.add(getString(R.string.pin));
                        if (functionsClassLegacy.UsageStatsEnabled()) {
                            popupItems.add(getString(R.string.close));
                        }
                        popupItems.add(getString(R.string.splitIt));
                        if (functionsClassLegacy.addFloatItItem()) {
                            popupItems.add(getString(R.string.floatIt));
                        }
                    }

                    itemMenuView.setX(xPosition - (functionsClassLegacy.DpToInteger(177) - HW));
                    itemMenuView.setY(yPosition - ((functionsClassLegacy.DpToInteger(37) * popupItems.size()) + functionsClassLegacy.DpToInteger(3)));

                    break;
                default:
                    if (functionsClassLegacy.returnAPI() < 24) {
                        if (functionsClassLegacy.UsageStatsEnabled()) {
                            popupItems.add(getString(R.string.close));
                        }
                        popupItems.add(getString(R.string.pin));
                        popupItems.add(getString(R.string.unpin));
                        popupItems.add(getString(R.string.remove));
                    } else {
                        if (functionsClassLegacy.addFloatItItem()) {
                            popupItems.add(getString(R.string.floatIt));
                        }
                        popupItems.add(getString(R.string.splitIt));
                        if (functionsClassLegacy.UsageStatsEnabled()) {
                            popupItems.add(getString(R.string.close));
                        }
                        popupItems.add(getString(R.string.pin));
                        popupItems.add(getString(R.string.unpin));
                        popupItems.add(getString(R.string.remove));
                    }

                    itemMenuView.setX(xPosition);
                    itemMenuView.setY(yPosition + HW);

                    break;
            }

            for (int i = 0; i < popupItems.size(); i++) {
                navDrawerItemsSaved.add(new AdapterItemsFloatingShortcutsPopuiOptions(
                        popupItems.get(i),
                        popupItemsIcon));
            }


            if (className != null) {
                floatingShortcutsPopupOptionsAdapter =
                        new FloatingShortcutsPopupOptionsAdapter(getApplicationContext(), navDrawerItemsSaved,
                                classNameCommand, packageName, className, startIdCommand);
            } else {
                floatingShortcutsPopupOptionsAdapter =
                        new FloatingShortcutsPopupOptionsAdapter(getApplicationContext(), navDrawerItemsSaved,
                                classNameCommand, packageName, startIdCommand);
            }

            popupOptionsItems.setAdapter(floatingShortcutsPopupOptionsAdapter);


        } catch (Exception e) {
            e.printStackTrace();
        }

        layoutParams = functionsClassLegacy.splashRevealParams(
                0,
                0
        );
        windowManager.addView(viewGroup, layoutParams);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    functionsClassLegacy.circularRevealViewScreen(
                            wholeMenuView,
                            xPosition,
                            yPosition,
                            true
                    );

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            itemMenuView.setVisibility(View.VISIBLE);
                        }
                    }, 130);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 100);

        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopSelf();
                    }
                }, 113);
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionsClassLegacy = new FunctionsClassLegacy(getApplicationContext());

        if (functionsClassLegacy.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClassLegacy.customIconPackageName());
        }
    }

    @Override
    public void onDestroy() {
        try {
            functionsClassLegacy.circularRevealViewScreen(
                    wholeMenuView,
                    xPosition,
                    yPosition,
                    false
            );
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    windowManager.removeViewImmediate(viewGroup);
                }
            }, 333);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
