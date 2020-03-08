/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/8/20 7:23 AM
 * Last modified 3/8/20 7:08 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.IAP.billing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Util.IAP.Util.PurchasesCheckpoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BillingManager implements PurchasesUpdatedListener {

    private static final String TAG = "BillingManager";

    FunctionsClass functionsClass;

    private BillingClient billingClient;
    private AppCompatActivity appCompatActivity;

    String UserEmailAddress = null;

    private static final HashMap<String, List<String>> SKUS;

    static {
        SKUS = new HashMap<>();
        SKUS.put(BillingClient.SkuType.INAPP, Arrays.asList("donation", "floating.widgets"));
        SKUS.put(BillingClient.SkuType.SUBS, Arrays.asList("security.services", "search.engines"));
    }

    public static final String iapDonation = "donation";
    public static final String iapFloatingWidgets = "floating.widgets";
    public static final String iapSecurityServices = "security.services";
    public static final String iapSearchEngines = "search.engines";

    public List<String> getSkus(@BillingClient.SkuType String type) {
        return SKUS.get(type);
    }

    public BillingManager(AppCompatActivity appCompatActivity, String UserEmailAddress) {
        this.appCompatActivity = appCompatActivity;
        this.UserEmailAddress = UserEmailAddress;

        functionsClass = new FunctionsClass(appCompatActivity.getApplicationContext(), appCompatActivity);

        billingClient = new PurchasesCheckpoint(appCompatActivity).trigger();
    }

    public BillingResult startPurchaseFlow(SkuDetails skuDetails) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .setAccountId(UserEmailAddress)
                .build();

        return billingClient.launchBillingFlow(appCompatActivity, billingFlowParams);
    }

    public void querySkuDetailsAsync(@BillingClient.SkuType final String itemType, final List<String> skuList, final SkuDetailsResponseListener skuDetailsResponseListener) {
        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(itemType).build();

        billingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {

            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                skuDetailsResponseListener.onSkuDetailsResponse(billingResult, skuDetailsList);
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        //ResponseCode 7 = Item Owned
        FunctionsClassDebug.Companion.PrintDebug("*** Purchases Updated Response: " + billingResult.getResponseCode() + " ***");

        new PurchasesCheckpoint(appCompatActivity).trigger();
    }
}
