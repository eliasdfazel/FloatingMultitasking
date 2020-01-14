/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/14/20 6:50 AM
 * Last modified 1/14/20 6:47 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.SecurityServices.Authentication;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import net.geekstools.floatshort.PRO.Checkpoint;
import net.geekstools.floatshort.PRO.Configurations;
import net.geekstools.floatshort.PRO.R;
import net.geekstools.floatshort.PRO.SearchEngine.SearchEngineAdapter;
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.Fingerprint.FingerprintProcessHelper;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClass;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassDebug;
import net.geekstools.floatshort.PRO.Util.Functions.FunctionsClassSecurity;
import net.geekstools.floatshort.PRO.Util.Functions.PublicVariable;
import net.geekstools.floatshort.PRO.Util.InteractionObserver.InteractionObserver;
import net.geekstools.floatshort.PRO.Util.UI.Splash.FloatingSplash;
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataInterface;
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.WidgetDataModel;
import net.geekstools.floatshort.PRO.Widget.WidgetConfigurations;
import net.geekstools.imageview.customshapes.ShapesImage;

import static android.content.Context.ACCESSIBILITY_SERVICE;

public class AuthenticationDialogFragment extends DialogFragment {

    private FunctionsClass functionsClass;
    private FunctionsClassSecurity functionsClassSecurity;

    private Dialog dialog;

    private View fingerprintContent;
    private ShapesImage dialogueIcon;
    private ImageView fingerprintIcon;
    private TextView dialogueTitle, fingerprintHint;
    private TextInputLayout textInputPassword;
    private TextInputEditText password;
    private Button cancelAuth;

    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintProcessHelper fingerprintProcessHelper;

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
        int dialogueWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        int dialogueHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, getResources().getDisplayMetrics());

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
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
        textInputPassword = (TextInputLayout) viewContainer.findViewById(R.id.textInputPassword);
        password = (TextInputEditText) viewContainer.findViewById(R.id.password);
        cancelAuth = (Button) viewContainer.findViewById(R.id.cancelAuth);

        fingerprintContent.setBackgroundTintList(ColorStateList.valueOf(PublicVariable.colorLightDark));

        String componentName = functionsClass.appName(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());

        dialogueIcon.setShapeDrawable(functionsClass.shapesDrawables());
        if (FunctionsClassSecurity.AuthOpenAppValues.getAuthWidgetConfigurationsUnlock()
                || FunctionsClassSecurity.AuthOpenAppValues.getAuthFloatingWidget()
                || FunctionsClassSecurity.AuthOpenAppValues.getAuthWidgetConfigurations()
                || FunctionsClassSecurity.AuthOpenAppValues.getAuthSearchEngine()
                || FunctionsClassSecurity.AuthOpenAppValues.getAuthForgotPinPassword()) {
            dialogueIcon.setImageDrawable(functionsClass
                    .shapedAppIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName()));

            dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite);
            fingerprintHint.setTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName())));
            cancelAuth.setTextColor(PublicVariable.colorLightDarkOpposite);
            cancelAuth.setBackgroundColor(PublicVariable.colorLightDark);

            password.setHintTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName())));
            password.setTextColor(PublicVariable.colorLightDarkOpposite);

            textInputPassword.setHintTextColor(ColorStateList.valueOf(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName()))));
            textInputPassword.setDefaultHintTextColor(ColorStateList.valueOf(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName()))));
        } else if (FunctionsClassSecurity.AuthOpenAppValues.getAuthFloatingShortcuts()) {
            try {
                ComponentName componentNameApplication = ComponentName.createRelative(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName(),
                        FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName());
                ActivityInfo activityInfo = getActivity().getPackageManager().getActivityInfo(componentNameApplication, 0);

                dialogueIcon.setImageDrawable(
                        componentName.equals("null") ?
                                null : functionsClass.shapedAppIcon(activityInfo)
                );

                dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite);
                fingerprintHint.setTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(activityInfo)));
                cancelAuth.setTextColor(PublicVariable.colorLightDarkOpposite);
                cancelAuth.setBackgroundColor(PublicVariable.colorLightDark);

                password.setHintTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(activityInfo)));
                password.setTextColor(PublicVariable.colorLightDarkOpposite);

                textInputPassword.setHintTextColor(ColorStateList.valueOf(functionsClass.extractVibrantColor(functionsClass.appIcon(activityInfo))));
                textInputPassword.setDefaultHintTextColor(ColorStateList.valueOf(functionsClass.extractVibrantColor(functionsClass.appIcon(activityInfo))));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            dialogueIcon.setImageDrawable(
                    componentName.equals("null") ?
                            null : functionsClass.shapedAppIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())
            );

            dialogueTitle.setTextColor(PublicVariable.colorLightDarkOpposite);
            fingerprintHint.setTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())));
            cancelAuth.setTextColor(PublicVariable.colorLightDarkOpposite);
            cancelAuth.setBackgroundColor(PublicVariable.colorLightDark);

            password.setHintTextColor(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName())));
            password.setTextColor(PublicVariable.colorLightDarkOpposite);

            textInputPassword.setHintTextColor(ColorStateList.valueOf(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName()))));
            textInputPassword.setDefaultHintTextColor(ColorStateList.valueOf(functionsClass.extractVibrantColor(functionsClass.appIcon(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName()))));
        }

        String dialogueTitleText = componentName.equals("null") ?
                FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName() : functionsClass.appName(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
        dialogueTitle.setText(Html.fromHtml("<big><font color='" + PublicVariable.colorLightDarkOpposite + "'>"
                +
                dialogueTitleText + " 🔒 "
                +
                "</font></big>"));

        password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        if (password.getText() != null) {
                            if (functionsClassSecurity.isEncryptedPinPasswordEqual(password.getText().toString())) {
                                if (FunctionsClassSecurity.AuthOpenAppValues.getAuthFloatingShortcuts()) {
                                    FunctionsClassDebug.Companion.PrintDebug("*** Authenticated | Open Application ***");

                                    if (functionsClass.splashReveal()) {
                                        Intent splashReveal = new Intent(getContext(), FloatingSplash.class);
                                        splashReveal.putExtra("packageName", FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
                                        splashReveal.putExtra("className", FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName());
                                        splashReveal.putExtra("X", FunctionsClassSecurity.AuthOpenAppValues.getAuthPositionX());
                                        splashReveal.putExtra("Y", FunctionsClassSecurity.AuthOpenAppValues.getAuthPositionY());
                                        splashReveal.putExtra("HW", FunctionsClassSecurity.AuthOpenAppValues.getAuthHW());
                                        getContext().startService(splashReveal);
                                    } else {
                                        if (functionsClass.FreeForm()) {
                                            functionsClass.openApplicationFreeForm(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName(),
                                                    FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName(),
                                                    FunctionsClassSecurity.AuthOpenAppValues.getAuthPositionX(),
                                                    (functionsClass.displayX() / 2),
                                                    FunctionsClassSecurity.AuthOpenAppValues.getAuthPositionY(),
                                                    (functionsClass.displayY() / 2)
                                            );
                                        } else {
                                            functionsClass.appsLaunchPad(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName(), FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName());
                                        }
                                    }
                                } else if (FunctionsClassSecurity.AuthOpenAppValues.getAuthSingleUnlockIt()) {
                                    FunctionsClassDebug.Companion.PrintDebug("*** Authenticated | Single Unlock It ***");

                                    if (FunctionsClassSecurity.AuthOpenAppValues.getAuthWidgetConfigurationsUnlock()) {
                                        functionsClassSecurity.doUnlockApps(FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName() + FunctionsClassSecurity.AuthOpenAppValues.getAuthWidgetProviderClassName());
                                    } else {
                                        functionsClassSecurity.doUnlockApps(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
                                    }

                                    functionsClassSecurity.uploadLockedAppsData();
                                    dismiss();
                                    try {
                                        getActivity().finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (FunctionsClassSecurity.AuthOpenAppValues.getAuthFolderUnlockIt()) {
                                    FunctionsClassDebug.Companion.PrintDebug("*** Authenticated | Folder Unlock It ***");

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
                                } else if (FunctionsClassSecurity.AuthOpenAppValues.getAuthSingleSplitIt()) {
                                    FunctionsClassDebug.Companion.PrintDebug("*** Authenticated | Single Split It ***");

                                    if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(InteractionObserver.class)) {
                                        getContext().startActivity(new Intent(getContext(), Checkpoint.class)
                                                .putExtra(getContext().getString(R.string.splitIt), getContext().getPackageName())
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    } else {
                                        final AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService(ACCESSIBILITY_SERVICE);
                                        PublicVariable.splitSinglePackage = FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName();
                                        PublicVariable.splitSingleClassName = FunctionsClassSecurity.AuthOpenAppValues.getAuthSecondComponentName();
                                        AccessibilityEvent accessibilityEvent = AccessibilityEvent.obtain();
                                        accessibilityEvent.setSource(FunctionsClassSecurity.AuthOpenAppValues.getAnchorView());
                                        accessibilityEvent.setEventType(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                                        accessibilityEvent.setAction(69201);
                                        accessibilityEvent.setClassName(FunctionsClassSecurity.AuthOpenAppValues.getAuthClassNameCommand());
                                        accessibilityEvent.getText().add(getContext().getPackageName());
                                        accessibilityManager.sendAccessibilityEvent(accessibilityEvent);
                                    }
                                } else if (FunctionsClassSecurity.AuthOpenAppValues.getAuthPairSplitIt()) {
                                    FunctionsClassDebug.Companion.PrintDebug("*** Authenticated | Pair Split It ***");

                                    if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(InteractionObserver.class)) {
                                        getContext().startActivity(new Intent(getContext(), Checkpoint.class)
                                                .putExtra(getContext().getString(R.string.splitIt), getContext().getPackageName())
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    } else {
                                        final AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService(ACCESSIBILITY_SERVICE);
                                        AccessibilityEvent accessibilityEvent = AccessibilityEvent.obtain();
                                        accessibilityEvent.setSource(FunctionsClassSecurity.AuthOpenAppValues.getAnchorView());
                                        accessibilityEvent.setEventType(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                                        accessibilityEvent.setAction(10296);
                                        accessibilityEvent.setClassName(FunctionsClassSecurity.AuthOpenAppValues.getAuthClassNameCommand());
                                        accessibilityEvent.getText().add(getContext().getPackageName());
                                        accessibilityManager.sendAccessibilityEvent(accessibilityEvent);
                                    }
                                } else if (FunctionsClassSecurity.AuthOpenAppValues.getAuthFloatingWidget()) {
                                    FunctionsClassDebug.Companion.PrintDebug("*** Authenticated | Floating Widget ***");

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            WidgetDataInterface widgetDataInterface = Room.databaseBuilder(getContext(), WidgetDataInterface.class, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                                    .fallbackToDestructiveMigration()
                                                    .addCallback(new RoomDatabase.Callback() {
                                                        @Override
                                                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                                            super.onCreate(db);
                                                        }

                                                        @Override
                                                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                                            super.onOpen(db);
                                                        }
                                                    })
                                                    .build();
                                            WidgetDataModel widgetDataModelsReallocation = widgetDataInterface.initDataAccessObject()
                                                    .loadWidgetByClassNameProviderWidget(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName(), FunctionsClassSecurity.AuthOpenAppValues.getAuthWidgetProviderClassName());

                                            getActivity().runOnUiThread(() -> {
                                                functionsClass.runUnlimitedWidgetService(widgetDataModelsReallocation.getWidgetId(),
                                                        widgetDataModelsReallocation.getWidgetLabel());
                                            });
                                        }
                                    }).start();


                                } else if (FunctionsClassSecurity.AuthOpenAppValues.getAuthWidgetConfigurations()) {
                                    FunctionsClassDebug.Companion.PrintDebug("*** Authenticated | Open Widget Configurations ***");

                                    WidgetConfigurations.alreadyAuthenticatedWidgets = true;
                                } else if (FunctionsClassSecurity.AuthOpenAppValues.getAuthSearchEngine()) {
                                    FunctionsClassDebug.Companion.PrintDebug("*** Authenticated | Run Search Engine ***");

                                    SearchEngineAdapter.alreadyAuthenticatedSearchEngine = true;
                                    getContext().sendBroadcast(new Intent("SEARCH_ENGINE_AUTHENTICATED"));
                                } else if (FunctionsClassSecurity.AuthOpenAppValues.getAuthRecovery()) {
                                    FunctionsClassDebug.Companion.PrintDebug("*** Authenticated | Recovery ***");

                                    getContext().sendBroadcast(new Intent("RECOVERY_AUTHENTICATED"));
                                } else {
                                    FunctionsClassDebug.Companion.PrintDebug("*** Authenticated | Open Application ***");

                                    if (functionsClass.splashReveal()) {
                                        Intent splashReveal = new Intent(getContext(), FloatingSplash.class);
                                        splashReveal.putExtra("packageName", FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
                                        splashReveal.putExtra("X", FunctionsClassSecurity.AuthOpenAppValues.getAuthPositionX());
                                        splashReveal.putExtra("Y", FunctionsClassSecurity.AuthOpenAppValues.getAuthPositionY());
                                        splashReveal.putExtra("HW", FunctionsClassSecurity.AuthOpenAppValues.getAuthHW());
                                        getContext().startService(splashReveal);
                                    } else {
                                        if (functionsClass.FreeForm()) {
                                            functionsClass.openApplicationFreeForm(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName(),
                                                    FunctionsClassSecurity.AuthOpenAppValues.getAuthPositionX(),
                                                    (functionsClass.displayX() / 2),
                                                    FunctionsClassSecurity.AuthOpenAppValues.getAuthPositionY(),
                                                    (functionsClass.displayY() / 2)
                                            );
                                        } else {
                                            functionsClass.appsLaunchPad(FunctionsClassSecurity.AuthOpenAppValues.getAuthComponentName());
                                        }
                                    }
                                }

                                dismiss();
                                try {
                                    getActivity().finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    functionsClassSecurity.resetAuthAppValues();
                                }
                            } else {
                                fingerprintHint.setTextColor(getContext().getColor(R.color.warning_color));
                                fingerprintHint.setText(getString(R.string.passwordError));

                                textInputPassword.setError(getString(R.string.passwordError));
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

            fingerprintProcessHelper = new FingerprintProcessHelper(
                    getActivity(),
                    getContext(),
                    getActivity().getSystemService(FingerprintManager.class),
                    fingerprintIcon,
                    fingerprintHint,
                    password,
                    new FingerprintProcessHelper.Callback() {
                        @Override
                        public void onAuthenticated() {
                            functionsClassSecurity.Authed(true, AuthenticationDialogFragment.this.cryptoObject);

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
                        fingerprintHint.setHeight(functionsClass.DpToInteger(13));
                        fingerprintHint.setText("");

                        fingerprintIcon.setVisibility(View.INVISIBLE);

                        textInputPassword.setVisibility(View.VISIBLE);
                        password.setVisibility(View.VISIBLE);

                        int dialogueWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
                        int dialogueHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 265, getResources().getDisplayMetrics());

                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        layoutParams.width = dialogueWidth;
                        layoutParams.height = dialogueHeight;
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        layoutParams.dimAmount = 0.57f;
                        dialog.getWindow().setAttributes(layoutParams);
                    }
                }
            };
            try {
                getContext().registerReceiver(broadcastReceiver, intentFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FunctionsClassDebug.Companion.PrintDebug("*** Finger Print Not Available ***");

            fingerprintHint.setText("");

            fingerprintIcon.setVisibility(View.INVISIBLE);

            textInputPassword.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);

            WindowManager.LayoutParams layoutParamsPin = new WindowManager.LayoutParams();
            int dialogueWidthPin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
            int dialogueHeightPin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 265, getResources().getDisplayMetrics());

            layoutParamsPin.width = dialogueWidthPin;
            layoutParamsPin.height = dialogueHeightPin;
            dialog.getWindow().setAttributes(layoutParamsPin);
        }

        cancelAuth.setOnLongClickListener(view -> {
            if (FunctionsClassSecurity.AuthOpenAppValues.getAuthWidgetConfigurations()) {
                startActivity(new Intent(getContext(), Configurations.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        ActivityOptions.makeCustomAnimation(getContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
            }
            getActivity().finish();

            functionsClassSecurity.resetAuthAppValues();
            return true;
        });

        return viewContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (functionsClassSecurity.fingerprintSensorAvailable() && functionsClassSecurity.fingerprintEnrolled()) {
            fingerprintProcessHelper.startListening(this.cryptoObject);
        }

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {

                    return true;
                } else {

                    return false;
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (functionsClassSecurity.fingerprintSensorAvailable() && functionsClassSecurity.fingerprintEnrolled()) {
            fingerprintProcessHelper.stopListening();
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
