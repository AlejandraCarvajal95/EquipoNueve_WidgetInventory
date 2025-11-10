package com.univalle.widgetinventory.view.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
// import com.univalle.widgetinventory.data.AppDatabase
// import com.univalle.widgetinventory.repository.ProductRepository
import com.univalle.widgetinventory.databinding.FragmentProductDetailsBinding
import com.univalle.widgetinventory.viewModel.DetalleProductoViewModel

class DetalleProductoFragment : Fragment() {
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetalleProductoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getInt("product_id") ?: 0
        viewModel.cargarProducto(productId)

        viewModel.producto.observe(viewLifecycleOwner) { product ->
            binding.tvCodigoProducto.text = product.codigo.toString()
            binding.tvNombreProducto.text = product.nombre
            binding.tvPrecioProducto.text = product.precio.toString()
            binding.tvCantidadProducto.text = product.cantidad.toString()
        }

        binding.detalleProductoToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}