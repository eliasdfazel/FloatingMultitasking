package net.geekstools.floatshort.PRO.Shortcuts.NavAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.util.ArrayList;

public class CardGridAdapter extends BaseAdapter {

    FunctionsClass functionsClass;
    Button[] recoveryIndicator;
    String PackageName;
    int layoutInflater;
    private Context context;
    private Activity activity;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public CardGridAdapter(Activity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.activity = activity;
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        functionsClass = new FunctionsClass(context);

        PublicVariable.size = functionsClass.readDefaultPreference("floatingSize", 39);
        PublicVariable.HW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.size, context.getResources().getDisplayMetrics());

        recoveryIndicator = new Button[navDrawerItems.size()];

        switch (functionsClass.shapesImageId()) {
            case 1:
                layoutInflater = R.layout.item_card_grid_droplet;
                break;
            case 2:
                layoutInflater = R.layout.item_card_grid_circle;
                break;
            case 3:
                layoutInflater = R.layout.item_card_grid_square;
                break;
            case 4:
                layoutInflater = R.layout.item_card_grid_squircle;
                break;
            case 0:
                layoutInflater = R.layout.item_card_grid_noshape;
                break;
        }
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
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
            viewHolder.item = (RelativeLayout) convertView.findViewById(R.id.item);
            viewHolder.imgIcon = (ShapesImage) convertView.findViewById(R.id.icon);
            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final LayerDrawable drawIndicator = (LayerDrawable) context.getResources().getDrawable(R.drawable.draw_recovery_indicator);
        final GradientDrawable backIndicator = (GradientDrawable) drawIndicator.findDrawableByLayerId(R.id.backtemp);
        backIndicator.setColor(PublicVariable.primaryColor);

        viewHolder.imgIcon.setImageDrawable(navDrawerItems.get(position).getAppIcon());
        viewHolder.txtDesc.setText(navDrawerItems.get(position).getAppName());
        viewHolder.txtDesc.setTextColor(PublicVariable.colorLightDarkOpposite);

        try {
            recoveryIndicator[position] = (Button) convertView.findViewById(R.id.recoveryIndicator);
            recoveryIndicator[position].setVisibility(View.INVISIBLE);
            if (functionsClass.loadRecoveryIndicator(navDrawerItems.get(position).getPackageName()) == true) {
                recoveryIndicator[position].setVisibility(View.VISIBLE);
                recoveryIndicator[position].setBackground(drawIndicator);

            } else {
                recoveryIndicator[position].setVisibility(View.INVISIBLE);
            }
            recoveryIndicator[position].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    functionsClass.removeLine(".uFile", navDrawerItems.get(position).getPackageName());
                    try {
                        recoveryIndicator[position].setVisibility(View.INVISIBLE);
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
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageName = navDrawerItems.get(position).getPackageName();
                functionsClass.runUnlimitedShortcutsService(PackageName);

                try {
                    recoveryIndicator[position].setBackground(drawIndicator);
                    recoveryIndicator[position].setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PackageName = navDrawerItems.get(position).getPackageName();
                functionsClass.popupOptionShortcuts(context, view, PackageName);
                return true;
            }
        });

        RippleDrawable drawItem = (RippleDrawable) context.getResources().getDrawable(R.drawable.ripple_effect);
        GradientDrawable gradientDrawable = (GradientDrawable) drawItem.findDrawableByLayerId(android.R.id.mask);
        gradientDrawable.setColor(PublicVariable.themeTextColor);
        drawItem.setColor(ColorStateList.valueOf(PublicVariable.themeTextColor));
        viewHolder.item.setBackground(drawItem);

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout item;
        ShapesImage imgIcon;
        TextView txtDesc;
    }
}
