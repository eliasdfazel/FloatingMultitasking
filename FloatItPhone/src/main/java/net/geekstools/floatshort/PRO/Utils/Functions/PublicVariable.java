/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/21/20 10:19 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.Functions;

import android.app.PendingIntent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PublicVariable {

    public static int allFloatingCounter = 0;

    public static int floatingShortcutsCounter = -1;

    public static int floatingFolderCounter_Folder = 0;
    public static int floatingFolderCounter_Wifi = 0;
    public static int floatingFolderCounter_Bluetooth = 0;
    public static int floatingFolderCounter_Gps = 0;
    public static int floatingFolderCounter_Nfc = 0;
    public static int floatingFolderCounter_Time = 0;
    public static int floatingFolderCounter = -1;

    public static int floatingWidgetsCounter = -1;

    public static int HW = 0;
    public static int size;

    public static int actionBarHeight;
    public static int statusBarHeight;
    public static int navigationBarHeight;

    public static int primaryColor;
    public static int primaryColorOpposite;
    public static int colorLightDark;
    public static int colorLightDarkOpposite;
    public static int dominantColor;

    public static int vibrantColor;
    public static int darkMutedColor;
    public static String darkMutedColorString;

    public static int itemPosition;
    public static int freqLength;

    public static float confirmButtonX;
    public static float confirmButtonY;

    public static String autoID = null;
    public static String folderName = "FloatingFolder";
    public static String splitPairPackage;
    public static String splitSinglePackage;
    public static String splitSingleClassName;
    public static String previousDuplicated;
    public static final String WIDGET_DATA_DATABASE_NAME = "WidgetData";

    public static boolean inMemory = false;
    public static boolean hearBeatCheckPoint = false;
    /**
     * True -> Light
     * False -> Dark
     **/
    public static boolean themeLightDark = true;
    public static boolean Stable = false;
    public static boolean actionCenter = false;
    public static boolean recoveryCenter = false;
    public static boolean receiveWiFi = false;
    public static boolean receiveBluetooth = false;
    public static boolean receiverGPS = false;
    public static boolean receiverNFC = false;
    public static boolean splitScreen = true;
    public static boolean triggerWifiBroadcast = false;
    public static boolean forceReload = false;
    public static boolean updateCancelByUser = false;

    public static String[] frequentlyUsedApps;

    public static ArrayList<String> FloatingShortcutsList = new ArrayList<String>();
    public static ArrayList<String> RecoveryShortcuts = new ArrayList<String>();

    public static ArrayList<String> floatingFoldersList = new ArrayList<String>();

    public static ArrayList<Integer> FloatingWidgets = new ArrayList<Integer>();

    public static List<String> customIconsPackages = new ArrayList<String>();

    public static Map<String, PendingIntent> notificationIntent = new LinkedHashMap<String, PendingIntent>();
}
