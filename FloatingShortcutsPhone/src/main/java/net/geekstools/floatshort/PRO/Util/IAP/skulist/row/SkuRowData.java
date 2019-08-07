package net.geekstools.floatshort.PRO.Util.IAP.skulist.row;

import com.android.billingclient.api.SkuDetails;

public class SkuRowData {
    SkuDetails skuDetails;
    final String sku, title, price, description, billingType;

    public SkuRowData(SkuDetails skuDetails, String sku, String title, String price, String description, String type) {
        this.skuDetails = skuDetails;
        this.sku = sku;
        this.title = title;
        this.price = price;
        this.description = description;
        this.billingType = type;
    }

    public SkuDetails getSkuDetails() {
        return this.skuDetails;
    }

    public String getSku() {
        return sku;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getBillingType() {
        return billingType;
    }
}
