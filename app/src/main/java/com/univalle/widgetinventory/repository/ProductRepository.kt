package com.univalle.widgetinventory.repository

import android.content.Context
import android.content.Intent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.univalle.widgetinventory.data.ProductsDAO
import com.univalle.widgetinventory.model.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.univalle.widgetinventory.widget.WidgetProvider
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val context: Context,
    private val productsDAO: ProductsDAO
) {

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
            // Notify widgets that data changed
            notifyWidgets()
        }
    }

    suspend fun updateProduct(product: ProductEntity) {
        withContext(Dispatchers.IO) {
            productsDAO.updateProducto(product)
            // Notify widgets that data changed
            notifyWidgets()
        }
    }

    suspend fun deleteProduct(id: Int) {
        withContext(Dispatchers.IO) {
            productsDAO.deleteProducto(id)
            // Notify widgets that data changed
            notifyWidgets()
        }
    }

    private fun notifyWidgets() {
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, WidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(thisWidget)
            if (ids != null && ids.isNotEmpty()) {
                android.util.Log.d("ProductRepository", "Notifying widgets, ids=${ids.joinToString()}")
                val updateIntent = Intent(context, WidgetProvider::class.java)
                updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                context.sendBroadcast(updateIntent)
            }
        } catch (_: Exception) {
            // ignore
        }
    }
}