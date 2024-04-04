/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.AdapterDataItem;

import android.app.Dialog;
import android.content.Context;
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
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

import java.util.ArrayList;

public class CustomIconsThemeAdapter extends RecyclerView.Adapter<CustomIconsThemeAdapter.ViewHolder> {

    private FunctionsClassLegacy functionsClassLegacy;

    private PreferencesUtil preferencesUtil;

    private Context context;

    private Dialog dialog;

    private ArrayList<AdapterItems> adapterItems;

    public CustomIconsThemeAdapter(PreferencesUtil preferencesUtil, Context context, ArrayList<AdapterItems> adapterItems, Dialog dialog) {
        this.preferencesUtil = preferencesUtil;
        this.context = context;
        this.adapterItems = adapterItems;
        this.dialog = dialog;

        functionsClassLegacy = new FunctionsClassLegacy(context);
    }

    @Override
    public CustomIconsThemeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_icon_items, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolderBinder, final int position) {

        viewHolderBinder.appName.setText(adapterItems.get(position).getAppName());
        viewHolderBinder.appName.setTextColor(PublicVariable.colorLightDarkOpposite);

        viewHolderBinder.appIcon.setImageDrawable(adapterItems.get(position).getAppIcon());

        viewHolderBinder.customIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClassLegacy.saveDefaultPreference("customIcon", adapterItems.get(position).getPackageName());

                functionsClassLegacy.saveDefaultPreference("LitePreferences", false);
                if (functionsClassLegacy.customIconsEnable()) {
                    LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, adapterItems.get(position).getPackageName());
                    loadCustomIcons.load();
                }

                functionsClassLegacy.saveDefaultPreference("LitePreferences", false);

                preferencesUtil.getCustomDialogueDismiss().postValue(true);
                dialog.dismiss();
            }
        });

        viewHolderBinder.customIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (functionsClassLegacy.customIconsEnable()) {
                    LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, adapterItems.get(position).getPackageName());
                    loadCustomIcons.load();


                    functionsClassLegacy.Toast(String.valueOf(loadCustomIcons.getTotalIconsNumber()), PublicVariable.primaryColor);
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
