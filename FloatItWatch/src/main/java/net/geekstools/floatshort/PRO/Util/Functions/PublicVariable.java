/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:18 PM
 * Last modified 11/11/19 7:16 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.Functions;

import android.content.Context;

import java.util.ArrayList;

public class PublicVariable {

    public static Context contextStatic;

    public static int floatingCategoryCounter_category = 0;
    public static int shortcutsCounter = -1;
    public static int categoriesCounter = -1;
    public static int floatingCounter = 0;
    public static int HW = 0;
    public static int alpha = 133;
    public static int opacity = 255;
    public static int size;
    public static int primaryColor;
    public static int primaryColorOpposite;
    public static int colorLightDark;
    public static int colorLightDarkOpposite;

    public static String categoryFloating;

    public static boolean hide = false;
    public static boolean Return = false;

    public static ArrayList<String> FloatingShortcuts = new ArrayList<String>();
    public static ArrayList<String> RecoveryShortcuts = new ArrayList<String>();

    public static ArrayList<String> FloatingCategories = new ArrayList<String>();
}
