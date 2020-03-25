/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/25/20 2:16 PM
 * Last modified 3/25/20 1:49 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.SearchEngine.SearchEngineAdapter
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsSearchEngine
import net.geekstools.floatshort.PRO.Utils.Functions.*
import java.util.*

class FoldersConfigurationsXYZ : AppCompatActivity()/*, View.OnClickListener, OnLongClickListener, GestureListenerInterface*/ {

    var functionsClassDataActivity: FunctionsClassDataActivity? = null

    var functionsClass: FunctionsClass? = null
    var functionsClassSecurity: FunctionsClassSecurity? = null
    var functionsClassDialogues: FunctionsClassDialogues? = null
    var functionsClassRunServices: FunctionsClassRunServices? = null

    /*Search Engine*/
    var searchRecyclerViewAdapter: SearchEngineAdapter? = null
    /*Search Engine*/

    /*Search Engine*/
    var categoryListAdapter: RecyclerView.Adapter<*>? = null
    var adapterItems: ArrayList<AdapterItems>? = null
    var searchAdapterItems: ArrayList<AdapterItemsSearchEngine>? = null
}