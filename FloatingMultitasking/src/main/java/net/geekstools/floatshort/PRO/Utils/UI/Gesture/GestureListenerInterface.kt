/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package  net.geekstools.floatshort.PRO.Utils.UI.Gesture

import android.view.MotionEvent

interface GestureListenerInterface {
    fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {}

    fun onSingleTapUp(motionEvent: MotionEvent) {}
    fun onLongPress(motionEvent: MotionEvent) {}
}