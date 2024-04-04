/*
 * Copyright © 2022 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/19/22, 3:40 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions;

import static android.content.Context.ACCESSIBILITY_SERVICE;
import static android.content.Context.RECEIVER_NOT_EXPORTED;
import static android.content.Context.VIBRATOR_SERVICE;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.ColorUtils;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.palette.graphics.Palette;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.BuildConfig;
import net.geekstools.floatshort.PRO.Checkpoint;
import net.geekstools.floatshort.PRO.Configurations;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.FloatingFolders;
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations;
import net.geekstools.floatshort.PRO.Folders.PopupDialogue.PopupOptionsFloatingFolders;
import net.geekstools.floatshort.PRO.HomeScreen.HomeScreenShortcuts;
import net.geekstools.floatshort.PRO.Notifications.NotificationListener;
import net.geekstools.floatshort.PRO.Notifications.PopupAdapter.PopupShortcutsNotification;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.UserInterfaceExtraData;
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Fingerprint.AuthenticationFingerprint;
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.AuthenticationCallback;
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions;
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder;
import net.geekstools.floatshort.PRO.Shortcuts.ApplicationsViewPhone;
import net.geekstools.floatshort.PRO.Shortcuts.PopupDialogue.PopupOptionsFloatingShortcuts;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems;
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.InitializeInAppBilling;
import net.geekstools.floatshort.PRO.Utils.InAppStore.DigitalAssets.Items.InAppBillingData;
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver;
import net.geekstools.floatshort.PRO.Utils.LaunchPad.OpenApplicationsLaunchPad;
import net.geekstools.floatshort.PRO.Utils.RemoteTask.RemoteController;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Utils.UI.Splash.FloatingSplash;
import net.geekstools.floatshort.PRO.Widgets.FloatingServices.WidgetUnlimitedFloating;
import net.geekstools.imageview.customshapes.ShapesImage;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class FunctionsClassLegacy {

    BitmapExtractor bitmapExtractor;
    Context context;

    public FunctionsClassLegacy(@NonNull Context context) {

        this.context = context;

        bitmapExtractor = new BitmapExtractor(context);

    }

    public static boolean ComponentEnabled(PackageManager packageManager, String packageName, String className) {
        ComponentName componentName = new ComponentName(packageName, className);
        int componentEnabledSetting = packageManager.getComponentEnabledSetting(componentName);

        switch (componentEnabledSetting) {
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
                return false;
            case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                return true;
            case PackageManager.COMPONENT_ENABLED_STATE_DEFAULT:
            default:
                // We need to get the application info to get the component's default state
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES
                            | PackageManager.GET_RECEIVERS
                            | PackageManager.GET_SERVICES
                            | PackageManager.GET_PROVIDERS
                            | PackageManager.GET_DISABLED_COMPONENTS);

                    List<ComponentInfo> components = new ArrayList<>();
                    if (packageInfo.activities != null)
                        Collections.addAll(components, packageInfo.activities);
                    if (packageInfo.services != null)
                        Collections.addAll(components, packageInfo.services);
                    if (packageInfo.providers != null)
                        Collections.addAll(components, packageInfo.providers);

                    for (ComponentInfo componentInfo : components) {
                        if (componentInfo.name.equals(className)) {
                            return componentInfo.isEnabled();
                        }
                    }

                    // the component is not declared in the AndroidManifest
                    return false;
                } catch (PackageManager.NameNotFoundException e) {
                    // the package isn't installed on the device
                    return false;
                }
        }
    }

    /*Unlimited Shortcuts Function*/
    public void saveUnlimitedShortcutsService(String packageName) {

        FileIO fileIO = new FileIO(context);

        boolean duplicated = false;

        String fileName = ".uFile";
        File uFile = context.getFileStreamPath(".uFile");

        if (!uFile.exists()) {
            fileIO.saveFileAppendLine(fileName, packageName);
        } else if (uFile.exists()) {
            int countLine = fileIO.fileLinesCounter(fileName);
            String[] contentLine = new String[countLine];
            contentLine = fileIO.readFileLinesAsArray(fileName);
            for (String aContentLine : contentLine) {
                if (aContentLine.equals(packageName)) {
                    duplicated = true;
                    break;
                }
            }
            if (duplicated == false) {
                fileIO.saveFileAppendLine(fileName, packageName);
            }
        }

    }

    public void PopupOptionShortcuts(View anchorView, final String packageName, String classNameCommand, int startId, int X, int Y) {
        try {
            Intent popupOptionsShortcuts = new Intent(context, PopupOptionsFloatingShortcuts.class);
            popupOptionsShortcuts.putExtra("PackageName", packageName);
            popupOptionsShortcuts.putExtra("classNameCommand", classNameCommand);
            popupOptionsShortcuts.putExtra("startIdCommand", startId);
            popupOptionsShortcuts.putExtra("X", X);
            popupOptionsShortcuts.putExtra("Y", Y);
            popupOptionsShortcuts.putExtra("HW", anchorView.getWidth());
            context.startService(popupOptionsShortcuts);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("Hide_PopupListView_Shortcuts" + context.getPackageName());
            final BroadcastReceiver counterReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("Hide_PopupListView_Shortcuts" + context.getPackageName())) {
                        context.stopService(new Intent(context, PopupOptionsFloatingShortcuts.class));
                    }
                }
            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.registerReceiver(counterReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
            } else {
                context.registerReceiver(counterReceiver, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void PopupOptionShortcuts(View anchorView, final String packageName, String className, String classNameCommand,
                                     int startId,
                                     int X, int Y) {
        try {
            Intent popupOptionsShortcuts = new Intent(context, PopupOptionsFloatingShortcuts.class);
            popupOptionsShortcuts.putExtra("PackageName", packageName);
            popupOptionsShortcuts.putExtra("ClassName", className);
            popupOptionsShortcuts.putExtra("classNameCommand", classNameCommand);
            popupOptionsShortcuts.putExtra("startIdCommand", startId);
            popupOptionsShortcuts.putExtra("X", X);
            popupOptionsShortcuts.putExtra("Y", Y);
            popupOptionsShortcuts.putExtra("HW", anchorView.getWidth());
            context.startService(popupOptionsShortcuts);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("Hide_PopupListView_Shortcuts" + context.getPackageName());
            final BroadcastReceiver counterReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("Hide_PopupListView_Shortcuts" + context.getPackageName())) {
                        context.stopService(new Intent(context, PopupOptionsFloatingShortcuts.class));
                    }
                }
            };
            try {
                context.unregisterReceiver(counterReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.registerReceiver(counterReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
                } else {
                    context.registerReceiver(counterReceiver, intentFilter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Category Function*/
    @Deprecated
    public void runUnlimitedFolderService(String categoryName) {
        if (Build.VERSION.SDK_INT > 22) {
            if (!Settings.canDrawOverlays(context)) {
                context.startActivity(new Intent(context, Checkpoint.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return;
            }
        }
        try {
            PublicVariable.allFloatingCounter++;
            PublicVariable.floatingFolderCounter_Folder++;
            PublicVariable.floatingFolderCounter++;
            PublicVariable.floatingFoldersList.add(PublicVariable.floatingFolderCounter, categoryName);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();

            PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter + 1;
            PublicVariable.floatingFolderCounter_Folder = PublicVariable.floatingFolderCounter_Folder + 1;
            PublicVariable.floatingFolderCounter = PublicVariable.floatingFolderCounter + 1;
            PublicVariable.floatingFoldersList.add(PublicVariable.floatingFolderCounter, categoryName);
        }

        Intent c = new Intent(context, FloatingFolders.class);
        c.putExtra("folderName", categoryName);
        c.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(c);
        if (PublicVariable.allFloatingCounter == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, BindServices.class));
            } else {
                context.startService(new Intent(context, BindServices.class));
            }
        }
    }

    public void PopupAppListFolder(View anchorView,
                                   String categoryName, String[] packagesName, String classNameCommand, int startIdCommand, int X, int Y, int HW) {
        if (!context.getFileStreamPath(categoryName).exists() && !context.getFileStreamPath(categoryName).isFile()) {
            return;
        }

        try {
            Intent popupOptionsFloatingCategory = new Intent(context, PopupOptionsFloatingFolders.class);
            popupOptionsFloatingCategory.putExtra("MODE", "AppsList");
            popupOptionsFloatingCategory.putExtra("folderName", categoryName);
            popupOptionsFloatingCategory.putExtra("PackagesNames", packagesName);
            popupOptionsFloatingCategory.putExtra("classNameCommand", classNameCommand);
            popupOptionsFloatingCategory.putExtra("startIdCommand", startIdCommand);
            popupOptionsFloatingCategory.putExtra("X", X);
            popupOptionsFloatingCategory.putExtra("Y", Y);
            popupOptionsFloatingCategory.putExtra("HW", HW);
            context.startService(popupOptionsFloatingCategory);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("Hide_PopupListView_Category" + context.getPackageName());
            final BroadcastReceiver counterReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("Hide_PopupListView_Category" + context.getPackageName())) {
                        context.stopService(new Intent(context, PopupOptionsFloatingFolders.class));
                    }
                }
            };
            try {
                context.unregisterReceiver(counterReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.registerReceiver(counterReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
                } else {
                    context.registerReceiver(counterReceiver, intentFilter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void PopupOptionFolder(View anchorView,
                                  String categoryName, String classNameCommand, int startIdCommand, int X, int Y) {
        if (!context.getFileStreamPath(categoryName).exists() && !context.getFileStreamPath(categoryName).isFile()) {
            return;
        }
        try {
            Intent popupOptionsFloatingCategory = new Intent(context, PopupOptionsFloatingFolders.class);
            popupOptionsFloatingCategory.putExtra("MODE", "Options");
            popupOptionsFloatingCategory.putExtra("folderName", categoryName);
            popupOptionsFloatingCategory.putExtra("classNameCommand", classNameCommand);
            popupOptionsFloatingCategory.putExtra("startIdCommand", startIdCommand);
            popupOptionsFloatingCategory.putExtra("X", X);
            popupOptionsFloatingCategory.putExtra("Y", Y);
            popupOptionsFloatingCategory.putExtra("HW", anchorView.getWidth());
            context.startService(popupOptionsFloatingCategory);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("Hide_PopupListView_Category" + context.getPackageName());
            final BroadcastReceiver counterReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("Hide_PopupListView_Category" + context.getPackageName())) {
                        context.stopService(new Intent(context, PopupOptionsFloatingFolders.class));
                    }
                }
            };
            try {
                context.unregisterReceiver(counterReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.registerReceiver(counterReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
                } else {
                    context.registerReceiver(counterReceiver, intentFilter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Floating Widgets Function*/
    public void runUnlimitedWidgetService(int WidgetId, String widgetLabel) {
        if (!Settings.canDrawOverlays(context)) {
            context.startActivity(new Intent(context, Checkpoint.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            return;
        }

        try {
            PublicVariable.allFloatingCounter++;
            PublicVariable.floatingWidgetsCounter++;
            PublicVariable.floatingWidgetsIdList.add(PublicVariable.floatingWidgetsCounter, WidgetId);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();

            PublicVariable.allFloatingCounter = PublicVariable.allFloatingCounter + 1;
            PublicVariable.floatingWidgetsCounter = PublicVariable.floatingWidgetsCounter + 1;
            PublicVariable.floatingWidgetsIdList.add(PublicVariable.floatingWidgetsCounter, WidgetId);
        }

        Intent w = new Intent(context, WidgetUnlimitedFloating.class);
        w.putExtra("WidgetId", WidgetId);
        w.putExtra("WidgetLabel", widgetLabel);
        w.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(w);

        if (PublicVariable.allFloatingCounter == 1) {
            if (Build.VERSION.SDK_INT < 26) {
                context.startService(new Intent(context, BindServices.class));
            } else {
                context.startForegroundService(new Intent(context, BindServices.class));
            }
        }
    }

    /*Floating Notification*/
    public void PopupNotificationShortcuts(View view, final String notificationPackage, String className, int startId, int iconColor, int X, int Y, int HW) {
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(50);

        FileIO fileIO = new FileIO(context);

        ArrayList<AdapterItems> navDrawerItemsSaved = new ArrayList<AdapterItems>();
        try {
            int W = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    221,
                    context.getResources().getDisplayMetrics());

            navDrawerItemsSaved.clear();

            LoadCustomIcons loadCustomIcons = null;
            if (customIconsEnable()) {
                loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
            }

            String[] packageContentTime = fileIO.readFileLinesAsArray(notificationPackage + "_" + "Notification" + "Package");
            for (String notificationTime : packageContentTime) {
                navDrawerItemsSaved.add(
                        new AdapterItems(
                                notificationTime,
                                notificationPackage,
                                applicationName(notificationPackage),
                                fileIO.readFile(notificationTime + "_" + "Notification" + "Title"),
                                fileIO.readFile(notificationTime + "_" + "Notification" + "Text"),
                                customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(notificationPackage, shapedAppIcon(notificationPackage)) : shapedAppIcon(notificationPackage),
                                shapedNotificationUser(Drawable.createFromPath(context.getFileStreamPath(notificationTime + "_" + "Notification" + "Icon").getPath())),
                                fileIO.readFile(notificationTime + "_" + "Notification" + "Key"),
                                PublicVariable.notificationIntent.get(notificationTime)
                        )
                );
            }
            PopupShortcutsNotification popupShortcutsNotification =
                    new PopupShortcutsNotification(context,
                            navDrawerItemsSaved,
                            className, notificationPackage, iconColor,
                            startId,
                            X, Y, HW);

            final ListPopupWindow listPopupWindow = new ListPopupWindow(context);
            listPopupWindow.setAdapter(popupShortcutsNotification);
            listPopupWindow.setAnchorView(view);
            listPopupWindow.setWidth(W);
            listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
            listPopupWindow.setModal(true);
            listPopupWindow.setBackgroundDrawable(null);
            try {
                listPopupWindow.show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (returnAPI() < 23) {
                    if (listPopupWindow.isShowing()) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listPopupWindow.dismiss();
                            }
                        }, 2000);
                    }
                }
            }

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("Hide_PopupListView_Shortcuts_Notification" + context.getPackageName());
            final ListPopupWindow finalListPopupWindow = listPopupWindow;
            final BroadcastReceiver counterReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("Hide_PopupListView_Shortcuts_Notification" + context.getPackageName())) {
                        if (finalListPopupWindow.isShowing()) {
                            finalListPopupWindow.dismiss();
                        }
                    }
                }
            };
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.registerReceiver(counterReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
                } else {
                    context.registerReceiver(counterReceiver, intentFilter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    context.unregisterReceiver(counterReceiver);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Checkpoint Dialogue Functions*/
    public void NotificationAccessService(Activity activity, final SwitchPreference switchPreference) {
        context = activity.getApplicationContext();
        if (returnAPI() < 23) {
            //"android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
            Intent notification = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            notification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(notification);

            return;
        }

        AlertDialog.Builder alertDialog = null;
        if (PublicVariable.themeLightDark == true) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light);
        } else if (PublicVariable.themeLightDark == false) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Dark);
        }
        alertDialog.setTitle(Html.fromHtml(context.getResources().getString(R.string.notificationTitle), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setMessage(Html.fromHtml(context.getResources().getString(R.string.notificationDesc), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setIcon(context.getDrawable(R.drawable.ic_launcher));
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(context.getString(R.string.grant), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent notification = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                notification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(notification);

                dialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                try {
                    switchPreference.setChecked(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.show();
    }

    public void AccessibilityServiceDialogue(Activity activity, final SwitchPreference switchPreference) {
        context = activity.getApplicationContext();
        if (returnAPI() < 23) {
            Intent notification = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            notification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(notification);

            return;
        }
        AlertDialog.Builder alertDialog = null;
        if (PublicVariable.themeLightDark == true) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light);
        } else if (PublicVariable.themeLightDark == false) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Dark);
        }
        alertDialog.setTitle(Html.fromHtml(context.getResources().getString(R.string.observeTitle), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setMessage(Html.fromHtml(context.getResources().getString(R.string.observeDesc), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setIcon(context.getDrawable(R.drawable.ic_launcher));
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(context.getString(R.string.agree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(context.getString(R.string.skipNow), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                try {
                    switchPreference.setChecked(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.show();
    }

    public void AccessibilityServiceDialogue(final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light);
        if (PublicVariable.themeLightDark == true) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light);
        } else if (PublicVariable.themeLightDark == false) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Dark);
        }
        alertDialog.setTitle(Html.fromHtml(context.getResources().getString(R.string.observeTitle), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setMessage(Html.fromHtml(context.getResources().getString(R.string.observeDesc), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setIcon(context.getDrawable(R.drawable.ic_launcher));
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(context.getString(R.string.agree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(context.getString(R.string.skipNow), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.finish();
            }
        });
        alertDialog.show();
    }

    public void UsageAccess(final Activity activity, final SwitchPreference switchPreference) {
        context = activity.getApplicationContext();
        if (returnAPI() < 23) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            return;
        }
        AlertDialog.Builder alertDialog = null;
        if (PublicVariable.themeLightDark == true) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light);
        } else if (PublicVariable.themeLightDark == false) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Dark);
        }
        alertDialog.setTitle(context.getString(R.string.smartTitle));
        alertDialog.setMessage(Html.fromHtml(context.getString(R.string.smartPermission), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setIcon(context.getDrawable(R.drawable.ic_launcher));
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(context.getString(R.string.grant), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                dialog.dismiss();

                activity.finish();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                switchPreference.setChecked(false);
            }
        });
        alertDialog.show();
    }

    public void RemoteRecovery(Activity activity) {
        context = activity.getApplicationContext();
        AlertDialog.Builder alertDialog = null;
        if (PublicVariable.themeLightDark == true) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light);
        } else if (PublicVariable.themeLightDark == false) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Dark);
        }
        alertDialog.setTitle(Html.fromHtml(context.getResources().getString(R.string.boot), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setMessage(Html.fromHtml(context.getResources().getString(R.string.bootPermission), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (!UsageStatsEnabled()) {
            alertDialog.setNeutralButton(context.getString(R.string.grant), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    dialog.dismiss();
                }
            });
        }
        alertDialog.show();
    }

    public void FreeFormInformation(final Activity activity, final SwitchPreference switchPreference) {
        context = activity.getApplicationContext();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Dark);
        if (PublicVariable.themeLightDark == true) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Light);
        } else if (PublicVariable.themeLightDark == false) {
            alertDialog = new AlertDialog.Builder(activity, R.style.GeeksEmpire_Dialogue_Dark);
        }
        alertDialog.setTitle(Html.fromHtml(context.getResources().getString(R.string.freeFormTitle), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setMessage(Html.fromHtml(context.getResources().getString(R.string.freeFormInfo), Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setIcon(context.getDrawable(R.drawable.ic_launcher));
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(context.getString(R.string.grantFreeForm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                switchPreference.setChecked(true);
            }
        });
        alertDialog.setNeutralButton(context.getString(R.string.support), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                switchPreference.setChecked(false);
                ContactSupport(activity);
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (freeFormSupport(context)) {
                    switchPreference.setChecked(true);
                } else {
                    switchPreference.setChecked(false);
                }
            }
        });
        alertDialog.show();
    }
    public void ContactSupport(final Activity activity) {

        Intent a = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.link_contacts)));
        a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(a);

    }

    public void litePreferenceConfirm(Activity instanceOfActivity) {
        AlertDialog.Builder alertDialog = null;
        if (PublicVariable.themeLightDark == true) {
            alertDialog = new AlertDialog.Builder(instanceOfActivity, R.style.GeeksEmpire_Dialogue_Light);
        } else if (PublicVariable.themeLightDark == false) {
            alertDialog = new AlertDialog.Builder(instanceOfActivity, R.style.GeeksEmpire_Dialogue_Dark);
        }
        alertDialog.setTitle(Html.fromHtml("<small>" + context.getString(R.string.liteTitle) + "</small>", Html.FROM_HTML_MODE_COMPACT));
        alertDialog.setMessage(Html.fromHtml(context.getString(R.string.liteDesc), Html.FROM_HTML_MODE_COMPACT));

        LayerDrawable drawPrefLite = (LayerDrawable) context.getDrawable(R.drawable.draw_pref).mutate();
        Drawable backPrefLite = drawPrefLite.findDrawableByLayerId(R.id.backgroundTemporary).mutate();
        Drawable drawablePrefLite = drawPrefLite.findDrawableByLayerId(R.id.wPref);
        if (PublicVariable.themeLightDark == true) {
            backPrefLite.setTint(context.getColor(R.color.dark));
            drawablePrefLite.setTint(context.getColor(R.color.light));
        } else if (PublicVariable.themeLightDark == false) {
            backPrefLite.setTint(context.getColor(R.color.light));
            drawablePrefLite.setTint(context.getColor(R.color.dark));
        }
        alertDialog.setIcon(drawPrefLite);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        liteAppPreferences(instanceOfActivity);
                    }
                }, 333);
                Runtime.getRuntime().gc();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /*Checkpoint Functions*/
    public boolean NotificationAccess() {
        boolean notificationAccess = false;
        try {
            notificationAccess = (Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners")).contains(context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
                for (String packageName : packageNames) {
                    if (packageName.contains(context.getPackageName())) {
                        return true;
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return notificationAccess;
    }

    public boolean NotificationListenerRunning() {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (NotificationListener.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Deprecated
    public boolean UsageStatsEnabled() {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOp("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
        return (mode == AppOpsManager.MODE_ALLOWED);
    }

    public boolean AccessibilityServiceEnabled() {
        ComponentName expectedComponentName = new ComponentName(context, InteractionObserver.class);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    public void navigateToClass(Class returnClass, final Activity activityToFinish) {
        context.startActivity(new Intent(context, returnClass)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                activityToFinish.finish();
            }
        }, 313);
    }

    public void navigateToClass(Activity instanceOfActivity, Class returnClass, ActivityOptions activityOptions) {
        Intent intentOverride = new Intent(context, returnClass);
        if (returnClass.getSimpleName().equals(ApplicationsViewPhone.class.getSimpleName())) {
            intentOverride.putExtra("frequentApps", PublicVariable.frequentlyUsedApps);
            intentOverride.putExtra("frequentAppsNumbers", PublicVariable.freqLength);
        }
        instanceOfActivity.startActivity(intentOverride, activityOptions.toBundle());
    }

    public void overrideBackPressToMain(Activity instanceOfActivity, final Activity activityToFinish) throws Exception {
        if (readPreference("OpenMode", "openClassName", ApplicationsViewPhone.class.getSimpleName()).equals(FoldersConfigurations.class.getSimpleName())) {
            Intent categoryInten = new Intent(context, FoldersConfigurations.class);
            instanceOfActivity.startActivity(categoryInten);
        } else {
            Intent hybridViewOff = new Intent(context, ApplicationsViewPhone.class);
            hybridViewOff.putExtra("frequentApps", PublicVariable.frequentlyUsedApps);
            hybridViewOff.putExtra("frequentAppsNumbers", PublicVariable.freqLength);
            hybridViewOff.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
            instanceOfActivity.startActivity(hybridViewOff);
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                activityToFinish.finish();
            }
        }, 313);
    }

    public void overrideBackPressToShortcuts(Activity instanceOfActivity, final Activity activityToFinish) throws Exception {
        Intent hybridViewOff = new Intent(context, ApplicationsViewPhone.class);
        hybridViewOff.putExtra("frequentApps", PublicVariable.frequentlyUsedApps);
        hybridViewOff.putExtra("frequentAppsNumbers", PublicVariable.freqLength);
        hybridViewOff.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        instanceOfActivity.startActivity(hybridViewOff);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                activityToFinish.finish();
            }
        }, 313);
    }


    public String applicationName(String packageName) {
        String Name = "null";
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(packageName, 0);
            Name = packageManager.getApplicationLabel(app).toString();
        } catch (Exception e) {
            e.printStackTrace();
            Name = "null";
        }
        return Name;
    }

    public String activityLabel(ActivityInfo activityInfo) {

        String appName = context.getString(R.string.app_name);

        try {
            appName = activityInfo.loadLabel(context.getPackageManager()).toString();
        } catch (Exception e) {
            e.printStackTrace();

            appName = applicationName(activityInfo.packageName);
        }

        return appName;
    }

    public String applicationVersionName(String packageName) {
        String Version = "0";
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            Version = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Version;
    }

    public int applicationVersionCode(String packageName) {
        int VersionCode = 0;
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            VersionCode = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return VersionCode;
    }


    public boolean SettingServiceRunning(Class aClass) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (aClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendInteractionObserverEvent(View view, String packageName, int accessibilityEvent, int accessibilityAction) {
        if (!AccessibilityServiceEnabled()) {
            Intent splitIntent = new Intent();
            splitIntent.putExtra(context.getString(R.string.splitIt), packageName);
            splitIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(splitIntent);
        } else {
            AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(ACCESSIBILITY_SERVICE);
            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setSource(view);
            event.setEventType(accessibilityEvent);
            event.setAction(accessibilityAction);
            event.setPackageName(packageName);
            event.getText().add(context.getPackageName());
            accessibilityManager.sendAccessibilityEvent(event);
        }
    }

    public boolean FreeForm() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("freeForm", false);
    }

    public boolean ControlPanel() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("stable", true);
    }

    /*System Checkpoint Functions*/
    public void CheckSystemRAM(Activity activityToHandle) {
        try {
            ActivityManager activityManager = (ActivityManager) activityToHandle.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            if (activityManager != null) {
                activityManager.getMemoryInfo(memoryInfo);

                if (memoryInfo.lowMemory) {
                    activityToHandle.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int returnAPI() {
        return Build.VERSION.SDK_INT;
    }

    public boolean appIsInstalled(String packageName) {
        PackageManager packageManager = context.getPackageManager();

        boolean appInstalled = false;
        try {
            packageManager.getPackageInfo(packageName, 0);

            appInstalled = true;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

            appInstalled = false;

        } catch (Exception e) {
            e.printStackTrace();

            appInstalled = false;
        }

        return appInstalled;
    }

    public void uninstallApp(String packageName) {
        Uri packageUri = Uri.parse("package:" + packageName);
        Intent uninstallIntent =
                new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }

    public boolean ifSystem(String packageName) {
        boolean ifSystem = false;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo targetPkgInfo = packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            PackageInfo sys = packageManager.getPackageInfo(
                    "android", PackageManager.GET_SIGNATURES);
            ifSystem = (targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0]
                    .equals(targetPkgInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            ifSystem = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ifSystem;
    }

    public boolean isDefaultLauncher(String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String defaultLauncherStr = defaultLauncher.activityInfo.packageName;
        return defaultLauncherStr.equals(packageName);
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String aWord) {
        if (aWord == null || aWord.length() == 0) {
            return "";
        }

        char first = aWord.charAt(0);
        if (Character.isUpperCase(first)) {
            return aWord;
        } else {
            return Character.toUpperCase(first) + aWord.substring(1);
        }
    }

    public String getCountryIso() {
        String countryISO = "Undefined";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            countryISO = telephonyManager.getSimCountryIso();
            if (countryISO.length() < 2) {
                countryISO = "Undefined";
            }
        } catch (Exception e) {
            e.printStackTrace();
            countryISO = "Undefined";
        }
        return countryISO;
    }

    /*FreeForm*/
    public boolean addFloatItItem() {
        boolean forceFloatIt = false;
        if (freeFormSupport(context)) {
            forceFloatIt = true;
        }
        return forceFloatIt;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public boolean freeFormSupport(Context context) {
        return (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FREEFORM_WINDOW_MANAGEMENT)) ||
                (Settings.Global.getInt(context.getContentResolver(), "enable_freeform_support", 0) != 0) &&
                        (Settings.Global.getInt(context.getContentResolver(), "force_resizable_activities", 0) != 0);
    }

    public String getWindowingModeMethodName() {
        if (returnAPI() >= 28)
            return "setLaunchWindowingMode";
        else
            return "setLaunchStackId";
    }

    private int getFreeformWindowModeId() {
        if (returnAPI() >= 28) {
            return WindowMode.WINDOWING_MODE_FREEFORM;
        } else {
            return WindowMode.FREEFORM_WORKSPACE_STACK_ID;
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    public static void allowReflection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("");
        }
    }

    /*Open Functions*/
    public boolean canLaunch(String packageName) {
        return (context.getPackageManager().getLaunchIntentForPackage(packageName) != null);
    }

    public void openApplicationFromActivity(Activity instanceOfActivity, String packageName) {
        if (appIsInstalled(packageName) == true) {
            try {
                Toast(applicationName(packageName), Gravity.BOTTOM);

                Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(packageName);
                launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                instanceOfActivity.startActivity(launchIntentForPackage);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getString(R.string.not_install), Toast.LENGTH_LONG).show();
                Intent playStore = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(context.getString(R.string.play_store_link) + packageName));
                playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                instanceOfActivity.startActivity(playStore);
            }
        } else {
            Toast.makeText(context, context.getString(R.string.not_install), Toast.LENGTH_LONG).show();
            Intent playStore = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.play_store_link) + packageName));
            playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            instanceOfActivity.startActivity(playStore);
        }
    }

    public void openApplicationFromActivity(Activity instanceOfActivity, String packageName, String className) {
        if (appIsInstalled(packageName) == true) {
            try {
                Toast(String.valueOf(context.getPackageManager().getActivityInfo(new ComponentName(packageName, className), 0).loadLabel(context.getPackageManager())), Gravity.BOTTOM);

                Intent openAlias = new Intent();
                openAlias.setClassName(packageName, className);
                openAlias.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                instanceOfActivity.startActivity(openAlias);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getString(R.string.not_install), Toast.LENGTH_LONG).show();
                Intent playStore = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(context.getString(R.string.play_store_link) + packageName));
                playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                instanceOfActivity.startActivity(playStore);
            }
        } else {
            Toast.makeText(context, context.getString(R.string.not_install), Toast.LENGTH_LONG).show();
            Intent playStore = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.play_store_link) + packageName));
            playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            instanceOfActivity.startActivity(playStore);
        }
    }

    public void openApplicationFreeForm(final String PackageName, final int leftPositionX/*X*/, final int rightPositionX/*displayX/2*/, final int topPositionY/*Y*/, final int bottomPositionY/*displayY/2*/) {
        //Enable Developer Option & Turn ON 'Force Activities to be Resizable'
        //adb shell settings put global enable_freeform_support 1
        //adb shell settings put global force_resizable_activities 1
        if (returnAPI() < 28) {
            Intent homeScreen = new Intent(Intent.ACTION_MAIN);
            homeScreen.addCategory(Intent.CATEGORY_HOME);
            homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(homeScreen);
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityOptions activityOptions = ActivityOptions.makeBasic();
                try {
                    allowReflection();

                    Method method = ActivityOptions.class.getMethod(getWindowingModeMethodName(), int.class);
                    method.invoke(activityOptions, getFreeformWindowModeId());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                switch (displaySection(leftPositionX, topPositionY)) {
                    case DisplaySection.TopLeft: {
                        activityOptions.setLaunchBounds(
                                new Rect(
                                        leftPositionX,
                                        topPositionY,
                                        rightPositionX,
                                        bottomPositionY)
                        );
                        break;
                    }
                    case DisplaySection.TopRight: {
                        activityOptions.setLaunchBounds(
                                new Rect(
                                        leftPositionX - (displayX() / 2),
                                        topPositionY,
                                        leftPositionX,
                                        bottomPositionY)
                        );
                        break;
                    }
                    case DisplaySection.BottomRight: {
                        activityOptions.setLaunchBounds(
                                new Rect(
                                        leftPositionX - (displayX() / 2),
                                        topPositionY - (displayY() / 2),
                                        leftPositionX,
                                        topPositionY)
                        );
                        break;
                    }
                    case DisplaySection.BottomLeft: {
                        activityOptions.setLaunchBounds(
                                new Rect(
                                        leftPositionX,
                                        topPositionY - (displayY() / 2),
                                        leftPositionX + (displayX() / 2),
                                        topPositionY)
                        );
                        break;
                    }
                    default: {
                        activityOptions.setLaunchBounds(
                                new Rect(
                                        displayX() / 4,
                                        (displayX() / 2),
                                        displayY() / 4,
                                        (displayY() / 2))
                        );
                        break;
                    }
                }

                Intent openAlias = context.getPackageManager().getLaunchIntentForPackage(PackageName);
                if (openAlias != null) {
                    openAlias.setFlags(
                            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_NO_ANIMATION);
                }
                context.startActivity(openAlias, activityOptions.toBundle());
            }
        }, 3000);
    }

    public void openApplicationFreeForm(String PackageName, String ClassName, int leftPositionX/*X*/, int rightPositionX, int topPositionY/*Y*/, int bottomPositionY) {
        if (returnAPI() < 28) {
            Intent homeScreen = new Intent(Intent.ACTION_MAIN);
            homeScreen.addCategory(Intent.CATEGORY_HOME);
            homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(homeScreen);
        }

        //Enable Developer Option & Turn ON 'Force Activities to be Resizable'
        //adb shell settings put global enable_freeform_support 1
        //adb shell settings put global force_resizable_activities 1
        ActivityOptions activityOptions = ActivityOptions.makeBasic();
        try {
            allowReflection();

            Method method = ActivityOptions.class.getMethod(getWindowingModeMethodName(), int.class);
            method.invoke(activityOptions, getFreeformWindowModeId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (displaySection(leftPositionX, topPositionY)) {
            case DisplaySection.TopLeft -> {
                activityOptions.setLaunchBounds(
                        new Rect(
                                leftPositionX,
                                topPositionY,
                                rightPositionX,
                                bottomPositionY)
                );
            }
            case DisplaySection.TopRight -> {
                activityOptions.setLaunchBounds(
                        new Rect(
                                leftPositionX - (displayX() / 2),
                                topPositionY,
                                leftPositionX,
                                bottomPositionY)
                );
            }
            case DisplaySection.BottomRight -> {
                activityOptions.setLaunchBounds(
                        new Rect(
                                leftPositionX - (displayX() / 2),
                                topPositionY - (displayY() / 2),
                                leftPositionX,
                                topPositionY)
                );
            }
            case DisplaySection.BottomLeft -> {
                activityOptions.setLaunchBounds(
                        new Rect(
                                leftPositionX,
                                topPositionY - (displayY() / 2),
                                leftPositionX + (displayX() / 2),
                                topPositionY)
                );
            }
            default -> {
                activityOptions.setLaunchBounds(
                        new Rect(
                                displayX() / 4,
                                (displayX() / 2),
                                displayY() / 4,
                                (displayY() / 2))
                );
            }
        }

        Intent openAlias = new Intent();
        openAlias.setClassName(PackageName, ClassName);
        openAlias.addCategory(Intent.CATEGORY_LAUNCHER);
        openAlias.setFlags(
                Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(openAlias, activityOptions.toBundle());
    }

    public void appsLaunchPad(String packageName) {
        Intent intent = new Intent(context, OpenApplicationsLaunchPad.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("PackageName", packageName);
        context.startActivity(intent);
    }

    public void appsLaunchPad(String packageName, String className) {
        Intent intent = new Intent(context, OpenApplicationsLaunchPad.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("PackageName", packageName);
        intent.putExtra("ClassName", className);
        context.startActivity(intent);
    }

    public void goToHomeScreen() {
        Intent homeScreen = new Intent(Intent.ACTION_MAIN);
        homeScreen.addCategory(Intent.CATEGORY_HOME);
        homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(homeScreen,
                ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
    }

    /*Preferences Functions*/
    @Deprecated
    public void savePreference(String PreferenceName, String KEY, String VALUE) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putString(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    @Deprecated
    public void savePreference(String PreferenceName, String KEY, int VALUE) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putInt(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    @Deprecated
    public void savePreference(String PreferenceName, String KEY, float VALUE) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putFloat(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    @Deprecated
    public void savePreference(String PreferenceName, String KEY, boolean VALUE) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putBoolean(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    @Deprecated
    public void saveDefaultPreference(String KEY, String VALUE) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putString(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    @Deprecated
    public void saveDefaultPreference(String KEY, int VALUE) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putInt(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    @Deprecated
    public void saveDefaultPreference(String KEY, boolean VALUE) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putBoolean(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    @Deprecated
    public String readPreference(String PreferenceName, String KEY, String defaultVALUE) {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getString(KEY, defaultVALUE);
    }

    @Deprecated
    public int readPreference(String PreferenceName, String KEY, int defaultVALUE) {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getInt(KEY, defaultVALUE);
    }

    @Deprecated
    public float readPreference(String PreferenceName, String KEY, float defaultVALUE) {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getFloat(KEY, defaultVALUE);
    }

    @Deprecated
    public boolean readPreference(String PreferenceName, String KEY, boolean defaultVALUE) {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getBoolean(KEY, defaultVALUE);
    }

    @Deprecated
    public int readDefaultPreference(String KEY, int defaultVALUE) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY, defaultVALUE);
    }

    /*Shaping Functions*/
    public Drawable applicationIcon(String packageName) {
        Drawable icon = null;
        try {
            PackageManager packManager = context.getPackageManager();
            icon = packManager.getApplicationIcon(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (icon == null) {
                try {
                    PackageManager packManager = context.getPackageManager();
                    icon = packManager.getDefaultActivityIcon();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return icon;
    }

    public Drawable applicationIcon(ActivityInfo activityInfo) {
        Drawable icon = null;
        try {
            icon = activityInfo.loadIcon(context.getPackageManager());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (icon == null) {
                try {
                    PackageManager packManager = context.getPackageManager();
                    icon = packManager.getDefaultActivityIcon();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return icon;
    }

    public int shapesImageId() {
        int shapesImageId = 0;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (sharedPreferences.getInt("iconShape", 0)) {
            case 1:
                shapesImageId = 1;
                break;
            case 2:
                shapesImageId = 2;
                break;
            case 3:
                shapesImageId = 3;
                break;
            case 4:
                shapesImageId = 4;
                break;
            case 5:
                shapesImageId = 5;
                break;
            case 0:
                shapesImageId = 0;
                break;
        }
        return shapesImageId;
    }

    public int shapedDrablesResourceId() {
        int shapeDrawable = R.drawable.droplet_icon;
        switch (shapesImageId()) {
            case 1:
                shapeDrawable = (R.drawable.droplet_icon);
                break;
            case 2:
                shapeDrawable = (R.drawable.circle_icon);
                break;
            case 3:
                shapeDrawable = (R.drawable.square_icon);
                break;
            case 4:
                shapeDrawable = (R.drawable.squircle_icon);
                break;

            case 0:
                shapeDrawable = R.drawable.droplet_icon;

                break;
        }
        return shapeDrawable;
    }

    public Drawable shapesDrawables() {
        Drawable shapeDrawable = null;
        switch (shapesImageId()) {
            case 1:
                shapeDrawable = context.getDrawable(R.drawable.droplet_icon);
                break;
            case 2:
                shapeDrawable = context.getDrawable(R.drawable.circle_icon);
                break;
            case 3:
                shapeDrawable = context.getDrawable(R.drawable.square_icon);
                break;
            case 4:
                shapeDrawable = context.getDrawable(R.drawable.squircle_icon);
                break;

            case 0:
                shapeDrawable = null;
                break;
        }
        return shapeDrawable;
    }

    public Drawable shapesDrawablesCategory(TextView categoryChar) {
        Drawable shapeDrawable = null;
        switch (shapesImageId()) {
            case 1:
                shapeDrawable = context.getDrawable(R.drawable.category_droplet_icon).mutate();
                break;
            case 2:
                shapeDrawable = context.getDrawable(R.drawable.category_circle_icon).mutate();
                break;
            case 3:
                shapeDrawable = context.getDrawable(R.drawable.category_square_icon).mutate();
                break;
            case 4:
                shapeDrawable = context.getDrawable(R.drawable.category_squircle_icon).mutate();
                break;
            case 0:
                shapeDrawable = context.getDrawable(R.drawable.brilliant).mutate();
                shapeDrawable.setAlpha(0);
                categoryChar.setTextColor(PublicVariable.colorLightDarkOpposite);
                break;
        }
        return shapeDrawable;
    }

    public ShapesImage initShapesImage(ViewGroup viewGroup, int viewId) {
        ShapesImage shapesImage = viewGroup.findViewById(viewId);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (sharedPreferences.getInt("iconShape", 0)) {
            case 1:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.droplet_icon));
                break;
            case 2:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.circle_icon));
                break;
            case 3:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.square_icon));
                break;
            case 4:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.squircle_icon));
                break;
            case 0:
                shapesImage.setShapeDrawable(null);
                break;
        }
        return shapesImage;
    }

    public ShapesImage initShapesImage(View view, int viewId) {
        ShapesImage shapesImage = view.findViewById(viewId);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (sharedPreferences.getInt("iconShape", 0)) {
            case 1:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.droplet_icon));
                break;
            case 2:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.circle_icon));
                break;
            case 3:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.square_icon));
                break;
            case 4:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.squircle_icon));
                break;
            case 0:
                shapesImage.setShapeDrawable(null);
                break;
        }
        return shapesImage;
    }

    public ShapesImage initShapesImage(Activity activity, int viewId) {
        ShapesImage shapesImage = activity.findViewById(viewId);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (sharedPreferences.getInt("iconShape", 0)) {
            case 1:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.droplet_icon));
                break;
            case 2:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.circle_icon));
                break;
            case 3:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.square_icon));
                break;
            case 4:
                shapesImage.setShapeDrawable(context.getDrawable(R.drawable.squircle_icon));
                break;
            case 0:
                shapesImage.setShapeDrawable(null);
                break;
        }
        return shapesImage;
    }

    public ShapesImage initShapesImage(ShapesImage shapesImageView) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (sharedPreferences.getInt("iconShape", 0)) {
            case 1:
                shapesImageView.setShapeDrawable(context.getDrawable(R.drawable.droplet_icon));
                break;
            case 2:
                shapesImageView.setShapeDrawable(context.getDrawable(R.drawable.circle_icon));
                break;
            case 3:
                shapesImageView.setShapeDrawable(context.getDrawable(R.drawable.square_icon));
                break;
            case 4:
                shapesImageView.setShapeDrawable(context.getDrawable(R.drawable.squircle_icon));
                break;
            case 0:
                shapesImageView.setShapeDrawable(null);
                break;
        }
        return shapesImageView;
    }

    public Drawable shapedNotificationUser(Drawable drawable) {
        Drawable drawableBack = new ColorDrawable(extractDominantColor(drawable));
        Drawable drawableFront = drawable;
        LayerDrawable shapedDrawable = new LayerDrawable(new Drawable[]{drawableBack, drawableFront});
        shapedDrawable.setPaddingMode(LayerDrawable.PADDING_MODE_NEST);
        shapedDrawable.setLayerInset(1, 7, 7, 7, 7);
        return shapedDrawable;
    }

    public Drawable shapedAppIcon(String packageName) {
        Drawable appIconDrawable = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getInt("iconShape", 0) == 1
                || sharedPreferences.getInt("iconShape", 0) == 2
                || sharedPreferences.getInt("iconShape", 0) == 3
                || sharedPreferences.getInt("iconShape", 0) == 4
                || sharedPreferences.getInt("iconShape", 0) == 5) {
            Drawable drawableBack = null;
            Drawable drawableFront = null;
            LayerDrawable layerDrawable = null;
            if (Build.VERSION.SDK_INT >= 26) {
                AdaptiveIconDrawable adaptiveIconDrawable = null;
                try {
                    Drawable tempAppIcon = applicationIcon(packageName);
                    if (tempAppIcon instanceof AdaptiveIconDrawable) {
                        adaptiveIconDrawable = (AdaptiveIconDrawable) tempAppIcon;
                        drawableBack = adaptiveIconDrawable.getBackground();
                        drawableFront = adaptiveIconDrawable.getForeground();
                        layerDrawable = new LayerDrawable(new Drawable[]{drawableBack, drawableFront});
                        appIconDrawable = layerDrawable;
                    } else {
                        drawableBack = new ColorDrawable(extractDominantColor(tempAppIcon));
                        drawableFront = tempAppIcon;
                        layerDrawable = new LayerDrawable(new Drawable[]{drawableBack, drawableFront});
                        layerDrawable.setPaddingMode(LayerDrawable.PADDING_MODE_NEST);
                        layerDrawable.setLayerInset(1, 35, 35, 35, 35);
                        appIconDrawable = layerDrawable;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Drawable tempAppIcon = applicationIcon(packageName);
                drawableBack = new ColorDrawable(extractDominantColor(tempAppIcon));
                drawableFront = tempAppIcon;
                layerDrawable = new LayerDrawable(new Drawable[]{drawableBack, drawableFront});
                layerDrawable.setPaddingMode(LayerDrawable.PADDING_MODE_NEST);
                layerDrawable.setLayerInset(1, 35, 35, 35, 35);
                appIconDrawable = layerDrawable;
            }
        } else if (sharedPreferences.getInt("iconShape", 0) == 0) {
            appIconDrawable = applicationIcon(packageName);
        }
        return appIconDrawable;
    }

    public Drawable shapedAppIcon(ActivityInfo activityInfo) {
        Drawable appIconDrawable = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getInt("iconShape", 0) == 1
                || sharedPreferences.getInt("iconShape", 0) == 2
                || sharedPreferences.getInt("iconShape", 0) == 3
                || sharedPreferences.getInt("iconShape", 0) == 4
                || sharedPreferences.getInt("iconShape", 0) == 5) {
            Drawable drawableBack = null;
            Drawable drawableFront = null;
            LayerDrawable layerDrawable = null;
            if (Build.VERSION.SDK_INT >= 26) {
                AdaptiveIconDrawable adaptiveIconDrawable = null;
                try {
                    Drawable tempAppIcon = applicationIcon(activityInfo);
                    if (tempAppIcon instanceof AdaptiveIconDrawable) {
                        adaptiveIconDrawable = (AdaptiveIconDrawable) tempAppIcon;
                        drawableBack = adaptiveIconDrawable.getBackground();
                        drawableFront = adaptiveIconDrawable.getForeground();
                        layerDrawable = new LayerDrawable(new Drawable[]{drawableBack, drawableFront});
                        appIconDrawable = layerDrawable;
                    } else {
                        drawableBack = new ColorDrawable(extractDominantColor(tempAppIcon));
                        drawableFront = tempAppIcon;
                        layerDrawable = new LayerDrawable(new Drawable[]{drawableBack, drawableFront});
                        layerDrawable.setPaddingMode(LayerDrawable.PADDING_MODE_NEST);
                        layerDrawable.setLayerInset(1, 35, 35, 35, 35);
                        appIconDrawable = layerDrawable;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Drawable tempAppIcon = applicationIcon(activityInfo);
                drawableBack = new ColorDrawable(extractDominantColor(tempAppIcon));
                drawableFront = tempAppIcon;
                layerDrawable = new LayerDrawable(new Drawable[]{drawableBack, drawableFront});
                layerDrawable.setPaddingMode(LayerDrawable.PADDING_MODE_NEST);
                layerDrawable.setLayerInset(1, 35, 35, 35, 35);
                appIconDrawable = layerDrawable;
            }
        } else if (sharedPreferences.getInt("iconShape", 0) == 0) {
            appIconDrawable = applicationIcon(activityInfo);
        }
        return appIconDrawable;
    }

    public Bitmap genericDrawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap layerDrawableToBitmap(LayerDrawable layerDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(layerDrawable.getIntrinsicWidth() / 2, layerDrawable.getIntrinsicHeight() / 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        layerDrawable.draw(canvas);

        return bitmap;
    }

    public Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public Bitmap appIconBitmap(String packageName) {
        Bitmap bitmap = null;
        try {
            LayerDrawable drawAppShortcuts;
            Drawable drawableIcon = applicationIcon(packageName);
            Drawable shapeTempDrawable = shapesDrawables();
            if (shapeTempDrawable != null) {
                shapeTempDrawable.setTint(extractDominantColor(drawableIcon));
                drawAppShortcuts = new LayerDrawable(new Drawable[]{
                        shapeTempDrawable,
                        drawableIcon
                });
                drawAppShortcuts.setLayerInset(1, 43, 43, 43, 43);
            } else {
                shapeTempDrawable = new ColorDrawable(Color.TRANSPARENT);
                drawAppShortcuts = new LayerDrawable(new Drawable[]{
                        shapeTempDrawable,
                        drawableIcon
                });
                drawAppShortcuts.setLayerInset(1, 1, 1, 1, 1);
            }
            bitmap = Bitmap.createBitmap(drawAppShortcuts.getIntrinsicWidth(), drawAppShortcuts.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            drawAppShortcuts.setBounds(0, 0, drawAppShortcuts.getIntrinsicWidth(), drawAppShortcuts.getIntrinsicHeight());
            drawAppShortcuts.draw(new Canvas(bitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Drawable resizeDrawable(Drawable drawable, int dstWidth, int dstHeight) {
        Drawable resizedDrawable = null;
        try {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
            resizedDrawable = new BitmapDrawable(context.getResources(), bitmapResized).mutate();
        } catch (Exception e) {
            e.printStackTrace();
            resizedDrawable = new ColorDrawable(Color.TRANSPARENT);
        }

        return resizedDrawable;
    }

    /*App GUI Functions*/
    public void checkLightDarkTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String LightDark = sharedPreferences.getString("themeColor", "2");
        if (LightDark.equals("1")) {//light
            PublicVariable.themeLightDark = true;
        } else if (LightDark.equals("2")) {//dark
            PublicVariable.themeLightDark = false;
        } else if (LightDark.equals("3")) {
            if (colorLightDarkWallpaper()) {//light
                PublicVariable.themeLightDark = true;
            } else if (!colorLightDarkWallpaper()) {//dark
                PublicVariable.themeLightDark = false;
            }
        }
    }

    /*GUI Functions*/
    public WindowManager.LayoutParams normalLayoutParams(int HW, int X, int Y) {
        int marginClear = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
        WindowManager.LayoutParams layoutParams = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams = new WindowManager.LayoutParams(
                    HW + marginClear,
                    HW + marginClear,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            layoutParams = new WindowManager.LayoutParams(
                    HW + marginClear,
                    HW + marginClear,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = X;
        layoutParams.y = Y;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;

        return layoutParams;
    }

    public WindowManager.LayoutParams splashRevealParams(int X, int Y) {
        int navBarHeight = 0;
        int resourceIdNav = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceIdNav > 0) {
            navBarHeight = context.getResources().getDimensionPixelSize(resourceIdNav);
        }
        int statusBarHeight = 0;
        int resourceIdStatus = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceIdStatus > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceIdStatus);
        }
        WindowManager.LayoutParams layoutParams = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams = new WindowManager.LayoutParams(
                    displayX(),
                    displayY() + statusBarHeight + navBarHeight,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        } else {
            layoutParams = new WindowManager.LayoutParams(
                    displayX(),
                    displayY() + statusBarHeight + navBarHeight,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        }

        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = X;
        layoutParams.y = Y - statusBarHeight;
        return layoutParams;
    }

    public WindowManager.LayoutParams backFromEdge(int HW, int X, int Y) {
        WindowManager.LayoutParams layoutParams = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams = new WindowManager.LayoutParams(
                    HW,
                    HW,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            layoutParams = new WindowManager.LayoutParams(
                    HW,
                    HW,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = X;
        layoutParams.y = Y;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        return layoutParams;
    }

    public WindowManager.LayoutParams moveToEdge(String packageName, int HW) {
        WindowManager.LayoutParams layoutParams = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams = new WindowManager.LayoutParams(
                    HW,
                    HW,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        } else {
            layoutParams = new WindowManager.LayoutParams(
                    HW,
                    HW,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        }
        SharedPreferences sharedPrefPosition = null;
        try {
            sharedPrefPosition = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        layoutParams.gravity = Gravity.TOP | Gravity.START;
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("stick", "1").equals("1")) {//Left
            layoutParams.x = -(HW / 2);
        } else if (PreferenceManager.getDefaultSharedPreferences(context).getString("stick", "1").equals("2")) {//Right
            layoutParams.x = displayX() - (HW / 2);
        }
        layoutParams.y = sharedPrefPosition.getInt("Y", 0);
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        return layoutParams;
    }

    public WindowManager.LayoutParams handleOrientationLandscape(String packageName, int HW) {
        WindowManager.LayoutParams layoutParams = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams = new WindowManager.LayoutParams(
                    HW,
                    HW,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            layoutParams = new WindowManager.LayoutParams(
                    HW,
                    HW,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        SharedPreferences sharedPrefPosition = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);

        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = sharedPrefPosition.getInt("Y", 0);
        layoutParams.y = displayY() - sharedPrefPosition.getInt("X", 0);
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;

        SharedPreferences.Editor editor = sharedPrefPosition.edit();
        editor.putInt("X", layoutParams.x);
        editor.putInt("Y", layoutParams.y);
        editor.apply();

        return layoutParams;
    }

    public WindowManager.LayoutParams handleOrientationPortrait(String packageName, int HW) {
        WindowManager.LayoutParams layoutParams = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams = new WindowManager.LayoutParams(
                    HW,
                    HW,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            layoutParams = new WindowManager.LayoutParams(
                    HW,
                    HW,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        SharedPreferences sharedPrefPosition = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);

        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = displayX() - sharedPrefPosition.getInt("Y", 0);
        layoutParams.y = sharedPrefPosition.getInt("X", 0);
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;

        SharedPreferences.Editor editor = sharedPrefPosition.edit();
        editor.putInt("X", layoutParams.x);
        editor.putInt("Y", layoutParams.y);
        editor.apply();

        return layoutParams;
    }

    public WindowManager.LayoutParams normalWidgetLayoutParams(String packageName, int widgetId, int widgetWidth, int widgetHeight) {
        WindowManager.LayoutParams layoutParams = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        } else {
            layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(widgetId + packageName, Context.MODE_PRIVATE);

        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.width = sharedPreferences.getInt("WidgetWidth", widgetWidth);
        layoutParams.height = sharedPreferences.getInt("WidgetHeight", widgetHeight);
        layoutParams.x = sharedPreferences.getInt("X", 19);
        layoutParams.y = sharedPreferences.getInt("Y", 19);
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;

        return layoutParams;
    }

    public void popupOptionShortcuts(FloatingServices floatingServices, final Context context, View anchorView, final String PackageName, String ClassName) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView, Gravity.CENTER);
        if (PublicVariable.themeLightDark == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                popupMenu = new PopupMenu(context, anchorView, Gravity.CENTER, 0, R.style.GeeksEmpire_Dialogue_Light);
            }
        } else if (PublicVariable.themeLightDark == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                popupMenu = new PopupMenu(context, anchorView, Gravity.CENTER, 0, R.style.GeeksEmpire_Dialogue_Dark);
            }
        }


        SecurityFunctions securityFunctions = new SecurityFunctions(context);
        String[] menuItems = securityFunctions.isAppLocked(PackageName) ? context.getResources().getStringArray(R.array.ContextMenuUnlock) : context.getResources().getStringArray(R.array.ContextMenuLock);
        Drawable backgroundDrawable = shapesDrawables();
        Drawable foregroundDrawable = returnAPI() >= 28 ? resizeDrawable(context.getDrawable(R.drawable.w_pref_popup).mutate(), 100, 100) : context.getDrawable(R.drawable.w_pref_popup).mutate();
        if (shapesDrawables() == null) {
            backgroundDrawable = returnAPI() >= 28 ? resizeDrawable(context.getDrawable(R.drawable.ic_launcher).mutate(), 100, 100) : context.getDrawable(R.drawable.ic_launcher).mutate();
            backgroundDrawable.setAlpha(0);
            if (!customIconsEnable()) {
                foregroundDrawable.setTint(extractVibrantColor(applicationIcon(PackageName)));
            }
        } else {
            try {
                backgroundDrawable = returnAPI() >= 28 ? resizeDrawable(shapesDrawables().mutate(), 100, 100) : shapesDrawables().mutate();
                backgroundDrawable.setTint(extractVibrantColor(applicationIcon(PackageName)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (customIconsEnable()) {
            backgroundDrawable = returnAPI() >= 28 ? resizeDrawable(applicationIcon(customIconPackageName()).mutate(), 100, 100) : applicationIcon(customIconPackageName()).mutate();
            backgroundDrawable.setTint(extractVibrantColor(applicationIcon(PackageName)));
        }
        LayerDrawable popupItemIcon = new LayerDrawable(
                new Drawable[]{
                        backgroundDrawable,
                        foregroundDrawable
                });
        for (int itemId = 0; itemId < menuItems.length; itemId++) {
            popupMenu.getMenu()
                    .add(Menu.NONE, itemId, itemId, Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + menuItems[itemId] + "</font>", Html.FROM_HTML_MODE_COMPACT))
                    .setIcon(popupItemIcon);
        }

        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == 0) {
                    PublicVariable.floatingSizeNumber = 26;
                    PublicVariable.floatingViewsHW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber, context.getResources().getDisplayMetrics());

                    floatingServices.runUnlimitedShortcutsService(PackageName, ClassName);

                    revertFloatingSize();

                } else if (item.getItemId() == 1) {
                    PublicVariable.floatingSizeNumber = 39;
                    PublicVariable.floatingViewsHW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber, context.getResources().getDisplayMetrics());

                    floatingServices.runUnlimitedShortcutsService(PackageName, ClassName);

                    revertFloatingSize();

                } else if (item.getItemId() == 2) {
                    PublicVariable.floatingSizeNumber = 52;
                    PublicVariable.floatingViewsHW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber, context.getResources().getDisplayMetrics());

                    floatingServices.runUnlimitedShortcutsService(PackageName, ClassName);

                    revertFloatingSize();

                } else if (item.getItemId() == 3) {
                    if (securityFunctions.isAppLocked(PackageName)) {

                        SecurityInterfaceHolder.authenticationCallback = new AuthenticationCallback() {

                            @Override
                            public void invokedPinPassword() {

                            }

                            @Override
                            public void failedAuthenticated() {

                            }

                            @Override
                            public void authenticatedFloatIt(@Nullable Bundle extraInformation) {

                                securityFunctions.doUnlockApps(PackageName);

                            }
                        };

                        context.startActivity(new Intent(context, AuthenticationFingerprint.class)
                                        .putExtra(UserInterfaceExtraData.OtherTitle, applicationName(PackageName))
                                        .putExtra(UserInterfaceExtraData.DoLockUnlock, true)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, 0).toBundle());

                    } else {
                        if (securityServicesSubscribed()) {
                            securityFunctions.doLockApps(PackageName);

                        } else {

                            context.startActivity(new Intent(context, InitializeInAppBilling.class)
                                            .putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.SubscriptionPurchase)
                                            .putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemSecurityServices)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());

                        }
                    }
                } else if (item.getItemId() == 4) {
                    new HomeScreenShortcuts(context).create(
                            PackageName,
                            ClassName,
                            FunctionsClassLegacy.this
                    );
                }

                new RuntimeIO(context, FunctionsClassLegacy.this).updateRecoverShortcuts();

                return true;
            }
        });
        popupMenu.show();
    }

    public void popupOptionFolders(FoldersConfigurations foldersConfigurations, final Context context, View anchorView, final String folderName, final int indicatorPosition) {

        FileIO fileIO = new FileIO(context);

        PopupMenu popupMenu = new PopupMenu(context, anchorView, Gravity.CENTER);
        if (PublicVariable.themeLightDark == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                popupMenu = new PopupMenu(context, anchorView, Gravity.CENTER, 0, R.style.GeeksEmpire_Dialogue_Category_Light);
            }
        } else if (PublicVariable.themeLightDark == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                popupMenu = new PopupMenu(context, anchorView, Gravity.CENTER, 0, R.style.GeeksEmpire_Dialogue_Category_Dark);
            }
        }
        String[] menuItems;
        if (loadRecoveryIndicatorCategory(folderName)) {
            menuItems = context.getResources().getStringArray(R.array.ContextMenuCategoryRemove);
        } else {
            menuItems = context.getResources().getStringArray(R.array.ContextMenuCategory);
        }

        Drawable popupItemIcon = returnAPI() >= 28 ? resizeDrawable(context.getDrawable(R.drawable.w_pref_popup), 100, 100) : context.getDrawable(R.drawable.w_pref_popup);
        popupItemIcon.setTint(PublicVariable.primaryColorOpposite);

        for (int itemId = 0; itemId < menuItems.length; itemId++) {
            popupMenu.getMenu()
                    .add(Menu.NONE, itemId, itemId, Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + menuItems[itemId] + "</font>", Html.FROM_HTML_MODE_COMPACT))
                    .setIcon(popupItemIcon);
        }

        SecurityFunctions securityFunctions = new SecurityFunctions(context);
        if (securityFunctions.isAppLocked(folderName)) {
            popupMenu.getMenu()
                    .add(Menu.NONE, menuItems.length, menuItems.length, Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + context.getString(R.string.unLockIt) + "</font>", Html.FROM_HTML_MODE_COMPACT))
                    .setIcon(popupItemIcon);
        } else {
            popupMenu.getMenu()
                    .add(Menu.NONE, menuItems.length, menuItems.length, Html.fromHtml("<font color='" + PublicVariable.colorLightDarkOpposite + "'>" + context.getString(R.string.lockIt) + "</font>", Html.FROM_HTML_MODE_COMPACT))
                    .setIcon(popupItemIcon);
        }

        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == 0) {
                    PublicVariable.floatingSizeNumber = 26;
                    runUnlimitedFolderService(folderName);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            revertFloatingSize();
                        }
                    }, 100);
                } else if (item.getItemId() == 1) {
                    PublicVariable.floatingSizeNumber = 52;
                    runUnlimitedFolderService(folderName);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            revertFloatingSize();
                        }
                    }, 100);
                } else if (item.getItemId() == 2) {
                    PublicVariable.floatingSizeNumber = 78;
                    runUnlimitedFolderService(folderName);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            revertFloatingSize();
                        }
                    }, 100);
                } else if (item.getItemId() == 3) {
                    if (!loadRecoveryIndicatorCategory(folderName)) {
                        fileIO.saveFileAppendLine(".uCategory", folderName);
                    } else {

                        fileIO.removeLine(".uCategory", folderName);

                    }

                    foldersConfigurations.loadFolders();
                } else if (item.getItemId() == 4) {
                    try {
                        String[] categoryContent = fileIO.readFileLinesAsArray(folderName);

                        for (String packageName : categoryContent) {
                            context.deleteFile(packageName + folderName);
                        }

                        fileIO.removeLine(".categoryInfo", folderName);
                        fileIO.removeLine(".uCategory", folderName);

                        context.deleteFile(folderName);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                        foldersConfigurations.loadFolders();
                    }
                } else if (item.getItemId() == 5) {
                    if (securityFunctions.isAppLocked(folderName)) {

                        SecurityInterfaceHolder.authenticationCallback = new AuthenticationCallback() {

                            @Override
                            public void invokedPinPassword() {

                            }

                            @Override
                            public void failedAuthenticated() {

                            }

                            @Override
                            public void authenticatedFloatIt(@Nullable Bundle extraInformation) {

                                if (context.getFileStreamPath(folderName).exists() && context.getFileStreamPath(folderName).isFile()) {
                                    String[] packageNames = fileIO.readFileLinesAsArray(folderName);

                                    for (String packageName : packageNames) {
                                        securityFunctions.doUnlockApps(packageName);
                                    }

                                    securityFunctions.doUnlockApps(folderName);
                                }
                            }
                        };

                        context.startActivity(new Intent(context, AuthenticationFingerprint.class)
                                        .putExtra(UserInterfaceExtraData.OtherTitle, folderName)
                                        .putExtra(UserInterfaceExtraData.DoLockUnlock, true)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, 0).toBundle());

                    } else {
                        if (securityServicesSubscribed()) {

                            if (context.getFileStreamPath(folderName).exists() && context.getFileStreamPath(folderName).isFile()) {
                                savePreference(".LockedApps", folderName, true);

                                String[] packageNames = fileIO.readFileLinesAsArray(folderName);
                                for (String packageName : packageNames) {
                                    securityFunctions.doLockApps(packageName);
                                }

                            }
                        } else {

                            context.startActivity(new Intent(context, InitializeInAppBilling.class)
                                            .putExtra(InitializeInAppBilling.Entry.PurchaseType, InitializeInAppBilling.Entry.SubscriptionPurchase)
                                            .putExtra(InitializeInAppBilling.Entry.ItemToPurchase, InAppBillingData.SKU.InAppItemSecurityServices)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                    ActivityOptions.makeCustomAnimation(context, R.anim.down_up, android.R.anim.fade_out).toBundle());

                        }
                    }
                }
                return true;
            }
        });
        popupMenu.show();
    }

    public void revertFloatingSize() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                PublicVariable.floatingSizeNumber = readDefaultPreference("floatingSize", 39);
                PublicVariable.floatingViewsHW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber, context.getResources().getDisplayMetrics());
            }
        }, 555);
    }

    public void Toast(String toastContent, int toastGravity/*, int toastColor*/) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_view, null/*(ViewGroup) activity.findViewById(R.id.toastView)*/);

        LayerDrawable drawToast = null;
        if (toastGravity == Gravity.TOP) {
            drawToast = (LayerDrawable) context.getDrawable(R.drawable.toast_background_top);
        } else if (toastGravity == Gravity.BOTTOM) {
            drawToast = (LayerDrawable) context.getDrawable(R.drawable.toast_background_bottom);
        }
        Drawable backToast = drawToast.findDrawableByLayerId(R.id.backgroundTemporary);

        TextView textView = layout.findViewById(R.id.toastText);
        textView.setText(Html.fromHtml("<small>" + toastContent + "</small>", Html.FROM_HTML_MODE_COMPACT));
        backToast.setTint(context.getColor(R.color.light_transparent));
        textView.setBackground(drawToast);
        textView.setTextColor(context.getColor(R.color.dark));
        textView.setShadowLayer(0.02f, 2, 2, context.getColor(R.color.dark_transparent_high));
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.FILL_HORIZONTAL | toastGravity, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void Toast(String toastContent, int toastGravity, int toastColor) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_view, null/*(ViewGroup) activity.findViewById(R.id.toastView)*/);

        LayerDrawable drawToast = null;
        if (toastGravity == Gravity.TOP) {
            drawToast = (LayerDrawable) context.getDrawable(R.drawable.toast_background_top);
        } else if (toastGravity == Gravity.BOTTOM) {
            drawToast = (LayerDrawable) context.getDrawable(R.drawable.toast_background_bottom);
        }
        Drawable backToast = drawToast.findDrawableByLayerId(R.id.backgroundTemporary);
        backToast.setTint(toastColor);

        TextView textView = layout.findViewById(R.id.toastText);
        textView.setText(Html.fromHtml("<small>" + toastContent + "</small>", Html.FROM_HTML_MODE_COMPACT));
        backToast.setTint(context.getColor(R.color.light_transparent));
        textView.setBackground(drawToast);
        textView.setTextColor(context.getColor(R.color.dark));
        textView.setShadowLayer(0.02f, 2, 2, context.getColor(R.color.dark_transparent_high));
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.FILL_HORIZONTAL | toastGravity, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public boolean loadRecoveryIndicator(String packageName) {
        boolean inRecovery = false;
        try {
            if (PublicVariable.recoveryFloatingShortcuts != null) {
                for (String anAppNameArrayRecovery : PublicVariable.recoveryFloatingShortcuts) {
                    if (packageName.equals(anAppNameArrayRecovery)) {
                        inRecovery = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inRecovery;
    }

    public boolean loadRecoveryIndicatorCategory(String categoryName) {

        boolean inRecovery = false;

        FileIO fileIO = new FileIO(context);

        try {
            for (String anAppNameArrayRecovery : fileIO.readFileLinesAsArray(".uCategory")) {
                if (categoryName.equals(anAppNameArrayRecovery)) {
                    inRecovery = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inRecovery;
    }

    public float DpToPixel(float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public float PixelToDp(float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public int DpToInteger(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public int displayX() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public int displayY() {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public int displaySection(int X, int Y) {
        int section = 1;
        if (X < displayX() / 2 && Y < displayY() / 2) {//top-left
            section = 1;
        } else if (X > displayX() / 2 && Y < displayY() / 2) {//top-right
            section = 2;
        } else if (X < displayX() / 2 && Y > displayY() / 2) {//bottom-left
            section = 3;
        } else if (X > displayX() / 2 && Y > displayY() / 2) {//bottom-right
            section = 4;
        }
        return section;
    }

    public int columnCount(int itemWidth) {
        return (int) (displayX() / DpToPixel(itemWidth));
    }

    public void shortcutToHomeScreen(Class className, String shortcutName, Drawable drawableIcon, int shortcutId) {
        Intent differentIntent = new Intent(context, className);
        differentIntent.setAction(Intent.ACTION_MAIN);
        differentIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        differentIntent.putExtra("ShortcutsId", shortcutId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, String.valueOf(shortcutId))
                    .setShortLabel(shortcutName)
                    .setLongLabel(shortcutName)
                    .setIcon(Icon.createWithBitmap(genericDrawableToBitmap(drawableIcon)))
                    .setIntent(differentIntent)
                    .build();
            context.getSystemService(ShortcutManager.class).requestPinShortcut(shortcutInfo, null);
        } else {
            Intent addIntent = new Intent().setPackage(context.getPackageName());
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, differentIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, genericDrawableToBitmap(drawableIcon));
            addIntent.putExtra("duplicate", true);
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            context.sendBroadcast(addIntent);
        }
    }

    public boolean checkStickyEdge() {
        boolean stickyEdge = true;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String stick = sharedPrefs.getString("stick", "1");
        if (stick.equals("1")) {//Left
            stickyEdge = true;
        } else if (stick.equals("2")) {//Right
            stickyEdge = false;
        }
        return stickyEdge;
    }

    public static class WindowMode {
        static final int WINDOWING_MODE_FREEFORM = 5;
        private static final int FREEFORM_WORKSPACE_STACK_ID = 2;
    }

    public static class DisplaySection {
        public static final int TopLeft = 1;
        public static final int TopRight = 2;
        public static final int BottomLeft = 3;
        public static final int BottomRight = 4;
    }

    public class HeartBeat {
        String packageName;
        View viewToBeat;
        boolean getFirstApp = false;
        Animator.AnimatorListener scaleDownListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewToBeat.animate().scaleXBy(0.33f).scaleYBy(0.33f).setDuration(233).setListener(scaleUpListener);
                if (PublicVariable.hearBeatCheckPoint) {
                    if (getFirstApp) {
                        try {
                            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                            List<UsageStats> queryUsageStats = mUsageStatsManager
                                    .queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                                            System.currentTimeMillis() - 1000 * 60,            //begin
                                            System.currentTimeMillis());                            //end
                            Collections.sort(queryUsageStats, new Comparator<UsageStats>() {
                                @Override
                                public int compare(UsageStats left, UsageStats right) {
                                    return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
                                }
                            });
                            String inFrontPackageName = queryUsageStats.get(0).getPackageName();
                            String secondPackageName = queryUsageStats.get(1).getPackageName();
                            if (inFrontPackageName.contains(packageName) || secondPackageName.contains(packageName)) {
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        context.stopService(new Intent(context, FloatingSplash.class));
                                    }
                                }, 133);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
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
                viewToBeat.animate().scaleXBy(-0.33f).scaleYBy(-0.33f).setDuration(333).setListener(scaleDownListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        };

        public HeartBeat(String packageName, View viewToBeat) {
            this.packageName = packageName;
            this.viewToBeat = viewToBeat;
            this.viewToBeat.animate().scaleXBy(0.33f).scaleYBy(0.33f).setDuration(233).setListener(scaleUpListener);

            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOp("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
            if (mode == AppOpsManager.MODE_ALLOWED) {
                getFirstApp = true;
            }
        }
    }

    public class HeartBeatClose {
        String packageName;
        View viewToBeat;
        Animator.AnimatorListener scaleDownListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewToBeat.animate().scaleXBy(0.33f).scaleYBy(0.33f).setDuration(233).setListener(scaleUpListener);

                try {
                    UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                    List<UsageStats> queryUsageStats = mUsageStatsManager
                            .queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                                    System.currentTimeMillis() - 1000 * 60,            //begin
                                    System.currentTimeMillis());                    //end
                    Collections.sort(queryUsageStats, new Comparator<UsageStats>() {
                        @Override
                        public int compare(UsageStats left, UsageStats right) {
                            return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
                        }
                    });
                    String inFrontPackageName = queryUsageStats.get(0).getPackageName();

                    PackageManager localPackageManager = context.getPackageManager();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    String defaultLauncher = localPackageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
                    if (inFrontPackageName.contains(defaultLauncher)) {
                        context.stopService(new Intent(context, FloatingSplash.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    context.stopService(new Intent(context, FloatingSplash.class));
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
                viewToBeat.animate().scaleXBy(-0.33f).scaleYBy(-0.33f).setDuration(333).setListener(scaleDownListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        };

        public HeartBeatClose(String packageName, View viewToBeat) {
            this.packageName = packageName;
            this.viewToBeat = viewToBeat;
            this.viewToBeat.animate().scaleXBy(0.33f).scaleYBy(0.33f).setDuration(233).setListener(scaleUpListener);
        }
    }

    /*Floating Splash*/
    public void circularRevealViewScreen(final View view,
                                         final int xPosition, final int yPosition,
                                         boolean circularExpandCollapse) {
        if (circularExpandCollapse) {
            int startRadius = 0;
            int endRadius = (int) Math.hypot(displayX(), displayY());
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    view,
                    (xPosition),
                    (yPosition),
                    startRadius,
                    endRadius
            );
            animator.setInterpolator(new FastOutLinearInInterpolator());
            animator.setDuration(555);
            animator.start();
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        } else {
            int startRadius = (int) Math.hypot(displayX(), displayY());
            int endRadius = 0;
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    view,
                    (xPosition),
                    (yPosition),
                    startRadius,
                    endRadius
            );
            animator.setInterpolator(new FastOutLinearInInterpolator());
            animator.setDuration(333);
            animator.start();
        }
    }

    public void circularRevealSplashScreenRemoval(final View view,
                                                  final int xPosition, final int yPosition) {
        int startRadius = 0;
        int endRadius = (int) Math.hypot(displayX(), displayY());
        Animator animator = ViewAnimationUtils.createCircularReveal(
                view,
                (xPosition),
                (yPosition),
                startRadius,
                endRadius
        );
        animator.setInterpolator(new FastOutLinearInInterpolator());
        animator.setDuration(555);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void circularRevealSplashScreen(final View view, final View childView,
                                           final int xPosition, final int yPosition, final int iconColor,
                                           final String packageName, final String className,
                                           boolean circularExpandCollapse) throws Exception {
        if (circularExpandCollapse) {
            view.setBackgroundColor(iconColor);
            new HeartBeat(packageName, childView);

            int startRadius = 0;
            int endRadius = (int) Math.hypot(displayX(), displayY());
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    view,
                    (xPosition + (childView.getWidth() / 2)),
                    (yPosition + (childView.getHeight() / 2)),
                    startRadius,
                    endRadius
            );
            animator.setInterpolator(new FastOutLinearInInterpolator());
            if (UsageStatsEnabled()) {
                animator.setDuration(333);
            } else {
                animator.setDuration(555);
            }
            animator.start();
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (className != null) {
                                if (FreeForm()) {
                                    openApplicationFreeForm(packageName,
                                            className,
                                            xPosition,
                                            (displayX() / 2),
                                            yPosition,
                                            (displayY() / 2)
                                    );
                                } else {
                                    appsLaunchPad(packageName, className);
                                }
                            } else {
                                if (FreeForm()) {
                                    openApplicationFreeForm(packageName,
                                            xPosition,
                                            (displayX() / 2),
                                            yPosition,
                                            (displayY() / 2)
                                    );
                                } else {
                                    appsLaunchPad(packageName);
                                }
                            }
                        }
                    }, 51);
                    if (UsageStatsEnabled()) {
                        try {
                            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                            List<UsageStats> queryUsageStats = usageStatsManager
                                    .queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                                            System.currentTimeMillis() - 1000 * 60,            //begin
                                            System.currentTimeMillis());                    //end
                            Collections.sort(queryUsageStats, new Comparator<UsageStats>() {
                                @Override
                                public int compare(UsageStats left, UsageStats right) {
                                    return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
                                }
                            });
                            String inFrontPackage = queryUsageStats.get(0).getPackageName();
                            if (inFrontPackage.contains(packageName)) {
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        context.stopService(new Intent(context, FloatingSplash.class));
                                    }
                                }, 133);
                            } else {
                                PublicVariable.hearBeatCheckPoint = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        context.stopService(new Intent(context, FloatingSplash.class));
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        } else {
            view.setBackgroundColor(iconColor);
            int startRadius = (int) Math.hypot(displayX(), displayY());
            int endRadius = 0;
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    view,
                    (xPosition + (childView.getWidth() / 2)),
                    (yPosition + (childView.getHeight() / 2)),
                    startRadius,
                    endRadius
            );
            animator.setInterpolator(new FastOutLinearInInterpolator());
            animator.setDuration(123);
            animator.start();
        }
    }

    public boolean splashReveal() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("floatingSplash", false);
    }

    /*Color GUI Functions*/
    public void loadSavedColor() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(".themeColor", Context.MODE_PRIVATE);

        PublicVariable.vibrantColor = sharedPreferences.getInt("vibrantColor", context.getColor(R.color.default_color));//getVibrantColor
        PublicVariable.darkMutedColor = sharedPreferences.getInt("darkMutedColor", context.getColor(R.color.default_color));//getDarkMutedColor
        PublicVariable.darkMutedColorString = sharedPreferences.getString("darkMutedColorString", String.valueOf((context.getColor(R.color.default_color))));

        if (PublicVariable.themeLightDark) {

            PublicVariable.primaryColor = sharedPreferences.getInt("vibrantColor", context.getColor(R.color.default_color));//getVibrantColor

            PublicVariable.primaryColorOpposite = sharedPreferences.getInt("darkMutedColor", context.getColor(R.color.default_color_game));//getDarkMutedColor

            PublicVariable.colorLightDark = context.getColor(R.color.light);

            PublicVariable.colorLightDarkOpposite = context.getColor(R.color.dark);

        } else if (!PublicVariable.themeLightDark) {

            PublicVariable.primaryColor = sharedPreferences.getInt("darkMutedColor", context.getColor(R.color.default_color));//getDarkMutedColor

            PublicVariable.primaryColorOpposite = sharedPreferences.getInt("vibrantColor", context.getColor(R.color.default_color_game));//getVibrantColor

            PublicVariable.colorLightDark = context.getColor(R.color.dark);

            PublicVariable.colorLightDarkOpposite = context.getColor(R.color.light);

        }

        PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.getColor(R.color.default_color));
    }

    public void extractWallpaperColor() {

        int vibrantColor = context.getColor(R.color.default_color_light);
        int darkMutedColor = context.getColor(R.color.default_color_darker);
        String darkMutedColorString = "" + context.getColor(R.color.default_color);

        int dominantColor = context.getColor(R.color.default_color);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {

            WallpaperColors wallpaperColors = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);

            try {
                vibrantColor = wallpaperColors.getSecondaryColor().toArgb();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                darkMutedColor = wallpaperColors.getSecondaryColor().toArgb();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                darkMutedColorString = String.valueOf(darkMutedColor);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(".themeColor", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("vibrantColor", vibrantColor);
        editor.putInt("darkMutedColor", darkMutedColor);
        editor.putString("darkMutedColorString", darkMutedColorString);
        editor.putInt("dominantColor", dominantColor);
        editor.apply();
    }

    public int extractVibrantColor(Drawable drawable) {
        int VibrantColor = context.getColor(R.color.default_color);
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (drawable instanceof VectorDrawable) {
                bitmap = bitmapExtractor.drawableToBitmap(drawable);
            } else if (drawable instanceof AdaptiveIconDrawable) {
                try {
                    bitmap = ((BitmapDrawable) ((AdaptiveIconDrawable) drawable).getBackground()).getBitmap();
                } catch (Exception e) {
                    try {
                        bitmap = ((BitmapDrawable) ((AdaptiveIconDrawable) drawable).getForeground()).getBitmap();
                    } catch (Exception e1) {
                        bitmap = bitmapExtractor.drawableToBitmap(drawable);
                    }
                }
            } else {
                bitmap = bitmapExtractor.drawableToBitmap(drawable);
            }
        } else {
            bitmap = bitmapExtractor.drawableToBitmap(drawable);
        }
        Palette currentColor;
        try {
            if (bitmap != null && !bitmap.isRecycled()) {
                currentColor = Palette.from(bitmap).generate();

                int defaultColor = context.getColor(R.color.default_color);
                VibrantColor = currentColor.getVibrantColor(defaultColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (bitmap != null && !bitmap.isRecycled()) {
                    currentColor = Palette.from(bitmap).generate();

                    int defaultColor = context.getColor(R.color.default_color);
                    VibrantColor = currentColor.getMutedColor(defaultColor);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return VibrantColor;
    }

    public int extractDominantColor(Drawable drawable) {
        int DominanctColor = context.getColor(R.color.default_color);
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (drawable instanceof VectorDrawable) {
                bitmap = bitmapExtractor.drawableToBitmap(drawable);
            } else if (drawable instanceof AdaptiveIconDrawable) {
                try {
                    bitmap = ((BitmapDrawable) ((AdaptiveIconDrawable) drawable).getBackground()).getBitmap();
                } catch (Exception e) {
                    try {
                        bitmap = ((BitmapDrawable) ((AdaptiveIconDrawable) drawable).getForeground()).getBitmap();
                    } catch (Exception e1) {
                        bitmap = bitmapExtractor.drawableToBitmap(drawable);
                    }
                }
            } else {
                bitmap = bitmapExtractor.drawableToBitmap(drawable);
            }
        } else {
            bitmap = bitmapExtractor.drawableToBitmap(drawable);
        }
        Palette currentColor;
        try {
            if (bitmap != null && !bitmap.isRecycled()) {
                currentColor = Palette.from(bitmap).generate();

                int defaultColor = context.getColor(R.color.default_color);
                DominanctColor = currentColor.getDominantColor(defaultColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (bitmap != null && !bitmap.isRecycled()) {
                    currentColor = Palette.from(bitmap).generate();

                    int defaultColor = context.getColor(R.color.default_color);
                    DominanctColor = currentColor.getMutedColor(defaultColor);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return DominanctColor;
    }

    public Bitmap brightenBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Color.RED);
        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00222222); // lighten
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        return bitmap;
    }

    public Bitmap darkenBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Color.RED);
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
        ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        return bitmap;
    }

    public int manipulateColor(int color, float aFactor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * aFactor);
        int g = Math.round(Color.green(color) * aFactor);
        int b = Math.round(Color.blue(color) * aFactor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    public int mixColors(int color1, int color2, float ratio /*0 -- 1*/) {
        /*ratio = 1 >> color1*/
        /*ratio = 0 >> color2*/
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    /**
     * 255 is Transparent.
     **/
    public int setColorAlpha(int color, float alphaValue /*1 -- 255*/) {
        int alpha = Math.round(Color.alpha(color) * alphaValue);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public boolean colorLightDarkWallpaper() {
        boolean LightDark = false;
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(".themeColor", Context.MODE_PRIVATE);
            int vibrantColor = sharedPreferences.getInt("vibrantColor", context.getColor(R.color.default_color));
            int darkMutedColor = sharedPreferences.getInt("darkMutedColor", context.getColor(R.color.default_color));
            int dominantColor = sharedPreferences.getInt("dominantColor", context.getColor(R.color.default_color));

            int initMix = mixColors(vibrantColor, darkMutedColor, 0.50f);
            int finalMix = mixColors(dominantColor, initMix, 0.50f);

            double calculateLuminance = ColorUtils.calculateLuminance(dominantColor);
            if (calculateLuminance > 0.50) {//light
                LightDark = true;
            } else if (calculateLuminance <= 0.50) {//dark
                LightDark = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return LightDark;
    }

    public boolean isDrawableLightDark(Drawable drawable) {
        boolean isLightDark = false;

        double calculateLuminance = ColorUtils.calculateLuminance(extractDominantColor(drawable));
        if (calculateLuminance > 0.66) {//light
            isLightDark = true;
        } else if (calculateLuminance <= 0.50) {//dark
            isLightDark = false;
        }

        return isLightDark;
    }

    public void doVibrate(long millisecondVibrate) {
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(millisecondVibrate, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } else {
            if (vibrator != null) {
                vibrator.vibrate(millisecondVibrate);
            }
        }
    }

    /*Custom Icons*/
    public String customIconPackageName() {
        PreferencesIO preferencesIO = new PreferencesIO(context);
        //com.Fraom.Smugy
        return preferencesIO.readDefaultPreference("customIcon", context.getPackageName());
    }

    public boolean customIconsEnable() {
        boolean doLoadCustomIcon = false;

        if (customIconPackageName().equals(context.getPackageName())) {
            doLoadCustomIcon = false;
        } else if (!customIconPackageName().equals(context.getPackageName())) {
            doLoadCustomIcon = true;
        }

        return doLoadCustomIcon;
    }

    public Bitmap getAppIconBitmapCustomIcon(String packageName) {
        LoadCustomIcons loadCustomIcons = null;
        if (customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
            loadCustomIcons.load();
        }
        return customIconsEnable() ? loadCustomIcons.getIconForPackage(packageName, (appIconBitmap(packageName))) : (appIconBitmap(packageName));
    }

    public Drawable getAppIconDrawableCustomIcon(String packageName) {
        LoadCustomIcons loadCustomIcons = null;
        if (customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(context, customIconPackageName());
            loadCustomIcons.load();
        }
        return customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(packageName, (applicationIcon(packageName))) : (applicationIcon(packageName));
    }

    /*Action Center*/
    public Notification notificationCreator(String titleText, String contentText, int notificationId) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notificationBuilder = new Notification.Builder(context);
        notificationBuilder.setContentTitle(Html.fromHtml("<b><font color='" + PublicVariable.primaryColorOpposite + "'>" + titleText + "</font></b>", Html.FROM_HTML_MODE_COMPACT));
        notificationBuilder.setContentText(Html.fromHtml("<font color='" + PublicVariable.primaryColor + "'>" + contentText + "</font>", Html.FROM_HTML_MODE_COMPACT));
        notificationBuilder.setTicker(context.getResources().getString(R.string.app_name));
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setColor(context.getColor(R.color.default_color));
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

        Intent newUpdate = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.play_store_link) + context.getPackageName()));
        newUpdate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);;
        PendingIntent newUpdatePendingIntent = PendingIntent.getActivity(context, 5, newUpdate, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(context.getPackageName(), context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder.setChannelId(context.getPackageName());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Notification.Action.Builder builderActionNotification = new Notification.Action.Builder(
                    Icon.createWithResource(context, R.drawable.draw_share_menu),
                    context.getString(R.string.rateReview),
                    newUpdatePendingIntent
            );
            notificationBuilder.addAction(builderActionNotification.build());
        }
        notificationBuilder.setContentIntent(newUpdatePendingIntent);
        notificationManager.notify(notificationId, notificationBuilder.build());

        return notificationBuilder.build();
    }

    public Notification bindServiceNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notificationBuilder = new Notification.Builder(context);
        int notification_controller;
        if (returnAPI() < 24) {
            notification_controller = R.layout.notification_controller_low;
        } else {
            notification_controller = R.layout.notification_controller;
        }
        RemoteViews remoteNotification = new RemoteViews(context.getPackageName(), notification_controller);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        remoteNotification.setTextColor(R.id.recoverAll, PublicVariable.colorLightDarkOpposite);
        remoteNotification.setTextColor(R.id.removeAll, PublicVariable.colorLightDarkOpposite);
        remoteNotification.setTextColor(R.id.moveEdge, PublicVariable.colorLightDarkOpposite);
        remoteNotification.setTextColor(R.id.backEdge, PublicVariable.colorLightDarkOpposite);

        String sticky = sharedPreferences.getString("stick", "1");
        if (sticky.equals("1")) {
            remoteNotification.setTextViewText(R.id.moveEdge, context.getString(R.string.moveEdgeLeft));
            remoteNotification.setTextViewText(R.id.backEdge, context.getString(R.string.backToScreenLeft));
        } else if (sticky.equals("2")) {
            remoteNotification.setTextViewText(R.id.moveEdge, context.getString(R.string.moveEdgeRight));
            remoteNotification.setTextViewText(R.id.backEdge, context.getString(R.string.backToScreenRight));
        }

        Intent CancelStable = new Intent(context, RemoteController.class);
        CancelStable.putExtra("RemoteController", RemoteController.COMMANDS.CancelRemote);
        PendingIntent cancelPending = PendingIntent.getService(context, 0, CancelStable, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent RecoverAll = new Intent(context, RemoteController.class);
        RecoverAll.putExtra("RemoteController", RemoteController.COMMANDS.RecoverAll);
        PendingIntent pendingRecoverAll = PendingIntent.getService(context, 1, RecoverAll, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent RemoveAll = new Intent(context, RemoteController.class);
        RemoveAll.putExtra("RemoteController", RemoteController.COMMANDS.RemoveAll);
        PendingIntent pendingRemoveAll = PendingIntent.getService(context, 2, RemoveAll, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent Sticky_Edge = new Intent(context, RemoteController.class);
        Sticky_Edge.putExtra("RemoteController", RemoteController.COMMANDS.Sticky_Edge);
        PendingIntent pendingSticky_Edge = PendingIntent.getService(context, 3, Sticky_Edge, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent Sticky_Edge_No = new Intent(context, RemoteController.class);
        Sticky_Edge_No.putExtra("RemoteController", RemoteController.COMMANDS.Sticky_Edge_No);
        PendingIntent pendingSticky_Edge_No = PendingIntent.getService(context, 4, Sticky_Edge_No, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        remoteNotification.setOnClickPendingIntent(R.id.recoverAll, pendingRecoverAll);
        remoteNotification.setOnClickPendingIntent(R.id.removeAll, pendingRemoveAll);
        remoteNotification.setOnClickPendingIntent(R.id.moveEdge, pendingSticky_Edge);
        remoteNotification.setOnClickPendingIntent(R.id.backEdge, pendingSticky_Edge_No);

        notificationBuilder.setContentTitle(Html.fromHtml("<b><font color='" + PublicVariable.primaryColor + "'>" + context.getResources().getString(R.string.app_name) + "</font></b>", Html.FROM_HTML_MODE_COMPACT));
        notificationBuilder.setContentText(Html.fromHtml("<font color='" + PublicVariable.primaryColor + "'>" + context.getResources().getString(R.string.bindDesc) + "</font>", Html.FROM_HTML_MODE_COMPACT));
        notificationBuilder.setTicker(context.getResources().getString(R.string.app_name));
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setColor(PublicVariable.primaryColor);
        notificationBuilder.setColorized(true);
        notificationBuilder.setPriority(Notification.PRIORITY_MIN);

        Intent ListGrid = new Intent(context, Configurations.class);
        PendingIntent ListGridPendingIntent = PendingIntent.getActivity(context, 5, ListGrid, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(ListGridPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel notificationChannel = new NotificationChannel(context.getPackageName(), context.getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN);
                notificationManager.createNotificationChannel(notificationChannel);
                notificationBuilder.setChannelId(context.getPackageName());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();

                NotificationChannel notificationChannel = new NotificationChannel(context.getPackageName(), context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
                notificationBuilder.setChannelId(context.getPackageName());
            }
        }

        if (returnAPI() < 24) {
            notificationBuilder.setContent(remoteNotification);
        }
        Notification notification = notificationBuilder.build();
        if (returnAPI() >= 24) {
            notification.bigContentView = remoteNotification;
        }

        return notification;
    }

    /*Lite App Preferences*/
    public void liteAppPreferences(Activity instanceOfActivity) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor defaultSharedPreferencesEditor = defaultSharedPreferences.edit();

        /*OFF Control Panel*/
        defaultSharedPreferencesEditor.putBoolean("stable", false);
        context.stopService(new Intent(context, BindServices.class));

        /*Dark App Theme*/
        defaultSharedPreferencesEditor.putString(".themeColor", "2");

        /*OFF Floating Splash*/
        defaultSharedPreferencesEditor.putBoolean("floatingSplash", false);

        /*OFF Floating Transparency*/
        defaultSharedPreferencesEditor.putInt("autoTrans", 255);
        defaultSharedPreferencesEditor.putInt("autoTransProgress", 0);

        /*No Icon Theme*/
        saveDefaultPreference("customIcon", context.getPackageName());

        /*No Icon Shape*/
        defaultSharedPreferencesEditor.putInt("iconShape", 0);

        /*Apply All Lite Preferences*/
        defaultSharedPreferencesEditor.apply();

        saveDefaultPreference("LitePreferences", true);

        instanceOfActivity.finish();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                instanceOfActivity.startActivity(new Intent(context, Configurations.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }, 333);
    }

    public boolean litePreferencesEnabled() {
        PreferencesIO preferencesIO = new PreferencesIO(context);

        return preferencesIO.readDefaultPreference("LitePreferences", false);
    }

    /*In-App Purchase*/
    public boolean securityServicesSubscribed() {

        return (BuildConfig.DEBUG) ? true :
                readPreference(".SubscribedItem", InAppBillingData.SKU.InAppItemSecurityServices, false);
    }

    public boolean searchEngineSubscribed() {

        return (BuildConfig.DEBUG) ? false :
                readPreference(".SubscribedItem", InAppBillingData.SKU.InAppItemSearchEngines, false);
    }

    public boolean floatingWidgetsPurchased() {

        return (BuildConfig.DEBUG) ? false :
                readPreference(".PurchasedItem", InAppBillingData.SKU.InAppItemFloatingWidgets, false);
    }

    public boolean alreadyDonated() {

        return (BuildConfig.DEBUG) ? false :
                readPreference(".PurchasedItem", InAppBillingData.SKU.InAppItemDonation, false);
    }
}