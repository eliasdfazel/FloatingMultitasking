package net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.UI.FingerprintUiHelper;

public class FingerprintAuthenticationDialogFragment extends DialogFragment implements FingerprintUiHelper.Callback {

    FunctionsClass functionsClass;
    FunctionsClassSecurity functionsClassSecurity;

    private Button cancelAuth;
    private TextView fingerprintHint;
    private View fingerprintContent;

    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintUiHelper fingerprintUiHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        functionsClass = new FunctionsClass(getContext(), getActivity());
        functionsClassSecurity = new FunctionsClassSecurity(getActivity(), getContext());

        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("*** Reference Activity ::: " + getActivity().getClass().getSimpleName());

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(PublicVariable.colorLightDark));

        String dialogueTitle = functionsClass.appName(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName()).equals("null") ? FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName() : functionsClass.appName(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
        try {
            getDialog().setTitle(Html.fromHtml("<big><font color='" + PublicVariable.colorLightDarkOpposite + "'>"
                    +
                    dialogueTitle + " 🔒 "
                    +
                    "</font></big>"));
        } catch (Exception e) {
            e.printStackTrace();
            getDialog().setTitle(Html.fromHtml("<big><font color='" + PublicVariable.colorLightDarkOpposite + "'>"
                    +
                    dialogueTitle + " 🔒 "
                    +
                    "</font></big>"));
        }

        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setWindowAnimations(android.R.style.Animation_Dialog);

        View viewContainer = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);

        fingerprintContent = (RelativeLayout) viewContainer.findViewById(R.id.fingerprint_container);
        cancelAuth = (Button) viewContainer.findViewById(R.id.cancelAuth);
        fingerprintHint = (TextView) viewContainer.findViewById(R.id.fingerprint_status);

        try {
            fingerprintHint.setTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())));
        } catch (Exception e) {
            e.printStackTrace();
            fingerprintHint.setTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())));
        }
        cancelAuth.setTextColor(PublicVariable.colorLightDarkOpposite);

        fingerprintUiHelper = new FingerprintUiHelper(
                getContext(),
                getActivity().getSystemService(FingerprintManager.class),
                (ImageView) viewContainer.findViewById(R.id.fingerprint_icon),
                fingerprintHint,
                this);

        cancelAuth.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getActivity().finish();

                return true;
            }
        });

        return viewContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        fingerprintUiHelper.startListening(this.cryptoObject);

        try {
            int dialogueWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 333, getResources().getDisplayMetrics());
            int dialogueHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 393, getResources().getDisplayMetrics());
            getDialog().getWindow().setLayout(dialogueWidth, dialogueHeight);
            getDialog().getWindow().setGravity(Gravity.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        fingerprintUiHelper.stopListening();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAuthenticated() {
        functionsClassSecurity.Authed(true, this.cryptoObject);

        dismiss();
        try {
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError() {
        functionsClassSecurity.authError();
    }

    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        this.cryptoObject = cryptoObject;
    }
}
