package com.univalle.widgetinventory.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.widgetinventory.R
import com.univalle.widgetinventory.databinding.FragmentEditarProductoBinding
import com.univalle.widgetinventory.viewModel.EditarProductoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditarProductoFragment : Fragment() {

    private var _binding: FragmentEditarProductoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditarProductoViewModel by viewModels()

    private var productId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el ID del producto desde los argumentos
        productId = arguments?.getInt("product_id") ?: 0

        // Cargar el producto
        if (productId != 0) {
            viewModel.cargarProducto(productId)
        }

        // Configurar Toolbar - Criterio 1: Flecha de atrás navega a DetalleProductoFragment
        binding.editarProductoToolbar.setNavigationOnClickListener {
            // Navegar de vuelta a DetalleProductoFragment (HU 5.0)
            val bundle = Bundle().apply { putInt("product_id", productId) }
            findNavController().navigate(R.id.detalleProductoFragment, bundle)
        }

        // Configurar validación y estado del botón
        setupValidationAndButtonState()

        // Configurar click del botón Editar - Criterio 4
        binding.btnEditar.setOnClickListener {
            val nombre = binding.etNombreArticulo.text.toString()
            val precio = binding.etPrecioProducto.text.toString().toDoubleOrNull() ?: 0.0
            val cantidad = binding.etCantidadProducto.text.toString().toIntOrNull() ?: 0

            viewModel.updateProduct(
                codigo = productId,
                nombre = nombre,
                precio = precio,
                cantidad = cantidad
            )
        }

        // Observar cuando se carga el producto - Criterio 3: Pre-llenar campos
        viewModel.producto.observe(viewLifecycleOwner) { producto ->
            if (producto != null) {
                // Criterio 2: Mostrar el ID del producto (no editable)
                binding.tvIdProducto.text = "Id: ${producto.codigo}"
                // Pre-llenar los campos con la información del producto
                binding.etNombreArticulo.setText(producto.nombre)
                binding.etPrecioProducto.setText(producto.precio.toString())
                binding.etCantidadProducto.setText(producto.cantidad.toString())
                // Verificar validez después de pre-llenar
                checkFieldsValidity(arrayOf(
                    binding.etNombreArticulo,
                    binding.etPrecioProducto,
                    binding.etCantidadProducto
                ))
            }
        }

        // Observar el resultado de la actualización
        viewModel.isUpdated.observe(viewLifecycleOwner) { isUpdated ->
            if (isUpdated) {
                // ÉXITO: Navegar a HomeFragment para mostrar el ítem actualizado - Criterio 4
                Toast.makeText(requireContext(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.homeFragment)
            } else {
                // ERROR: Mostrar Toast de error
                Toast.makeText(requireContext(), "Error al actualizar el producto", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Criterio 5: Validar que todos los campos estén llenos
    private fun setupValidationAndButtonState() {
        val allFields = arrayOf(
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
    }

    private fun checkFieldsValidity(fields: Array<out com.google.android.material.textfield.TextInputEditText>) {
        val allFieldsAreFilled = fields.all { it.text?.isNotEmpty() == true }
        binding.btnEditar.isEnabled = allFieldsAreFilled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
