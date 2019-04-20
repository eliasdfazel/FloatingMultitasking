package net.geekstools.floatshort.PRO.Util;

import android.animation.Animator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.floatshort.PRO.Util.NavAdapter.ShareListAdapter;

import java.util.ArrayList;

public class SharingService extends Service {

    FunctionsClass functionsClass;
    WindowManager windowManager;
    WindowManager.LayoutParams params;

    ViewGroup vG;
    RelativeLayout fullActionButton;
    ListView listView;

    ShareListAdapter shareListAdapter;
    ArrayList<NavDrawerItem> navDrawerItems;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int API = android.os.Build.VERSION.SDK_INT;
        if (API > 22) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                stopSelf();
                return Service.START_NOT_STICKY;
            }
        }

        if (PublicVariable.activityStatic == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        functionsClass = new FunctionsClass(getApplicationContext());

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels;
        final float dpWidth = displayMetrics.widthPixels;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vG = (ViewGroup) layoutInflater.inflate(R.layout.share_rate, null, false);
        fullActionButton = (RelativeLayout) vG.findViewById(R.id.fullActionViews);
        listView = (ListView) vG.findViewById(R.id.shareElementsList);

        vG.setBackgroundColor(functionsClass.setColorAlpha(functionsClass.mixColors(PublicVariable.primaryColor, PublicVariable.colorLightDark, 0.65f), 77));

        LayerDrawable drawShareMenu = (LayerDrawable) getResources().getDrawable(R.drawable.draw_share_menu);
        GradientDrawable backShareMenu = (GradientDrawable) drawShareMenu.findDrawableByLayerId(R.id.backtemp);
        backShareMenu.setColor(PublicVariable.primaryColorOpposite);

        CharSequence[] charSequence = new CharSequence[]{
                getString(R.string.facebook),
                getString(R.string.twitter),
                getString(R.string.share),
                getString(R.string.email),
                Html.fromHtml("<big><b><font>" + getString(R.string.rate) + "</font></b></big>")
        };
        Drawable[] drawables = new Drawable[]{
                getResources().getDrawable(R.drawable.ic_share_fb),
                getResources().getDrawable(R.drawable.ic_share_twitter),
                getResources().getDrawable(R.drawable.ic_share_full),
                getResources().getDrawable(R.drawable.ic_share_mail),
                drawShareMenu,
        };

        navDrawerItems = new ArrayList<NavDrawerItem>();
        for (int navItem = 0; navItem < charSequence.length; navItem++) {
            CharSequence itemText = charSequence[navItem];
            Drawable itemIcon = drawables[navItem];

            navDrawerItems.add(new NavDrawerItem(itemText, itemIcon));
        }
        shareListAdapter = new ShareListAdapter(getApplicationContext(), navDrawerItems);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (API > 25) {
            params = new WindowManager.LayoutParams(
                    (int) dpWidth,
                    (int) dpHeight /*+ (PublicVariable.actionBarHeight + PublicVariable.statusBarHeight)*/,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    (int) dpWidth,
                    (int) dpHeight /*+ (PublicVariable.actionBarHeight + PublicVariable.statusBarHeight)*/,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        }

        params.gravity = Gravity.TOP;
        params.x = 0;
        params.y = PublicVariable.activityStatic.getActionBar().getHeight();
        windowManager.addView(vG, params);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int xPosition = functionsClass.displayX() - functionsClass.DpToInteger(23);
                int yPosition = -(PublicVariable.actionBarHeight / 2);

                int startRadius = 0;
                int endRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());

                vG.setVisibility(View.VISIBLE);
                Animator animator = ViewAnimationUtils.createCircularReveal(vG, xPosition, yPosition, startRadius, endRadius);
                animator.setDuration(777);
                animator.start();

                Animation elementsAnim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                final LayoutAnimationController itemController = new LayoutAnimationController(elementsAnim, 0.19f);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(shareListAdapter);
                        listView.setLayoutAnimation(itemController);
                    }
                }, 157);
            }
        }, 100);

        fullActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
                PublicVariable.showShare = false;
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vG == null) {
            return;
        }
        if (vG.isShown()) {
            int xPosition = functionsClass.displayX() - functionsClass.DpToInteger(23);
            int yPosition = -(PublicVariable.actionBarHeight / 2);

            int startRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());
            int endRadius = 0;

            Animator animator = ViewAnimationUtils.createCircularReveal(vG, xPosition, yPosition, startRadius, endRadius);
            animator.setDuration(555);
            animator.start();
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    vG.setVisibility(View.INVISIBLE);
                    windowManager.removeView(vG);
                    PublicVariable.showShare = false;
                    functionsClass.saveFile(".Updated", String.valueOf(functionsClass.appVersionCode(getPackageName())));
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

        }
    }
}
