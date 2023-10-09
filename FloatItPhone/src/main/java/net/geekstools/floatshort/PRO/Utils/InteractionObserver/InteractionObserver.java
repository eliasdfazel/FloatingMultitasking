/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.InteractionObserver;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy;

public class InteractionObserver extends AccessibilityService {

    FunctionsClassLegacy functionsClassLegacy;

    boolean doSplitPair = false;
    boolean doSplitSingle = false;

    String classNameCommand = "Default";

    @Override
    protected void onServiceConnected() {
        functionsClassLegacy = new FunctionsClassLegacy(getApplicationContext());
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        functionsClassLegacy = new FunctionsClassLegacy(getApplicationContext());

        switch (accessibilityEvent.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if (accessibilityEvent.getAction() == 10296) {
                    classNameCommand = (String) accessibilityEvent.getClassName();

                    if (doSplitPair == false) {
                        doSplitPair = true;
                        startActivity(new Intent(getApplicationContext(), SplitTransparentPair.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    IntentFilter intentFilterTest = new IntentFilter();
                    intentFilterTest.addAction("perform_split_pair" + getApplicationContext().getPackageName());
                    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent.getAction().equals("perform_split_pair") && doSplitPair == true) {
                                doSplitPair = false;
                                performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                                sendBroadcast(new Intent("Split_Apps_Pair_" + classNameCommand));
                            }
                        }
                    };
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            registerReceiver(broadcastReceiver, intentFilterTest, RECEIVER_NOT_EXPORTED);
                        } else {
                            registerReceiver(broadcastReceiver, intentFilterTest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (accessibilityEvent.getAction() == 69201) {
                    classNameCommand = (String) accessibilityEvent.getClassName();

                    if (doSplitSingle == false) {
                        doSplitSingle = true;
                        startActivity(new Intent(getApplicationContext(), SplitTransparentSingle.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    IntentFilter intentFilterTest = new IntentFilter();
                    intentFilterTest.addAction("perform_split_single" + getApplicationContext().getPackageName());
                    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent.getAction().equals("perform_split_single" + getApplicationContext().getPackageName()) && doSplitSingle == true) {
                                doSplitSingle = false;
                                performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);

                                sendBroadcast(new Intent("Split_Apps_Single_" + classNameCommand));
                            }
                        }
                    };
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            registerReceiver(broadcastReceiver, intentFilterTest, RECEIVER_NOT_EXPORTED);
                        } else {
                            registerReceiver(broadcastReceiver, intentFilterTest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (accessibilityEvent.getAction() == 66666) {
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                }
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        return START_STICKY;
    }

    @Override
    public void onInterrupt() {
        startService(new Intent(getApplicationContext(), InteractionObserver.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
