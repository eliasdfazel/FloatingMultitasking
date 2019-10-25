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
import net.geekstools.floatshort.PRO.Util.NavAdapter.AdapterItems;

import java.util.ArrayList;

public class SearchEngineAdapter extends BaseAdapter implements Filterable {

    private Context context;

    FunctionsClass functionsClass;

    int layoutInflater;

    private ArrayList<AdapterItems> dataListAllItems;

    public static ArrayList<AdapterItems> allSearchResultItems = new ArrayList<AdapterItems>();

    public SearchEngineAdapter(Context context, ArrayList<AdapterItems> allSearchResultItems) {
        this.context = context;

        SearchEngineAdapter.allSearchResultItems = allSearchResultItems;

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
        return allSearchResultItems.size();
    }

    @Override
    public Object getItem(int position) {
        return allSearchResultItems.get(position);
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

        int dominantColor = 0;

        /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/
        switch (allSearchResultItems.get(position).getSearchResultType()) {
            case SearchResultType.SearchShortcuts: {
                dominantColor = functionsClass.extractDominantColor(allSearchResultItems.get(position).getAppIcon());

                viewHolder.itemAppIcon.setImageDrawable(allSearchResultItems.get(position).getAppIcon());
                viewHolder.itemAppName.setText(allSearchResultItems.get(position).getAppName());

                break;
            }
            case SearchResultType.SearchFolders: {
                dominantColor = context.getColor(R.color.default_color);

                viewHolder.itemAppIcon.setImageDrawable(functionsClass.writeOnDrawable(
                        R.drawable.ic_launcher_balloon,
                        String.valueOf(allSearchResultItems.get(position).getCategory().charAt(0)).toUpperCase(),
                        context.getColor(R.color.light),
                        functionsClass.DpToPixel(85),
                        functionsClass.DpToPixel(120)));
                viewHolder.itemAppName.setText(allSearchResultItems.get(position).getCategory());

                break;
            }
            case SearchResultType.SearchWidgets: {
                dominantColor = functionsClass.extractDominantColor(allSearchResultItems.get(position).getAppWidgetProviderInfo().loadPreviewImage(context, DisplayMetrics.DENSITY_MEDIUM));

                viewHolder.itemAppIcon.setImageDrawable(allSearchResultItems.get(position).getAppWidgetProviderInfo().loadPreviewImage(context, DisplayMetrics.DENSITY_MEDIUM));
                viewHolder.itemAppName.setText(allSearchResultItems.get(position).getWidgetLabel());

                break;
            }
        }
        /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/

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
                /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/
                switch (allSearchResultItems.get(position).getSearchResultType()) {
                    case SearchResultType.SearchShortcuts: {
                        functionsClass
                                .runUnlimitedShortcutsService(allSearchResultItems.get(position).getPackageName());

                        break;
                    }
                    case SearchResultType.SearchFolders: {
                        functionsClass
                                .runUnlimitedFolderService(allSearchResultItems.get(position).getCategory());

                        break;
                    }
                    case SearchResultType.SearchWidgets: {
                        functionsClass
                                .runUnlimitedWidgetService(allSearchResultItems.get(position).getAppWidgetId(),
                                        allSearchResultItems.get(position).getWidgetLabel());

                        break;
                    }
                }
                /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/
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

    public static class SearchResultType {
        public static final int SearchShortcuts = 1;
        public static final int SearchFolders = 2;
        public static final int SearchWidgets = 3;
    }

    public class ListFilter extends Filter {

        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<AdapterItems>(allSearchResultItems);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<AdapterItems> matchValues = new ArrayList<AdapterItems>();
                for (AdapterItems dataItem : dataListAllItems) {
                    /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/
                    switch (dataItem.getSearchResultType()) {
                        case SearchResultType.SearchShortcuts: {
                            if (dataItem.getAppName().toLowerCase().contains(searchStrLowerCase)) {
                                matchValues.add(dataItem);
                            }

                            break;
                        }
                        case SearchResultType.SearchFolders: {
                            if (dataItem.getCategory().toLowerCase().contains(searchStrLowerCase)) {
                                matchValues.add(dataItem);
                            }

                            break;
                        }
                        case SearchResultType.SearchWidgets: {
                            if (dataItem.getWidgetLabel().toLowerCase().contains(searchStrLowerCase)) {
                                matchValues.add(dataItem);
                            }

                            break;
                        }
                    }
                    /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                allSearchResultItems = (ArrayList<AdapterItems>) results.values;
            } else {
                allSearchResultItems = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
