/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/13/20 4:27 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SearchEngine.Widgets

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geekstools.floatshort.PRO.SearchEngine.UI.SearchEngine
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.SearchEngineWidgetActivityBinding

class WidgetActivity : AppCompatActivity() {

    lateinit var searchEngineWidgetActivityBinding: SearchEngineWidgetActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchEngineWidgetActivityBinding = SearchEngineWidgetActivityBinding.inflate(layoutInflater)
        setContentView(searchEngineWidgetActivityBinding.root)

        val functionsClassLegacy = FunctionsClassLegacy(applicationContext)

        val loadCustomIcons = LoadCustomIcons(applicationContext, functionsClassLegacy.customIconPackageName())

        functionsClassLegacy.doVibrate(159)

        if (Build.VERSION.SDK_INT >= 26) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                val wallpaperManager = WallpaperManager.getInstance(applicationContext)

                searchEngineWidgetActivityBinding.wallpaperBackground.setImageDrawable(wallpaperManager.drawable)

            }

        } else {

            val wallpaperManager = WallpaperManager.getInstance(applicationContext)

            searchEngineWidgetActivityBinding.wallpaperBackground.setImageDrawable(wallpaperManager.drawable)

        }

        /*Search Engine*/
        SearchEngine(activity = this@WidgetActivity, context = applicationContext,
                searchEngineViewBinding = searchEngineWidgetActivityBinding.searchEngineViewInclude,
                functionsClassLegacy = functionsClassLegacy,
                fileIO = FileIO(applicationContext),
                floatingServices = FloatingServices(applicationContext),
                customIcons = loadCustomIcons,
                firebaseAuth = Firebase.auth).apply {

            initializeSearchEngineData()
        }
        /*Search Engine*/

    }

}