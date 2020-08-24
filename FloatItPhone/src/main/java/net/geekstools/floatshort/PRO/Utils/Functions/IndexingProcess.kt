/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 7:14 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.net.Uri
import com.google.firebase.appindexing.Action
import com.google.firebase.appindexing.FirebaseAppIndex
import com.google.firebase.appindexing.FirebaseUserActions
import com.google.firebase.appindexing.Indexable
import com.google.firebase.appindexing.builders.Actions

class IndexingProcess {

    fun indexAppInfoShortcuts(contentAppIndex: String) {

        val baseUri = Uri.parse("https://www.geeksempire.net/createshortcuts.html/")

        val articleToIndex = Indexable.Builder()
                .setName(contentAppIndex)
                .setUrl(baseUri.buildUpon().appendPath(contentAppIndex).build().toString())
                .build()

        FirebaseAppIndex.getInstance().update(articleToIndex)
                .addOnSuccessListener {


                }.addOnFailureListener { e ->
                    e.printStackTrace()

                }

        FirebaseUserActions.getInstance().start(getAction(contentAppIndex, baseUri.buildUpon().appendPath(contentAppIndex).build().toString()))
                .addOnSuccessListener {


                }.addOnFailureListener { e ->
                    e.printStackTrace()

                }

    }

    private fun getAction(titleForAction: String, urlForAction: String): Action {

        return Actions.newView(titleForAction, urlForAction)
    }

}