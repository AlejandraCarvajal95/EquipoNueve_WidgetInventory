/*package com.univalle.widgetinventory.view.fragment


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.univalle.widgetinventory.R
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
	private var eyeOpen = true

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val tilEmail = view.findViewById<TextInputLayout>(R.id.tilEmail)
		val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
		val tilPassword = view.findViewById<TextInputLayout>(R.id.tilPassword)
		val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
		val btnLogin = view.findViewById<MaterialButton>(R.id.btnLogin)
		val tvRegister = view.findViewById<View>(R.id.tvRegister)

		// Toggle de visibilidad con ícono a la izquierda
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

		// Validación UI de contraseña en tiempo real
		val watcher = object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				val emailFilled = !etEmail.text.isNullOrBlank()
				val passLen = etPassword.text?.length ?: 0
				val passOk = passLen >= 6
				// Error visual
				tilPassword.error = if (!passOk && passLen > 0) getString(R.string.min_6_digits) else null

				// Habilitar/Deshabilitar botones de UI
				val enable = emailFilled && passLen > 0
				btnLogin.isEnabled = enable
				tvRegister.isEnabled = enable
			}
			override fun afterTextChanged(s: Editable?) {}
		}
		etEmail.addTextChangedListener(watcher)
		etPassword.addTextChangedListener(watcher)

		// Estado inicial oculto
		etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
		tilPassword.startIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_open)

		// Foco inicial y teclado
		etEmail.post {
			etEmail.requestFocus()
			val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
			imm.showSoftInput(etEmail, InputMethodManager.SHOW_IMPLICIT)
		}

		// Navegación básica de UI (solo front): ir a Home cuando están habilitados
		btnLogin.setOnClickListener {
			if (btnLogin.isEnabled) {
				try { findNavController().navigate(R.id.homeFragment) } catch (_: Exception) {}
			}
		}
		tvRegister.setOnClickListener {
			// Siempre permitir ir a Registro para que el usuario pueda registrarse
			try { findNavController().navigate(R.id.action_loginFragment_to_registerFragment) } catch (_: Exception) {}
		}
	}
}*/
