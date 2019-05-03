package net.geekstools.floatshort.PRO.Util.IAP;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.geekstools.floatshort.PRO.BuildConfig;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingProvider;
import net.geekstools.floatshort.PRO.Util.IAP.skulist.SkusAdapter;
import net.geekstools.floatshort.PRO.Util.IAP.skulist.row.SkuRowData;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class AcquireFragment extends DialogFragment implements View.OnClickListener {

    FunctionsClass functionsClass;

    RecyclerView recyclerView;
    ProgressBar progressBar;

    HorizontalScrollView floatingWidgetDemo;
    LinearLayout floatingWidgetDemoList;
    TextView floatingWidgetDemoDescription;

    SkusAdapter skusAdapter;

    BillingProvider billingProvider;

    TreeMap<Integer, Drawable> mapIndexDrawable = new TreeMap<Integer, Drawable>();
    TreeMap<Integer, Uri> mapIndexURI = new TreeMap<Integer, Uri>();

    int screenshotsNumber = 6, glideLoadCounter = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        functionsClass = new FunctionsClass(getContext(), getActivity());
        if (PublicVariable.themeLightDark) {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.AppThemeLight);
        } else {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.AppThemeDark);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.iap_fragment, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.list);
        progressBar = (ProgressBar) root.findViewById(R.id.progress_circular);
        floatingWidgetDemo = (HorizontalScrollView) root.findViewById(R.id.floatingWidgetDemo);
        floatingWidgetDemoList = (LinearLayout) root.findViewById(R.id.floatingWidgetDemoList);
        floatingWidgetDemoDescription = (TextView) root.findViewById(R.id.floatingWidgetDemoDescription);

        root.findViewById(R.id.backgroundFull).setBackgroundColor(PublicVariable.themeLightDark ? getContext().getColor(R.color.light) : getContext().getColor(R.color.dark));

        onManagerReady((BillingProvider) getActivity());

        floatingWidgetDemoDescription.setText(Html.fromHtml(getString(R.string.floatingWidgetsDemoDescriptions)));
        floatingWidgetDemoDescription.setTextColor(PublicVariable.themeLightDark ? getContext().getColor(R.color.dark) : getContext().getColor(R.color.light));


        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default);
        firebaseRemoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                floatingWidgetDemoDescription.setText(Html.fromHtml(firebaseRemoteConfig.getString("floating_widgets_description")));
                screenshotsNumber = (int) firebaseRemoteConfig.getLong("floating_widgets_demo_screenshots");

                for (int i = 1; i <= screenshotsNumber; i++) {
                    String sceenshotFileName = "FloatingWidgetsDemo" + i + ".png";
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference firebaseStorageReference = firebaseStorage.getReference();
                    StorageReference storageReference = firebaseStorageReference
                            //gs://floating-shortcuts-pro.appspot.com/Assets/Images/Screenshots/FloatingWidgets/IAP.Demo/FloatingWidgetsDemo1.png
                            .child("Assets/Images/Screenshots/FloatingWidgets/IAP.Demo/" + sceenshotFileName);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri screenshotURI) {
                            Glide.with(getContext())
                                    .load(screenshotURI)
                                    .addListener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            glideLoadCounter++;

                                            String beforeToken = screenshotURI.toString().split("\\?alt=media&token=")[0];
                                            int drawableIndex = Integer.parseInt(String.valueOf(beforeToken.charAt(beforeToken.length() - 5)));

                                            mapIndexDrawable.put(drawableIndex, resource);
                                            mapIndexURI.put(drawableIndex, screenshotURI);

                                            if (screenshotsNumber == glideLoadCounter) {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getContext().sendBroadcast(new Intent("LOAD_SCREENSHOTS"));
                                                    }
                                                }, 113);
                                            }

                                            return false;
                                        }
                                    })
                                    .submit();
                        }
                    });
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LOAD_SCREENSHOTS");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("LOAD_SCREENSHOTS")) {
                    for (int i = 1; i <= screenshotsNumber; i++) {
                        FunctionsClass.println(">>> " + mapIndexURI.get(i) + " <<<");

                        RelativeLayout demoLayout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.floating_widgets_demo_item, null);
                        ImageView floatingWidgetsDemoItem = (ImageView) demoLayout.findViewById(R.id.floatingWidgetsDemoItem);

                        floatingWidgetsDemoItem.setImageDrawable(mapIndexDrawable.get(i));
                        floatingWidgetsDemoItem.setOnClickListener(AcquireFragment.this);
                        floatingWidgetsDemoItem.setTag(mapIndexURI.get(i));
                        floatingWidgetDemoList.addView(demoLayout);
                    }
                }
            }
        };
        getContext().registerReceiver(broadcastReceiver, intentFilter);

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, android.view.KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    getActivity().finish();
                }
                return true;
            }
        });

        return root;
    }

    @Override
    public void onClick(View view) {
        if (view instanceof ImageView) {
            String screenshotURI = view.getTag().toString();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(screenshotURI));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }

    public void refreshUI() {
        if (skusAdapter != null) {
            skusAdapter.notifyDataSetChanged();
        }
    }

    public void onManagerReady(BillingProvider billingProvider) {
        this.billingProvider = billingProvider;
        if (recyclerView != null) {
            skusAdapter = new SkusAdapter(this.billingProvider, getActivity());
            if (recyclerView.getAdapter() == null) {
                recyclerView.setAdapter(skusAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
            handleManagerAndUiReady();
        }
    }

    private void handleManagerAndUiReady() {
        List<String> inAppSkus = billingProvider.getBillingManager().getSkus(BillingClient.SkuType.INAPP);
        billingProvider.getBillingManager().querySkuDetailsAsync(BillingClient.SkuType.INAPP,
                inAppSkus,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                            List<SkuRowData> skuRowDataList = new ArrayList<>();
                            for (SkuDetails skuDetails : skuDetailsList) {
                                if (skuDetails.getSku().equals("floating.widgets") && functionsClass.floatingWidgetsPurchased()) {

                                    continue;
                                }

                                skuRowDataList.add(new SkuRowData(
                                        skuDetails,
                                        skuDetails.getSku(),
                                        skuDetails.getTitle(),
                                        skuDetails.getPrice(),
                                        skuDetails.getDescription(),
                                        skuDetails.getType())
                                );
                            }
                            if (skuRowDataList.size() == 0) {
                                displayError();
                            } else {
                                skusAdapter.updateData(skuRowDataList);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
    }

    private void displayError() {
        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
    }
}

