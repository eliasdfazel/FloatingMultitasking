/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/26/20 2:51 PM
 * Last modified 3/26/20 2:17 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Folders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.AppSavedListAdapter;
import net.geekstools.floatshort.PRO.Folders.FoldersAdapter.AppSelectionListAdapter;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons;
import net.geekstools.floatshort.PRO.databinding.AdvanceAppSelectionListBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppSelectionList extends Activity implements View.OnClickListener {

    FunctionsClass functionsClass;

    ListPopupWindow listPopupWindow;

    List<ApplicationInfo> applicationInfoList;
    Map<String, Integer> mapIndexFirstItem, mapIndexLastItem;
    Map<Integer, String> mapRangeIndex;
    ArrayList<AdapterItems> adapterItems, navDrawerItemsSaved;
    RecyclerView.Adapter appSelectionListAdapter;
    LinearLayoutManager recyclerViewLayoutManager;
    AppSavedListAdapter advanceSavedListAdapter;

    String PackageName, AppName = "Application";
    Drawable AppIcon;

    boolean resetAdapter = false;

    LoadCustomIcons loadCustomIcons;

    AdvanceAppSelectionListBinding advanceAppSelectionListBinding;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        advanceAppSelectionListBinding = AdvanceAppSelectionListBinding.inflate(getLayoutInflater());
        setContentView(advanceAppSelectionListBinding.getRoot());

        functionsClass = new FunctionsClass(getApplicationContext());

        listPopupWindow = new ListPopupWindow(AppSelectionList.this);

        advanceAppSelectionListBinding.temporaryFallingIcon.bringToFront();

        /*advanceAppSelectionListBinding.firstSplitIcon = */functionsClass.initShapesImage(advanceAppSelectionListBinding.firstSplitIcon);
        /*advanceAppSelectionListBinding.secondSplitIcon = */functionsClass.initShapesImage(advanceAppSelectionListBinding.secondSplitIcon);

        advanceAppSelectionListBinding.confirmLayout.bringToFront();

        if (functionsClass.appThemeTransparent() == true) {
            functionsClass.setThemeColorFloating(AppSelectionList.this, advanceAppSelectionListBinding.getRoot(), true);
        } else {
            functionsClass.setThemeColorFloating(AppSelectionList.this, advanceAppSelectionListBinding.getRoot(), false);
        }

        recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        advanceAppSelectionListBinding.recyclerListView.setLayoutManager(recyclerViewLayoutManager);

        adapterItems = new ArrayList<AdapterItems>();
        navDrawerItemsSaved = new ArrayList<AdapterItems>();
        mapIndexFirstItem = new LinkedHashMap<String, Integer>();
        mapIndexLastItem = new LinkedHashMap<String, Integer>();
        mapRangeIndex = new LinkedHashMap<Integer, String>();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        advanceAppSelectionListBinding.loadingDescription.setTypeface(typeface);
        advanceAppSelectionListBinding.loadingDescription.setTextColor(PublicVariable.colorLightDarkOpposite);
        advanceAppSelectionListBinding.loadingDescription.setText(PublicVariable.categoryName);
        advanceAppSelectionListBinding.appSelectedCounterView.setTypeface(typeface);
        advanceAppSelectionListBinding.appSelectedCounterView.bringToFront();

        if (functionsClass.loadRecoveryIndicatorCategory(PublicVariable.categoryName)) {
            advanceAppSelectionListBinding.folderNameView.setText(PublicVariable.categoryName + " " + "\uD83D\uDD04");
        } else {
            advanceAppSelectionListBinding.folderNameView.setText(PublicVariable.categoryName);
        }

        advanceAppSelectionListBinding.folderNameView.setBackgroundColor(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColor, 51) : PublicVariable.primaryColor);
        advanceAppSelectionListBinding.folderNameView.setRippleColor(ColorStateList.valueOf(functionsClass.appThemeTransparent() ? functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 51) : PublicVariable.primaryColorOpposite));

        LayerDrawable layerDrawableLoadLogo = (LayerDrawable) getDrawable(R.drawable.ic_launcher_layer);
        BitmapDrawable gradientDrawableLoadLogo = (BitmapDrawable) layerDrawableLoadLogo.findDrawableByLayerId(R.id.ic_launcher_back_layer);
        gradientDrawableLoadLogo.setTint(PublicVariable.primaryColor);
        advanceAppSelectionListBinding.loadingLogo.setImageDrawable(layerDrawableLoadLogo);

        ProgressBar loadingProgress = (ProgressBar) findViewById(R.id.loadingProgress);
        loadingProgress.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), PorterDuff.Mode.MULTIPLY);

        /*
         *
         *
         * convert to interface
         * define view programmatically and add it to frameLayout
         *
         *
         * */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.counterActionAdvance));
        intentFilter.addAction(getString(R.string.savedActionAdvance));//Called From Button
        intentFilter.addAction(getString(R.string.savedActionHideAdvance));
        intentFilter.addAction(getString(R.string.checkboxActionAdvance));
        intentFilter.addAction(getString(R.string.splitActionAdvance));
        BroadcastReceiver counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.counterActionAdvance))) {

                    advanceAppSelectionListBinding.appSelectedCounterView.setText(String.valueOf(functionsClass.countLineInnerFile(PublicVariable.categoryName)));

                } else if (intent.getAction().equals(context.getString(R.string.savedActionAdvance))) {

                    if (getFileStreamPath(PublicVariable.categoryName).exists() && functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                        navDrawerItemsSaved.clear();
                        String[] savedLine = functionsClass.readFileLine(PublicVariable.categoryName);
                        for (String aSavedLine : savedLine) {
                            navDrawerItemsSaved.add(new AdapterItems(
                                    functionsClass.appName(aSavedLine),
                                    aSavedLine,
                                    functionsClass.customIconsEnable() ?
                                            loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClass.shapedAppIcon(aSavedLine))
                                            :
                                            functionsClass.shapedAppIcon(aSavedLine)));
                        }
                        advanceSavedListAdapter = new AppSavedListAdapter(AppSelectionList.this, getApplicationContext(), navDrawerItemsSaved, 1);
                        listPopupWindow = new ListPopupWindow(AppSelectionList.this);
                        listPopupWindow.setAdapter(advanceSavedListAdapter);
                        listPopupWindow.setAnchorView(advanceAppSelectionListBinding.popupAnchorView);
                        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setModal(true);
                        listPopupWindow.setBackgroundDrawable(null);
                        try {
                            listPopupWindow.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (functionsClass.returnAPI() < 23) {
                                if (listPopupWindow.isShowing()) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            listPopupWindow.dismiss();
                                        }
                                    }, 2000);
                                }
                            }
                        }
                        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                sendBroadcast(new Intent(getString(R.string.visibilityActionAdvance)));
                            }
                        });
                    }
                } else if (intent.getAction().equals(getString(R.string.savedActionHideAdvance))) {
                    try {
                        listPopupWindow.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if ((intent.getAction().equals(getString(R.string.checkboxActionAdvance)))) {
                    resetAdapter = true;
                    loadDataOff();
                    listPopupWindow.dismiss();
                    sendBroadcast(new Intent(getString(R.string.visibilityActionAdvance)));
                } else if (intent.getAction().equals(getString(R.string.splitActionAdvance))) {
                    listPopupWindow.dismiss();


                    if (getFileStreamPath(PublicVariable.categoryName + ".SplitOne").exists()) {
                        advanceAppSelectionListBinding.firstSplitIcon.setImageDrawable(functionsClass.customIconsEnable() ?
                                loadCustomIcons.getDrawableIconForPackage(functionsClass.readFile(PublicVariable.categoryName + ".SplitOne"), functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName + ".SplitOne")))
                                :
                                functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName + ".SplitOne")));
                    }
                    if (getFileStreamPath(PublicVariable.categoryName + ".SplitTwo").exists()) {
                        advanceAppSelectionListBinding.secondSplitIcon.setImageDrawable(functionsClass.customIconsEnable() ?
                                loadCustomIcons.getDrawableIconForPackage(functionsClass.readFile(PublicVariable.categoryName + ".SplitTwo"), functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName + ".SplitOne")))
                                :
                                functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName + ".SplitTwo")));
                    }
                }
            }
        };
        try {
            registerReceiver(counterReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadDataOff();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (functionsClass.returnAPI() < 24) {
            advanceAppSelectionListBinding.firstSplitIcon.setVisibility(View.INVISIBLE);
            advanceAppSelectionListBinding.secondSplitIcon.setVisibility(View.INVISIBLE);
            advanceAppSelectionListBinding.splitHint.setVisibility(View.INVISIBLE);

            int padTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            advanceAppSelectionListBinding.recyclerListView.setPaddingRelative(
                    advanceAppSelectionListBinding.recyclerListView.getPaddingStart(),
                    padTop,
                    advanceAppSelectionListBinding.recyclerListView.getPaddingEnd(),
                    advanceAppSelectionListBinding.recyclerListView.getPaddingBottom());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            advanceAppSelectionListBinding.nestedScrollView.setLayoutParams(params);
        } else {
            advanceAppSelectionListBinding.splitHint.setTextColor(PublicVariable.primaryColorOpposite);
            if (getFileStreamPath(PublicVariable.categoryName + ".SplitOne").exists()) {
                advanceAppSelectionListBinding.firstSplitIcon.setImageDrawable(functionsClass.customIconsEnable() ?
                        loadCustomIcons.getDrawableIconForPackage(functionsClass.readFile(PublicVariable.categoryName + ".SplitOne"), functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName + ".SplitOne")))
                        :
                        functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName + ".SplitOne")));
            } else {
                Drawable addOne = getDrawable(R.drawable.add_quick_app);
                addOne.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175));
                advanceAppSelectionListBinding.firstSplitIcon.setImageDrawable(addOne);
            }
            if (getFileStreamPath(PublicVariable.categoryName + ".SplitTwo").exists()) {
                advanceAppSelectionListBinding.secondSplitIcon.setImageDrawable(functionsClass.customIconsEnable() ?
                        loadCustomIcons.getDrawableIconForPackage(functionsClass.readFile(PublicVariable.categoryName + ".SplitTwo"), functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName + ".SplitOne")))
                        :
                        functionsClass.shapedAppIcon(functionsClass.readFile(PublicVariable.categoryName + ".SplitTwo")));

            } else {
                Drawable addTwo = getDrawable(R.drawable.add_quick_app);
                addTwo.setTint(functionsClass.setColorAlpha(PublicVariable.primaryColorOpposite, 175));
                advanceAppSelectionListBinding.secondSplitIcon.setImageDrawable(addTwo);
            }

            advanceAppSelectionListBinding.firstSplitIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getFileStreamPath(PublicVariable.categoryName).exists() && functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                        navDrawerItemsSaved.clear();
                        String[] savedLine = functionsClass.readFileLine(PublicVariable.categoryName);
                        for (String aSavedLine : savedLine) {
                            navDrawerItemsSaved.add(new AdapterItems(
                                    functionsClass.appName(aSavedLine),
                                    aSavedLine,
                                    functionsClass.customIconsEnable() ?
                                            loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClass.shapedAppIcon(aSavedLine))
                                            :
                                            functionsClass.shapedAppIcon(aSavedLine)));
                        }
                        advanceSavedListAdapter = new AppSavedListAdapter(AppSelectionList.this, getApplicationContext(), navDrawerItemsSaved, 1);
                        listPopupWindow = new ListPopupWindow(AppSelectionList.this);
                        listPopupWindow.setAdapter(advanceSavedListAdapter);
                        listPopupWindow.setAnchorView(advanceAppSelectionListBinding.popupAnchorView);
                        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setModal(true);
                        listPopupWindow.setBackgroundDrawable(null);
                        try {
                            listPopupWindow.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            sendBroadcast(new Intent(getString(R.string.setDismissAdvance)));
                        }
                        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                sendBroadcast(new Intent(getString(R.string.visibilityActionAdvance)));
                            }
                        });
                    }
                }
            });
            advanceAppSelectionListBinding.secondSplitIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getFileStreamPath(PublicVariable.categoryName).exists() && functionsClass.countLineInnerFile(PublicVariable.categoryName) > 0) {
                        navDrawerItemsSaved.clear();
                        String[] savedLine = functionsClass.readFileLine(PublicVariable.categoryName);
                        for (String aSavedLine : savedLine) {
                            navDrawerItemsSaved.add(new AdapterItems(
                                    functionsClass.appName(aSavedLine),
                                    aSavedLine,
                                    functionsClass.customIconsEnable() ?
                                            loadCustomIcons.getDrawableIconForPackage(aSavedLine, functionsClass.shapedAppIcon(aSavedLine))
                                            :
                                            functionsClass.shapedAppIcon(aSavedLine)));
                        }
                        advanceSavedListAdapter = new AppSavedListAdapter(AppSelectionList.this, getApplicationContext(), navDrawerItemsSaved, 2);
                        listPopupWindow = new ListPopupWindow(AppSelectionList.this);
                        listPopupWindow.setAdapter(advanceSavedListAdapter);
                        listPopupWindow.setAnchorView(advanceAppSelectionListBinding.popupAnchorView);
                        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
                        listPopupWindow.setModal(true);
                        listPopupWindow.setBackgroundDrawable(null);
                        try {
                            listPopupWindow.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            sendBroadcast(new Intent(getString(R.string.setDismissAdvance)));
                        }
                        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                sendBroadcast(new Intent(getString(R.string.visibilityActionAdvance)));
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        functionsClass.navigateToClass(FoldersConfigurations.class, AppSelectionList.this);
    }

    @Override
    public void onClick(View view) {

    }

    public void loadDataOff() {
        if (functionsClass.customIconsEnable()) {
            loadCustomIcons = new LoadCustomIcons(getApplicationContext(), functionsClass.customIconPackageName());
        }

        LoadApplicationsOffLimited loadApplicationsOffLimited = new LoadApplicationsOffLimited();
        loadApplicationsOffLimited.execute();
    }

    private class LoadApplicationsOffLimited extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            advanceAppSelectionListBinding.indexView.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                applicationInfoList = getApplicationContext().getPackageManager().getInstalledApplications(0);
                Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(getPackageManager()));

                for (int appInfo = 0; appInfo < applicationInfoList.size(); appInfo++) {
                    if (getApplicationContext().getPackageManager().getLaunchIntentForPackage(applicationInfoList.get(appInfo).packageName) != null) {
                        try {
                            PackageName = applicationInfoList.get(appInfo).packageName;
                            AppName = functionsClass.appName(PackageName);
//                            AppIcon = functionsClass.shapedAppIcon(PackageName);
                            AppIcon = functionsClass.customIconsEnable() ? loadCustomIcons.getDrawableIconForPackage(PackageName, functionsClass.shapedAppIcon(PackageName)) : functionsClass.shapedAppIcon(PackageName);

                            adapterItems.add(new AdapterItems(AppName, PackageName, AppIcon));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                appSelectionListAdapter = new AppSelectionListAdapter(AppSelectionList.this, getApplicationContext(), adapterItems);
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
            advanceAppSelectionListBinding.recyclerListView.setAdapter(appSelectionListAdapter);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    advanceAppSelectionListBinding.folderNameView.setVisibility(View.VISIBLE);

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    advanceAppSelectionListBinding.loadingSplash.setVisibility(View.INVISIBLE);
                    if (!resetAdapter) {
                        advanceAppSelectionListBinding.loadingSplash.startAnimation(anim);
                    }
                    sendBroadcast(new Intent(getString(R.string.visibilityActionAdvance)));

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                    advanceAppSelectionListBinding.appSelectedCounterView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            advanceAppSelectionListBinding.appSelectedCounterView.setText(String.valueOf(functionsClass.countLineInnerFile(PublicVariable.categoryName)));
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            advanceAppSelectionListBinding.appSelectedCounterView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    resetAdapter = false;
                }
            }, 100);

            LoadApplicationsIndex loadApplicationsIndex = new LoadApplicationsIndex();
            loadApplicationsIndex.execute();
        }
    }

    private class LoadApplicationsIndex extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            advanceAppSelectionListBinding.indexView.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int itemCount = 0; itemCount < adapterItems.size(); itemCount++) {
                try {
                    String index = (adapterItems.get(itemCount).getAppName()).substring(0, 1).toUpperCase();
                    if (mapIndexFirstItem.get(index) == null) {
                        mapIndexFirstItem.put(index, itemCount);
                    }

                    mapIndexLastItem.put(index, itemCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            TextView textView = (TextView) getLayoutInflater()
                    .inflate(R.layout.side_index_item, null);
            List<String> indexList = new ArrayList<String>(mapIndexFirstItem.keySet());
            for (String index : indexList) {
                textView = (TextView) getLayoutInflater()
                        .inflate(R.layout.side_index_item, null);
                textView.setText(index.toUpperCase());
                textView.setTextColor(PublicVariable.colorLightDarkOpposite);
                advanceAppSelectionListBinding.indexView.addView(textView);
            }

            TextView finalTextView = textView;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int upperRange = (int) (advanceAppSelectionListBinding.indexView.getY() - finalTextView.getHeight());
                    for (int i = 0; i < advanceAppSelectionListBinding.indexView.getChildCount(); i++) {
                        String indexText = ((TextView) advanceAppSelectionListBinding.indexView.getChildAt(i)).getText().toString();
                        int indexRange = (int) (advanceAppSelectionListBinding.indexView.getChildAt(i).getY() + advanceAppSelectionListBinding.indexView.getY() + finalTextView.getHeight());
                        for (int jRange = upperRange; jRange <= (indexRange); jRange++) {
                            mapRangeIndex.put(jRange, indexText);
                        }

                        upperRange = indexRange;
                    }

                    setupFastScrollingIndexing();
                }
            }, 700);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupFastScrollingIndexing() {
        Drawable popupIndexBackground = getDrawable(R.drawable.ic_launcher_balloon).mutate();
        popupIndexBackground.setTint(PublicVariable.primaryColorOpposite);
        advanceAppSelectionListBinding.popupIndex.setBackground(popupIndexBackground);

        advanceAppSelectionListBinding.nestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        advanceAppSelectionListBinding.nestedIndexScrollView.setVisibility(View.VISIBLE);

        float popupIndexOffsetY = PublicVariable.statusBarHeight + PublicVariable.actionBarHeight + (functionsClass.UsageStatsEnabled() ? functionsClass.DpToInteger(7) : functionsClass.DpToInteger(7));
        advanceAppSelectionListBinding.nestedIndexScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (functionsClass.litePreferencesEnabled()) {

                        } else {
                            String indexText = mapRangeIndex.get(((int) motionEvent.getY()));

                            if (indexText != null) {
                                advanceAppSelectionListBinding.popupIndex.setY(motionEvent.getRawY() - popupIndexOffsetY);
                                advanceAppSelectionListBinding.popupIndex.setText(indexText);
                                advanceAppSelectionListBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                advanceAppSelectionListBinding.popupIndex.setVisibility(View.VISIBLE);
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (functionsClass.litePreferencesEnabled()) {

                        } else {
                            String indexText = mapRangeIndex.get(((int) motionEvent.getY()));

                            if (indexText != null) {
                                if (!advanceAppSelectionListBinding.popupIndex.isShown()) {
                                    advanceAppSelectionListBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                                    advanceAppSelectionListBinding.popupIndex.setVisibility(View.VISIBLE);
                                }
                                advanceAppSelectionListBinding.popupIndex.setY(motionEvent.getRawY() - popupIndexOffsetY);
                                advanceAppSelectionListBinding.popupIndex.setText(indexText);

                                try {
                                    advanceAppSelectionListBinding.nestedScrollView.smoothScrollTo(
                                            0,
                                            ((int) advanceAppSelectionListBinding.recyclerListView.getChildAt(mapIndexFirstItem.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY())
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (advanceAppSelectionListBinding.popupIndex.isShown()) {
                                    advanceAppSelectionListBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                    advanceAppSelectionListBinding.popupIndex.setVisibility(View.INVISIBLE);
                                }
                            }
                        }

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (functionsClass.litePreferencesEnabled()) {
                            try {
                                advanceAppSelectionListBinding.nestedScrollView.smoothScrollTo(
                                        0,
                                        ((int) advanceAppSelectionListBinding.recyclerListView.getChildAt(mapIndexFirstItem.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY())
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (advanceAppSelectionListBinding.popupIndex.isShown()) {
                                try {
                                    advanceAppSelectionListBinding.nestedScrollView.smoothScrollTo(
                                            0,
                                            ((int) advanceAppSelectionListBinding.recyclerListView.getChildAt(mapIndexFirstItem.get(mapRangeIndex.get(((int) motionEvent.getY())))).getY())
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                advanceAppSelectionListBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                                advanceAppSelectionListBinding.popupIndex.setVisibility(View.INVISIBLE);
                            }
                        }

                        break;
                    }
                }
                return true;
            }
        });
    }
}
