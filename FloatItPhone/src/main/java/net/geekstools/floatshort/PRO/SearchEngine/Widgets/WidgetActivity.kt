/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/14/20 4:27 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SearchEngine.Widgets

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.SearchEngine.Widgets.Extensions.setupWidgetActivityUserInterface
import net.geekstools.floatshort.PRO.Utils.Functions.*
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.SearchEngineWidgetActivityBinding

class WidgetActivity : AppCompatActivity() {

    lateinit var searchEngineWidgetActivityBinding: SearchEngineWidgetActivityBinding

    val functionsClassLegacy: FunctionsClassLegacy by lazy {
        FunctionsClassLegacy(applicationContext)
    }

    val preferencesIO: PreferencesIO by lazy {
        PreferencesIO(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchEngineWidgetActivityBinding = SearchEngineWidgetActivityBinding.inflate(layoutInflater)
        setContentView(searchEngineWidgetActivityBinding.root)

        val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClassLegacy.customIconPackageName())
        loadCustomIcons.load()

        functionsClassLegacy.checkLightDarkTheme()
        functionsClassLegacy.loadSavedColor()

        PublicVariable.floatingSizeNumber = preferencesIO.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), this.resources.displayMetrics).toInt()

        setupWidgetActivityUserInterface()

        /*Search Engine*/
        SearchEngine(activity = this@WidgetActivity, context = applicationContext,
                searchEngineViewBinding = searchEngineWidgetActivityBinding.searchEngineViewInclude,
                functionsClassLegacy = functionsClassLegacy,
                fileIO = FileIO(applicationContext),
                floatingServices = FloatingServices(applicationContext),
                customIcons = loadCustomIcons,
                firebaseAuth = Firebase.auth,
                requestFocus = true).apply {

            initializeSearchEngineData()
        }
        /*Search Engine*/

    }

}