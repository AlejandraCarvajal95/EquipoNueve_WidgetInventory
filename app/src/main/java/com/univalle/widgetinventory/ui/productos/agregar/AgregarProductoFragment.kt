package com.univalle.widgetinventory.ui.productos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Importa la delegación de ViewModels
import androidx.navigation.fragment.findNavController
import com.univalle.widgetinventory.R
import com.univalle.widgetinventory.databinding.FragmentAgregarProductoBinding
import com.univalle.widgetinventory.ui.productos.agregar.AgregarProductoViewModel // Asegúrate de tener esta importación

class AgregarProductoFragment : Fragment() {

    private var _binding: FragmentAgregarProductoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AgregarProductoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgregarProductoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // (Configuración de Toolbar y Listeners existentes)
        binding.agregarProductoToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupValidationAndButtonState()

        binding.btnGuardar.setOnClickListener {
            val codigo = binding.etCodigoProducto.text.toString().toIntOrNull() ?: 0

            val nombre = binding.etNombreArticulo.text.toString()

            val precio = binding.etPrecioProducto.text.toString().toDoubleOrNull() ?: 0.0

            val cantidad = binding.etCantidadProducto.text.toString().toIntOrNull() ?: 0

            viewModel.insertProduct(
                codigo = codigo,
                nombre = nombre,
                precio = precio,
                cantidad = cantidad
            )
        }

    }


    private fun setupValidationAndButtonState() {

        // 1. Array de todos los campos de texto que deben ser validados
        val allFields = arrayOf(
            binding.etCodigoProducto,
            binding.etNombreArticulo,
            binding.etPrecioProducto,
            binding.etCantidadProducto
        )

        val validationWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                checkFieldsValidity(allFields)
            }
        }

        allFields.forEach { it.addTextChangedListener(validationWatcher) }

        checkFieldsValidity(allFields)
    }

    private fun checkFieldsValidity(fields: Array<out com.google.android.material.textfield.TextInputEditText>) {

        val allFieldsAreFilled = fields.all { it.text?.isNotEmpty() == true }

        binding.btnGuardar.isEnabled = allFieldsAreFilled

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}