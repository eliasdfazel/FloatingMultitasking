package net.geekstools.floatshort.PRO.Shortcuts;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.geekstools.floatshort.PRO.BindServices;
import net.geekstools.floatshort.PRO.BuildConfig;
import net.geekstools.floatshort.PRO.Category.CategoryHandler;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Shortcuts.NavAdapter.CardGridAdapter;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.IAP.InAppBilling;
import net.geekstools.floatshort.PRO.Util.LicenseValidator;
import net.geekstools.floatshort.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Util.UI.SimpleGestureFilterSwitch;
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations;
import net.geekstools.imageview.customshapes.ShapesImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class GridViewOff extends Activity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener, SimpleGestureFilterSwitch.SimpleGestureListener {

    Activity activity;
    FunctionsClass functionsClass;
    GridView loadView;
    RelativeLayout fullActionButton, MainView, loadingSplash;
    ListView actionElementsList;
    LinearLayout indexView, freqView;
    ProgressBar loadingBarLTR;
    ImageView loadLogo;

    List<ApplicationInfo> applicationInfoList;
    Map<String, Integer> mapIndex;
    ArrayList<NavDrawerItem> navDrawerItemsInit, navDrawerItems;
    CardGridAdapter cardGridAdapter;

    String PackageName;
    String AppName = "Application";
    Drawable AppIcon;

    HorizontalScrollView freqlist;
    Drawable[] freqAppsIcons;
    String[] freqApps;
    int limitedCountLine, loadViewPosition = 0;
    int[] counter;
    boolean loadFreq = false;

    SimpleGestureFilterSwitch simpleGestureFilterSwitch;

    FirebaseRemoteConfig firebaseRemoteConfig;
    ProgressDialog progressDialog;
    LoadCustomIcons loadCustomIcons;
    private FirebaseAuth firebaseAuth;


    public void floatingWidget(View view) {

        if (functionsClass.floatingWidgetsPurchased() || functionsClass.appVersionName(getPackageName()).contains("[BETA]")) {
            startActivity(new Intent(getApplicationContext(), WidgetConfigurations.class),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.down_up, android.R.anim.fade_out).toBundle());
        } else {
            startActivity(new Intent(getApplicationContext(), InAppBilling.class)
                            .putExtra("UserEmailAddress", functionsClass.readPreference(".BETA", "testerEmail", null)),
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.down_up, android.R.anim.fade_out).toBundle());
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.off_grid_view);
        loadView = (GridView) findViewById(R.id.grid);
        indexView = (LinearLayout) findViewById(R.id.side_index);
        freqView = (LinearLayout) findViewById(R.id.freqItem);
        MainView = (RelativeLayout) findViewById(R.id.MainView);
        fullActionButton = (RelativeLayout) findViewById(R.id.fullActionButton);
        loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
        actionElementsList = (ListView) findViewById(R.id.acttionElementsList);

        simpleGestureFilterSwitch = new SimpleGestureFilterSwitch(getApplicationContext(), this);
        functionsClass = new FunctionsClass(getApplicationContext(), this);
        functionsClass.ChangeLog(GridViewOff.this, false);
        activity = this;

        if (functionsClass.appThemeTransparent() == true) {
            functionsClass.setThemeColor(MainView, true, getString(R.string.floatingHint), "");
        } else {
            functionsClass.setThemeColor(MainView, false, getString(R.string.floatingHint), "");
        }

        navDrawerItemsInit = new ArrayList<NavDrawerItem>();
        navDrawerItems = new ArrayList<NavDrawerItem>();
        mapIndex = new LinkedHashMap<String, Integer>();

        if (functionsClass.loadCustomIcons()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }

        LoadApplicationsOffLimited loadApplicationsOffLimited = new LoadApplicationsOffLimited();
        loadApplicationsOffLimited.execute();

        Button switchShortcuts = (Button) findViewById(R.id.switchShortcuts);
        Button switchCategories = (Button) findViewById(R.id.switchCategories);

        switchShortcuts.setTextColor(getResources().getColor(R.color.light));
        switchCategories.setTextColor(getResources().getColor(R.color.light));
        if (PublicVariable.themeLightDark && functionsClass.appThemeTransparent()) {
            switchShortcuts.setTextColor(getResources().getColor(R.color.dark));
            switchCategories.setTextColor(getResources().getColor(R.color.dark));
        }

        RippleDrawable rippleDrawableShortcuts = (RippleDrawable) getResources().getDrawable(R.drawable.draw_shortcuts);
        GradientDrawable gradientDrawableShortcutsForeground = (GradientDrawable) rippleDrawableShortcuts.findDrawableByLayerId(R.id.foregroundItem);
        GradientDrawable gradientDrawableShortcutsBackground = (GradientDrawable) rippleDrawableShortcuts.findDrawableByLayerId(R.id.backgroundItem);
        GradientDrawable gradientDrawableMaskShortcuts = (GradientDrawable) rippleDrawableShortcuts.findDrawableByLayerId(android.R.id.mask);

        if (functionsClass.appThemeTransparent()) {
            rippleDrawableShortcuts.setColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
            gradientDrawableShortcutsForeground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColor, 255));
            if (functionsClass.returnAPI() > 21) {
                gradientDrawableShortcutsBackground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColor, 155));
            } else {
                gradientDrawableShortcutsBackground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColor, 155));
            }
            gradientDrawableMaskShortcuts.setColor(PublicVariable.primaryColorOpposite);
        } else {
            rippleDrawableShortcuts.setColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
            gradientDrawableShortcutsForeground.setColor(PublicVariable.primaryColor);
            gradientDrawableShortcutsBackground.setTint(PublicVariable.primaryColor);
            gradientDrawableMaskShortcuts.setColor(PublicVariable.primaryColorOpposite);
        }

        RippleDrawable rippleDrawableCategories = (RippleDrawable) getResources().getDrawable(R.drawable.draw_categories);
        GradientDrawable gradientDrawableCategoriesForeground = (GradientDrawable) rippleDrawableCategories.findDrawableByLayerId(R.id.foregroundItem);
        GradientDrawable gradientDrawableCategoriesBackground = (GradientDrawable) rippleDrawableCategories.findDrawableByLayerId(R.id.backgroundItem);
        GradientDrawable gradientDrawableMaskCategories = (GradientDrawable) rippleDrawableCategories.findDrawableByLayerId(android.R.id.mask);

        if (functionsClass.appThemeTransparent()) {
            rippleDrawableCategories.setColor(ColorStateList.valueOf(PublicVariable.primaryColor));
            gradientDrawableCategoriesForeground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 255));
            if (functionsClass.returnAPI() > 21) {
                gradientDrawableCategoriesBackground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175));
            } else {
                gradientDrawableShortcutsBackground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175));
            }
            gradientDrawableMaskCategories.setColor(PublicVariable.primaryColor);
        } else {
            rippleDrawableCategories.setColor(ColorStateList.valueOf(PublicVariable.primaryColor));
            gradientDrawableCategoriesForeground.setColor(PublicVariable.primaryColorOpposite);
            gradientDrawableCategoriesBackground.setTint(PublicVariable.primaryColorOpposite);
            gradientDrawableMaskCategories.setColor(PublicVariable.primaryColor);
        }

        switchShortcuts.setBackground(rippleDrawableShortcuts);
        switchCategories.setBackground(rippleDrawableCategories);
        switchCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPressToClass(CategoryHandler.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
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
                    functionsClass.dialogueLicense(GridViewOff.this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopService(new Intent(getApplicationContext(), LicenseValidator.class));
                        }
                    }, 1000);
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
        try {
            if (!getFileStreamPath(".License").exists() && functionsClass.networkConnection() == true) {
                if (!BuildConfig.DEBUG || !functionsClass.appVersionName(getPackageName()).contains("[BETA]")) {
                    startService(new Intent(getApplicationContext(), LicenseValidator.class));
                }
            } else {
                try {
                    unregisterReceiver(broadcastReceiver);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                loadViewPosition = firstVisibleItem;
            }
        });

        if (functionsClass.SystemCache()) {
            startService(new Intent(getApplicationContext(), BindServices.class));
        }

        try {
            if (!BuildConfig.DEBUG && functionsClass.networkConnection()) {
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
                                        functionsClass.savePreference(".BETA", "testerEmail", null);

                                    } else {

                                    }


                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (functionsClass.readPreference(".BETA", "isBetaTester", false) && functionsClass.readPreference(".BETA", "testerEmail", null) == null) {
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

                    progressDialog = new ProgressDialog(activity,
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
    }

    @Override
    public void onResume() {
        super.onResume();
        PublicVariable.inMemory = true;

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default);
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(GridViewOff.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activateFetched();
                            if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(getPackageName())) {
                                getActionBar().setDisplayHomeAsUpEnabled(true);
                                LayerDrawable layerDrawableNewUpdate = (LayerDrawable) getDrawable(R.drawable.ic_update);
                                BitmapDrawable gradientDrawableNewUpdate = (BitmapDrawable) layerDrawableNewUpdate.findDrawableByLayerId(R.id.ic_launcher_back_layer);
                                gradientDrawableNewUpdate.setTint(PublicVariable.primaryColor);

                                Bitmap tempBitmap = functionsClass.drawableToBitmap(layerDrawableNewUpdate);
                                Bitmap scaleBitmap = Bitmap.createScaledBitmap(tempBitmap, tempBitmap.getWidth() / 4, tempBitmap.getHeight() / 4, false);
                                Drawable logoDrawable = new BitmapDrawable(getResources(), scaleBitmap);
                                activity.getActionBar().setHomeAsUpIndicator(logoDrawable);

                                functionsClass.notificationCreator(
                                        getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogSummaryConfigKey()),
                                        (int) firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey())
                                );
                            } else {
                            }
                        } else {
                        }
                    }
                });

        functionsClass.addAppShortcuts();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        functionsClass.addAppShortcuts();
        functionsClass.savePreference("LoadView", "LoadViewPosition", loadView.getFirstVisiblePosition());
        if (PublicVariable.actionCenter == true) {
            functionsClass.closeMenuOption(fullActionButton, actionElementsList);
        }
        if (PublicVariable.recoveryCenter == true) {
            functionsClass.closeRecoveryMenuOption(fullActionButton, actionElementsList);
        }
        functionsClass.savePreference("OpenMode", "openClassName", this.getClass().getSimpleName());
        functionsClass.CheckSystemRAM(GridViewOff.this);

        if (functionsClass.SystemCache() || functionsClass.automationFeatureEnable()) {
            startService(new Intent(getApplicationContext(), BindServices.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PublicVariable.inMemory = false;
    }

    @Override
    public void onBackPressed() {
        Intent homeScreen = new Intent(Intent.ACTION_MAIN);
        homeScreen.addCategory(Intent.CATEGORY_HOME);
        homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeScreen);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof ImageView) {
            ImageView freqApp = (ImageView) view;
            int position = freqApp.getId();
            functionsClass.runUnlimitedShortcutsService(freqApps[position]);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view instanceof ImageView) {
            ImageView freqApp = (ImageView) view;
            int position = freqApp.getId();
            functionsClass.appsLaunchPad(freqApps[position]);
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (view instanceof TextView) {
            final TextView selectedIndex = (TextView) view;
            loadView.post(new Runnable() {
                @Override
                public void run() {
                    loadView.smoothScrollToPositionFromTop(mapIndex.get(selectedIndex.getText().toString()), 0, 213);
                }
            });
        }
        return true;
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilterSwitch.SWIPE_RIGHT: {
                System.out.println("Swipe Right");

                break;
            }
            case SimpleGestureFilterSwitch.SWIPE_LEFT: {
                System.out.println("Swipe Left");
                try {
                    functionsClass.overrideBackPressToClass(CategoryHandler.class,
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_from_right, R.anim.slide_to_left));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        this.simpleGestureFilterSwitch.onTouchEvent(me);

        return super.dispatchTouchEvent(me);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_scope, menu);
        MenuItem floating = menu.findItem(R.id.floating);
        MenuItem recovery = menu.findItem(R.id.recov);

        LayerDrawable drawFloating = (LayerDrawable) getResources().getDrawable(R.drawable.draw_floating);
        GradientDrawable backFloating = (GradientDrawable) drawFloating.findDrawableByLayerId(R.id.backtemp);

        LayerDrawable drawRecov = (LayerDrawable) getResources().getDrawable(R.drawable.draw_recovery);
        GradientDrawable backRecov = (GradientDrawable) drawRecov.findDrawableByLayerId(R.id.backtemp);

        backFloating.setColor(functionsClass.optionMenuColor());
        backRecov.setColor(functionsClass.optionMenuColor());

        floating.setIcon(drawFloating);
        recovery.setIcon(drawRecov);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.floating: {
                if (fullActionButton.isShown()) {
                    PublicVariable.recoveryCenter = false;
                }
                if (PublicVariable.actionCenter == false) {
                    functionsClass.menuOption(fullActionButton, actionElementsList, fullActionButton.isShown());
                } else {
                    functionsClass.closeMenuOption(fullActionButton, actionElementsList);
                }
                break;
            }
            case R.id.recov: {
                if (fullActionButton.isShown()) {
                    PublicVariable.actionCenter = false;
                }
                if (PublicVariable.recoveryCenter == false) {
                    functionsClass.recoveryOption(fullActionButton, actionElementsList, fullActionButton.isShown());
                } else {
                    functionsClass.closeRecoveryMenuOption(fullActionButton, actionElementsList);
                }
                break;
            }
            case android.R.id.home: {
                functionsClass.upcomingChangeLog(
                        GridViewOff.this,
                        firebaseRemoteConfig.getString(functionsClass.upcomingChangeLogRemoteConfigKey()),
                        String.valueOf(firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()))
                );
                break;
            }
        }
        return super.onOptionsItemSelected(item);
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
                                        functionsClass.savePreference(".BETA", "testerEmail", firebaseUser.getEmail());

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    File betaFile = new File("/data/data/" + getPackageName() + "/shared_prefs/.BETA.xml");
                                                    Uri uriBetaFile = Uri.fromFile(betaFile);
                                                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

                                                    StorageReference storageReference = firebaseStorage.getReference("/betaTesters/" + "API" + functionsClass.returnAPI() + "/" +
                                                            functionsClass.readPreference(".BETA", "testerEmail", null));
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
                                                            System.out.println("Firebase Activities Done Successfully");
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

    private void LoadFrequentlyApps() {
        try {
            freqView.removeAllViews();

            freqAppsIcons = new Drawable[25];
            counter = new int[25];

            freqApps = getIntent().getStringArrayExtra("freq");
            int freqLength = getIntent().getIntExtra("num", -1);
            if (getFileStreamPath("Frequently").exists()) {
                functionsClass.removeLine(".categoryInfo", "Frequently");
                deleteFile("Frequently");
            }
            functionsClass.saveFileAppendLine(".categoryInfo", "Frequently");

            freqlist = (HorizontalScrollView) findViewById(R.id.freqlist);
            LayerDrawable drawFreq = (LayerDrawable) getResources().getDrawable(R.drawable.layer_freq);
            GradientDrawable backFreq = (GradientDrawable) drawFreq.findDrawableByLayerId(R.id.backtemp);
            backFreq.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColor, 155));
            freqlist.setBackground(drawFreq);
            freqlist.setVisibility(View.VISIBLE);

            ShapesImage shapesImage;
            RelativeLayout freqLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.freq_item, null);
            for (int i = 0; i < freqLength; i++) {
                freqLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.freq_item, null);
                shapesImage = functionsClass.initShapesImage(freqLayout, R.id.freqItems);
                shapesImage.setId(i);
                shapesImage.setOnClickListener(GridViewOff.this);
                shapesImage.setOnLongClickListener(GridViewOff.this);
                shapesImage.setImageDrawable(functionsClass.loadCustomIcons() ?
                        loadCustomIcons.getDrawableIconForPackage(freqApps[i], functionsClass.shapedAppIcon(freqApps[i]))
                        :
                        functionsClass.shapedAppIcon(freqApps[i]));
                freqView.addView(freqLayout);

                functionsClass.saveFileAppendLine("Frequently", freqApps[i]);
                functionsClass.saveFile(freqApps[i] + "Frequently", freqApps[i]);
            }
            functionsClass.addAppShortcuts();

            if (functionsClass.appThemeTransparent()) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        functionsClass.DpToInteger(50)
                );
                params.topMargin = 0;
                params.addRule(RelativeLayout.BELOW, R.id.switchFloating);
                freqlist.setLayoutParams(params);

                Button switchShortcuts = (Button) findViewById(R.id.switchShortcuts);
                Button switchCategories = (Button) findViewById(R.id.switchCategories);

                RippleDrawable rippleDrawableShortcuts = (RippleDrawable) getResources().getDrawable(R.drawable.draw_shortcuts_no_gradiant);
                GradientDrawable gradientDrawableShortcutsForeground = (GradientDrawable) rippleDrawableShortcuts.findDrawableByLayerId(R.id.foregroundItem);
                GradientDrawable gradientDrawableShortcutsBackground = (GradientDrawable) rippleDrawableShortcuts.findDrawableByLayerId(R.id.backgroundItem);
                GradientDrawable gradientDrawableMaskShortcuts = (GradientDrawable) rippleDrawableShortcuts.findDrawableByLayerId(android.R.id.mask);

                if (functionsClass.appThemeTransparent()) {
                    rippleDrawableShortcuts.setColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
                    gradientDrawableShortcutsForeground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColor, 255));
                    if (functionsClass.returnAPI() > 21) {
                        gradientDrawableShortcutsBackground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColor, 155));
                    } else {
                        gradientDrawableShortcutsBackground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColor, 155));
                    }
                    gradientDrawableMaskShortcuts.setColor(PublicVariable.primaryColorOpposite);
                } else {
                    rippleDrawableShortcuts.setColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
                    gradientDrawableShortcutsForeground.setColor(PublicVariable.primaryColor);
                    gradientDrawableShortcutsBackground.setTint(PublicVariable.primaryColor);
                    gradientDrawableMaskShortcuts.setColor(PublicVariable.primaryColorOpposite);
                }

                RippleDrawable rippleDrawableCategories = (RippleDrawable) getResources().getDrawable(R.drawable.draw_categories_no_gradiant);
                GradientDrawable gradientDrawableCategoriesForeground = (GradientDrawable) rippleDrawableCategories.findDrawableByLayerId(R.id.foregroundItem);
                GradientDrawable gradientDrawableCategoriesBackground = (GradientDrawable) rippleDrawableCategories.findDrawableByLayerId(R.id.backgroundItem);
                GradientDrawable gradientDrawableMaskCategories = (GradientDrawable) rippleDrawableCategories.findDrawableByLayerId(android.R.id.mask);

                if (functionsClass.appThemeTransparent()) {
                    rippleDrawableCategories.setColor(ColorStateList.valueOf(PublicVariable.primaryColor));
                    gradientDrawableCategoriesForeground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 255));
                    if (functionsClass.returnAPI() > 21) {
                        gradientDrawableCategoriesBackground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175));
                    } else {
                        gradientDrawableShortcutsBackground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175));
                    }
                    gradientDrawableMaskCategories.setColor(PublicVariable.primaryColor);
                } else {
                    rippleDrawableCategories.setColor(ColorStateList.valueOf(PublicVariable.primaryColor));
                    gradientDrawableCategoriesForeground.setColor(PublicVariable.primaryColorOpposite);
                    gradientDrawableCategoriesBackground.setTint(PublicVariable.primaryColorOpposite);
                    gradientDrawableMaskCategories.setColor(PublicVariable.primaryColor);
                }

                switchShortcuts.setBackground(rippleDrawableShortcuts);
                switchCategories.setBackground(rippleDrawableCategories);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LoadApplicationsOffLimited extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
            if (functionsClass.appThemeTransparent() == true) {
                loadingSplash.setBackgroundColor(Color.TRANSPARENT);
            } else {
                loadingSplash.setBackgroundColor(getWindow().getNavigationBarColor());
            }

            loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgressltr);
            if (PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeTextColor, android.graphics.PorterDuff.Mode.MULTIPLY);
            } else if (!PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeColor, android.graphics.PorterDuff.Mode.MULTIPLY);
            }

            loadLogo = (ImageView) findViewById(R.id.loadLogo);
            LayerDrawable layerDrawableLoadLogo = (LayerDrawable) getDrawable(R.drawable.ic_launcher_layer);
            BitmapDrawable gradientDrawableLoadLogo = (BitmapDrawable) layerDrawableLoadLogo.findDrawableByLayerId(R.id.ic_launcher_back_layer);
            gradientDrawableLoadLogo.setTint(PublicVariable.primaryColor);
            loadLogo.setImageDrawable(layerDrawableLoadLogo);

            loadView.clearChoices();
            indexView.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                applicationInfoList = getApplicationContext().getPackageManager().getInstalledApplications(0);
                Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(getPackageManager()));
                limitedCountLine = ((int) applicationInfoList.size() / 3);

                if (functionsClass.loadCustomIcons()) {
                    loadCustomIcons.load();
                    if (BuildConfig.DEBUG) {
                        System.out.println("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                    }
                }

                for (int appInfo = 0; appInfo < limitedCountLine; appInfo++) {
                    if (getPackageManager().getLaunchIntentForPackage(applicationInfoList.get(appInfo).packageName) != null) {
                        try {
                            PackageName = applicationInfoList.get(appInfo).packageName;
                            AppName = functionsClass.appName(PackageName);
                            AppIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(PackageName, functionsClass.shapedAppIcon(PackageName)) : functionsClass.shapedAppIcon(PackageName);

                            navDrawerItems.add(new NavDrawerItem(AppName, PackageName, AppIcon));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                cardGridAdapter = new CardGridAdapter(activity, getApplicationContext(), navDrawerItems);
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
                finish();
            } finally {
                if (getIntent().getStringArrayExtra("freq") != null) {
                    freqApps = getIntent().getStringArrayExtra("freq");
                    int freqLength = getIntent().getIntExtra("num", -1);
                    PublicVariable.freqApps = freqApps;
                    PublicVariable.freqLength = freqLength;
                    if (freqLength > 1) {
                        loadFreq = true;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (loadFreq == false) {
                freqlist = (HorizontalScrollView) findViewById(R.id.freqlist);
                MainView.removeView(freqlist);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );
                params.addRule(RelativeLayout.BELOW, R.id.switchFloating);
                loadView.setLayoutParams(params);
            }
            cardGridAdapter.notifyDataSetChanged();
            loadView.setAdapter(cardGridAdapter);

            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
            loadingSplash.startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (loadFreq == true) {
                        LoadFrequentlyApps();
                    }
                    ((LinearLayout) findViewById(R.id.switchFloating)).setVisibility(View.VISIBLE);
                    LoadApplicationsOff loadApplicationsOff = new LoadApplicationsOff();
                    loadApplicationsOff.execute();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    loadingSplash.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    private class LoadApplicationsOff extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int appInfo = limitedCountLine; appInfo < applicationInfoList.size(); appInfo++) {
                    if (getPackageManager().getLaunchIntentForPackage(applicationInfoList.get(appInfo).packageName) != null) {
                        try {
                            PackageName = applicationInfoList.get(appInfo).packageName;
                            AppName = functionsClass.appName(PackageName);
                            AppIcon = functionsClass.loadCustomIcons() ? loadCustomIcons.getDrawableIconForPackage(PackageName, functionsClass.shapedAppIcon(PackageName)) : functionsClass.shapedAppIcon(PackageName);

                            navDrawerItems.add(new NavDrawerItem(AppName, PackageName, AppIcon));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                cardGridAdapter = new CardGridAdapter(activity, getApplicationContext(), navDrawerItems);
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
                finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            cardGridAdapter.notifyDataSetChanged();
            loadView.setAdapter(cardGridAdapter);

            if (loadViewPosition == 0) {
                loadView.setSelection(getSharedPreferences("LoadView", Context.MODE_PRIVATE).getInt("LoadViewPosition", 0));
            } else {
                loadView.setSelection(loadViewPosition);
            }

            LoadApplicationsIndex loadApplicationsIndex = new LoadApplicationsIndex();
            loadApplicationsIndex.execute();

            try {
                Intent goHome = getIntent();
                if (goHome.hasExtra("goHome")) {
                    if (goHome.getBooleanExtra("goHome", false)) {
                        Intent homeScreen = new Intent(Intent.ACTION_MAIN);
                        homeScreen.addCategory(Intent.CATEGORY_HOME);
                        homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(homeScreen);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    private class LoadApplicationsIndex extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            indexView.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int itemCount = 0; itemCount < navDrawerItems.size(); itemCount++) {
                try {
                    String index = (navDrawerItems.get(itemCount).getAppName()).substring(0, 1).toUpperCase();
                    if (mapIndex.get(index) == null) {
                        mapIndex.put(index, itemCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            LayerDrawable drawIndex = (LayerDrawable) getResources().getDrawable(R.drawable.draw_index);
            GradientDrawable backIndex = (GradientDrawable) drawIndex.findDrawableByLayerId(R.id.backtemp);
            backIndex.setColor(Color.TRANSPARENT);

            TextView textView = null;
            List<String> indexList = new ArrayList<String>(mapIndex.keySet());
            for (String index : indexList) {
                textView = (TextView) getLayoutInflater()
                        .inflate(R.layout.side_index_item, null);
                textView.setBackground(drawIndex);
                textView.setText(index.toUpperCase());
                textView.setTextColor(PublicVariable.colorLightDarkOpposite);
                textView.setOnTouchListener(GridViewOff.this);
                indexView.addView(textView);
            }

            LoadInstalledCustomIcons loadInstalledCustomIcons = new LoadInstalledCustomIcons();
            loadInstalledCustomIcons.execute();
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
                    if (BuildConfig.DEBUG) {
                        System.out.println("CustomIconPackages ::: " + resolveInfo.activityInfo.packageName);
                    }
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
}
