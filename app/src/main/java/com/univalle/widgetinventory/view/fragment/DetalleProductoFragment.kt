package com.univalle.widgetinventory.view.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.univalle.widgetinventory.R
import com.univalle.widgetinventory.databinding.FragmentProductDetailsBinding
import com.univalle.widgetinventory.viewModel.DetalleProductoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetalleProductoFragment : Fragment() {
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetalleProductoViewModel by viewModels()

    private var productId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productId = arguments?.getInt("product_id") ?: 0

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.handler = this

        viewModel.cargarProducto(productId)

        binding.detalleProductoToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
    }

    fun onDeleteClick() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("¿Desea eliminar el producto?")
            .setNegativeButton("No", null)
            .setPositiveButton("Si") { _, _ ->
                viewModel.eliminarProducto(productId)
                findNavController().navigate(R.id.homeFragment)
            }
            .show()
    }

    fun onEditClick() {
        val bundle = Bundle().apply { putInt("product_id", productId) }
        findNavController().navigate(R.id.editarProductoFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}