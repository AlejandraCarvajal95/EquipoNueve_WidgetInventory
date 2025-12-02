package com.univalle.widgetinventory.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.univalle.widgetinventory.R
import androidx.navigation.fragment.findNavController
import android.view.inputmethod.InputMethodManager
import android.content.Context

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var eyeOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tilEmail = view.findViewById<TextInputLayout>(R.id.tilEmailR)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmailR)
        val tilPassword = view.findViewById<TextInputLayout>(R.id.tilPasswordR)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPasswordR)
        val tilPasswordRepeat = view.findViewById<TextInputLayout>(R.id.tilPasswordRepeatR)
        val etPasswordRepeat = view.findViewById<TextInputEditText>(R.id.etPasswordRepeatR)
        val btnRegister = view.findViewById<MaterialButton>(R.id.btnRegister)
        val tvGoLogin = view.findViewById<View>(R.id.tvGoLogin)

        tilPassword.setStartIconOnClickListener {
            eyeOpen = !eyeOpen
            if (eyeOpen) {
                tilPassword.startIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_open)
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                tilPassword.startIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_closed)
                etPassword.transformationMethod = null
            }
            etPassword.setSelection(etPassword.text?.length ?: 0)
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailFilled = !etEmail.text.isNullOrBlank()
                val passLen = etPassword.text?.length ?: 0
                val passOk = passLen >= 6
                tilPassword.error = if (!passOk && passLen > 0) getString(R.string.min_6_digits) else null

                val repeatLen = etPasswordRepeat.text?.length ?: 0
                val repeatOk = repeatLen >= 6
                val same = (etPassword.text?.toString() ?: "") == (etPasswordRepeat.text?.toString() ?: "")
                tilPasswordRepeat.error = if (repeatLen > 0 && (!repeatOk || !same)) getString(R.string.password_mismatch) else null

                val enable = emailFilled && passOk && repeatOk && same
                btnRegister.isEnabled = enable
                tvGoLogin.isEnabled = enable
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        etEmail.addTextChangedListener(watcher)
        etPassword.addTextChangedListener(watcher)
        etPasswordRepeat.addTextChangedListener(watcher)

        etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        tilPassword.startIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_open)
        tilPasswordRepeat.startIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_open)

        // Foco en email y mostrar teclado
        etEmail.post {
            etEmail.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etEmail, InputMethodManager.SHOW_IMPLICIT)
        }

        btnRegister.setOnClickListener {
            if (btnRegister.isEnabled) {
                try { findNavController().navigate(R.id.homeFragment) } catch (_: Exception) {}
            }
        }
        tvGoLogin.setOnClickListener {
            if (tvGoLogin.isEnabled) {
                val intent = android.content.Intent(requireContext(), com.univalle.widgetinventory.view.LoginActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
}
