package com.univalle.widgetinventory.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat.getMainExecutor
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.univalle.widgetinventory.R
import androidx.navigation.fragment.findNavController
import java.util.concurrent.Executor
import com.airbnb.lottie.LottieAnimationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
	private var eyeOpen = true
	private var biometricPrompt: BiometricPrompt? = null
	private var promptInfo: BiometricPrompt.PromptInfo? = null

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val tilEmail = view.findViewById<TextInputLayout>(R.id.tilEmail)
		val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
		val tilPassword = view.findViewById<TextInputLayout>(R.id.tilPassword)
		val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
		val btnLogin = view.findViewById<MaterialButton>(R.id.btnLogin)
		val tvRegister = view.findViewById<View>(R.id.tvRegister)
		val btnBiometric = view.findViewById<MaterialButton>(R.id.btnBiometric)
		val lottieFingerprint = view.findViewById<LottieAnimationView>(R.id.lottieFingerprint)

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

		// Configurar biometría: solo lanzar cuando el usuario toca la huella
		val canAuth = BiometricManager.from(requireContext())
			.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
		if (canAuth == BiometricManager.BIOMETRIC_SUCCESS) {
			setupBiometric()
			lottieFingerprint.visibility = View.VISIBLE
		} else {
			lottieFingerprint.visibility = View.GONE
		}

		// Preferimos el ícono de huella; ocultamos el botón auxiliar si existe
		btnBiometric.visibility = View.GONE

		lottieFingerprint.setOnClickListener {
			if (promptInfo == null) setupBiometric()
			biometricPrompt?.authenticate(requireNotNull(promptInfo))
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

	private fun setupBiometric() {
		val executor: Executor = getMainExecutor(requireContext())
		biometricPrompt = BiometricPrompt(this, executor,
			object : BiometricPrompt.AuthenticationCallback() {
				override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
					super.onAuthenticationError(errorCode, errString)
					Toast.makeText(requireContext(), errString, Toast.LENGTH_SHORT).show()
				}

				override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
					super.onAuthenticationSucceeded(result)
					// Guardar sesión
					val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
					sharedPreferences.edit().putBoolean("is_logged_in", true).apply()
					try { findNavController().navigate(R.id.action_loginFragment_to_homeFragment) } catch (_: Exception) {}
				}

				override fun onAuthenticationFailed() {
					super.onAuthenticationFailed()
					Toast.makeText(requireContext(), getString(R.string.biometric_error), Toast.LENGTH_SHORT).show()
				}
			}
		)

		promptInfo = BiometricPrompt.PromptInfo.Builder()
			.setTitle(getString(R.string.biometric_title))
			.setSubtitle(getString(R.string.biometric_subtitle))
			.setNegativeButtonText(getString(R.string.biometric_cancel))
			.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
			.build()
	}
}
