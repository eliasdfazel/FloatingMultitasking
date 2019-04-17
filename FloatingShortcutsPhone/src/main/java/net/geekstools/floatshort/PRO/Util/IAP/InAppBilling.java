package net.geekstools.floatshort.PRO.Util.IAP;

import android.os.Bundle;
import android.os.Handler;

import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingManager;
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingProvider;
import net.geekstools.floatshort.PRO.Util.IAP.skulist.AcquireFragment;

import androidx.fragment.app.FragmentActivity;

public class InAppBilling extends FragmentActivity implements BillingProvider {

    private static final String TAG = "InAppBilling";
    private static final String DIALOG_TAG = "InAppBillingDialogue";

    FunctionsClass functionsClass;

    private BillingManager billingManager;
    private AcquireFragment acquireFragment;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        functionsClass = new FunctionsClass(getApplicationContext(), InAppBilling.this);

        if (savedInstanceState != null) {
            acquireFragment = (AcquireFragment) getFragmentManager().findFragmentByTag(DIALOG_TAG);
        }

        billingManager = new BillingManager(InAppBilling.this, getIntent().hasExtra("UserEmailAddress") ? getIntent().getStringExtra("UserEmailAddress") : null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onPurchaseButtonClicked();
            }
        }, 500);

        showRefreshedUi();
    }

    @Override
    public BillingManager getBillingManager() {
        return billingManager;
    }

    public void onPurchaseButtonClicked() {
        if (acquireFragment == null) {
            acquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            acquireFragment.show(getFragmentManager(), DIALOG_TAG);
        }
    }

    public void showRefreshedUi() {
        if (isAcquireFragmentShown()) {
            acquireFragment.refreshUI();
        }
    }

    public boolean isAcquireFragmentShown() {
        return acquireFragment != null && acquireFragment.isVisible();
    }
}
