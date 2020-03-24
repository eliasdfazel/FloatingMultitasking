/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/24/20 1:15 PM
 * Last modified 3/24/20 10:35 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.IAP.skulist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.IAP.billing.BillingManager;
import net.geekstools.floatshort.PRO.Utils.IAP.billing.BillingProvider;
import net.geekstools.floatshort.PRO.Utils.IAP.skulist.row.RowViewHolder;
import net.geekstools.floatshort.PRO.Utils.IAP.skulist.row.SkuRowData;

import java.util.List;

public class SkusAdapter extends RecyclerView.Adapter<RowViewHolder> implements RowViewHolder.OnButtonClickListener {

    Activity activity;

    List<SkuRowData> rowDataList;
    BillingProvider billingProvider;

    public SkusAdapter(BillingProvider billingProvider, Activity activity) {
        this.billingProvider = billingProvider;
        this.activity = activity;
    }

    public void updateData(List<SkuRowData> skuRowData) {
        rowDataList = skuRowData;

        notifyDataSetChanged();
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.iap_sku_details_item, parent, false);
        return new RowViewHolder(inflate, this);
    }

    @Override
    public void onBindViewHolder(RowViewHolder rowViewHolder, int position) {
        SkuRowData skuRowData = getData(position);
        if (skuRowData != null) {
            rowViewHolder.purchaseItemName.setText(skuRowData.getTitle());
            rowViewHolder.purchaseItemDescription.setText(skuRowData.getDescription());
            rowViewHolder.purchaseItemButton.setEnabled(true);

            switch (skuRowData.getSku()) {
                case BillingManager.iapFloatingWidgets: {
                    rowViewHolder.purchaseItemIcon.setImageResource(R.drawable.ic_floating_widgets);
                    rowViewHolder.purchaseItemButton.setText(activity.getString(R.string.purchase));
                    rowViewHolder.purchaseItemPrice.setText(skuRowData.getPrice());

                    break;
                }
                case BillingManager.iapSearchEngines: {
                    rowViewHolder.purchaseItemIcon.setImageResource(R.drawable.search_icon_iap);
                    rowViewHolder.purchaseItemButton.setText(activity.getString(R.string.purchase));
                    rowViewHolder.purchaseItemPrice.setText(skuRowData.getPrice());

                    break;
                }
                case BillingManager.iapSecurityServices: {
                    rowViewHolder.purchaseItemIcon.setImageResource(R.drawable.draw_security);
                    rowViewHolder.purchaseItemPrice.setText(skuRowData.getPrice());
                    rowViewHolder.purchaseItemButton.setText(activity.getString(R.string.purchase));

                    break;
                }
                case BillingManager.iapDonation: {
                    rowViewHolder.purchaseItemIcon.setImageResource(R.drawable.logo);
                    rowViewHolder.purchaseItemButton.setText(activity.getString(R.string.donate));

                    rowViewHolder.purchaseItemDescription.append("\n");
                    rowViewHolder.purchaseItemDescription.append(activity.getString(R.string.thanks));

                    rowViewHolder.purchaseItemPrice.setText(skuRowData.getPrice());

                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return rowDataList == null ? 0 : rowDataList.size();
    }

    @Override
    public void onButtonClicked(int position) {
        SkuRowData skuRowData = getData(position);
        billingProvider.getBillingManager().startPurchaseFlow(skuRowData.getSkuDetails());

    }

    public SkuRowData getData(int position) {
        return rowDataList == null ? null : rowDataList.get(position);
    }
}

