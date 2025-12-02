package com.univalle.widgetinventory.view.viewholder

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.univalle.widgetinventory.R
import com.univalle.widgetinventory.databinding.ItemProductBinding
import com.univalle.widgetinventory.model.ProductEntity
import com.univalle.widgetinventory.model.ProductsFS
import com.univalle.widgetinventory.view.fragment.HomeFragmentDirections
import java.text.NumberFormat
import java.util.Locale

class ProductViewHolder(private val binding: ItemProductBinding, navController: NavController) :
    RecyclerView.ViewHolder(binding.root) {

    val bindingItem = binding
    val navController = navController

    fun setItemProduct(product: ProductsFS) {
        // Mostrar datos del producto
        binding.tvProductName.text = product.nombre
        
        // Formatear precio en formato colombiano
        val formatoColombia = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        binding.tvProductPrice.text = formatoColombia.format(product.precio)
        
        binding.tvProductId.text = "Id: ${product.codigo}"

        // Hacer la tarjeta clickeable para ir a detalle
        binding.cvProduct.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDetalleProductoFragment(
                productId = product.codigo
            )
            navController.navigate(action)
        }
    }
}

