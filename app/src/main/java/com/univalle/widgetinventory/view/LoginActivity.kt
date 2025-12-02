package com.univalle.widgetinventory.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import com.univalle.widgetinventory.MainActivity
import com.univalle.widgetinventory.R
import com.univalle.widgetinventory.databinding.ActivityLoginBinding
import com.univalle.widgetinventory.model.UserRequest
import com.univalle.widgetinventory.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private var eyeOpen = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)
        sesion()
        setupUI()
        setup()
        viewModelObservers()
    }


    
    private fun setupUI() {
        // Toggle de visibilidad de contraseña con ícono
        binding.tilPassword.setStartIconOnClickListener {
            eyeOpen = !eyeOpen
            if (eyeOpen) {
                binding.tilPassword.startIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_eye_open)
                binding.etPass.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                binding.tilPassword.startIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_eye_closed)
                binding.etPass.transformationMethod = null
            }
            binding.etPass.setSelection(binding.etPass.text?.length ?: 0)
        }

        // Validación en tiempo real
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailFilled = !binding.etEmail.text.isNullOrBlank()
                val passLen = binding.etPass.text?.length ?: 0
                val passOk = passLen >= 6
                
                // Error visual
                binding.tilPassword.error = if (passLen > 0 && passLen < 6) "Mínimo 6 dígitos" else null

                // Habilitar/Deshabilitar botones
                val enable = emailFilled && passOk
                binding.btnLogin.isEnabled = enable
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etEmail.addTextChangedListener(watcher)
        binding.etPass.addTextChangedListener(watcher)

        // Estado inicial - contraseña oculta
        binding.etPass.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.tilPassword.startIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_eye_open)

        // Foco inicial en email
        binding.etEmail.post {
            binding.etEmail.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etEmail, InputMethodManager.SHOW_IMPLICIT)
        }
    }
    private fun viewModelObservers() {
        observerIsRegister()
    }
    private fun observerIsRegister() {
        loginViewModel.isRegister.observe(this) { userResponse ->
            if (userResponse.isRegister) {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().putString("email",userResponse.email).apply()
                goToHome()
            } else {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setup() {
        binding.tvRegister.setOnClickListener {
            registerUser()
        }

        binding.btnLogin.setOnClickListener {
            if (binding.btnLogin.isEnabled) {
                loginUser()
            }
        }
    }


    private fun registerUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()
        val userRequest = UserRequest(email, pass)

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            loginViewModel.registerUser(userRequest)
        } else {
            Toast.makeText(this, "Campos Vacíos", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun goToHome() {
        sharedPreferences.edit().putBoolean("is_logged_in", true).apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun loginUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()
        
        loginViewModel.loginUser(email, pass) { isLogin ->
            if (isLogin) {
                sharedPreferences.edit().putString("email", email).apply()
                goToHome()
            } else {
                Toast.makeText(this, "Login incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun sesion() {
        val email = sharedPreferences.getString("email", null)
        loginViewModel.sesion(email) { isEnableView ->
            if (isEnableView) {
                binding.clContenedor.visibility = View.INVISIBLE
                goToHome()
            }
        }
    }
}