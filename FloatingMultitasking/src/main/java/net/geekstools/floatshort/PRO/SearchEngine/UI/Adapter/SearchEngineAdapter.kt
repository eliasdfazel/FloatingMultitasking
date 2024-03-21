/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/14/20 11:43 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SearchEngine.UI.Adapter

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchListFilter
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchResultType
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine.Companion.allSearchResults
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsSearchEngine
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.imageview.customshapes.ShapesImage

class SearchEngineAdapter (val context: Context, val allSearchData: ArrayList<AdapterItemsSearchEngine>) : BaseAdapter(), Filterable {

    private val functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(context)

    private val floatingServices: FloatingServices = FloatingServices(context)

    private var searchLayoutId: Int = R.layout.search_items

    companion object {
        var alreadyAuthenticatedSearchEngine = false

        const val SEARCH_ENGINE_USED_LOG = "search_engine_used"
        const val SEARCH_ENGINE_QUERY_LOG = "search_engine_query"
    }

    init {
        SearchEngine.allSearchData = allSearchData

        searchLayoutId = when (functionsClassLegacy.shapesImageId()) {
            1 -> R.layout.search_items_droplet
            2 -> R.layout.search_items_circle
            3 -> R.layout.search_items_square
            4 -> R.layout.search_items_squircle
            0 -> R.layout.search_items
            else -> R.layout.search_items
        }
    }

    internal class ViewHolder {
        var searchItem: RelativeLayout? = null
        var itemAppIcon: ShapesImage? = null
        var itemInitialLetter: TextView? = null
        var itemAppName: TextView? = null
    }

    override fun getCount(): Int {

        return allSearchResults.size
    }

    override fun getItem(position: Int): Any {

        return allSearchResults[position]
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getView(position: Int, initialConvertView: View?, parent: ViewGroup?): View {

        var convertView = initialConvertView
        var viewHolder: SearchEngineAdapter.ViewHolder? = null

        if (convertView == null) {

            val layoutInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(searchLayoutId, null)

            viewHolder = SearchEngineAdapter.ViewHolder()
            viewHolder.searchItem = convertView.findViewById<View>(R.id.searchItem) as RelativeLayout
            viewHolder.itemAppIcon = convertView.findViewById<View>(R.id.itemAppIcon) as ShapesImage
            viewHolder.itemInitialLetter = convertView.findViewById<View>(R.id.itemInitialLetter) as TextView
            viewHolder.itemAppName = convertView.findViewById<View>(R.id.titleViewItem) as TextView

            convertView.tag = viewHolder

        } else {

            viewHolder = convertView.tag as SearchEngineAdapter.ViewHolder
        }

        convertView!!
        var dominantColor = 0

        /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/
        when (allSearchResults[position].searchResultType) {
            SearchResultType.SearchShortcuts -> {

                dominantColor = functionsClassLegacy.extractDominantColor(allSearchResults[position].appIcon)

                viewHolder.itemAppIcon?.setImageDrawable(allSearchResults[position].appIcon)
                viewHolder.itemAppName?.text = allSearchResults[position].appName

                viewHolder.itemInitialLetter?.text = ""

            }
            SearchResultType.SearchFolders -> {

                dominantColor = context.getColor(R.color.default_color)

                viewHolder.itemAppName?.text = Html.fromHtml(allSearchResults[position].folderName
                        + " "
                        + context.getString(R.string.searchFolderHint), Html.FROM_HTML_MODE_COMPACT)

                viewHolder.itemInitialLetter?.text = allSearchResults[position].folderName!![0].toString().toUpperCase()
                viewHolder.itemInitialLetter?.setTextColor(PublicVariable.colorLightDarkOpposite)

                var backgroundDrawable: Drawable? = null
                try {
                    backgroundDrawable = functionsClassLegacy.shapesDrawables().mutate()
                    backgroundDrawable.setTint(PublicVariable.primaryColor)
                    viewHolder.itemAppIcon?.alpha = 0.59f
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                viewHolder.itemAppIcon?.setImageDrawable(backgroundDrawable)

            }
            SearchResultType.SearchWidgets -> {

                dominantColor = functionsClassLegacy.extractDominantColor(allSearchResults[position].appWidgetProviderInfo!!.loadPreviewImage(context, DisplayMetrics.DENSITY_MEDIUM))

                viewHolder.itemAppIcon?.setImageDrawable(allSearchResults[position].appWidgetProviderInfo!!.loadPreviewImage(context, DisplayMetrics.DENSITY_MEDIUM))

                viewHolder.itemAppName?.text = Html.fromHtml(allSearchResults[position].widgetLabel
                        + " "
                        + context.getString(R.string.searchWidgetHint), Html.FROM_HTML_MODE_COMPACT)


                viewHolder.itemInitialLetter?.text = ""

            }
        }
        /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/

        /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/
        val searchItemBackground = context.getDrawable(R.drawable.background_search_items) as RippleDrawable
        val backSearchItemBackground = searchItemBackground.findDrawableByLayerId(R.id.backgroundTemporary)
        val frontSearchItemBackground = searchItemBackground.findDrawableByLayerId(R.id.frontTemporary)
        searchItemBackground.setColor(ColorStateList.valueOf(dominantColor))
        backSearchItemBackground.setTint(dominantColor)
        backSearchItemBackground.alpha = 175
        frontSearchItemBackground.setTint(PublicVariable.colorLightDark)

        viewHolder.searchItem?.background = searchItemBackground
        viewHolder.itemAppName?.setTextColor(PublicVariable.colorLightDarkOpposite)

        convertView.setOnClickListener {

            val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
            val bundleSearchEngineQuery = Bundle()
            when (allSearchResults[position].searchResultType) {
                SearchResultType.SearchShortcuts -> {
                    allSearchResults[position].className?.let { className ->
                        floatingServices
                            .runUnlimitedShortcutsService(allSearchResults[position].packageName!!,
                                className
                            )
                    }
                    bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", allSearchResults[position].packageName)
                }
                SearchResultType.SearchFolders -> {
                    floatingServices
                            .runUnlimitedFoldersService(allSearchResults[position].folderName!!)
                    bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", allSearchResults[position].folderName)
                }
                SearchResultType.SearchWidgets -> {
                    functionsClassLegacy
                            .runUnlimitedWidgetService(allSearchResults[position].appWidgetId!!,
                                    allSearchResults[position].widgetLabel)
                    bundleSearchEngineQuery.putString("QUERY_USED_SEARCH_ENGINE", allSearchResults[position].widgetLabel)
                }
            }
            firebaseAnalytics.logEvent(SearchEngineAdapter.SEARCH_ENGINE_QUERY_LOG, bundleSearchEngineQuery)
            /*Add Switch To Change for Different Type: Apps/Folders/Widgets*/

        }

        return convertView
    }

    override fun getFilter(): Filter {

        return SearchListFilter(this@SearchEngineAdapter)
    }

}