/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SearchEngine.UI.Adapter;

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
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.ListFilter;
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchResultType;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsSearchEngine;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassRunServices;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.util.ArrayList;

public class SearchEngineAdapter extends BaseAdapter implements Filterable {

    private Context context;

    private FunctionsClass functionsClass;
    private FunctionsClassRunServices functionsClassRunServices;

    private int searchLayoutId;


    public static ArrayList<AdapterItemsSearchEngine> allSearchData = new ArrayList<AdapterItemsSearchEngine>();
    public static ArrayList<AdapterItemsSearchEngine> allSearchResults = new ArrayList<AdapterItemsSearchEngine>();

    public static boolean alreadyAuthenticatedSearchEngine = false;

    public static String SEARCH_ENGINE_USED_LOG = "search_engine_used";
    private static String SEARCH_ENGINE_QUERY_LOG = "search_engine_query";

    public SearchEngineAdapter(Context context, ArrayList<AdapterItemsSearchEngine> allSearchData) {
        this.context = context;

        SearchEngineAdapter.allSearchData = allSearchData;

        functionsClass = new FunctionsClass(context);
        functionsClassRunServices = new FunctionsClassRunServices(context);

        switch (functionsClass.shapesImageId()) {
            case 1:
                searchLayoutId = R.layout.search_items_droplet;

                break;
            case 2:
                searchLayoutId = R.layout.search_items_circle;

                break;
            case 3:
                searchLayoutId = R.layout.search_items_square;

                break;
            case 4:
                searchLayoutId = R.layout.search_items_squircle;

                break;
            case 0:
                searchLayoutId = R.layout.search_items;

                break;
            default:
                searchLayoutId = R.layout.search_items;

                break;
        }
    }

    @Override
    public int getCount() {

        return allSearchResults.size();
    }

    @Override
    public Object getItem(int position) {

        return allSearchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(this.searchLayoutId, null);

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
        switch (SearchEngineAdapter.allSearchResults.get(position).getSearchResultType()) {
            case SearchResultType.SearchShortcuts: {
                dominantColor = functionsClass.extractDominantColor(allSearchResults.get(position).getAppIcon());

                viewHolder.itemAppIcon.setImageDrawable(allSearchResults.get(position).getAppIcon());
                viewHolder.itemAppName.setText(allSearchResults.get(position).getAppName());

                break;
            }
            case SearchResultType.SearchFolders: {
                dominantColor = context.getColor(R.color.default_color);

                viewHolder.itemAppName.setText(allSearchResults.get(position).getFolderName());

                viewHolder.itemInitialLetter.setText(String.valueOf(allSearchResults.get(position).getFolderName().charAt(0)).toUpperCase());
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
                dominantColor = functionsClass.extractDominantColor(allSearchResults.get(position).getAppWidgetProviderInfo().loadPreviewImage(context, DisplayMetrics.DENSITY_MEDIUM));

                viewHolder.itemAppIcon.setImageDrawable(allSearchResults.get(position).getAppWidgetProviderInfo().loadPreviewImage(context, DisplayMetrics.DENSITY_MEDIUM));
                viewHolder.itemAppName.setText(allSearchResults.get(position).getWidgetLabel());

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

                switch (SearchEngineAdapter.allSearchResults.get(position).getSearchResultType()) {
                    case SearchResultType.SearchShortcuts: {
                        functionsClassRunServices
                                .runUnlimitedShortcutsService(allSearchResults.get(position).getPackageName(), allSearchResults.get(position).getClassName());

                        bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", allSearchResults.get(position).getPackageName());

                        break;
                    }
                    case SearchResultType.SearchFolders: {
                        functionsClass
                                .runUnlimitedFolderService(allSearchResults.get(position).getFolderName());

                        bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", allSearchResults.get(position).getFolderName());

                        break;
                    }
                    case SearchResultType.SearchWidgets: {
                        functionsClass
                                .runUnlimitedWidgetService(allSearchResults.get(position).getAppWidgetId(),
                                        allSearchResults.get(position).getWidgetLabel());

                        bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", allSearchResults.get(position).getWidgetLabel());

                        break;
                    }
                }

                firebaseAnalytics.logEvent(SearchEngineAdapter.SEARCH_ENGINE_QUERY_LOG, bundleSearchEngineQuery);
                /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {

        return new ListFilter(SearchEngineAdapter.this);
    }

    static class ViewHolder {
        RelativeLayout searchItem;
        ShapesImage itemAppIcon;
        TextView itemInitialLetter;
        TextView itemAppName;
    }
}
