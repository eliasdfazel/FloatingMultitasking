/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/7/20 8:01 AM
 * Last modified 1/7/20 5:03 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.geekstools.floatshort.PRO.Automation.Categories.CategoryAutoFeatures;
import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.BuildConfig;
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.FoldersListAdapter;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Shortcuts.HybridApplicationsView;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.GeneralAdapters.AdapterItems;
import net.geekstools.floatshort.PRO.Util.IAP.InAppBilling;
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingManager;
import net.geekstools.floatshort.PRO.Util.InAppUpdate.InAppUpdateProcess;
import net.geekstools.floatshort.PRO.Util.Preferences.PreferencesActivity;
import net.geekstools.floatshort.PRO.Util.RemoteProcess.LicenseValidator;
import net.geekstools.floatshort.PRO.Util.RemoteTask.RecoveryFolders;
import net.geekstools.floatshort.PRO.Util.RemoteTask.RecoveryShortcuts;
import net.geekstools.floatshort.PRO.Util.RemoteTask.RecoveryWidgets;
import net.geekstools.floatshort.PRO.Util.SearchEngine.SearchEngineAdapter;
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.PinPassword.HandlePinPassword;
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Util.UI.SimpleGestureFilterSwitch;
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataInterface;
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataModel;
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class FoldersConfigurations extends Activity implements View.OnClickListener, View.OnLongClickListener, SimpleGestureFilterSwitch.SimpleGestureListener {

    FunctionsClass functionsClass;
    FunctionsClassSecurity functionsClassSecurity;

    RelativeLayout fullActionViews, wholeCategory;
    RecyclerView categorylist;
    ImageView actionButton, recoverFloatingApps, recoverFloatingWidgets;
    MaterialButton switchWidgets, switchApps, recoveryAction, automationAction;

    /*Search Engine*/
    SearchEngineAdapter searchRecyclerViewAdapter;
    TextInputLayout textInputSearchView;
    AppCompatAutoCompleteTextView searchView;
    ImageView searchIcon, searchFloatIt,
            searchClose;
    /*Search Engine*/

    RecyclerView.Adapter categoryListAdapter;
    ArrayList<AdapterItems> adapterItems, searchAdapterItems;

    RelativeLayout loadingSplash;
    ProgressBar loadingBarLTR;
    TextView gx;

    BroadcastReceiver broadcastReceiver;

    SimpleGestureFilterSwitch simpleGestureFilterSwitch;

    LoadCustomIcons loadCustomIcons;

    FirebaseRemoteConfig firebaseRemoteConfig;

    ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_handler);

        wholeCategory = (RelativeLayout) findViewById(R.id.wholeCategory);
        categorylist = (RecyclerView) findViewById(R.id.categorylist);
        fullActionViews = (RelativeLayout) findViewById(R.id.fullActionViews);

        actionButton = (ImageView) findViewById(R.id.actionButton);
        switchWidgets = (MaterialButton) findViewById(R.id.switchWidgets);
        switchApps = (MaterialButton) findViewById(R.id.switchApps);
        recoveryAction = (MaterialButton) findViewById(R.id.recoveryAction);
        automationAction = (MaterialButton) findViewById(R.id.automationAction);
        recoverFloatingApps = (ImageView) findViewById(R.id.recoverFloatingApps);
        recoverFloatingWidgets = (ImageView) findViewById(R.id.recoverFloatingWidgets);

        /*Search Engine*/
        textInputSearchView = (TextInputLayout) findViewById(R.id.textInputSearchView);
        searchView = (AppCompatAutoCompleteTextView) findViewById(R.id.searchView);
        searchIcon = (ImageView) findViewById(R.id.searchIcon);
        searchFloatIt = (ImageView) findViewById(R.id.searchFloatIt);
        searchClose = (ImageView) findViewById(R.id.searchClose);
        /*Search Engine*/

        functionsClass = new FunctionsClass(getApplicationContext(), this);
        functionsClassSecurity = new FunctionsClassSecurity(this, getApplicationContext());
        functionsClass.ChangeLog(FoldersConfigurations.this, false);

        simpleGestureFilterSwitch = new SimpleGestureFilterSwitch(getApplicationContext(), this);

        if (functionsClass.appThemeTransparent() == true) {
            functionsClass.setThemeColorFloating(wholeCategory, true);
        } else {
            functionsClass.setThemeColorFloating(wholeCategory, false);
        }

        adapterItems = new ArrayList<AdapterItems>();

        LinearLayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext(), OrientationHelper.VERTICAL, false);
        categorylist.setLayoutManager(recyclerViewLayoutManager);

        ImageView floatingLogo = (ImageView) findViewById(R.id.loadLogo);
        LayerDrawable drawFloatingLogo = (LayerDrawable) getDrawable(R.drawable.draw_floating_logo);
        Drawable backFloatingLogo = drawFloatingLogo.findDrawableByLayerId(R.id.backtemp);
        backFloatingLogo.setTint(PublicVariable.primaryColorOpposite);
        floatingLogo.setImageDrawable(drawFloatingLogo);

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }

        LoadCategory loadCategory = new LoadCategory();
        loadCategory.execute();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Category_Reload");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("Category_Reload")) {
                    LoadCategory loadCategory = new LoadCategory();
                    loadCategory.execute();
                }
            }
        };
        try {
            registerReceiver(broadcastReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LayerDrawable drawPreferenceAction = (LayerDrawable) getDrawable(R.drawable.draw_pref_action);
        Drawable backPreferenceAction = drawPreferenceAction.findDrawableByLayerId(R.id.backtemp);
        backPreferenceAction.setTint(PublicVariable.primaryColorOpposite);
        actionButton.setImageDrawable(drawPreferenceAction);

        switchWidgets.setTextColor(getColor(R.color.light));
        switchApps.setTextColor(getColor(R.color.light));
        if (PublicVariable.themeLightDark /*light*/ && functionsClass.appThemeTransparent() /*transparent*/) {
            switchWidgets.setTextColor(getColor(R.color.dark));
            switchApps.setTextColor(getColor(R.color.dark));
        }

        switchApps.setBackgroundColor(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColor, 51) : PublicVariable.primaryColor);
        switchApps.setRippleColor(ColorStateList.valueOf(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51) : PublicVariable.primaryColorOpposite));

        switchWidgets.setBackgroundColor(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColor, 51) : PublicVariable.primaryColor);
        switchWidgets.setRippleColor(ColorStateList.valueOf(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51) : PublicVariable.primaryColorOpposite));

        recoveryAction.setBackgroundColor(PublicVariable.primaryColorOpposite);
        recoveryAction.setRippleColor(ColorStateList.valueOf(PublicVariable.primaryColor));

        automationAction.setBackgroundColor(PublicVariable.primaryColorOpposite);
        automationAction.setRippleColor(ColorStateList.valueOf(PublicVariable.primaryColor));

        try {
            LayerDrawable drawRecoverFloatingCategories = (LayerDrawable) getDrawable(R.drawable.draw_recovery).mutate();
            Drawable backRecoverFloatingCategories = drawRecoverFloatingCategories.findDrawableByLayerId(R.id.backtemp).mutate();
            backRecoverFloatingCategories.setTint(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColor, 51) : PublicVariable.primaryColor);

            LayerDrawable drawRecoverFloatingWidgets = (LayerDrawable) getDrawable(R.drawable.draw_recovery_widgets).mutate();
            Drawable backRecoverFloatingWidgets = drawRecoverFloatingWidgets.findDrawableByLayerId(R.id.backtemp).mutate();
            backRecoverFloatingWidgets.setTint(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColor, 51) : PublicVariable.primaryColor);

            recoverFloatingApps.setImageDrawable(drawRecoverFloatingCategories);
            recoverFloatingWidgets.setImageDrawable(drawRecoverFloatingWidgets);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.doVibrate(33);

                if (PublicVariable.actionCenter == false) {

                    int finalRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());
                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(recoveryAction, (int) actionButton.getX(), (int) actionButton.getY(), finalRadius, functionsClass.DpToInteger(13));
                    circularReveal.setDuration(777);
                    circularReveal.setInterpolator(new AccelerateInterpolator());
                    circularReveal.start();
                    circularReveal.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            recoveryAction.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    functionsClass.openActionMenuOption(fullActionViews, actionButton, fullActionViews.isShown());
                } else {
                    recoveryAction.setVisibility(View.VISIBLE);

                    int finalRadius = (int) Math.hypot(functionsClass.displayX(), functionsClass.displayY());
                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(recoveryAction, (int) actionButton.getX(), (int) actionButton.getY(), functionsClass.DpToInteger(13), finalRadius);
                    circularReveal.setDuration(1300);
                    circularReveal.setInterpolator(new AccelerateInterpolator());
                    circularReveal.start();
                    circularReveal.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            recoveryAction.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    functionsClass.closeActionMenuOption(fullActionViews, actionButton);
                }
            }
        });
        switchApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.navigateToClass(HybridApplicationsView.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        switchWidgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (functionsClass.networkConnection() && firebaseAuth.getCurrentUser() != null) {
                        if (functionsClass.floatingWidgetsPurchased() || functionsClass.appVersionName(getPackageName()).contains("[BETA]")) {
                            try {
                                functionsClass.navigateToClass(WidgetConfigurations.class,
                                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            InAppBilling.ItemIAB = BillingManager.iapFloatingWidgets;

                            startActivity(new Intent(getApplicationContext(), InAppBilling.class)
                                            .putExtra("UserEmailAddress", functionsClass.readPreference(".UserInformation", "userEmail", null)),
                                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.down_up, android.R.anim.fade_out).toBundle());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.internetError), Toast.LENGTH_LONG).show();

                        if (firebaseAuth.getCurrentUser() == null) {
                            Toast.makeText(getApplicationContext(), getString(R.string.authError), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        automationAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionsClass.doVibrate(50);

                Intent intent = new Intent(getApplicationContext(), CategoryAutoFeatures.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.up_down, android.R.anim.fade_out).toBundle());
            }
        });
        recoveryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecoveryFolders.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);
            }
        });
        recoverFloatingApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecoveryShortcuts.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_hide);
                recoverFloatingApps.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        recoverFloatingApps.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
        recoverFloatingWidgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecoveryWidgets.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_hide);
                recoverFloatingWidgets.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        recoverFloatingWidgets.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

        actionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(FoldersConfigurations.this, actionButton, "transition");
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(FoldersConfigurations.this, PreferencesActivity.class);
                        startActivity(intent, activityOptionsCompat.toBundle());
                    }
                }, 113);

                return true;
            }
        });
        switchApps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!recoverFloatingApps.isShown()) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_show);
                    recoverFloatingApps.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            recoverFloatingApps.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_hide);
                    recoverFloatingApps.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            recoverFloatingApps.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

                return true;
            }
        });
        switchWidgets.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!recoverFloatingWidgets.isShown()) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_show);
                    recoverFloatingWidgets.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            recoverFloatingWidgets.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recovery_actions_hide);
                    recoverFloatingWidgets.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            recoverFloatingWidgets.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

                return true;
            }
        });

        //Consume Donation
        try {
            if (functionsClass.alreadyDonated() && functionsClass.networkConnection()) {
                BillingClient billingClient = BillingClient.newBuilder(FoldersConfigurations.this).setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchaseList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchaseList != null) {

                        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

                        } else {

                        }
                    }
                }).enablePendingPurchases().build();
                billingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                            for (Purchase purchase : purchases) {
                                FunctionsClassDebug.Companion.PrintDebug("*** Purchased Item: " + purchase + " ***");


                                if (purchase.getSku().equals(BillingManager.iapDonation)) {
                                    ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
                                        @Override
                                        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                                FunctionsClassDebug.Companion.PrintDebug("*** Consumed Item: " + purchaseToken + " ***");

                                                functionsClass.savePreference(".PurchasedItem", purchase.getSku(), false);
                                            }
                                        }
                                    };

                                    ConsumeParams.Builder consumeParams = ConsumeParams.newBuilder();
                                    consumeParams.setPurchaseToken(purchase.getPurchaseToken());
                                    billingClient.consumeAsync(consumeParams.build(), consumeResponseListener);
                                }
                            }
                        }
                    }

                    @Override
                    public void onBillingServiceDisconnected() {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Restore Purchased Item
        if (!functionsClass.floatingWidgetsPurchased() || !functionsClass.searchEngineSubscribed()) {
            BillingClient billingClient = BillingClient.newBuilder(FoldersConfigurations.this).setListener(new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchaseList) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchaseList != null) {

                    } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

                    } else {

                    }
                }
            }).enablePendingPurchases().build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        functionsClass.savePreference(".PurchasedItem", BillingManager.iapFloatingWidgets, false);

                        List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                        for (Purchase purchase : purchases) {
                            FunctionsClassDebug.Companion.PrintDebug("*** Purchased Item: " + purchase + " ***");
                            functionsClass.savePreference(".PurchasedItem", purchase.getSku(), true);
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {

                }
            });
        }

        //Restore Subscribed Item
        try {
            if (functionsClass.networkConnection()) {
                BillingClient billingClient = BillingClient.newBuilder(FoldersConfigurations.this).setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {

                        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

                        } else {

                        }

                    }
                }).enablePendingPurchases().build();
                billingClient.startConnection(new BillingClientStateListener() {

                    @Override
                    public void onBillingSetupFinished(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            functionsClass.savePreference(".SubscribedItem", BillingManager.iapSecurityServices, false);

                            List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
                            for (Purchase purchase : purchases) {
                                FunctionsClassDebug.Companion.PrintDebug("*** Subscribed Item: " + purchase + " ***");
                                functionsClass.savePreference(".SubscribedItem", purchase.getSku(), true);
                            }
                        }
                    }

                    @Override
                    public void onBillingServiceDisconnected() {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*Search Engine*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        /*Search Engine*/

        firebaseAuth = FirebaseAuth.getInstance();

        if (BuildConfig.VERSION_NAME.contains("[BETA]")
                && !functionsClass.readPreference(".UserInformation", "SubscribeToBeta", false)) {
            FirebaseMessaging.getInstance().subscribeToTopic("BETA").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    functionsClass.savePreference(".UserInformation", "SubscribeToBeta", true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.license));
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(getString(R.string.license))) {
                    functionsClass.dialogueLicense(FoldersConfigurations.this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopService(new Intent(getApplicationContext(), LicenseValidator.class));
                        }
                    }, 1000);
                }
            }
        };
        try {
            registerReceiver(broadcastReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!getFileStreamPath(".License").exists() && functionsClass.networkConnection() == true) {
                startService(new Intent(getApplicationContext(), LicenseValidator.class));
            } else {
                try {
                    unregisterReceiver(broadcastReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (functionsClass.SystemCache()) {
            startService(new Intent(getApplicationContext(), BindServices.class));
        }

        try {
            if (functionsClass.networkConnection()) {
                try {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user == null) {
                                        functionsClass.savePreference(".UserInformation", "userEmail", null);
                                    } else {

                                    }


                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (functionsClass.readPreference(".UserInformation", "userEmail", null) == null) {
                    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.webClientId))
                            .requestEmail()
                            .build();

                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
                    try {
                        googleSignInClient.signOut();
                        googleSignInClient.revokeAccess();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 666);

                    progressDialog = new ProgressDialog(FoldersConfigurations.this,
                            PublicVariable.themeLightDark ? R.style.GeeksEmpire_Dialogue_Light : R.style.GeeksEmpire_Dialogue_Dark);
                    progressDialog.setMessage(Html.fromHtml(
                            "<big><font color='" + PublicVariable.colorLightDarkOpposite + "'>" + getString(R.string.signinTitle) + "</font></big>"
                                    + "<br/>" +
                                    "<small><font color='" + PublicVariable.colorLightDarkOpposite + "'>" + getString(R.string.signinMessage) + "</font></small>"));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView shareIt = (ImageView) findViewById(R.id.shareIt);
        LayerDrawable drawableShare = (LayerDrawable) getDrawable(R.drawable.draw_share);
        Drawable backgroundShare = drawableShare.findDrawableByLayerId(R.id.backtemp);
        backgroundShare.setTint(PublicVariable.primaryColor);
        shareIt.setImageDrawable(drawableShare);
        shareIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareText =
                        getString(R.string.shareTitle) +
                                "\n" + getString(R.string.shareSummary) +
                                "\n" + getString(R.string.play_store_link) + getPackageName();

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                sharingIntent.setType("text/plain");
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sharingIntent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        PublicVariable.inMemory = true;

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default);

        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(FoldersConfigurations.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (((int) firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey())) > functionsClass.appVersionCode(getPackageName())) {
                                        LayerDrawable layerDrawableNewUpdate = (LayerDrawable) getDrawable(R.drawable.ic_update);
                                        BitmapDrawable gradientDrawableNewUpdate = (BitmapDrawable) layerDrawableNewUpdate.findDrawableByLayerId(R.id.ic_launcher_back_layer);
                                        gradientDrawableNewUpdate.setTint(PublicVariable.primaryColor);

                                        ImageView newUpdate = (ImageView) findViewById(R.id.newUpdate);
                                        newUpdate.setImageDrawable(layerDrawableNewUpdate);
                                        newUpdate.setVisibility(View.VISIBLE);
                                        newUpdate.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                functionsClass.upcomingChangeLog(
                                                        FoldersConfigurations.this,
                                                        firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()),
                                                        String.valueOf(firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()))
                                                );

                                            }
                                        });

                                        functionsClass.notificationCreator(
                                                getString(R.string.updateAvailable),
                                                firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogSummaryConfigKey()),
                                                (int) firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey())
                                        );

                                        int inAppUpdateTriggeredTime = Integer.parseInt(
                                                String.valueOf(Calendar.getInstance().get(Calendar.YEAR))
                                                        + String.valueOf(Calendar.getInstance().get(Calendar.MONTH))
                                                        + String.valueOf(Calendar.getInstance().get(Calendar.DATE))
                                        );

                                        if ((firebaseAuth.getCurrentUser() != null)
                                                && (functionsClass.readPreference("InAppUpdate", "TriggeredDate", Calendar.getInstance().get(Calendar.DATE)) < inAppUpdateTriggeredTime)) {
                                            startActivity(new Intent(getApplicationContext(), InAppUpdateProcess.class)
                                                            .putExtra("UPDATE_CHANGE_LOG", String.valueOf(firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey())))
                                                            .putExtra("UPDATE_VERSION", String.valueOf(firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey())))
                                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                                    ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                                        }
                                    } else {

                                    }
                                }
                            });
                        } else {

                        }
                    }
                });

        functionsClass.addAppShortcuts();
        functionsClassSecurity.resetAuthAppValues();

        /*Search Engine*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        /*Search Engine*/
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (PublicVariable.actionCenter == true) {
            functionsClass.closeActionMenuOption(fullActionViews, actionButton);
        }

        functionsClass.savePreference("OpenMode", "openClassName", this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PublicVariable.inMemory = false;

        functionsClass.endIndexAppInfo();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent homeScreen = new Intent(Intent.ACTION_MAIN);
        homeScreen.addCategory(Intent.CATEGORY_HOME);
        homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeScreen, ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());

        functionsClass.CheckSystemRAM(FoldersConfigurations.this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        return true;
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilterSwitch.SWIPE_RIGHT: {
                try {
                    functionsClass.navigateToClass(HybridApplicationsView.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_left, R.anim.slide_to_right));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case SimpleGestureFilterSwitch.SWIPE_LEFT: {
                try {
                    if (functionsClass.floatingWidgetsPurchased()) {
                        functionsClass.navigateToClass(WidgetConfigurations.class,
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                    } else {
                        InAppBilling.ItemIAB = BillingManager.iapFloatingWidgets;

                        functionsClass.navigateToClass(InAppBilling.class,
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.simpleGestureFilterSwitch.onTouchEvent(motionEvent);

        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666) {
            try {
                Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);

                AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                firebaseAuth.signInWithCredential(authCredential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        functionsClass.savePreference(".UserInformation", "userEmail", firebaseUser.getEmail());

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    File betaFile = new File("/data/data/" + getPackageName() + "/shared_prefs/.UserInformation.xml");
                                                    Uri uriBetaFile = Uri.fromFile(betaFile);
                                                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

                                                    StorageReference storageReference = firebaseStorage.getReference("/Users/" + "API" + functionsClass.returnAPI() + "/" +
                                                            functionsClass.readPreference(".UserInformation", "userEmail", null));
                                                    UploadTask uploadTask = storageReference.putFile(uriBetaFile);

                                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            exception.printStackTrace();
                                                            try {
                                                                progressDialog.setMessage(Html.fromHtml(
                                                                        "<big><font color='" + PublicVariable.colorLightDarkOpposite + "'>" + getString(R.string.error) + "</font></big>"
                                                                                + "<br/>" +
                                                                                "<small><font color='" + PublicVariable.colorLightDarkOpposite + "'>" + exception.getMessage() + "</font></small>"));
                                                                progressDialog.setCancelable(true);
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        progressDialog.dismiss();
                                                                    }
                                                                }, 777);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            FunctionsClassDebug.Companion.PrintDebug("Firebase Activities Done Successfully");
                                                            functionsClass.Toast(getString(R.string.signinFinished), Gravity.TOP);
                                                            try {
                                                                progressDialog.dismiss();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, 333);

                                        functionsClassSecurity.downloadLockedAppsData();
                                    }
                                } else {

                                }
                            }
                        });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    public class LoadCategory extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
            if (functionsClass.appThemeTransparent() == true) {
                loadingSplash.setBackgroundColor(Color.TRANSPARENT);
            } else {
                loadingSplash.setBackgroundColor(getWindow().getNavigationBarColor());
            }

            loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgress);
            gx = (TextView) findViewById(R.id.gx);
            Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
            gx.setTypeface(face);

            if (PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeTextColor, android.graphics.PorterDuff.Mode.MULTIPLY);
                gx.setTextColor(getColor(R.color.dark));
            } else {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeColor, android.graphics.PorterDuff.Mode.MULTIPLY);
                gx.setTextColor(getColor(R.color.light));
            }
            if (!getFileStreamPath(".categoryInfo").exists()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                        loadingSplash.setVisibility(View.INVISIBLE);
                        loadingSplash.startAnimation(anim);
                    }
                }, 200);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (functionsClass.loadCustomIcons()) {
                    loadCustomIcons.load();
                    FunctionsClassDebug.Companion.PrintDebug("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                }

                if (getFileStreamPath(".categoryInfo").exists() && functionsClass.countLineInnerFile(".categoryInfo") > 0) {
                    try {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(getFileStreamPath(".categoryInfo"));
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                            int linesNumber = 0;
                            String folderData = "";
                            while ((folderData = bufferedReader.readLine()) != null) {
                                try {
                                    adapterItems.add(new AdapterItems(folderData,
                                            functionsClass.readFileLine(folderData), SearchEngineAdapter.SearchResultType.SearchFolders));

                                    linesNumber++;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    functionsClass.IndexAppInfoCategory(folderData + " | " + getString(R.string.floatingCategory));
                                }
                            }

                            fileInputStream.close();
                            bufferedReader.close();

                            if (linesNumber > 0) {
                                adapterItems.add(new AdapterItems(getPackageName(), new String[]{getPackageName()}, SearchEngineAdapter.SearchResultType.SearchFolders));
                                categoryListAdapter = new FoldersListAdapter(FoldersConfigurations.this, getApplicationContext(), adapterItems);
                            } else {
                                adapterItems = new ArrayList<AdapterItems>();
                                adapterItems.add(new AdapterItems(getPackageName(), new String[]{getPackageName()}, SearchEngineAdapter.SearchResultType.SearchFolders));
                                categoryListAdapter = new FoldersListAdapter(FoldersConfigurations.this, getApplicationContext(), adapterItems);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.cancel(true);
                    } finally {

                    }
                } else {
                    adapterItems = new ArrayList<AdapterItems>();
                    adapterItems.add(new AdapterItems(getPackageName(), new String[]{getPackageName()}, SearchEngineAdapter.SearchResultType.SearchFolders));
                    categoryListAdapter = new FoldersListAdapter(FoldersConfigurations.this, getApplicationContext(), adapterItems);
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            categorylist.setAdapter(categoryListAdapter);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
                    loadingSplash.setVisibility(View.INVISIBLE);
                    loadingSplash.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            categorylist.scrollTo(0, 0);
                            ((LinearLayout) findViewById(R.id.switchFloating)).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }, 333);

            LoadInstalledCustomIcons loadInstalledCustomIcons = new LoadInstalledCustomIcons();
            loadInstalledCustomIcons.execute();

            LoadSearchEngineData loadSearchEngineData = new LoadSearchEngineData();
            loadSearchEngineData.execute();
        }
    }

    private class LoadInstalledCustomIcons extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PackageManager packageManager = getApplicationContext().getPackageManager();

                //ACTION: com.novalauncher.THEME
                //CATEGORY: com.novalauncher.category.CUSTOM_ICON_PICKER
                Intent intentCustomIcons = new Intent();
                intentCustomIcons.setAction("com.novalauncher.THEME");
                intentCustomIcons.addCategory("com.novalauncher.category.CUSTOM_ICON_PICKER");
                List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intentCustomIcons, 0);
                try {
                    PublicVariable.customIconsPackages.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (ResolveInfo resolveInfo : resolveInfos) {
                    FunctionsClassDebug.Companion.PrintDebug("CustomIconPackages ::: " + resolveInfo.activityInfo.packageName);
                    PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName);
                }

            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
        }
    }

    /*Search Engine*/
    private class LoadSearchEngineData extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            /*
             * Search Engine
             */
            if (SearchEngineAdapter.allSearchResultItems.isEmpty()) {
                searchAdapterItems = new ArrayList<AdapterItems>();

                //Loading Folders
                if (getFileStreamPath(".categoryInfo").exists()) {
                    try {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(getFileStreamPath(".categoryInfo"));
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                            String folderData = "";
                            while ((folderData = bufferedReader.readLine()) != null) {
                                try {
                                    searchAdapterItems.add(new AdapterItems(folderData,
                                            functionsClass.readFileLine(folderData), SearchEngineAdapter.SearchResultType.SearchFolders));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            fileInputStream.close();
                            bufferedReader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.cancel(true);
                    } finally {

                    }
                }

                //Loading Shortcuts
                try {
                    List<ApplicationInfo> applicationInfoList = getApplicationContext().getPackageManager().getInstalledApplications(0);
                    Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(getPackageManager()));

                    for (int appInfo = 0; appInfo < applicationInfoList.size(); appInfo++) {
                        if (getPackageManager().getLaunchIntentForPackage(applicationInfoList.get(appInfo).packageName) != null) {
                            try {
                                String PackageName = applicationInfoList.get(appInfo).packageName;
                                String AppName = functionsClass.appName(PackageName);
                                Drawable AppIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(PackageName, functionsClass.shapedAppIcon(PackageName)) : functionsClass.shapedAppIcon(PackageName);

                                searchAdapterItems.add(new AdapterItems(AppName, PackageName, AppIcon, SearchEngineAdapter.SearchResultType.SearchShortcuts));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Loading Widgets
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(FoldersConfigurations.this);
                WidgetDataInterface widgetDataInterface = Room.databaseBuilder(getApplicationContext(), WidgetDataInterface.class, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .addCallback(new RoomDatabase.Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                super.onCreate(supportSQLiteDatabase);
                            }

                            @Override
                            public void onOpen(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                super.onOpen(supportSQLiteDatabase);

                            }
                        })
                        .build();

                List<WidgetDataModel> widgetDataModels = widgetDataInterface.initDataAccessObject().getAllWidgetData();
                if (widgetDataModels.size() > 0) {
                    for (WidgetDataModel widgetDataModel : widgetDataModels) {
                        try {
                            int appWidgetId = widgetDataModel.getWidgetId();
                            String packageName = widgetDataModel.getPackageName();
                            String className = widgetDataModel.getClassNameProvider();
                            String configClassName = widgetDataModel.getConfigClassName();

                            FunctionsClassDebug.Companion.PrintDebug("*** " + appWidgetId + " *** PackageName: " + packageName + " - ClassName: " + className + " - Configure: " + configClassName + " ***");

                            if (functionsClass.appIsInstalled(packageName)) {
                                AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
                                String newAppName = functionsClass.appName(packageName);
                                Drawable appIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(packageName, functionsClass.shapedAppIcon(packageName)) : functionsClass.shapedAppIcon(packageName);


                                searchAdapterItems.add(new AdapterItems(
                                        newAppName,
                                        packageName,
                                        className,
                                        configClassName,
                                        widgetDataModel.getWidgetLabel(),
                                        appIcon,
                                        appWidgetProviderInfo,
                                        appWidgetId,
                                        widgetDataModel.getRecovery(),
                                        SearchEngineAdapter.SearchResultType.SearchWidgets
                                ));

                            } else {
                                widgetDataInterface.initDataAccessObject().deleteByWidgetClassNameProviderWidget(packageName, className);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                }
                searchRecyclerViewAdapter = new SearchEngineAdapter(getApplicationContext(), searchAdapterItems);
            } else {
                searchAdapterItems = SearchEngineAdapter.allSearchResultItems;

                searchRecyclerViewAdapter = new SearchEngineAdapter(getApplicationContext(), searchAdapterItems);
            }
            /*
             * Search Engine
             */
            return (searchAdapterItems.size() > 0);
        }

        @Override
        protected void onPostExecute(Boolean results) {
            super.onPostExecute(results);

            if (results) {
                setupSearchView();
            }
        }
    }

    public void setupSearchView() {
        searchView.setAdapter(searchRecyclerViewAdapter);

        searchView.setDropDownBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        searchView.setVerticalScrollBarEnabled(false);
        searchView.setScrollBarSize(0);

        searchView.setTextColor(PublicVariable.colorLightDarkOpposite);
        searchView.setCompoundDrawableTintList(ColorStateList.valueOf(functionsClass.setColorAlpha(PublicVariable.colorLightDarkOpposite, 175)));

        RippleDrawable layerDrawableSearchIcon = (RippleDrawable) getDrawable(R.drawable.search_icon);
        Drawable backgroundTemporarySearchIcon = layerDrawableSearchIcon.findDrawableByLayerId(R.id.backgroundTemporary);
        Drawable frontTemporarySearchIcon = layerDrawableSearchIcon.findDrawableByLayerId(R.id.frontTemporary);
        Drawable frontDrawableSearchIcon = layerDrawableSearchIcon.findDrawableByLayerId(R.id.frontDrawable);
        backgroundTemporarySearchIcon.setTint(PublicVariable.colorLightDarkOpposite);
        frontTemporarySearchIcon.setTint(PublicVariable.colorLightDark);
        frontDrawableSearchIcon.setTint(PublicVariable.themeLightDark ? functionsClass.manipulateColor(PublicVariable.primaryColor, 0.90f) : functionsClass.manipulateColor(PublicVariable.primaryColor, 3.00f));

        layerDrawableSearchIcon.setLayerInset(2,
                functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13));

        searchIcon.setImageDrawable(layerDrawableSearchIcon);
        searchIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        searchIcon.setVisibility(View.VISIBLE);

        textInputSearchView.setHintTextColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
        textInputSearchView.setBoxStrokeColor(PublicVariable.primaryColor);

        GradientDrawable backgroundTemporaryInput = new GradientDrawable();
        try {
            LayerDrawable layerDrawableBackgroundInput = (LayerDrawable) getDrawable(R.drawable.background_search_input);
            backgroundTemporaryInput = (GradientDrawable) layerDrawableBackgroundInput.findDrawableByLayerId(R.id.backgroundTemporary);
            backgroundTemporaryInput.setTint(PublicVariable.colorLightDark);
            textInputSearchView.setBackground(layerDrawableBackgroundInput);
        } catch (Exception e) {
            e.printStackTrace();

            textInputSearchView.setBackground(null);
        }

        GradientDrawable finalBackgroundTemporaryInput = backgroundTemporaryInput;
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleSearchEngineUsed = new Bundle();
                bundleSearchEngineUsed.putParcelable("USER_USED_SEARCH_ENGINE", firebaseAuth.getCurrentUser());
                bundleSearchEngineUsed.putInt("TYPE_USED_SEARCH_ENGINE", SearchEngineAdapter.SearchResultType.SearchFolders);

                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
                firebaseAnalytics.logEvent(SearchEngineAdapter.SEARCH_ENGINE_USED_LOG, bundleSearchEngineUsed);

                if (functionsClass.securityServicesSubscribed()) {
                    if (functionsClass.readPreference(".Password", "Pin", "0").equals("0") && functionsClass.securityServicesSubscribed()) {
                        startActivity(new Intent(getApplicationContext(), HandlePinPassword.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                    } else {
                        if (!SearchEngineAdapter.alreadyAuthenticatedSearchEngine) {
                            if (functionsClass.securityServicesSubscribed()) {
                                FunctionsClassSecurity.AuthOpenAppValues.setAuthComponentName(getString(R.string.securityServices));
                                FunctionsClassSecurity.AuthOpenAppValues.setAuthSecondComponentName(getPackageName());
                                FunctionsClassSecurity.AuthOpenAppValues.setAuthSearchEngine(true);

                                functionsClassSecurity.openAuthInvocation();

                                IntentFilter intentFilter = new IntentFilter();
                                intentFilter.addAction("SEARCH_ENGINE_AUTHENTICATED");
                                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        if (intent.getAction().equals("SEARCH_ENGINE_AUTHENTICATED")) {
                                            performSearchEngine(finalBackgroundTemporaryInput);
                                        }
                                    }
                                };
                                try {
                                    registerReceiver(broadcastReceiver, intentFilter);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            performSearchEngine(finalBackgroundTemporaryInput);
                        }
                    }
                } else {
                    performSearchEngine(finalBackgroundTemporaryInput);
                }
            }
        });
    }

    private void performSearchEngine(GradientDrawable finalBackgroundTemporaryInput) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (functionsClass.searchEngineSubscribed()) {
                    textInputSearchView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                    textInputSearchView.setVisibility(View.VISIBLE);

                    searchIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                    searchIcon.setVisibility(View.INVISIBLE);

                    ValueAnimator valueAnimatorCornerDown = ValueAnimator.ofInt(functionsClass.DpToInteger(51), functionsClass.DpToInteger(7));
                    valueAnimatorCornerDown.setDuration(777);
                    valueAnimatorCornerDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            int animatorValue = (int) animator.getAnimatedValue();

                            textInputSearchView.setBoxCornerRadii(animatorValue, animatorValue, animatorValue, animatorValue);
                            finalBackgroundTemporaryInput.setCornerRadius(animatorValue);
                        }
                    });
                    valueAnimatorCornerDown.start();

                    ValueAnimator valueAnimatorScalesUp = ValueAnimator.ofInt(functionsClass.DpToInteger(51), switchWidgets.getWidth());
                    valueAnimatorScalesUp.setDuration(777);
                    valueAnimatorScalesUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            int animatorValue = (int) animator.getAnimatedValue();

                            textInputSearchView.getLayoutParams().width = animatorValue;
                            textInputSearchView.requestLayout();
                        }
                    });
                    valueAnimatorScalesUp.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            searchView.requestFocus();

                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    searchFloatIt.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_up_bounce_interpolator));
                                    searchFloatIt.setVisibility(View.VISIBLE);

                                    searchClose.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_up_bounce_interpolator));
                                    searchClose.setVisibility(View.VISIBLE);
                                }
                            }, 555);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    valueAnimatorScalesUp.start();

                    searchFloatIt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!searchView.getText().toString().isEmpty() && (SearchEngineAdapter.allSearchResultItems.size() > 0) && (searchView.getText().toString().length() >= 2)) {
                                for (AdapterItems searchResultItem : SearchEngineAdapter.allSearchResultItems) {
                                    switch (searchResultItem.getSearchResultType()) {
                                        case SearchEngineAdapter.SearchResultType.SearchShortcuts: {
                                            functionsClass.runUnlimitedShortcutsService(searchResultItem.getPackageName());

                                            break;
                                        }
                                        case SearchEngineAdapter.SearchResultType.SearchFolders: {
                                            functionsClass.runUnlimitedFolderService(searchResultItem.getCategory());

                                            break;
                                        }
                                        case SearchEngineAdapter.SearchResultType.SearchWidgets: {
                                            functionsClass
                                                    .runUnlimitedWidgetService(searchResultItem.getAppWidgetId(),
                                                            searchResultItem.getWidgetLabel());

                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    });

                    searchView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                if (SearchEngineAdapter.allSearchResultItems.size() == 1 && !searchView.getText().toString().isEmpty() && (searchView.getText().toString().length() >= 2)) {
                                    switch (SearchEngineAdapter.allSearchResultItems.get(0).getSearchResultType()) {
                                        case SearchEngineAdapter.SearchResultType.SearchShortcuts: {
                                            functionsClass.runUnlimitedShortcutsService(SearchEngineAdapter.allSearchResultItems.get(0).getPackageName());

                                            break;
                                        }
                                        case SearchEngineAdapter.SearchResultType.SearchFolders: {
                                            functionsClass.runUnlimitedFolderService(SearchEngineAdapter.allSearchResultItems.get(0).getCategory());

                                            break;
                                        }
                                        case SearchEngineAdapter.SearchResultType.SearchWidgets: {
                                            functionsClass
                                                    .runUnlimitedWidgetService(SearchEngineAdapter.allSearchResultItems.get(0).getAppWidgetId(),
                                                            SearchEngineAdapter.allSearchResultItems.get(0).getWidgetLabel());

                                            break;
                                        }
                                    }

                                    searchView.setText("");

                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                                    ValueAnimator valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51));
                                    valueAnimatorCornerUp.setDuration(777);
                                    valueAnimatorCornerUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animator) {
                                            int animatorValue = (int) animator.getAnimatedValue();

                                            textInputSearchView.setBoxCornerRadii(animatorValue, animatorValue, animatorValue, animatorValue);
                                            finalBackgroundTemporaryInput.setCornerRadius(animatorValue);
                                        }
                                    });
                                    valueAnimatorCornerUp.start();

                                    ValueAnimator valueAnimatorScales = ValueAnimator.ofInt(textInputSearchView.getWidth(), functionsClass.DpToInteger(51));
                                    valueAnimatorScales.setDuration(777);
                                    valueAnimatorScales.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animator) {
                                            int animatorValue = (int) animator.getAnimatedValue();

                                            textInputSearchView.getLayoutParams().width = animatorValue;
                                            textInputSearchView.requestLayout();
                                        }
                                    });
                                    valueAnimatorScales.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            textInputSearchView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                            textInputSearchView.setVisibility(View.INVISIBLE);

                                            searchIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                            searchIcon.setVisibility(View.VISIBLE);

                                            searchFloatIt.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down_zero));
                                            searchFloatIt.setVisibility(View.INVISIBLE);

                                            searchClose.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down_zero));
                                            searchClose.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                    valueAnimatorScales.start();
                                } else {
                                    if (SearchEngineAdapter.allSearchResultItems.size() > 0 && (searchView.getText().toString().length() >= 2)) {
                                        searchView.showDropDown();
                                    }
                                }
                            }
                            return false;
                        }
                    });

                    searchClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            searchView.setText("");

                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                            ValueAnimator valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51));
                            valueAnimatorCornerUp.setDuration(777);
                            valueAnimatorCornerUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    int animatorValue = (int) animator.getAnimatedValue();

                                    textInputSearchView.setBoxCornerRadii(animatorValue, animatorValue, animatorValue, animatorValue);
                                    finalBackgroundTemporaryInput.setCornerRadius(animatorValue);
                                }
                            });
                            valueAnimatorCornerUp.start();

                            ValueAnimator valueAnimatorScales = ValueAnimator.ofInt(textInputSearchView.getWidth(), functionsClass.DpToInteger(51));
                            valueAnimatorScales.setDuration(777);
                            valueAnimatorScales.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    int animatorValue = (int) animator.getAnimatedValue();

                                    textInputSearchView.getLayoutParams().width = animatorValue;
                                    textInputSearchView.requestLayout();
                                }
                            });
                            valueAnimatorScales.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    textInputSearchView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                    textInputSearchView.setVisibility(View.INVISIBLE);

                                    searchIcon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                    searchIcon.setVisibility(View.VISIBLE);

                                    searchFloatIt.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down_zero));
                                    searchFloatIt.setVisibility(View.INVISIBLE);

                                    searchClose.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down_zero));
                                    searchClose.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            valueAnimatorScales.start();
                        }
                    });
                } else {
                    InAppBilling.ItemIAB = BillingManager.iapSearchEngines;

                    startActivity(new Intent(getApplicationContext(), InAppBilling.class),
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                }
            }
        }, 99);
    }
    /*Search Engine*/
}
