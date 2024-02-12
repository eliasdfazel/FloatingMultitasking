/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.UI.Gesture

sealed class GestureConstants {
    class SwipeHorizontal(var horizontalDirection: Int) : GestureConstants()
    class SwipeVertical(var verticallDirection: Int) : GestureConstants()
}

class GestureListenerConstants {

    companion object {
        const val SWIPE_UP = 1
        const val SWIPE_DOWN = 2
        const val SWIPE_LEFT = 3
        const val SWIPE_RIGHT = 4

        const val MODE_SOLID = 1
        const val MODE_DYNAMIC = 2

        const val ACTION_FAKE = -666
    }
}