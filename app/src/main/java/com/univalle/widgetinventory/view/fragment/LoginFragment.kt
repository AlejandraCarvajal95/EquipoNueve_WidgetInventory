package com.univalle.widgetinventory.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.univalle.widgetinventory.R
import com.airbnb.lottie.LottieAnimationView
import androidx.core.content.ContextCompat
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val lottieFingerprint = view.findViewById<LottieAnimationView>(R.id.lottieFingerprint)

		// Executor for BiometricPrompt callbacks
		val executor = ContextCompat.getMainExecutor(requireContext())

		val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
			override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
				super.onAuthenticationSucceeded(result)

				// Guardar sesión
				val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
				sharedPreferences.edit().putBoolean("is_logged_in", true).apply()

				// Navegar a Home
				findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
			}
		})

		// Saca el texto del archivo de strings
		val promptInfo = BiometricPrompt.PromptInfo.Builder()
			.setTitle(getString(R.string.biometric_title))
			.setSubtitle(getString(R.string.biometric_subtitle))
			.setNegativeButtonText(getString(R.string.biometric_cancel))
			.build()

		lottieFingerprint.setOnClickListener {
			// Verificar disponibilidad de biometría
			val biometricManager = BiometricManager.from(requireContext())
			val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

			if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
				// Mostrar el prompt biométrico
				biometricPrompt.authenticate(promptInfo)
			} else {
				// Si no hay biometría disponible
				Toast.makeText(requireContext(), getString(R.string.biometric_error), Toast.LENGTH_SHORT).show()
			}
		}
	}
}
