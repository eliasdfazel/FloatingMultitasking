/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.PopupDialogue;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.PopupFolderOptionAdapter;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

import java.util.ArrayList;

public class PopupOptionsFloatingFolders extends Service {

    FunctionsClassLegacy functionsClassLegacy;
    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;

    ViewGroup viewGroup;
    RelativeLayout wholeMenuView, itemMenuView;
    ListView popupOptionsItems;

    int HW, xPosition, yPosition, startIdCommand;
    int statusBarHeight, navBarHeight;
    String categoryName, classNameCommand,
            MODE = "Options";
    String[] packagesNames;

    PopupFolderOptionAdapter popupFolderOptionAdapter;
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
        MODE = intent.getStringExtra("MODE");
        classNameCommand = intent.getStringExtra("classNameCommand");
        categoryName = intent.getStringExtra("folderName");

        HW = intent.getIntExtra("HW", 0);
        xPosition = intent.getIntExtra("X", 0);
        yPosition = intent.getIntExtra("Y", 0);

        viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.popup_options_category, null, false);
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
            try {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(50);
            } catch (Exception e) {
                e.printStackTrace();
            }

            PublicVariable.splitPairPackage = categoryName;
            ArrayList<AdapterItems> navDrawerItemsSaved = new ArrayList<AdapterItems>();
            navDrawerItemsSaved.clear();

            if (MODE.equals("Options")) {
                String[] popupItems = null;
                Drawable[] popupItemsIcon;

                Drawable[] drawables = new Drawable[2];
                drawables[0] = getDrawable(R.drawable.ic_launcher);
                try {
                    drawables[0] = functionsClassLegacy.shapesDrawables().mutate();
                    drawables[0].setTint(PublicVariable.primaryColor);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    drawables[0].setAlpha(0);
                }
                drawables[1] = getDrawable(R.drawable.w_pref);
                LayerDrawable popupItemsIconCategory = new LayerDrawable(drawables);

                switch (functionsClassLegacy.displaySection(xPosition, yPosition)) {
                    case FunctionsClassLegacy.DisplaySection.TopLeft:
                        if (functionsClassLegacy.returnAPI() < 24) {
                            popupItems = new String[]{
                                    getString(R.string.pin_folder),
                                    getString(R.string.unpin_folder),
                                    getString(R.string.remove_folder) + " " + categoryName,
                            };
                            popupItemsIcon = new Drawable[]{
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory
                            };
                        } else {
                            popupItems = new String[]{
                                    getString(R.string.splitIt),
                                    getString(R.string.pin_folder),
                                    getString(R.string.unpin_folder),
                                    getString(R.string.remove_folder) + " " + categoryName,
                            };
                            popupItemsIcon = new Drawable[]{
                                    null,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory
                            };
                        }

                        itemMenuView.setX(xPosition);
                        itemMenuView.setY(yPosition + HW);
                        break;
                    case FunctionsClassLegacy.DisplaySection.TopRight:
                        if (functionsClassLegacy.returnAPI() < 24) {
                            popupItems = new String[]{
                                    getString(R.string.pin_folder),
                                    getString(R.string.unpin_folder),
                                    getString(R.string.remove_folder) + " " + categoryName,
                            };
                            popupItemsIcon = new Drawable[]{
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory
                            };
                        } else {
                            popupItems = new String[]{
                                    getString(R.string.splitIt),
                                    getString(R.string.pin_folder),
                                    getString(R.string.unpin_folder),
                                    getString(R.string.remove_folder) + " " + categoryName,
                            };
                            popupItemsIcon = new Drawable[]{
                                    null,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory
                            };
                        }

                        itemMenuView.setX(xPosition - (functionsClassLegacy.DpToInteger(213) - HW));
                        itemMenuView.setY(yPosition + HW);
                        break;
                    case FunctionsClassLegacy.DisplaySection.BottomLeft:
                        if (functionsClassLegacy.returnAPI() < 24) {
                            popupItems = new String[]{
                                    getString(R.string.remove_folder) + " " + categoryName,
                                    getString(R.string.unpin_folder),
                                    getString(R.string.pin_folder),
                            };
                            popupItemsIcon = new Drawable[]{
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory
                            };
                        } else {
                            popupItems = new String[]{
                                    getString(R.string.remove_folder) + " " + categoryName,
                                    getString(R.string.unpin_folder),
                                    getString(R.string.pin_folder),
                                    getString(R.string.splitIt),
                            };
                            popupItemsIcon = new Drawable[]{
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    null
                            };
                        }

                        itemMenuView.setX(xPosition);
                        itemMenuView.setY(yPosition - ((functionsClassLegacy.DpToInteger(52) * popupItems.length) + functionsClassLegacy.DpToInteger(3)));
                        break;
                    case FunctionsClassLegacy.DisplaySection.BottomRight:
                        if (functionsClassLegacy.returnAPI() < 24) {
                            popupItems = new String[]{
                                    getString(R.string.remove_folder) + " " + categoryName,
                                    getString(R.string.unpin_folder),
                                    getString(R.string.pin_folder),
                            };
                            popupItemsIcon = new Drawable[]{
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory
                            };
                        } else {
                            popupItems = new String[]{
                                    getString(R.string.remove_folder) + " " + categoryName,
                                    getString(R.string.unpin_folder),
                                    getString(R.string.pin_folder),
                                    getString(R.string.splitIt),
                            };
                            popupItemsIcon = new Drawable[]{
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    null
                            };
                        }

                        itemMenuView.setX(xPosition - (functionsClassLegacy.DpToInteger(213) - HW));
                        itemMenuView.setY(yPosition - ((functionsClassLegacy.DpToInteger(52) * popupItems.length) + functionsClassLegacy.DpToInteger(3)));
                        break;
                    default:
                        if (functionsClassLegacy.returnAPI() < 24) {
                            popupItems = new String[]{
                                    getString(R.string.pin_folder),
                                    getString(R.string.unpin_folder),
                                    getString(R.string.remove_folder) + " " + categoryName,
                            };
                            popupItemsIcon = new Drawable[]{
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory
                            };
                        } else {
                            popupItems = new String[]{
                                    getString(R.string.splitIt),
                                    getString(R.string.pin_folder),
                                    getString(R.string.unpin_folder),
                                    getString(R.string.remove_folder) + " " + categoryName,
                            };
                            popupItemsIcon = new Drawable[]{
                                    null,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory,
                                    popupItemsIconCategory
                            };
                        }

                        itemMenuView.setX(xPosition);
                        itemMenuView.setY(yPosition + HW);
                        break;
                }
                for (int i = 0; i < popupItems.length; i++) {
                    navDrawerItemsSaved.add(new AdapterItems(
                            popupItems[i],
                            categoryName,
                            popupItemsIcon[i]));
                }

                popupFolderOptionAdapter =
                        new PopupFolderOptionAdapter(getApplicationContext(),
                                navDrawerItemsSaved, categoryName, classNameCommand, startIdCommand);
            } else if (MODE.equals("AppsList")) {
                packagesNames = intent.getStringArrayExtra("PackagesNames");

                Drawable[] drawables = new Drawable[2];
                drawables[0] = getDrawable(R.drawable.ic_launcher);
                try {
                    drawables[0] = functionsClassLegacy.shapesDrawables().mutate();
                    drawables[0].setTint(PublicVariable.primaryColor);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    drawables[0].setAlpha(0);
                }
                drawables[1] = getDrawable(R.drawable.w_pref);
                LayerDrawable popupItemsIcon = new LayerDrawable(drawables);

                switch (functionsClassLegacy.displaySection(xPosition, yPosition)) {
                    case FunctionsClassLegacy.DisplaySection.TopLeft:
                        for (String packageName : packagesNames) {
                            navDrawerItemsSaved.add(new AdapterItems(
                                    functionsClassLegacy.applicationName(packageName),
                                    packageName,
                                    functionsClassLegacy.customIconsEnable() ?
                                            loadCustomIcons.getDrawableIconForPackage(packageName, functionsClassLegacy.shapedAppIcon(packageName))
                                            :
                                            functionsClassLegacy.shapedAppIcon(packageName)));
                        }
                        navDrawerItemsSaved.add(new AdapterItems(
                                getString(R.string.edit_folder) + " " + categoryName,
                                getPackageName(),
                                popupItemsIcon));

                        itemMenuView.setX(xPosition);
                        itemMenuView.setY(yPosition + HW + functionsClassLegacy.DpToInteger(19));
                        break;
                    case FunctionsClassLegacy.DisplaySection.TopRight:
                        for (String packageName : packagesNames) {
                            navDrawerItemsSaved.add(new AdapterItems(
                                    functionsClassLegacy.applicationName(packageName),
                                    packageName,
                                    functionsClassLegacy.customIconsEnable() ?
                                            loadCustomIcons.getDrawableIconForPackage(packageName, functionsClassLegacy.shapedAppIcon(packageName))
                                            :
                                            functionsClassLegacy.shapedAppIcon(packageName)));
                        }
                        navDrawerItemsSaved.add(new AdapterItems(
                                getString(R.string.edit_folder) + " " + categoryName,
                                getPackageName(),
                                popupItemsIcon));

                        itemMenuView.setX(xPosition - (functionsClassLegacy.DpToInteger(213) - HW));
                        itemMenuView.setY(yPosition + HW + functionsClassLegacy.DpToInteger(19));
                        break;
                    case FunctionsClassLegacy.DisplaySection.BottomLeft:
                        navDrawerItemsSaved.add(new AdapterItems(
                                getString(R.string.edit_folder) + " " + categoryName,
                                getPackageName(),
                                popupItemsIcon));
                        for (String packageName : packagesNames) {
                            navDrawerItemsSaved.add(new AdapterItems(
                                    functionsClassLegacy.applicationName(packageName),
                                    packageName,
                                    functionsClassLegacy.customIconsEnable() ?
                                            loadCustomIcons.getDrawableIconForPackage(packageName, functionsClassLegacy.shapedAppIcon(packageName))
                                            :
                                            functionsClassLegacy.shapedAppIcon(packageName)));
                        }

                        itemMenuView.setX(xPosition);
                        itemMenuView.setY(yPosition - (functionsClassLegacy.DpToInteger(191)));
                        break;
                    case FunctionsClassLegacy.DisplaySection.BottomRight:
                        navDrawerItemsSaved.add(new AdapterItems(
                                getString(R.string.edit_folder) + " " + categoryName,
                                getPackageName(),
                                popupItemsIcon));
                        for (String packageName : packagesNames) {
                            navDrawerItemsSaved.add(new AdapterItems(
                                    functionsClassLegacy.applicationName(packageName),
                                    packageName,
                                    functionsClassLegacy.customIconsEnable() ?
                                            loadCustomIcons.getDrawableIconForPackage(packageName, functionsClassLegacy.shapedAppIcon(packageName))
                                            :
                                            functionsClassLegacy.shapedAppIcon(packageName)));
                        }

                        itemMenuView.setX(xPosition - (functionsClassLegacy.DpToInteger(213) - HW));
                        itemMenuView.setY(yPosition - (functionsClassLegacy.DpToInteger(191)));
                        break;
                    default:
                        for (String packageName : packagesNames) {
                            navDrawerItemsSaved.add(new AdapterItems(
                                    functionsClassLegacy.applicationName(packageName),
                                    packageName,
                                    functionsClassLegacy.customIconsEnable() ?
                                            loadCustomIcons.getDrawableIconForPackage(packageName, functionsClassLegacy.shapedAppIcon(packageName))
                                            :
                                            functionsClassLegacy.shapedAppIcon(packageName)));
                        }
                        navDrawerItemsSaved.add(new AdapterItems(
                                getString(R.string.edit_folder) + " " + categoryName,
                                getPackageName(),
                                popupItemsIcon));

                        itemMenuView.setX(xPosition);
                        itemMenuView.setY(yPosition + HW + functionsClassLegacy.DpToInteger(19));
                        break;
                }

                popupFolderOptionAdapter =
                        new PopupFolderOptionAdapter(getApplicationContext(),
                                navDrawerItemsSaved, categoryName, classNameCommand, startIdCommand, xPosition, yPosition, HW);
            }

            popupOptionsItems.setAdapter(popupFolderOptionAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        layoutParams = functionsClassLegacy.splashRevealParams(
                0,
                0
        );
        windowManager.addView(viewGroup, layoutParams);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    functionsClassLegacy.circularRevealViewScreen(
                            wholeMenuView,
                            xPosition,
                            yPosition,
                            true
                    );

                    new Handler().postDelayed(new Runnable() {
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
                new Handler().postDelayed(new Runnable() {
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
            new Handler().postDelayed(new Runnable() {
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
