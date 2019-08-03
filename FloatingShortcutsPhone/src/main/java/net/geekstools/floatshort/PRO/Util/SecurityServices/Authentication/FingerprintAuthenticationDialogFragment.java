package net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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

import com.google.firebase.auth.FirebaseAuth;

import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.SecurityServices.Authentication.UI.FingerprintUiHelper;
import net.geekstools.imageview.customshapes.ShapesImage;

public class FingerprintAuthenticationDialogFragment extends DialogFragment {

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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialog.getWindow().setAttributes(layoutParams);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setWindowAnimations(android.R.style.Animation_Dialog);

        View viewContainer = inflater.inflate(R.layout.auth_dialog_content, container, false);

        fingerprintContent = (RelativeLayout) viewContainer.findViewById(R.id.fingerprintContainer);
        dialogueIcon = (ShapesImage) viewContainer.findViewById(R.id.dialogueIcon);
        fingerprintIcon = (ImageView) viewContainer.findViewById(R.id.fingerprint_icon);
        dialogueTitle = (TextView) viewContainer.findViewById(R.id.dialogueTitle);
        fingerprintHint = (TextView) viewContainer.findViewById(R.id.fingerprint_status);
        password = (EditText) viewContainer.findViewById(R.id.password);
        cancelAuth = (Button) viewContainer.findViewById(R.id.cancelAuth);

        fingerprintContent.setBackgroundTintList(ColorStateList.valueOf(PublicVariable.colorLightDark));

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
                    try {
                        if (password.getText() != null) {
                            String currentPassword = functionsClassSecurity.decryptEncodedData(functionsClassSecurity.rawStringToByteArray(functionsClass.readPreference(".Password", "Pin", getContext().getPackageName())), FirebaseAuth.getInstance().getCurrentUser().getUid());

                            if (password.getText().toString().equals(currentPassword)) {
                                if (FunctionsClassSecurity.AuthOpenAppValues.getAuthSingleUnlockIt()) {
                                    functionsClassSecurity.doUnlockApps(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());

                                    functionsClassSecurity.uploadLockedAppsData();
                                    dismiss();
                                    try {
                                        getActivity().finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (FunctionsClassSecurity.AuthOpenAppValues.getAuthFolderUnlockIt()) {
                                    if (getContext().getFileStreamPath(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName()).exists() && getContext().getFileStreamPath(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName()).isFile()) {
                                        String[] packageNames = functionsClass.readFileLine(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
                                        for (String packageName : packageNames) {
                                            functionsClassSecurity.doUnlockApps(packageName);
                                        }
                                        functionsClassSecurity.doUnlockApps(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
                                        functionsClassSecurity.uploadLockedAppsData();
                                        dismiss();
                                        try {
                                            getActivity().finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    functionsClass.appsLaunchPad(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
                                }
                            } else {
                                fingerprintHint.setTextColor(getContext().getColor(R.color.warning_color));
                                fingerprintHint.setText(getString(R.string.passwordError));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
                FunctionsClassDebug.Companion.PrintDebug("*** Pin Password ==" + sequence.toString() + " ***");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                fingerprintHint.setText("");
                fingerprintHint.setTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (functionsClassSecurity.fingerprintSensorAvailable() && functionsClassSecurity.fingerprintEnrolled()) {
            FunctionsClassDebug.Companion.PrintDebug("*** Finger Print Available ***");

            fingerprintUiHelper = new FingerprintUiHelper(
                    getContext(),
                    getActivity().getSystemService(FingerprintManager.class),
                    fingerprintIcon,
                    fingerprintHint,
                    password,
                    new FingerprintUiHelper.Callback() {
                        @Override
                        public void onAuthenticated() {
                            functionsClassSecurity.Authed(true, FingerprintAuthenticationDialogFragment.this.cryptoObject);

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
                    });

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
        } else {
            FunctionsClassDebug.Companion.PrintDebug("*** Finger Print Not Available ***");

            fingerprintHint.setText("");

            fingerprintIcon.setVisibility(View.INVISIBLE);
            password.setVisibility(View.VISIBLE);

            WindowManager.LayoutParams layoutParamsPin = new WindowManager.LayoutParams();
            int dialogueWidthPin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
            int dialogueHeightPin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 255, getResources().getDisplayMetrics());

            layoutParamsPin.width = dialogueWidthPin;
            layoutParamsPin.height = dialogueHeightPin;
            dialog.getWindow().setAttributes(layoutParamsPin);
        }

        cancelAuth.setOnLongClickListener(view -> {
            getActivity().finish();

            return true;
        });

        return viewContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (functionsClassSecurity.fingerprintSensorAvailable() && functionsClassSecurity.fingerprintEnrolled()) {
            fingerprintUiHelper.startListening(this.cryptoObject);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (functionsClassSecurity.fingerprintSensorAvailable() && functionsClassSecurity.fingerprintEnrolled()) {
            fingerprintUiHelper.stopListening();
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        this.cryptoObject = cryptoObject;
    }
}
