/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/23/20 9:33 AM
 * Last modified 2/23/20 9:09 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Util.IAP;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingManager;
import net.geekstools.floatshort.PRO.Util.IAP.billing.BillingProvider;

public class InAppBilling extends FragmentActivity implements BillingProvider {

    private static final String TAG = "InAppBilling";
    private static final String DIALOG_TAG = "InAppBillingDialogue";

    FunctionsClass functionsClass;

    private BillingManager billingManager;
    private AcquireFragment acquireFragment;

    public static String ItemIAB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        functionsClass = new FunctionsClass(getApplicationContext(), InAppBilling.this);

        if (InAppBilling.ItemIAB == null) {
            if (functionsClass.floatingWidgetsPurchased()) {
                InAppBilling.ItemIAB = BillingManager.iapSecurityServices;
            }
            if (functionsClass.searchEngineSubscribed()) {
                InAppBilling.ItemIAB = BillingManager.iapSearchEngines;
            }
            if (functionsClass.securityServicesSubscribed()) {
                InAppBilling.ItemIAB = BillingManager.iapFloatingWidgets;
            }
        }

        if (PublicVariable.themeLightDark) {
            setTheme(R.style.GeeksEmpire_Material_IAP_LIGHT);
        } else {
            setTheme(R.style.GeeksEmpire_Material_IAP_DARK);
        }

        if (savedInstanceState != null) {
            acquireFragment = (AcquireFragment) getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.authError), Toast.LENGTH_LONG).show();

            finish();
            return;
        }

        billingManager = new BillingManager(InAppBilling.this, getIntent().hasExtra("UserEmailAddress") ? getIntent().getStringExtra("UserEmailAddress") : null);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                proceedToPurchaseFragment();
            }
        });

        showRefreshedUi();
    }

    @Override
    public BillingManager getBillingManager() {
        return billingManager;
    }

    public void proceedToPurchaseFragment() {
        if (acquireFragment == null) {
            acquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            acquireFragment.show(getSupportFragmentManager(), DIALOG_TAG);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
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
