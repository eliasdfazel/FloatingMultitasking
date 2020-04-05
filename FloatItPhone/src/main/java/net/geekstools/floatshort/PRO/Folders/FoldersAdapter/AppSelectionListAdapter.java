/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 2:26 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders.FoldersAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassSecurity;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.databinding.AdvanceAppSelectionListBinding;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.io.File;
import java.util.ArrayList;

public class AppSelectionListAdapter extends RecyclerView.Adapter<AppSelectionListAdapter.ViewHolder> {

    private Context context;

    FunctionsClass functionsClass;
    FunctionsClassSecurity functionsClassSecurity;

    ConfirmButtonProcessInterface confirmButtonProcessInterface;

    ImageView temporaryFallingIcon;

    float fromX, fromY, toX, toY, dpHeight, dpWidth, systemUiHeight;
    int animationType, layoutInflater;

    LoadCustomIcons loadCustomIcons;

    private ArrayList<AdapterItems> adapterItems;

    public AppSelectionListAdapter(Context context, AdvanceAppSelectionListBinding advanceAppSelectionListBinding, 
                                   ArrayList<AdapterItems> adapterItems,
                                   ConfirmButtonProcessInterface confirmButtonProcessInterface) {
        this.context = context;
        this.adapterItems = adapterItems;

        this.confirmButtonProcessInterface = confirmButtonProcessInterface;

        functionsClass = new FunctionsClass(context);
        functionsClassSecurity = new FunctionsClassSecurity(context);


        temporaryFallingIcon = functionsClass.initShapesImage(advanceAppSelectionListBinding.temporaryFallingIcon);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels;
        dpWidth = displayMetrics.widthPixels;
        systemUiHeight = PublicVariable.actionBarHeight;
        fromX = toX = PublicVariable.confirmButtonX;
        toY = PublicVariable.confirmButtonY;
        animationType = Animation.ABSOLUTE;

        switch (functionsClass.shapesImageId()) {
            case 1:
                layoutInflater = R.layout.selection_item_card_list_droplet;
                break;
            case 2:
                layoutInflater = R.layout.selection_item_card_list_circle;
                break;
            case 3:
                layoutInflater = R.layout.selection_item_card_list_square;
                break;
            case 4:
                layoutInflater = R.layout.selection_item_card_list_squircle;
                break;
            case 0:
                layoutInflater = R.layout.selection_item_card_list_noshape;
                break;
        }

        if (functionsClass.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(context, functionsClass.customIconPackageName());
        }
    }

    @Override
    public AppSelectionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(context).inflate(layoutInflater, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder viewHolderBinder, final int position) {
        try {
            final String packageName = adapterItems.get(position).getPackageName();
            File autoFile = context.getFileStreamPath(packageName + PublicVariable.categoryName);
            viewHolderBinder.autoChoice.setChecked(false);
            if (autoFile.exists()) {
                viewHolderBinder.autoChoice.setChecked(true);
            } else {
                viewHolderBinder.autoChoice.setChecked(false);
            }

            if (PublicVariable.themeLightDark) {
                viewHolderBinder.autoChoice.setButtonTintList(ColorStateList.valueOf(context.getColor(R.color.dark)));
            } else if (!PublicVariable.themeLightDark) {
                viewHolderBinder.appName.setTextColor(context.getColor(R.color.light));
                viewHolderBinder.autoChoice.setButtonTintList(ColorStateList.valueOf(context.getColor(R.color.light)));
            }

            viewHolderBinder.appIcon.setImageDrawable(adapterItems.get(position).getAppIcon());
            viewHolderBinder.appName.setText(adapterItems.get(position).getAppName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolderBinder.item.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fromY = -((dpHeight - motionEvent.getRawY()) - (systemUiHeight));
                        break;
                    case MotionEvent.ACTION_UP:
                        final String pack = adapterItems.get(position).getPackageName();
                        File autoFile = context.getFileStreamPath(pack + PublicVariable.categoryName);
                        if (autoFile.exists()) {
                            context.deleteFile(pack + PublicVariable.categoryName);
                            functionsClass.removeLine(PublicVariable.categoryName, adapterItems.get(position).getPackageName());
                            viewHolderBinder.autoChoice.setChecked(false);

                            confirmButtonProcessInterface.savedShortcutCounter();

                            confirmButtonProcessInterface.hideSavedShortcutList();
                            context.sendBroadcast(new Intent(context.getString(R.string.visibilityActionAdvance)));

                            if (functionsClassSecurity.isAppLocked(PublicVariable.categoryName)) {
                                functionsClassSecurity.doUnlockApps(adapterItems.get(position).getPackageName());
                            }
                        } else {
                            functionsClass.saveFile(
                                    pack + PublicVariable.categoryName, pack);
                            functionsClass.saveFileAppendLine(
                                    PublicVariable.categoryName, pack);
                            viewHolderBinder.autoChoice.setChecked(true);

                            if (functionsClassSecurity.isAppLocked(PublicVariable.categoryName)) {
                                functionsClassSecurity.doLockApps(adapterItems.get(position).getPackageName());
                            }

                            TranslateAnimation translateAnimation =
                                    new TranslateAnimation(animationType, fromX,
                                            animationType, toX,
                                            animationType, fromY,
                                            animationType, toY);
                            translateAnimation.setDuration((long) Math.abs(fromY));

                            temporaryFallingIcon.setImageDrawable(functionsClass.customIconsEnable() ?
                                    loadCustomIcons.getDrawableIconForPackage(adapterItems.get(position).getPackageName(), functionsClass.shapedAppIcon(adapterItems.get(position).getPackageName()))
                                    :
                                    functionsClass.shapedAppIcon(adapterItems.get(position).getPackageName()));
                            temporaryFallingIcon.startAnimation(translateAnimation);
                            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    confirmButtonProcessInterface.hideSavedShortcutList();
                                    context.sendBroadcast(new Intent(context.getString(R.string.visibilityActionAdvance)));

                                    temporaryFallingIcon.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    temporaryFallingIcon.setVisibility(View.INVISIBLE);

                                    context.sendBroadcast(new Intent(context.getString(R.string.animtaionActionAdvance)));
                                    confirmButtonProcessInterface.savedShortcutCounter();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return adapterItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout item;
        ShapesImage appIcon;
        TextView appName;
        CheckBox autoChoice;

        public ViewHolder(View view) {
            super(view);
            item = (RelativeLayout) view.findViewById(R.id.item);
            appIcon = (ShapesImage) view.findViewById(R.id.icon);
            appName = (TextView) view.findViewById(R.id.loadingDescription);
            autoChoice = (CheckBox) view.findViewById(R.id.autoChoice);
        }
    }
}
