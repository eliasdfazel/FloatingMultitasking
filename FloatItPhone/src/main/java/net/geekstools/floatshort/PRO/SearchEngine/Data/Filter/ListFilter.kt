package net.geekstools.floatshort.PRO.SearchEngine.Data.Filter

import android.widget.Filter
import net.geekstools.floatshort.PRO.SearchEngine.UI.Adapter.SearchEngineAdapter
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsSearchEngine
import java.util.*
import kotlin.collections.ArrayList

class ListFilter(private val searchEngineAdapter: SearchEngineAdapter) : Filter() {

    private val lock = Object()

    override fun performFiltering(prefix: CharSequence?): FilterResults {
        val results = FilterResults()

        if (prefix == null || prefix.isEmpty()) {
            SearchEngineAdapter.allSearchData?.let {
                synchronized(lock) {
                    results.values = it
                    results.count = it.size
                }
            }
        } else {
            val searchStrLowerCase = prefix.toString().toLowerCase(Locale.getDefault())

            val matchValues = ArrayList<AdapterItemsSearchEngine>()

            SearchEngineAdapter.allSearchData?.let {

                for (dataItem in it) {
                    when (dataItem.searchResultType) {
                        SearchResultType.SearchShortcuts -> {

                            if (dataItem.AppName!!.toLowerCase(Locale.getDefault()).contains(searchStrLowerCase)) {
                                matchValues.add(dataItem)
                            }
                        }
                        SearchResultType.SearchFolders -> {

                            if (dataItem.folderName!!.toLowerCase(Locale.getDefault()).contains(searchStrLowerCase)) {
                                matchValues.add(dataItem)
                            }
                        }
                        SearchResultType.SearchWidgets -> {

                            if (dataItem.widgetLabel!!.toLowerCase(Locale.getDefault()).contains(searchStrLowerCase)) {
                                matchValues.add(dataItem)
                            }
                        }
                    }
                }
            }

            results.values = matchValues
            results.count = matchValues.size
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        results?.let {
            if (it.values != null) {
                SearchEngineAdapter.allSearchResults = it.values as ArrayList<AdapterItemsSearchEngine>
            } else {
                SearchEngineAdapter.allSearchResults = null
            }

            if (it.count > 0) {
                searchEngineAdapter.notifyDataSetChanged()
            } else {
                searchEngineAdapter.notifyDataSetInvalidated()
            }
        }
    }
}