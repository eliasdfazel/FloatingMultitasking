/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/13/20 9:16 AM
 * Last modified 1/13/20 9:14 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SearchEngine;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.AdapterItemsData.AdapterItemsSearchEngine;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassRunServices;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.util.ArrayList;

public class SearchEngineAdapter extends BaseAdapter implements Filterable {

    private Context context;

    FunctionsClass functionsClass;
    FunctionsClassRunServices functionsClassRunServices;

    int layoutInflater;

    private ArrayList<AdapterItemsSearchEngine> dataListAllItems;

    public static ArrayList<AdapterItemsSearchEngine> allSearchResultItems = new ArrayList<AdapterItemsSearchEngine>();

    public static boolean alreadyAuthenticatedSearchEngine = false;

    public static String SEARCH_ENGINE_USED_LOG = "search_engine_used";
    public static String SEARCH_ENGINE_Query_LOG = "search_engine_query";

    public SearchEngineAdapter(Context context, ArrayList<AdapterItemsSearchEngine> allSearchResultItems) {
        this.context = context;

        SearchEngineAdapter.allSearchResultItems = allSearchResultItems;

        functionsClass = new FunctionsClass(context);
        functionsClassRunServices = new FunctionsClassRunServices(context);

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
            viewHolder.itemAppIcon = (ShapesImage) convertView.findViewById(R.id.itemAppIcon);
            viewHolder.itemInitialLetter = (TextView) convertView.findViewById(R.id.itemInitialLetter);
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

                viewHolder.itemAppName.setText(allSearchResultItems.get(position).getFolderName());

                viewHolder.itemInitialLetter.setText(String.valueOf(allSearchResultItems.get(position).getFolderName().charAt(0)).toUpperCase());
                viewHolder.itemInitialLetter.setTextColor(PublicVariable.colorLightDarkOpposite);

                Drawable backgroundDrawable = null;
                try {
                    backgroundDrawable = functionsClass.shapesDrawables().mutate();
                    backgroundDrawable.setTint(PublicVariable.primaryColor);

                    viewHolder.itemAppIcon.setAlpha(0.59f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                viewHolder.itemAppIcon.setImageDrawable(backgroundDrawable);

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
                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle bundleSearchEngineQuery = new Bundle();

                switch (allSearchResultItems.get(position).getSearchResultType()) {
                    case SearchResultType.SearchShortcuts: {
                        functionsClassRunServices
                                .runUnlimitedShortcutsService(allSearchResultItems.get(position).getPackageName(), allSearchResultItems.get(position).getClassName());

                        bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", allSearchResultItems.get(position).getPackageName());

                        break;
                    }
                    case SearchResultType.SearchFolders: {
                        functionsClass
                                .runUnlimitedFolderService(allSearchResultItems.get(position).getFolderName());

                        bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", allSearchResultItems.get(position).getFolderName());

                        break;
                    }
                    case SearchResultType.SearchWidgets: {
                        functionsClass
                                .runUnlimitedWidgetService(allSearchResultItems.get(position).getAppWidgetId(),
                                        allSearchResultItems.get(position).getWidgetLabel());

                        bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", allSearchResultItems.get(position).getWidgetLabel());

                        break;
                    }
                }

                firebaseAnalytics.logEvent(SearchEngineAdapter.SEARCH_ENGINE_Query_LOG, bundleSearchEngineQuery);
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
        ShapesImage itemAppIcon;
        TextView itemInitialLetter;
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
                    dataListAllItems = new ArrayList<AdapterItemsSearchEngine>(allSearchResultItems);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<AdapterItemsSearchEngine> matchValues = new ArrayList<AdapterItemsSearchEngine>();
                for (AdapterItemsSearchEngine dataItem : dataListAllItems) {
                    /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/
                    switch (dataItem.getSearchResultType()) {
                        case SearchResultType.SearchShortcuts: {
                            if (dataItem.getAppName().toLowerCase().contains(searchStrLowerCase)) {
                                matchValues.add(dataItem);
                            }

                            break;
                        }
                        case SearchResultType.SearchFolders: {
                            if (dataItem.getFolderName().toLowerCase().contains(searchStrLowerCase)) {
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
                allSearchResultItems = (ArrayList<AdapterItemsSearchEngine>) results.values;
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
