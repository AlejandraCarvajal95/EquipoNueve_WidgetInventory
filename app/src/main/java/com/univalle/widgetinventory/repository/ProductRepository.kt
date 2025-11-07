package com.univalle.widgetinventory.data

import android.content.Context
import com.univalle.widgetinventory.model.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val context: Context) {

    private val productsDAO: ProductsDAO = AppDatabase.getDatabase(context).productsDao()

    suspend fun getAllProducts(): List<ProductEntity> {
        return withContext(Dispatchers.IO) {
            productsDAO.getAll()
        }
    }

    suspend fun getProductByID(id: Int): ProductEntity {
        return withContext(Dispatchers.IO) {
            productsDAO.getProductoByID(id)
        }
    }

    suspend fun insertProduct(product: ProductEntity) {
        withContext(Dispatchers.IO) {
            productsDAO.insertProducto(product)
        }
    }

    suspend fun updateProduct(product: ProductEntity) {
        withContext(Dispatchers.IO) {
            productsDAO.updateProducto(product)
        }
    }

    suspend fun deleteProduct(id: Int) {
        withContext(Dispatchers.IO) {
            productsDAO.deleteProducto(id)
        }
    }
}