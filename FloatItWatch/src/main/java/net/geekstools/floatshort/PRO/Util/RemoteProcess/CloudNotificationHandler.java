/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/14/20 6:50 AM
 * Last modified 1/14/20 6:48 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.RemoteProcess;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.geekstools.floatshort.PRO.BuildConfig;

public class CloudNotificationHandler extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (BuildConfig.DEBUG) {
            Log.d(">>> ", "From: " + remoteMessage.getFrom());
            Log.d(">>> ", "Notification Message Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
