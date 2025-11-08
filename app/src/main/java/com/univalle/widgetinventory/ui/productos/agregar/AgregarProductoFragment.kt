package com.univalle.widgetinventory.ui.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Importa la delegación de ViewModels
import androidx.navigation.fragment.findNavController
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

        binding.agregarProductoToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnGuardar.setOnClickListener {
            // 1. Recolectar datos
            val codigo = binding.etCodigoProducto.text.toString().toIntOrNull() ?: 0
            val nombre = binding.etNombreArticulo.text.toString()
            val precio = binding.etPrecioProducto.text.toString().toDoubleOrNull() ?: 0.0
            val cantidad = binding.etCantidadProducto.text.toString().toIntOrNull() ?: 0

            viewModel.insertProduct(codigo,nombre, precio, cantidad)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}