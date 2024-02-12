/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/25/20 4:33 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.RemoteProcess

import com.google.firebase.inappmessaging.model.Action
import com.google.firebase.inappmessaging.model.InAppMessage

class CloudMessageHandler {

    fun extractData(inAppMessage: InAppMessage, inAppMessageAction: Action) {

        val inAppMessageData: HashMap<String, String> = inAppMessage.data as HashMap<String, String>

    }
}