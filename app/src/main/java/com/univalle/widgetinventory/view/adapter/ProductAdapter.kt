package com.univalle.widgetinventory.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.univalle.widgetinventory.databinding.ItemProductBinding
import com.univalle.widgetinventory.model.ProductsFS
import com.univalle.widgetinventory.view.viewholder.ProductViewHolder


class ProductAdapter(private var listProducts: MutableList<ProductsFS>?, private val navController: NavController) :
    RecyclerView.Adapter<ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, navController)
    }


    override fun getItemCount(): Int {
        return listProducts?.size ?: 0
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = listProducts?.get(position)
        if(product != null) {
            holder.setItemProduct(product)
        }
    }

    fun updateProducts(newProducts: List<ProductsFS>) {
        listProducts?.clear()
        listProducts?.addAll(newProducts)
        notifyDataSetChanged()
   }
}