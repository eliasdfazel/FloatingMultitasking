package net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.UI.FingerprintUiHelper;
import net.geekstools.imageview.customshapes.ShapesImage;

public class FingerprintAuthenticationDialogFragment extends DialogFragment implements FingerprintUiHelper.Callback {

    private FunctionsClass functionsClass;
    private FunctionsClassSecurity functionsClassSecurity;

    private Dialog dialog;

    private View fingerprintContent;
    private ShapesImage dialogueIcon;
    private ImageView fingerprintIcon;
    private TextView dialogueTitle, fingerprintHint;
    private EditText password;
    private Button cancelAuth;

    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintUiHelper fingerprintUiHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        functionsClass = new FunctionsClass(getContext(), getActivity());
        functionsClassSecurity = new FunctionsClassSecurity(getActivity(), getContext());

        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Dialog);

        dialog = getDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        int dialogueWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        int dialogueHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, getResources().getDisplayMetrics());

        layoutParams.width = dialogueWidth;
        layoutParams.height = dialogueHeight;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.57f;

        dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getDecorView().setBackgroundColor(PublicVariable.colorLightDark);
        dialog.getWindow().setAttributes(layoutParams);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setWindowAnimations(android.R.style.Animation_Dialog);

        View viewContainer = inflater.inflate(R.layout.fingerprint_dialog_content, container, false);

        fingerprintContent = (RelativeLayout) viewContainer.findViewById(R.id.fingerprint_container);
        dialogueIcon = (ShapesImage) viewContainer.findViewById(R.id.dialogueIcon);
        fingerprintIcon = (ImageView) viewContainer.findViewById(R.id.fingerprint_icon);
        dialogueTitle = (TextView) viewContainer.findViewById(R.id.dialogueTitle);
        fingerprintHint = (TextView) viewContainer.findViewById(R.id.fingerprint_status);
        password = (EditText) viewContainer.findViewById(R.id.password);
        cancelAuth = (Button) viewContainer.findViewById(R.id.cancelAuth);

        String componentName = functionsClass.appName(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());

        dialogueIcon.setShapeDrawable(functionsClass.shapesDrawables());
        dialogueIcon.setImageDrawable(
                componentName.equals("null") ?
                        null : functionsClass.shapedAppIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())
        );

        String dialogueTitleText = componentName.equals("null") ?
                FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName() : functionsClass.appName(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
        dialogueTitle.setText(Html.fromHtml("<big><font color='" + PublicVariable.colorLightDarkOpposite + "'>"
                +
                dialogueTitleText + " 🔒 "
                +
                "</font></big>"));

        dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite);
        fingerprintHint.setTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())));
        password.setHintTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())));
        password.setTextColor(PublicVariable.colorLightDarkOpposite);
        cancelAuth.setTextColor(PublicVariable.colorLightDarkOpposite);

        password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    /*If Password Match Authed*/
                    //
//                    try {
//                        functionsClassSecurity.encryptEncodedData(password.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid()).asList().toString()
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        functionsClassSecurity.decryptEncodedData(functionsClassSecurity.rawStringToByteArray(password.getText().toString()), FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    functionsClass.appsLaunchPad(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());

                    return true;
                }
                return false;
            }
        });

        fingerprintUiHelper = new FingerprintUiHelper(
                getContext(),
                getActivity().getSystemService(FingerprintManager.class),
                fingerprintIcon,
                fingerprintHint,
                password,
                this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SHOW_PASSWORD");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("SHOW_PASSWORD")) {
                    fingerprintHint.setText("");

                    fingerprintIcon.setVisibility(View.INVISIBLE);
                    password.setVisibility(View.VISIBLE);

                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    int dialogueWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
                    int dialogueHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 255, getResources().getDisplayMetrics());

                    layoutParams.width = dialogueWidth;
                    layoutParams.height = dialogueHeight;
                    dialog.getWindow().setAttributes(layoutParams);
                }
            }
        };
        getContext().registerReceiver(broadcastReceiver, intentFilter);

        cancelAuth.setOnLongClickListener(view -> {
            getActivity().finish();

            return true;
        });

        return viewContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        fingerprintUiHelper.startListening(this.cryptoObject);
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
