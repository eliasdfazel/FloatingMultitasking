/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.PinPassword

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Extensions.setupAuthenticationPinPasswordUI
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityInterfaceHolder
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.databinding.AuthDialogContentBinding

class AuthenticationPinPassword : DialogFragment() {

    private lateinit var functionsClassLegacy: FunctionsClassLegacy
    private lateinit var securityFunctions: SecurityFunctions

    lateinit var authDialogContentBinding: AuthDialogContentBinding

    object ExtraInformation {
        var dialogueTitle: String? = null

        var primaryColor: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        functionsClassLegacy = FunctionsClassLegacy(requireContext())
        securityFunctions = SecurityFunctions(requireContext())

        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Dialog)

        retainInstance = true

        ExtraInformation.dialogueTitle = arguments?.getString("DialogueTitle")

        ExtraInformation.primaryColor = arguments?.getInt("PrimaryColor", 0)!!
    }

    override fun onCreateView(layoutInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 333f, resources.displayMetrics).toInt()
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.57f
        layoutParams.windowAnimations = android.R.style.Animation_Dialog

        val dialoguePinPassword = requireDialog()
        dialoguePinPassword.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialoguePinPassword.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialoguePinPassword.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        dialoguePinPassword.window?.attributes = layoutParams
        dialoguePinPassword.window?.setWindowAnimations(android.R.style.Animation_Dialog)

        dialoguePinPassword.setCancelable(false)
        dialoguePinPassword.setCanceledOnTouchOutside(false)

        authDialogContentBinding = AuthDialogContentBinding.inflate(getLayoutInflater())

        setupAuthenticationPinPasswordUI()

        return authDialogContentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authDialogContentBinding.pinPasswordEditText.requestFocus()
        val inputMethodManager: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(authDialogContentBinding.pinPasswordEditText, InputMethodManager.SHOW_IMPLICIT)

        authDialogContentBinding.pinPasswordEditText.setOnEditorActionListener { textView, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (authDialogContentBinding.pinPasswordEditText.text != null) {

                    if (securityFunctions.isEncryptedPinPasswordEqual(authDialogContentBinding.pinPasswordEditText.text.toString())) {

                        authDialogContentBinding.textInputPinPassword.isErrorEnabled = false

                        SecurityInterfaceHolder.authenticationCallback
                                .authenticatedFloatIt(null)

                        requireActivity().finish()

                    } else {

                        authDialogContentBinding.textInputPinPassword.isErrorEnabled = true
                        authDialogContentBinding.textInputPinPassword.error = getString(R.string.passwordError)

                        SecurityInterfaceHolder.authenticationCallback
                                .failedAuthenticated()

                    }
                }
            }

            false
        }

        authDialogContentBinding.pinPasswordEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(sequence: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(editable: Editable) {

                authDialogContentBinding.textInputPinPassword.isErrorEnabled = false
            }
        })

        authDialogContentBinding.cancelAuth.setOnLongClickListener {
            SecurityInterfaceHolder.authenticationCallback.failedAuthenticated()

            this@AuthenticationPinPassword.dismiss()

            requireActivity().finish()

            true
        }
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        super.onDismiss(dialogInterface)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()

        requireDialog().setOnKeyListener { dialogInterface, keyCode, keyEvent ->

            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    SecurityInterfaceHolder.authenticationCallback.failedAuthenticated()

                    this@AuthenticationPinPassword.dismiss()

                    requireActivity().finish()
                }
            }

            false
        }
    }

    override fun onPause() {
        super.onPause()
    }
}