/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/12/20 11:47 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SearchEngine.Widgets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.SearchEngineWidgetActivityBinding

class WidgetActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = Firebase.auth

    lateinit var searchEngineWidgetActivityBinding: SearchEngineWidgetActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchEngineWidgetActivityBinding = SearchEngineWidgetActivityBinding.inflate(layoutInflater)
        setContentView(searchEngineWidgetActivityBinding.root)

        val functionsClassLegacy = FunctionsClassLegacy(applicationContext)
        val fileIO = FileIO(applicationContext)
        val floatingServices = FloatingServices(applicationContext)
        val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClassLegacy.customIconPackageName())

        /*Search Engine*/
        SearchEngine(activity = this@WidgetActivity, context = applicationContext,
                searchEngineViewBinding = searchEngineWidgetActivityBinding.searchEngineViewInclude,
                functionsClassLegacy = functionsClassLegacy,
                fileIO = fileIO,
                floatingServices = floatingServices,
                customIcons = loadCustomIcons,
                firebaseAuth = firebaseAuth).apply {

            initializeSearchEngineData()
        }
        /*Search Engine*/

    }

}