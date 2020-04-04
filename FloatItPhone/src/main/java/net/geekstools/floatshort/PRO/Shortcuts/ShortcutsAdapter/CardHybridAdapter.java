/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 2:28 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsApplications;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassRunServices;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.util.ArrayList;

public class CardHybridAdapter extends RecyclerView.Adapter<CardHybridAdapter.ViewHolder> {

    Context context;

    FunctionsClass functionsClass;
    FunctionsClassRunServices functionsClassRunServices;

    ArrayList<AdapterItemsApplications> adapterItemsApplications;

    int layoutInflater, idRippleShape;

    View view;
    ViewHolder viewHolder;

    public CardHybridAdapter(Activity activity, Context context, ArrayList<AdapterItemsApplications> adapterItemsApplications) {
        this.context = context;
        this.adapterItemsApplications = adapterItemsApplications;

        functionsClass = new FunctionsClass(context);
        functionsClassRunServices = new FunctionsClassRunServices(context);

        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);
        PublicVariable.HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size, context.getResources().getDisplayMetrics());

        switch (functionsClass.shapesImageId()) {
            case 1:
                layoutInflater = R.layout.item_card_hybrid_droplet;
                idRippleShape = R.drawable.ripple_effect_shape_droplet;
                break;
            case 2:
                layoutInflater = R.layout.item_card_hybrid_circle;
                idRippleShape = R.drawable.ripple_effect_shape_circle;
                break;
            case 3:
                layoutInflater = R.layout.item_card_hybrid_square;
                idRippleShape = R.drawable.ripple_effect_shape_square;
                break;
            case 4:
                layoutInflater = R.layout.item_card_hybrid_squircle;
                idRippleShape = R.drawable.ripple_effect_shape_squircle;
                break;
            case 0:
                layoutInflater = R.layout.item_card_hybrid_noshape;
                idRippleShape = R.drawable.ripple_effect_no_bound;
                break;
            default:
                layoutInflater = R.layout.item_card_hybrid_noshape;
                idRippleShape = R.drawable.ripple_effect_no_bound;
                break;
        }
    }

    @Override
    public CardHybridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(layoutInflater, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolderBinder, final int position) {
        final LayerDrawable drawIndicator = (LayerDrawable) context.getDrawable(R.drawable.draw_recovery_indicator);
        final Drawable backIndicator = drawIndicator.findDrawableByLayerId(R.id.backgroundTemporary);
        backIndicator.setTint(PublicVariable.primaryColor);

        viewHolderBinder.shapedIcon.setImageDrawable(adapterItemsApplications.get(position).getAppIcon());
        viewHolderBinder.appName.setText(adapterItemsApplications.get(position).getAppName());
        viewHolderBinder.appName.setTextColor(PublicVariable.colorLightDarkOpposite);

        RippleDrawable drawItemRippleDrawable = (RippleDrawable) context.getDrawable(idRippleShape);
        drawItemRippleDrawable.setColor(ColorStateList.valueOf(functionsClass.extractDominantColor(adapterItemsApplications.get(position).getAppIcon())));
        viewHolderBinder.item.setBackground(drawItemRippleDrawable);

        try {
            //  viewHolderBinder.recoveryIndicator = viewHolderBinder.recoveryIndicator;
            viewHolderBinder.recoveryIndicator.setVisibility(View.INVISIBLE);
            if (functionsClass.loadRecoveryIndicator(adapterItemsApplications.get(position).getPackageName()) == true) {
                viewHolderBinder.recoveryIndicator.setVisibility(View.VISIBLE);
                viewHolderBinder.recoveryIndicator.setBackground(drawIndicator);

            } else {
                viewHolderBinder.recoveryIndicator.setVisibility(View.INVISIBLE);
            }
            viewHolderBinder.recoveryIndicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    functionsClass.removeLine(".uFile", adapterItemsApplications.get(position).getPackageName());
                    try {
                        viewHolderBinder.recoveryIndicator.setVisibility(View.INVISIBLE);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    functionsClass.updateRecoverShortcuts();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolderBinder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageName = adapterItemsApplications.get(position).getPackageName();
                String className = adapterItemsApplications.get(position).getClassName();

                functionsClassRunServices.runUnlimitedShortcutsService(packageName, className);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            viewHolderBinder.recoveryIndicator.setBackground(drawIndicator);
                            viewHolderBinder.recoveryIndicator.setVisibility(View.VISIBLE);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 177);
            }
        });
        viewHolderBinder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String packageName = adapterItemsApplications.get(position).getPackageName();
                String className = adapterItemsApplications.get(position).getClassName();

                functionsClass.popupOptionShortcuts(functionsClassRunServices, context, view,
                        packageName, className);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return adapterItemsApplications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout item;
        ShapesImage shapedIcon;
        TextView appName;
        Button recoveryIndicator;

        public ViewHolder(View view) {
            super(view);
            item = (RelativeLayout) view.findViewById(R.id.item);
            shapedIcon = (ShapesImage) view.findViewById(R.id.icon);
            appName = (TextView) view.findViewById(R.id.loadingDescription);
            recoveryIndicator = (Button) view.findViewById(R.id.recoveryIndicator);
        }
    }
}
