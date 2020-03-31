package net.geekstools.floatshort.PRO.SearchEngine.Data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsSearchEngine

class SearchEngineViewModel : ViewModel() {

    val allSearchData: MutableLiveData<ArrayList<AdapterItemsSearchEngine>> by lazy {
        MutableLiveData<ArrayList<AdapterItemsSearchEngine>>()
    }

    val allSearchResult: MutableLiveData<ArrayList<AdapterItemsSearchEngine>> by lazy {
        MutableLiveData<ArrayList<AdapterItemsSearchEngine>>()
    }

    override fun onCleared() {
        super.onCleared()
    }
}