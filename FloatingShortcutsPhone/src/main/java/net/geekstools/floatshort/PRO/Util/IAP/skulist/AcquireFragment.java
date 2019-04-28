package net.geekstools.floatshort.PRO.Util.IAP.skulist;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingProvider;
import net.geekstools.floatshort.PRO.Util.IAP.skulist.row.SkuRowData;

import java.util.ArrayList;
import java.util.List;

public class AcquireFragment extends DialogFragment {

    FunctionsClass functionsClass;

    private RecyclerView recyclerView;
    ProgressBar progressBar;

    HorizontalScrollView floatingWidgetDemo;
    LinearLayout floatingWidgetDemoList;
    TextView floatingWidgetDemoDescription;

    private SkusAdapter skusAdapter;

    private BillingProvider billingProvider;

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

        for (int i = 0; i < 3; i++) {
            RelativeLayout demoLayout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.demo_item, null);
            ImageView demoItem = (ImageView) demoLayout.findViewById(R.id.demoItem);

            Glide.with(getContext())
                    .load(Uri.parse("https://scontent-lga3-1.xx.fbcdn.net/v/t1.0-9/57154177_350550235575129_842545654306701312_n.jpg?_nc_cat=107&_nc_ht=scontent-lga3-1.xx&oh=68ce76e5f263114ef8d09ecf2088278a&oe=5D365890"))
//                    .into(demoItem)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            demoItem.setImageDrawable(resource);
                            floatingWidgetDemoList.addView(demoLayout);
                            return false;
                        }
                    })
                    .submit();
        }

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
                                        skuDetails.getSku(),
                                        skuDetails.getTitle().replace("(Floating Shortcuts ᴾᴿᴼ)", ""),
                                        skuDetails.getPrice(),
                                        skuDetails.getDescription(),
                                        skuDetails.getType())
                                );
                            }
                            if (skuRowDataList.size() == 0) {
                                displayAnErrorIfNeeded();
                            } else {
                                skusAdapter.updateData(skuRowDataList);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
        displayAnErrorIfNeeded();
    }

    private void displayAnErrorIfNeeded() {
        if (getActivity() == null || getActivity().isFinishing()) {

            return;
        }
    }
}

