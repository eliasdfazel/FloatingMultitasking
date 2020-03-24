/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:39 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.GeneralAdapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.floatshort.PRO.Preferences.PreferencesUtil;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

import java.util.ArrayList;

public class CustomIconsThemeAdapter extends RecyclerView.Adapter<CustomIconsThemeAdapter.ViewHolder> {

    FunctionsClass functionsClass;

    private Activity activity;
    private Context context;

    View view;
    ViewHolder viewHolder;

    Dialog dialog;

    private ArrayList<AdapterItems> adapterItems;

    public CustomIconsThemeAdapter(Activity activity, Context context, ArrayList<AdapterItems> adapterItems, Dialog dialog) {
        this.activity = activity;
        this.context = context;
        this.adapterItems = adapterItems;
        this.dialog = dialog;

        functionsClass = new FunctionsClass(context, activity);
    }

    @Override
    public CustomIconsThemeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.custom_icon_items, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolderBinder, final int position) {

        viewHolderBinder.appName.setText(adapterItems.get(position).getAppName());
        viewHolderBinder.appName.setTextColor(PublicVariable.colorLightDarkOpposite);

        viewHolderBinder.appIcon.setImageDrawable(adapterItems.get(position).getAppIcon());

        viewHolderBinder.customIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.saveDefaultPreference("customIcon", adapterItems.get(position).getPackageName());

                functionsClass.saveDefaultPreference("LitePreferences", false);
                if (functionsClass.loadCustomIcons()) {
                    LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, adapterItems.get(position).getPackageName());
                    loadCustomIcons.load();
                }

                functionsClass.saveDefaultPreference("LitePreferences", false);

                PreferencesUtil.Companion.getCUSTOM_DIALOGUE_DISMISS().setValue(true);
                dialog.dismiss();
            }
        });

        viewHolderBinder.customIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (functionsClass.loadCustomIcons()) {
                    LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, adapterItems.get(position).getPackageName());
                    loadCustomIcons.load();


                    functionsClass.Toast(String.valueOf(loadCustomIcons.getTotalIconsNumber()), Gravity.BOTTOM, PublicVariable.primaryColor);
                }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return adapterItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout customIcon;
        TextView appName;
        ImageView appIcon;

        public ViewHolder(View view) {
            super(view);
            customIcon = (RelativeLayout) view.findViewById(R.id.customIcon);
            appName = (TextView) view.findViewById(R.id.appName);
            appIcon = (ImageView) view.findViewById(R.id.appIcon);
        }
    }
}
