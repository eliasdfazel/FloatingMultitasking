/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 12:47 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FoldersAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.Checkpoint;
import net.geekstools.floatshort.PRO.Folders.FoldersConfigurations;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassSecurity;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.InteractionObserver.InteractionObserver;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Utils.UI.Splash.FloatingSplash;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.util.ArrayList;

import static android.content.Context.ACCESSIBILITY_SERVICE;

public class PopupCategoryOptionAdapter extends BaseAdapter {

    private Context context;

    FunctionsClass functionsClass;
    FunctionsClassSecurity functionsClassSecurity;

    Drawable splitOne = null, splitTwo = null;
    LoadCustomIcons loadCustomIcons;

    private ArrayList<AdapterItems> adapterItems;

    private String folderName, classNameCommand;
    private int startId, layoutInflater, xPosition, yPosition, HW;

    public PopupCategoryOptionAdapter(Context context, ArrayList<AdapterItems> adapterItems, String folderName, String classNameCommand, int startId) {
        this.context = context;
        this.adapterItems = adapterItems;

        this.folderName = folderName;
        this.classNameCommand = classNameCommand;

        this.startId = startId;

        functionsClass = new FunctionsClass(context);
        functionsClassSecurity = new FunctionsClassSecurity(context);

        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);

        switch (functionsClass.shapesImageId()) {
            case 1:
                layoutInflater = R.layout.item_popup_category_droplet;
                break;
            case 2:
                layoutInflater = R.layout.item_popup_category_circle;
                break;
            case 3:
                layoutInflater = R.layout.item_popup_category_square;
                break;
            case 4:
                layoutInflater = R.layout.item_popup_category_squircle;
                break;
            case 0:
                layoutInflater = R.layout.item_popup_category_noshape;
                break;
        }

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(context, functionsClass.customIconPackageName());
        }
    }

    public PopupCategoryOptionAdapter(Context context, ArrayList<AdapterItems> adapterItems, String folderName, String classNameCommand, int startId,
                                      int xPosition, int yPosition, int HW) {
        this.context = context;
        this.adapterItems = adapterItems;

        this.folderName = folderName;
        this.classNameCommand = classNameCommand;

        this.startId = startId;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.HW = HW;

        functionsClass = new FunctionsClass(context);
        functionsClassSecurity = new FunctionsClassSecurity(context);

        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);

        switch (functionsClass.shapesImageId()) {
            case 1:
                layoutInflater = R.layout.item_popup_category_droplet;
                break;
            case 2:
                layoutInflater = R.layout.item_popup_category_circle;
                break;
            case 3:
                layoutInflater = R.layout.item_popup_category_square;
                break;
            case 4:
                layoutInflater = R.layout.item_popup_category_squircle;
                break;
            case 0:
                layoutInflater = R.layout.item_popup_category_noshape;
                break;
        }

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(context, functionsClass.customIconPackageName());
        }
    }

    @Override
    public int getCount() {
        return adapterItems.size();
    }

    @Override
    public Object getItem(int position) {
        return adapterItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutInflater, null);

            viewHolder = new ViewHolder();
            viewHolder.items = (RelativeLayout) convertView.findViewById(R.id.items);
            viewHolder.imgIcon = (ShapesImage) convertView.findViewById(R.id.iconItem);
            viewHolder.split_one = (ShapesImage) convertView.findViewById(R.id.split_one);
            viewHolder.split_two = (ShapesImage) convertView.findViewById(R.id.split_two);
            viewHolder.textAppName = (TextView) convertView.findViewById(R.id.itemAppName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            if (context.getFileStreamPath(adapterItems.get(position).getPackageName() + ".SplitOne").exists()
                    && context.getFileStreamPath(adapterItems.get(position).getPackageName() + ".SplitTwo").exists()
                    && adapterItems.get(position).getAppName().equals(context.getString(R.string.splitIt))) {
                splitOne = functionsClass.loadCustomIcons() ?
                        loadCustomIcons.getDrawableIconForPackage(functionsClass.readFile(adapterItems.get(position).getPackageName() + ".SplitOne"), functionsClass.shapedAppIcon(functionsClass.readFile(adapterItems.get(position).getPackageName() + ".SplitOne")))
                        :
                        functionsClass.shapedAppIcon(functionsClass.readFile(adapterItems.get(position).getPackageName() + ".SplitOne"));
                splitTwo = functionsClass.loadCustomIcons() ?
                        loadCustomIcons.getDrawableIconForPackage(functionsClass.readFile(adapterItems.get(position).getPackageName() + ".SplitTwo"), functionsClass.shapedAppIcon(functionsClass.readFile(adapterItems.get(position).getPackageName() + ".SplitTwo")))
                        :
                        functionsClass.shapedAppIcon(functionsClass.readFile(adapterItems.get(position).getPackageName() + ".SplitTwo"));

                viewHolder.split_one.setImageDrawable(splitOne);
                viewHolder.split_two.setImageDrawable(splitTwo);

                viewHolder.split_one.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
                viewHolder.split_two.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
            } else if (adapterItems.get(position).getAppName().equals(context.getString(R.string.splitIt))) {
                splitOne = functionsClass.loadCustomIcons() ?
                        loadCustomIcons.getDrawableIconForPackage(functionsClass.readFileLine(adapterItems.get(position).getPackageName())[0], functionsClass.shapedAppIcon(functionsClass.readFileLine(adapterItems.get(position).getPackageName())[0]))
                        :
                        functionsClass.shapedAppIcon(functionsClass.readFileLine(adapterItems.get(position).getPackageName())[0]);
                splitTwo = functionsClass.loadCustomIcons() ?
                        loadCustomIcons.getDrawableIconForPackage(functionsClass.readFileLine(adapterItems.get(position).getPackageName())[1], functionsClass.shapedAppIcon(functionsClass.readFileLine(adapterItems.get(position).getPackageName())[1]))
                        :
                        functionsClass.shapedAppIcon(functionsClass.readFileLine(adapterItems.get(position).getPackageName())[1]);

                viewHolder.split_one.setImageDrawable(splitOne);
                viewHolder.split_two.setImageDrawable(splitTwo);

                viewHolder.split_one.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
                viewHolder.split_two.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
            } else {
                viewHolder.split_one.setImageDrawable(null);
                viewHolder.split_two.setImageDrawable(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.imgIcon.setImageDrawable(adapterItems.get(position).getAppIcon());
        viewHolder.textAppName.setText(adapterItems.get(position).getAppName());

        viewHolder.imgIcon.setImageAlpha(functionsClass.readDefaultPreference("autoTrans", 255));
        viewHolder.textAppName.setAlpha(functionsClass.readDefaultPreference("autoTrans", 255) < 130 ? 0.70f : 1.0f);

        int itemsListColor;
        if (functionsClass.appThemeTransparent() == true) {
            itemsListColor = functionsClass.setColorAlpha(PublicVariable.colorLightDark, 77);
        } else {
            itemsListColor = PublicVariable.colorLightDark;
        }

        LayerDrawable drawPopupShortcut = (LayerDrawable) context.getDrawable(R.drawable.popup_shortcut_whole);
        Drawable backPopupShortcut = drawPopupShortcut.findDrawableByLayerId(R.id.backgroundTemporary);
        backPopupShortcut.setTint(itemsListColor);
        viewHolder.items.setBackground(drawPopupShortcut);
        viewHolder.textAppName.setTextColor(PublicVariable.colorLightDarkOpposite);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (adapterItems.get(position).getAppName().contains(context.getString(R.string.edit_category))) {
                        context.startActivity(new Intent(context, FoldersConfigurations.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else if (adapterItems.get(position).getAppName().contains(context.getString(R.string.remove_category))) {
                        context.sendBroadcast(new Intent("Remove_Category_" + classNameCommand).putExtra("startId", startId));
                    } else if (adapterItems.get(position).getAppName().contains(context.getString(R.string.unpin_category))) {
                        context.sendBroadcast(new Intent("Unpin_App_" + classNameCommand).putExtra("startId", startId));
                    } else if (adapterItems.get(position).getAppName().contains(context.getString(R.string.pin_category))) {
                        context.sendBroadcast(new Intent("Pin_App_" + classNameCommand).putExtra("startId", startId));
                    } else if (adapterItems.get(position).getAppName().contains(context.getString(R.string.splitIt))) {
                        if (functionsClassSecurity.isAppLocked(adapterItems.get(position).getPackageName()) || functionsClassSecurity.isAppLocked(folderName)) {
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthComponentName(folderName);
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthPairSplitIt(true);

                            FunctionsClassSecurity.AuthOpenAppValues.setAuthClassNameCommand(classNameCommand);

                            functionsClassSecurity.openAuthInvocation();
                        } else {
                            if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(InteractionObserver.class)) {
                                context.startActivity(new Intent(context, Checkpoint.class)
                                        .putExtra(context.getString(R.string.splitIt), context.getPackageName())
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            } else {
                                final AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(ACCESSIBILITY_SERVICE);
                                AccessibilityEvent event = AccessibilityEvent.obtain();
                                event.setSource(view);
                                event.setEventType(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                                event.setAction(10296);
                                event.setClassName(classNameCommand);
                                event.getText().add(context.getPackageName());
                                accessibilityManager.sendAccessibilityEvent(event);
                            }
                        }
                    } else {
                        if (functionsClassSecurity.isAppLocked(adapterItems.get(position).getPackageName()) || functionsClassSecurity.isAppLocked(folderName)) {
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthComponentName(adapterItems.get(position).getPackageName());

                            FunctionsClassSecurity.AuthOpenAppValues.setAuthPositionX(xPosition);
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthPositionY(yPosition);
                            FunctionsClassSecurity.AuthOpenAppValues.setAuthHW(HW);

                            functionsClassSecurity.openAuthInvocation();
                        } else if (functionsClass.splashReveal()) {
                            Intent splashReveal = new Intent(context, FloatingSplash.class);
                            splashReveal.putExtra("packageName", adapterItems.get(position).getPackageName());
                            splashReveal.putExtra("X", xPosition);
                            splashReveal.putExtra("Y", yPosition);
                            splashReveal.putExtra("HW", HW);
                            context.startService(splashReveal);
                        } else {
                            if (functionsClass.FreeForm()) {
                                functionsClass.openApplicationFreeForm(adapterItems.get(position).getPackageName(),
                                        xPosition,
                                        (functionsClass.displayX() / 2),
                                        yPosition,
                                        (functionsClass.displayY() / 2)
                                );
                            } else {
                                functionsClass.appsLaunchPad(adapterItems.get(position).getPackageName());
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                context.sendBroadcast(new Intent("Hide_PopupListView_Category"));
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (functionsClass.returnAPI() >= 24) {
                    if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(InteractionObserver.class)) {
                        context.startActivity(new Intent(context, Checkpoint.class)
                                .putExtra(context.getString(R.string.splitIt), context.getPackageName())
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        PublicVariable.splitSinglePackage = adapterItems.get(position).getPackageName();
                        if (functionsClass.appIsInstalled(PublicVariable.splitSinglePackage)) {
                            final AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(ACCESSIBILITY_SERVICE);
                            AccessibilityEvent event = AccessibilityEvent.obtain();
                            event.setSource(view);
                            event.setEventType(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                            event.setAction(69201);
                            event.setClassName(classNameCommand);
                            event.getText().add(context.getPackageName());
                            accessibilityManager.sendAccessibilityEvent(event);
                        }
                    }
                }
                context.sendBroadcast(new Intent("Hide_PopupListView_Category"));
                return true;
            }
        });

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout items;
        ShapesImage imgIcon;
        ShapesImage split_one;
        ShapesImage split_two;
        TextView textAppName;
    }
}
