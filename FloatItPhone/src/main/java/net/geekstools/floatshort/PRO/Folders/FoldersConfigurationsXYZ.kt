/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 1:41 PM
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

    private val functionsClassDataActivity: FunctionsClassDataActivity by lazy {
        FunctionsClassDataActivity(this@FoldersConfigurationsXYZ)
    }

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }
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