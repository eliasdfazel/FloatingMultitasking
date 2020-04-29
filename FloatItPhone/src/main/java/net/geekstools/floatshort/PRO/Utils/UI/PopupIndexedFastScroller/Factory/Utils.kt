/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/29/20 1:03 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.UI.PopupIndexedFastScroller.Factory

import android.content.Context
import android.util.TypedValue

fun Float.convertToDp(context: Context) : Int {

    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            this@convertToDp,
            context.resources.displayMetrics).toInt()
}