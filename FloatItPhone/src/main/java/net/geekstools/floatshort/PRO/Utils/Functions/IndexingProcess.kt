/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 10/5/21, 8:13 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions

import android.content.Context
import android.net.Uri
import com.google.firebase.appindexing.Action
import com.google.firebase.appindexing.FirebaseAppIndex
import com.google.firebase.appindexing.FirebaseUserActions
import com.google.firebase.appindexing.Indexable
import com.google.firebase.appindexing.builders.Actions

class IndexingProcess (private val context: Context) {

    fun indexAppInfoShortcuts(contentAppIndex: String) {

        val baseUri = Uri.parse("https://www.geeksempire.net/createshortcuts.html/")

        val articleToIndex = Indexable.Builder()
                .setName(contentAppIndex)
                .setUrl(baseUri.buildUpon().appendPath(contentAppIndex).build().toString())
                .build()

        FirebaseAppIndex.getInstance(context).update(articleToIndex)
                .addOnSuccessListener {


                }.addOnFailureListener { e ->
                    e.printStackTrace()

                }

        FirebaseUserActions.getInstance(context).start(getAction(contentAppIndex, baseUri.buildUpon().appendPath(contentAppIndex).build().toString()))
                .addOnSuccessListener {


                }.addOnFailureListener { e ->
                    e.printStackTrace()

                }

    }

    private fun getAction(titleForAction: String, urlForAction: String): Action {

        return Actions.newView(titleForAction, urlForAction)
    }

}