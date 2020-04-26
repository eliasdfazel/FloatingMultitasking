/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/26/20 8:51 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Shortcuts;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import net.geekstools.floatshort.PRO.Automation.RecoveryShortcuts;
import net.geekstools.floatshort.PRO.Configurations;
import net.geekstools.floatshort.PRO.Folders.FloatingServices.FloatingFolders;
import net.geekstools.floatshort.PRO.Preferences.PreferencesUI;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter.ApplicationsViewAdapter;
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsApplications;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.databinding.ApplicationsViewWatchBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationsViewWatch extends WearableActivity {

    FunctionsClass functionsClass;

    List<ApplicationInfo> applicationsInformationList;
    ArrayList<AdapterItemsApplications> adapterItemsApplications;

    RecyclerView.Adapter applicationsViewAdapter;
    LinearLayoutManager recyclerViewLayoutManager;

    int loadViewPosition = 0, limitedCountLine;

    ApplicationsViewWatchBinding applicationsViewWatchBinding;

    @Override
    public void onStart() {
        super.onStart();


        applicationsViewWatchBinding.settingGUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PreferencesUI.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        applicationsViewWatchBinding.floatingShortcutsRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), RecoveryShortcuts.class));
            }
        });
        applicationsViewWatchBinding.floatingShortcutsRecovery.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                functionsClass.doVibrate(70);

                if (!FloatingFolders.running) {
                    String[] categoryContent = functionsClass.readFileLine(".uFile");

                    if (categoryContent.length > 0) {
                        functionsClass.runUnlimitedCategoryService(getString(R.string.group), categoryContent);
                    }
                }


                return true;
            }
        });

        applicationsViewWatchBinding.applicationsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                loadViewPosition = recyclerViewLayoutManager.findFirstVisibleItemPosition();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (functionsClass.checkThemeLightDark()) {
            applicationsViewWatchBinding.MainView.setBackgroundColor(getColor(R.color.light_trans));
        } else if (!functionsClass.checkThemeLightDark()) {
            applicationsViewWatchBinding.MainView.setBackgroundColor(getColor(R.color.trans_black));
        }

        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default);
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(ApplicationsViewWatch.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            firebaseRemoteConfig.activate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {

                                    if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(getPackageName())) {
                                        Toast.makeText(getApplicationContext(),
                                                getString(R.string.updateAvailable),
                                                Toast.LENGTH_LONG).show();

                                        LayerDrawable layerDrawableNewUpdate = (LayerDrawable) getDrawable(R.drawable.ic_update);
                                        BitmapDrawable gradientDrawableNewUpdate = (BitmapDrawable) layerDrawableNewUpdate.findDrawableByLayerId(R.id.ic_launcher_back_layer);
                                        gradientDrawableNewUpdate.setTint(PublicVariable.primaryColor);
                                        applicationsViewWatchBinding.newUpdate.setImageDrawable(layerDrawableNewUpdate);
                                        applicationsViewWatchBinding.newUpdate.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                });
    }


    @Override
    public void onPause() {
        super.onPause();
        functionsClass.savePreference("LoadView", "LoadViewPosition", recyclerViewLayoutManager.findFirstVisibleItemPosition());
        this.finish();
    }

    private class LoadApplicationsOffInit extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            applicationsViewWatchBinding.loadingSplash.setBackgroundColor(getColor(R.color.light));

            applicationsViewWatchBinding.loadingProgressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                applicationsInformationList = getApplicationContext().getPackageManager().getInstalledApplications(0);
                Collections.sort(applicationsInformationList, new ApplicationInfo.DisplayNameComparator(getPackageManager()));
                limitedCountLine = ((int) applicationsInformationList.size() / 3);

                for (int appInfo = 0; appInfo < limitedCountLine; appInfo++) {
                    if (getApplicationContext().getPackageManager().getLaunchIntentForPackage(applicationsInformationList.get(appInfo).packageName) != null) {
                        try {
                            String packageName = applicationsInformationList.get(appInfo).packageName;
                            String appName = functionsClass.appName(packageName);
                            Drawable appIcon = functionsClass.shapedAppIcon(packageName);

                            adapterItemsApplications.add(new AdapterItemsApplications(appName,
                                    packageName,
                                    appIcon,
                                    functionsClass.extractDominantColor(appIcon)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                applicationsViewAdapter = new ApplicationsViewAdapter(getApplicationContext(), adapterItemsApplications);
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), Configurations.class));
                    }
                }, 113);
                ApplicationsViewWatch.this.finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            applicationsViewWatchBinding.applicationsListView.setAdapter(applicationsViewAdapter);
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
            applicationsViewWatchBinding.loadingSplash.setVisibility(View.INVISIBLE);
            applicationsViewWatchBinding.loadingSplash.startAnimation(fadeOutAnimation);
            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            LoadApplicationsOff loadApplicationsOff = new LoadApplicationsOff();
            loadApplicationsOff.execute();
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
                for (int appInfo = limitedCountLine; appInfo < applicationsInformationList.size(); appInfo++) {
                    if (getApplicationContext().getPackageManager().getLaunchIntentForPackage(applicationsInformationList.get(appInfo).packageName) != null) {
                        try {
                            String packageName = applicationsInformationList.get(appInfo).packageName;
                            String appName = functionsClass.appName(packageName);
                            Drawable appIcon = functionsClass.shapedAppIcon(packageName);

                            adapterItemsApplications.add(new AdapterItemsApplications(appName,
                                    packageName,
                                    appIcon,
                                    functionsClass.extractDominantColor(appIcon)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                applicationsViewAdapter = new ApplicationsViewAdapter(getApplicationContext(), adapterItemsApplications);

                functionsClass.savePreference("InstalledApps", "countApps", adapterItemsApplications.size());
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);

                ApplicationsViewWatch.this.finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            applicationsViewWatchBinding.applicationsListView.setAdapter(applicationsViewAdapter);

            if (loadViewPosition == 0) {
                recyclerViewLayoutManager.scrollToPosition(getSharedPreferences("LoadView", Context.MODE_PRIVATE).getInt("LoadViewPosition", 0));
            } else {
                recyclerViewLayoutManager.scrollToPosition(loadViewPosition);
            }
        }
    }
}
