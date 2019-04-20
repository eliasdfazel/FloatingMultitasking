package net.geekstools.floatshort.PRO.Category;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

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
import net.geekstools.floatshort.PRO.Category.NavAdapter.CategoryListAdapter;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.IAP.InAppBilling;
import net.geekstools.floatshort.PRO.Util.LicenseValidator;
import net.geekstools.floatshort.PRO.Util.NavAdapter.NavDrawerItem;
import net.geekstools.floatshort.PRO.Util.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.Util.UI.SimpleGestureFilterSwitch;
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CategoryHandler extends Activity implements View.OnClickListener, View.OnLongClickListener, SimpleGestureFilterSwitch.SimpleGestureListener {

    FunctionsClass functionsClass;

    RelativeLayout fullActionButton;
    ListView actionElementsList;
    RecyclerView categorylist;
    RelativeLayout wholeCategory;

    RecyclerView.Adapter categoryListAdapter;
    ArrayList<NavDrawerItem> navDrawerItems;

    String[] appData;

    RelativeLayout loadingSplash;
    ProgressBar loadingBarLTR;
    TextView gx;

    BroadcastReceiver broadcastReceiver;

    SimpleGestureFilterSwitch simpleGestureFilterSwitch;

    LoadCustomIcons loadCustomIcons;

    FirebaseRemoteConfig firebaseRemoteConfig;

    ProgressDialog progressDialog;

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
        setContentView(R.layout.category_handler);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        wholeCategory = (RelativeLayout) findViewById(R.id.wholeCategory);
        categorylist = (RecyclerView) findViewById(R.id.categorylist);
        fullActionButton = (RelativeLayout) findViewById(R.id.fullActionViews);
        actionElementsList = (ListView) findViewById(R.id.actionElementsList);

        simpleGestureFilterSwitch = new SimpleGestureFilterSwitch(getApplicationContext(), this);
        functionsClass = new FunctionsClass(getApplicationContext(), this);
        functionsClass.ChangeLog(CategoryHandler.this, false);

        if (functionsClass.appThemeTransparent() == true) {
            functionsClass.setThemeColorFloating(wholeCategory, true);
        } else {
            functionsClass.setThemeColorFloating(wholeCategory, false);
        }
        navDrawerItems = new ArrayList<NavDrawerItem>();

        LinearLayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext(), OrientationHelper.VERTICAL, false);
        categorylist.setLayoutManager(recyclerViewLayoutManager);

        ImageView floatingLogo = (ImageView) findViewById(R.id.loadLogo);
        LayerDrawable drawFloatingLogo = (LayerDrawable) getResources().getDrawable(R.drawable.draw_floating_logo);
        GradientDrawable backFloatingLogo = (GradientDrawable) drawFloatingLogo.findDrawableByLayerId(R.id.backtemp);
        backFloatingLogo.setColor(PublicVariable.primaryColorOpposite);
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
        registerReceiver(broadcastReceiver, intentFilter);

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
            rippleDrawableShortcuts.setColor(ColorStateList.valueOf(PublicVariable.primaryColor));
            gradientDrawableShortcutsForeground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 255));
            if (functionsClass.returnAPI() > 21) {
                gradientDrawableShortcutsBackground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175));
            } else {
                gradientDrawableShortcutsBackground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175));
            }
            gradientDrawableMaskShortcuts.setColor(PublicVariable.primaryColor);
        } else {
            rippleDrawableShortcuts.setColor(ColorStateList.valueOf(PublicVariable.primaryColor));
            gradientDrawableShortcutsForeground.setColor(PublicVariable.primaryColorOpposite);
            gradientDrawableShortcutsBackground.setTint(PublicVariable.primaryColorOpposite);
            gradientDrawableMaskShortcuts.setColor(PublicVariable.primaryColor);
        }

        RippleDrawable rippleDrawableCategories = (RippleDrawable) getResources().getDrawable(R.drawable.draw_categories);
        GradientDrawable gradientDrawableCategoriesForeground = (GradientDrawable) rippleDrawableCategories.findDrawableByLayerId(R.id.foregroundItem);
        GradientDrawable gradientDrawableCategoriesBackground = (GradientDrawable) rippleDrawableCategories.findDrawableByLayerId(R.id.backgroundItem);
        GradientDrawable gradientDrawableMaskCategories = (GradientDrawable) rippleDrawableCategories.findDrawableByLayerId(android.R.id.mask);

        if (functionsClass.appThemeTransparent()) {
            rippleDrawableCategories.setColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
            gradientDrawableCategoriesForeground.setColor(functionsClass.setColorAlpha(PublicVariable.primaryColor, 255));
            if (functionsClass.returnAPI() > 21) {
                gradientDrawableCategoriesBackground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColor, 155));
            } else {
                gradientDrawableCategoriesBackground.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColor, 155));
            }
            gradientDrawableMaskCategories.setColor(PublicVariable.primaryColorOpposite);
        } else {
            rippleDrawableCategories.setColor(ColorStateList.valueOf(PublicVariable.primaryColorOpposite));
            gradientDrawableCategoriesForeground.setColor(PublicVariable.primaryColor);
            gradientDrawableCategoriesBackground.setTint(PublicVariable.primaryColor);
            gradientDrawableMaskCategories.setColor(PublicVariable.primaryColorOpposite);
        }

        switchShortcuts.setBackground(rippleDrawableShortcuts);
        switchCategories.setBackground(rippleDrawableCategories);
        switchShortcuts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    functionsClass.overrideBackPressToShortcuts(CategoryHandler.this);
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
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
                .addOnCompleteListener(CategoryHandler.this, new OnCompleteListener<Void>() {
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
                                getActionBar().setHomeAsUpIndicator(logoDrawable);

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
                    functionsClass.dialogueLicense(CategoryHandler.this);
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

                if (functionsClass.readPreference(".BETA", "isBetaTester", false)
                        && functionsClass.readPreference(".BETA", "testerEmail", null) == null) {
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

                    progressDialog = new ProgressDialog(CategoryHandler.this,
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (PublicVariable.actionCenter == true) {
            functionsClass.closeActionMenuOption(fullActionButton, null);
        }

        functionsClass.savePreference("OpenMode", "openClassName", this.getClass().getSimpleName());
        functionsClass.CheckSystemRAM(CategoryHandler.this);
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
        startActivity(homeScreen);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                System.out.println("Swipe Right");
                try {
                    functionsClass.overrideBackPressToShortcuts(CategoryHandler.this);
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case SimpleGestureFilterSwitch.SWIPE_LEFT: {
                System.out.println("Swipe Left");

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

            loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgressltr);
            gx = (TextView) findViewById(R.id.gx);
            Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
            gx.setTypeface(face);

            if (PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeTextColor, android.graphics.PorterDuff.Mode.MULTIPLY);
                gx.setTextColor(getResources().getColor(R.color.dark));
            } else if (!PublicVariable.themeLightDark) {
                loadingBarLTR.getIndeterminateDrawable().setColorFilter(PublicVariable.themeColor, android.graphics.PorterDuff.Mode.MULTIPLY);
                gx.setTextColor(getResources().getColor(R.color.light));
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
                navDrawerItems = new ArrayList<NavDrawerItem>();

                if (functionsClass.loadCustomIcons()) {
                    loadCustomIcons.load();
                    if (BuildConfig.DEBUG) {
                        System.out.println("*** Total Custom Icon ::: " + loadCustomIcons.getTotalIcons());
                    }
                }

                if (getFileStreamPath(".categoryInfo").exists() && functionsClass.countLineInnerFile(".categoryInfo") > 0) {
                    try {
                        appData = functionsClass.readFileLine(".categoryInfo");
                        navDrawerItems = new ArrayList<NavDrawerItem>();
                        for (int navItem = 0; navItem < appData.length; navItem++) {
                            try {
                                navDrawerItems.add(new NavDrawerItem(
                                        appData[navItem],
                                        functionsClass.readFileLine(appData[navItem])));
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                functionsClass.IndexAppInfoCategory(appData[navItem] + " | " + getString(R.string.floatingCategory));
                            }
                        }
                        navDrawerItems.add(new NavDrawerItem(getPackageName(), new String[]{getPackageName()}));
                        categoryListAdapter = new CategoryListAdapter(CategoryHandler.this, getApplicationContext(), navDrawerItems);
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.cancel(true);
                    }
                } else {
                    navDrawerItems = new ArrayList<NavDrawerItem>();
                    navDrawerItems.add(new NavDrawerItem(getPackageName(), new String[]{getPackageName()}));
                    categoryListAdapter = new CategoryListAdapter(CategoryHandler.this, getApplicationContext(), navDrawerItems);
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
            if (loadingSplash.isShown()) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
                loadingSplash.setVisibility(View.INVISIBLE);
                loadingSplash.startAnimation(anim);
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
