/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/16/20 4:33 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.IAP;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Utils.IAP.Util.PurchasesCheckpoint;
import net.geekstools.floatshort.PRO.Utils.IAP.billing.BillingManager;
import net.geekstools.floatshort.PRO.Utils.IAP.billing.BillingProvider;

@Deprecated
public class InAppBilling extends AppCompatActivity implements BillingProvider {

    private static final String DIALOG_TAG = "InAppBillingDialogue";

    FunctionsClass functionsClass;

    private BillingManager billingManager;
    private AcquireFragment acquireFragment;

    public static String ItemIAB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        functionsClass = new FunctionsClass(getApplicationContext());

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

        billingManager = new BillingManager(InAppBilling.this,
                getIntent().hasExtra("UserEmailAddress") ? getIntent().getStringExtra("UserEmailAddress") : null);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                proceedToPurchaseFragment();
            }
        });

        showRefreshedUi();
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
    public BillingManager getBillingManager() {

        return billingManager;
    }

    @Override
    public void onResume() {
        super.onResume();

        new PurchasesCheckpoint(InAppBilling.this).trigger();
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
