package com.univalle.widgetinventory.view.viewholder

import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.univalle.widgetinventory.databinding.ItemProductBinding
import com.univalle.widgetinventory.model.ProductEntity

class ProductViewHolder(private val binding: ItemProductBinding, navController: NavController) :
    RecyclerView.ViewHolder(binding.root) {

    val bindingItem = binding
    val navController = navController

    fun setItemProduct(product: ProductEntity) {
        // Mostrar datos del producto
        binding.tvProductName.text = product.nombre
        binding.tvProductPrice.text = "$${product.precio}"
        binding.tvProductId.text = "${product.codigo}"

        // (Opcional) Hacer la tarjeta clickeable
        // binding.cvProduct.setOnClickListener {
        //     // Aquí puedes agregar lógica de navegación después
        // }
    }
}

