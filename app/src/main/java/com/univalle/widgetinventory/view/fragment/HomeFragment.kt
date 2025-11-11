package com.univalle.widgetinventory.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.univalle.widgetinventory.R
import com.univalle.widgetinventory.databinding.FragmentHomeBinding
import com.univalle.widgetinventory.view.adapter.ProductAdapter
import com.univalle.widgetinventory.viewModel.HomeViewModel
import kotlin.getValue
import androidx.core.view.isVisible


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackButton()
        controladores()
        observadorViewModel()

        // Detectar click en el ícono de salida
        binding.ivExit.setOnClickListener {
            onExit()
        }

    }

    private fun onExit() {

        // lleva a login
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("is_logged_in").apply()
        findNavController().navigate(R.id.loginFragment)
    }

    private fun setupBackButton() {
        // Intercepta el botón atrás del dispositivo
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Minimiza la app al escritorio en lugar de volver al login
                requireActivity().moveTaskToBack(true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun controladores() {
        binding.fbagregar.setOnClickListener {
            // Ir a la pantalla de agregar producto
            findNavController().navigate(R.id.action_homeFragment_to_agregarProductoFragment)
        }
    }

    private fun observadorViewModel(){
        observerListProduct()
        observerProgress()
    }

    private fun observerListProduct(){
        viewModel.getProducts()
        viewModel.productos.observe(viewLifecycleOwner){ productos ->
            val recycler = binding.recyclerview
            val layoutManager = LinearLayoutManager(context)
            recycler.layoutManager = layoutManager
            val adapter = ProductAdapter(productos, findNavController())
            recycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun observerProgress(){
        viewModel.progresState.observe(viewLifecycleOwner){ status ->
            binding.progress.isVisible = status
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}