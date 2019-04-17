package net.geekstools.floatshort.PRO.Util.IAP.skulist;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingProvider;
import net.geekstools.floatshort.PRO.Util.IAP.skulist.row.SkuRowData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AcquireFragment extends DialogFragment {

    FunctionsClass functionsClass;

    private RecyclerView recyclerView;
    private SkusAdapter skusAdapter;
    private View loadingView;
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
        loadingView = root.findViewById(R.id.screen_wait);

        root.findViewById(R.id.backgroundFull).setBackgroundColor(PublicVariable.themeLightDark ? getContext().getColor(R.color.light) : getContext().getColor(R.color.dark));

        setWaitScreen(true);
        onManagerReady((BillingProvider) getActivity());

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

    private void setWaitScreen(boolean set) {
        recyclerView.setVisibility(set ? View.GONE : View.VISIBLE);
        loadingView.setVisibility(set ? View.VISIBLE : View.GONE);
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
                                setWaitScreen(false);
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

        loadingView.setVisibility(View.GONE);
    }
}

