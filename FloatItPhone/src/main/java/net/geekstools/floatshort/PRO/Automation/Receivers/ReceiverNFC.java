/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/24/20 6:17 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Automation.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcManager;

import net.geekstools.floatshort.PRO.Automation.RecoveryServices.RecoveryNfc;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.FloatingFoldersForNfc;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Shortcuts.FloatingServices.FloatingShortcutsForNfc;
import net.geekstools.floatshort.PRO.Utils.Functions.Debug;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;

public class ReceiverNFC extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            FunctionsClass functionsClass = new FunctionsClass(context);

            if (functionsClass.customIconsEnable()) {
                LoadCustomIcons loadCustomIcons = new LoadCustomIcons(context, functionsClass.customIconPackageName());
                loadCustomIcons.load();
                Debug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIconsNumber());
            }

            NfcManager nfcManager = (NfcManager) context.getApplicationContext().getSystemService(Context.NFC_SERVICE);
            if (nfcManager.getDefaultAdapter().isEnabled() == true && PublicVariable.receiverNFC == false) {
                Intent nfc = new Intent(context, RecoveryNfc.class);
                nfc.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(nfc);
                PublicVariable.receiverNFC = true;
            } else if (nfcManager.getDefaultAdapter().isEnabled() == false) {
                Intent w = new Intent(context, FloatingShortcutsForNfc.class);
                w.putExtra(context.getString(R.string.remove_all_floatings), context.getString(R.string.remove_all_floatings));
                w.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(w);

                Intent c = new Intent(context, FloatingFoldersForNfc.class);
                c.putExtra(context.getString(R.string.remove_all_floatings), context.getString(R.string.remove_all_floatings));
                c.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(c);

                PublicVariable.receiverNFC = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
