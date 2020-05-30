/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/29/20 7:37 PM
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
import android.text.Html;
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
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchListFilter;
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchResultType;
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine;
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

    public static boolean alreadyAuthenticatedSearchEngine = false;

    public static String SEARCH_ENGINE_USED_LOG = "search_engine_used";
    private static String SEARCH_ENGINE_QUERY_LOG = "search_engine_query";

    public SearchEngineAdapter(Context context, ArrayList<AdapterItemsSearchEngine> allSearchData) {
        this.context = context;

        SearchEngine.Companion.setAllSearchData(allSearchData);

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

        return SearchEngine.Companion.getAllSearchResults().size();
    }

    @Override
    public Object getItem(int position) {

        return SearchEngine.Companion.getAllSearchResults().get(position);
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
            viewHolder.itemAppName = (TextView) convertView.findViewById(R.id.titleViewItem);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int dominantColor = 0;

        /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/
        switch (SearchEngine.Companion.getAllSearchResults().get(position).getSearchResultType()) {
            case SearchResultType.SearchShortcuts: {
                dominantColor = functionsClass.extractDominantColor(SearchEngine.Companion.getAllSearchResults().get(position).getAppIcon());

                viewHolder.itemAppIcon.setImageDrawable(SearchEngine.Companion.getAllSearchResults().get(position).getAppIcon());
                viewHolder.itemAppName.setText(SearchEngine.Companion.getAllSearchResults().get(position).getAppName());

                viewHolder.itemInitialLetter.setText("");

                break;
            }
            case SearchResultType.SearchFolders: {
                dominantColor = context.getColor(R.color.default_color);

                viewHolder.itemAppName.setText(Html.fromHtml(SearchEngine.Companion.getAllSearchResults().get(position).getFolderName()
                        + " "
                        + context.getString(R.string.searchFolderHint)));

                viewHolder.itemInitialLetter.setText(String.valueOf(SearchEngine.Companion.getAllSearchResults().get(position).getFolderName().charAt(0)).toUpperCase());
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
                dominantColor = functionsClass.extractDominantColor(SearchEngine.Companion.getAllSearchResults().get(position).getAppWidgetProviderInfo().loadPreviewImage(context, DisplayMetrics.DENSITY_MEDIUM));

                viewHolder.itemAppIcon.setImageDrawable(SearchEngine.Companion.getAllSearchResults()
                        .get(position).getAppWidgetProviderInfo().loadPreviewImage(context, DisplayMetrics.DENSITY_MEDIUM));

                viewHolder.itemAppName.setText(Html.fromHtml(SearchEngine.Companion.getAllSearchResults().get(position).getWidgetLabel()
                        + " "
                        + context.getString(R.string.searchWidgetHint)));


                viewHolder.itemInitialLetter.setText("");

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

                switch (SearchEngine.Companion.getAllSearchResults().get(position).getSearchResultType()) {
                    case SearchResultType.SearchShortcuts: {
                        functionsClassRunServices
                                .runUnlimitedShortcutsService(SearchEngine.Companion.getAllSearchResults().get(position).getPackageName(), SearchEngine.Companion.getAllSearchResults().get(position).getClassName());

                        bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", SearchEngine.Companion.getAllSearchResults().get(position).getPackageName());

                        break;
                    }
                    case SearchResultType.SearchFolders: {
                        functionsClassRunServices
                                .runUnlimitedFoldersService(SearchEngine.Companion.getAllSearchResults().get(position).getFolderName());

                        bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", SearchEngine.Companion.getAllSearchResults().get(position).getFolderName());

                        break;
                    }
                    case SearchResultType.SearchWidgets: {
                        functionsClass
                                .runUnlimitedWidgetService(SearchEngine.Companion.getAllSearchResults().get(position).getAppWidgetId(),
                                        SearchEngine.Companion.getAllSearchResults().get(position).getWidgetLabel());

                        bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", SearchEngine.Companion.getAllSearchResults().get(position).getWidgetLabel());

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

        return new SearchListFilter(SearchEngineAdapter.this);
    }

    static class ViewHolder {
        RelativeLayout searchItem;
        ShapesImage itemAppIcon;
        TextView itemInitialLetter;
        TextView itemAppName;
    }
}
