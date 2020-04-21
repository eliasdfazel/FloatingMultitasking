/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/21/20 6:14 AM
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

public class SavedAppsListPopupAdapter extends BaseAdapter {


    private int layoutInflater;


    public SavedAppsListPopupAdapter(Context context, FunctionsClass functionsClass,
                                     ArrayList<AdapterItems> selectedAppsListItem, int splitNumber,
                                     AppsConfirmButton appsConfirmButton,
                                     ConfirmButtonProcessInterface confirmButtonProcessInterface) {

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
    public AdapterItems getItem(int position) {
        return selectedAppsListItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder savedAppsListPopupAdapterViewHolder = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutInflater, null);

            savedAppsListPopupAdapterViewHolder = new ViewHolder();
            savedAppsListPopupAdapterViewHolder.items = (RelativeLayout) convertView.findViewById(R.id.items);
            savedAppsListPopupAdapterViewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.iconViewItem);
            savedAppsListPopupAdapterViewHolder.textAppName = (TextView) convertView.findViewById(R.id.titleViewItem);
            savedAppsListPopupAdapterViewHolder.deleteItem = (Button) convertView.findViewById(R.id.deleteItem);
            savedAppsListPopupAdapterViewHolder.confirmItem = (Button) convertView.findViewById(R.id.confirmItem);
            convertView.setTag(savedAppsListPopupAdapterViewHolder);
        } else {
            savedAppsListPopupAdapterViewHolder = (ViewHolder) convertView.getTag();
        }

        if (functionsClass.returnAPI() < 24) {
            savedAppsListPopupAdapterViewHolder.confirmItem.setVisibility(View.INVISIBLE);
        }

        LayerDrawable drawConfirm = (LayerDrawable) context.getDrawable(R.drawable.ripple_effect_confirm);
        Drawable backConfirm = drawConfirm.findDrawableByLayerId(R.id.backgroundTemporary);
        backConfirm.setTint(PublicVariable.primaryColorOpposite);
        savedAppsListPopupAdapterViewHolder.confirmItem.setBackground(drawConfirm);
        savedAppsListPopupAdapterViewHolder.textAppName.setTextColor(context.getColor(R.color.light));

        savedAppsListPopupAdapterViewHolder.imgIcon.setImageDrawable(selectedAppsListItem.get(position).getAppIcon());
        savedAppsListPopupAdapterViewHolder.textAppName.setText(selectedAppsListItem.get(position).getAppName());

        savedAppsListPopupAdapterViewHolder.items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        savedAppsListPopupAdapterViewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
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
        savedAppsListPopupAdapterViewHolder.confirmItem.setOnClickListener(new View.OnClickListener() {
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

        savedAppsListPopupAdapterViewHolder.items.setBackground(rippleDrawableBack);

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
