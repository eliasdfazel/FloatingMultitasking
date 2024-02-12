/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 10:30 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteProcess

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CloudNotificationHandler : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
    }
}
