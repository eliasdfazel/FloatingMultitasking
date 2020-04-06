/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 2:17 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FoldersAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.Folders.UI.AppsConfirmButton;
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;

import java.util.ArrayList;

public class AppSavedListAdapter extends BaseAdapter {

    private Context context;

    private FunctionsClass functionsClass;

    private AppsConfirmButton appsConfirmButton;

    private ConfirmButtonProcessInterface confirmButtonProcessInterface;

    private int splitNumber, layoutInflater;

    private ArrayList<AdapterItems> selectedAppsListItem;

    public AppSavedListAdapter(Context context, FunctionsClass functionsClass,
                               ArrayList<AdapterItems> selectedAppsListItem, int splitNumber,
                               AppsConfirmButton appsConfirmButton,
                               ConfirmButtonProcessInterface confirmButtonProcessInterface) {
        this.context = context;
        this.functionsClass = functionsClass;
        this.appsConfirmButton = appsConfirmButton;

        this.confirmButtonProcessInterface = confirmButtonProcessInterface;

        this.selectedAppsListItem = selectedAppsListItem;
        this.splitNumber = splitNumber;

        switch (functionsClass.shapesImageId()) {
            case 1:
                layoutInflater = R.layout.item_saved_app_droplet;
                break;
            case 2:
                layoutInflater = R.layout.item_saved_app_circle;
                break;
            case 3:
                layoutInflater = R.layout.item_saved_app_square;
                break;
            case 4:
                layoutInflater = R.layout.item_saved_app_squircle;
                break;
            case 0:
                layoutInflater = R.layout.item_saved_app_noshape;
                break;
        }
    }

    @Override
    public int getCount() {
        return selectedAppsListItem.size();
    }

    @Override
    public Object getItem(int position) {
        return selectedAppsListItem.get(position);
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
            viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.iconItem);
            viewHolder.textAppName = (TextView) convertView.findViewById(R.id.itemAppName);
            viewHolder.deleteItem = (Button) convertView.findViewById(R.id.deleteItem);
            viewHolder.confirmItem = (Button) convertView.findViewById(R.id.confirmItem);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (functionsClass.returnAPI() < 24) {
            viewHolder.confirmItem.setVisibility(View.INVISIBLE);
        }

        LayerDrawable drawConfirm = (LayerDrawable) context.getDrawable(R.drawable.ripple_effect_confirm);
        Drawable backConfirm = drawConfirm.findDrawableByLayerId(R.id.backgroundTemporary);
        backConfirm.setTint(PublicVariable.primaryColorOpposite);
        viewHolder.confirmItem.setBackground(drawConfirm);
        viewHolder.textAppName.setTextColor(context.getColor(R.color.light));

        viewHolder.imgIcon.setImageDrawable(selectedAppsListItem.get(position).getAppIcon());
        viewHolder.textAppName.setText(selectedAppsListItem.get(position).getAppName());

        viewHolder.items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.deleteFile(selectedAppsListItem.get(position).getPackageName()
                        + PublicVariable.categoryName);

                functionsClass.removeLine(PublicVariable.categoryName,
                        selectedAppsListItem.get(position).getPackageName());

                confirmButtonProcessInterface.shortcutDeleted();
                confirmButtonProcessInterface.savedShortcutCounter();
            }
        });
        viewHolder.confirmItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (splitNumber == 1) {
                    functionsClass.saveFile(PublicVariable.categoryName + ".SplitOne", selectedAppsListItem.get(position).getPackageName());
                } else if (splitNumber == 2) {
                    functionsClass.saveFile(PublicVariable.categoryName + ".SplitTwo", selectedAppsListItem.get(position).getPackageName());
                }

                confirmButtonProcessInterface.showSplitShortcutPicker();
                appsConfirmButton.makeItVisible();
            }
        });

        RippleDrawable rippleDrawableBack = (RippleDrawable) context.getDrawable(R.drawable.saved_app_shortcut_whole);
        Drawable gradientDrawableBack = rippleDrawableBack.findDrawableByLayerId(R.id.backgroundTemporary);
        Drawable gradientDrawableBackMask = rippleDrawableBack.findDrawableByLayerId(android.R.id.mask);

        rippleDrawableBack.setColor(ColorStateList.valueOf(PublicVariable.colorLightDark));
        gradientDrawableBack.setTint(PublicVariable.primaryColorOpposite);
        gradientDrawableBackMask.setTint(PublicVariable.colorLightDark);

        viewHolder.items.setBackground(rippleDrawableBack);

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout items;
        ImageView imgIcon;
        TextView textAppName;
        Button deleteItem;
        Button confirmItem;
    }
}
