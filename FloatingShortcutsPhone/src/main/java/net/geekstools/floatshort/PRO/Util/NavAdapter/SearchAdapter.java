package net.geekstools.floatshort.PRO.Util.NavAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter implements Filterable {

    private Context context;

    FunctionsClass functionsClass;

    int layoutInflater;

    private ArrayList<NavDrawerItem> navDrawerItems, dataListAllItems;

    public SearchAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;

        functionsClass = new FunctionsClass(context);

        switch (functionsClass.shapesImageId()) {
            case 1:
                layoutInflater = R.layout.search_items_droplet;

                break;
            case 2:
                layoutInflater = R.layout.search_items_circle;

                break;
            case 3:
                layoutInflater = R.layout.search_items_square;

                break;
            case 4:
                layoutInflater = R.layout.search_items_squircle;

                break;
            case 0:
                layoutInflater = R.layout.search_items;

                break;
            default:
                layoutInflater = R.layout.search_items;

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
            viewHolder.searchItem = (RelativeLayout) convertView.findViewById(R.id.searchItem);
            viewHolder.itemAppIcon = (ImageView) convertView.findViewById(R.id.itemAppIcon);
            viewHolder.itemAppName = (TextView) convertView.findViewById(R.id.itemAppName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.itemAppIcon.setImageDrawable(navDrawerItems.get(position).getAppIcon());
        viewHolder.itemAppName.setText(navDrawerItems.get(position).getAppName());

        int itemsListColor = 0;
        if (functionsClass.appThemeTransparent() == true) {
            itemsListColor = functionsClass.setColorAlpha(PublicVariable.colorLightDark, 50);
        } else {
            itemsListColor = PublicVariable.colorLightDark;
        }

        LayerDrawable drawPopupShortcut = (LayerDrawable) context.getDrawable(R.drawable.popup_shortcut_whole);
        Drawable backPopupShortcut = drawPopupShortcut.findDrawableByLayerId(R.id.backtemp);
        backPopupShortcut.setTint(itemsListColor);
        viewHolder.searchItem.setBackground(drawPopupShortcut);
        viewHolder.itemAppName.setTextColor(PublicVariable.colorLightDarkOpposite);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass
                        .runUnlimitedShortcutsService(navDrawerItems.get(position).getPackageName());
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new ListFilter();
    }

    static class ViewHolder {
        RelativeLayout searchItem;
        ImageView itemAppIcon;
        TextView itemAppName;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<NavDrawerItem>(navDrawerItems);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<NavDrawerItem> matchValues = new ArrayList<NavDrawerItem>();
                for (NavDrawerItem dataItem : dataListAllItems) {
                    if (dataItem.getAppName().toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                navDrawerItems = (ArrayList<NavDrawerItem>) results.values;
            } else {
                navDrawerItems = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
