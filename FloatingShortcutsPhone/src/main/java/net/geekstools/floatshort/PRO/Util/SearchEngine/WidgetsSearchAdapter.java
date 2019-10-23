package net.geekstools.floatshort.PRO.Util.SearchEngine;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.DisplayMetrics;
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
import net.geekstools.floatshort.PRO.Util.NavAdapter.NavDrawerItem;

import java.util.ArrayList;

public class WidgetsSearchAdapter extends BaseAdapter implements Filterable {

    private Context context;

    FunctionsClass functionsClass;

    int layoutInflater;

    private ArrayList<NavDrawerItem> dataListAllItems;

    public static ArrayList<NavDrawerItem> widgetsSearchResultItems;

    public WidgetsSearchAdapter(Context context, ArrayList<NavDrawerItem> widgetsSearchResultItems) {
        this.context = context;
        this.widgetsSearchResultItems = widgetsSearchResultItems;

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
        return widgetsSearchResultItems.size();
    }

    @Override
    public Object getItem(int position) {
        return widgetsSearchResultItems.get(position);
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


        viewHolder.itemAppIcon.setImageDrawable(widgetsSearchResultItems.get(position).getAppWidgetProviderInfo().loadPreviewImage(context, DisplayMetrics.DENSITY_MEDIUM));
        viewHolder.itemAppName.setText(widgetsSearchResultItems.get(position).getWidgetLabel());

        int dominantColor = functionsClass.extractDominantColor(widgetsSearchResultItems.get(position).getAppIcon());
        RippleDrawable searchItemBackground = (RippleDrawable) context.getDrawable(R.drawable.background_search_items);
        Drawable backSearchItemBackground = searchItemBackground.findDrawableByLayerId(R.id.backgroundTemporary);
        Drawable frontSearchItemBackground = searchItemBackground.findDrawableByLayerId(R.id.frontTemporary);
        searchItemBackground.setColor(ColorStateList.valueOf(dominantColor));
        backSearchItemBackground.setTint(dominantColor);
        backSearchItemBackground.setAlpha(175);
        frontSearchItemBackground.setTint(PublicVariable.colorLightDark);

        viewHolder.searchItem.setBackground(searchItemBackground);
        viewHolder.itemAppName.setTextColor(PublicVariable.colorLightDarkOpposite);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass
                        .runUnlimitedWidgetService(widgetsSearchResultItems.get(position).getAppWidgetId(),
                                widgetsSearchResultItems.get(position).getWidgetLabel());
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
                    dataListAllItems = new ArrayList<NavDrawerItem>(widgetsSearchResultItems);
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
                    if (dataItem.getWidgetLabel().toLowerCase().contains(searchStrLowerCase)) {
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
                widgetsSearchResultItems = (ArrayList<NavDrawerItem>) results.values;
            } else {
                widgetsSearchResultItems = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
