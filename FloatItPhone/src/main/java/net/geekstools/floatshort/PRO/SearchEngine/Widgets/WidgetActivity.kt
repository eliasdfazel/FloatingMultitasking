/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/12/20 11:42 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SearchEngine.Widgets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.floatshort.PRO.databinding.SearchEngineWidgetActivityBinding

class WidgetActivity : AppCompatActivity() {

    lateinit var searchEngineWidgetActivityBinding: SearchEngineWidgetActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchEngineWidgetActivityBinding = SearchEngineWidgetActivityBinding.inflate(layoutInflater)
        setContentView(searchEngineWidgetActivityBinding.root)



    }

}