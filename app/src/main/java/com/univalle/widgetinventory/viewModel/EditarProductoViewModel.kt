package com.univalle.widgetinventory.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.widgetinventory.repository.ProductRepository
import com.univalle.widgetinventory.model.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarProductoViewModel @Inject constructor(
    application: Application,
    private val repository: ProductRepository
) : AndroidViewModel(application) {

    private val _producto = MutableLiveData<ProductEntity>()
    val producto: LiveData<ProductEntity> = _producto

    private val _isUpdated = MutableLiveData<Boolean>()
    val isUpdated: LiveData<Boolean> = _isUpdated

    fun cargarProducto(codigo: Int) {
        viewModelScope.launch {
            try {
                val productoCargado = repository.getProductByID(codigo)
                _producto.postValue(productoCargado)
            } catch (e: Exception) {
                e.printStackTrace()
                // Si hay error, no actualizamos el producto
            }
        }
    }

    fun updateProduct(codigo: Int, nombre: String, precio: Double, cantidad: Int) {
        viewModelScope.launch {
            try {
                val updatedProduct = ProductEntity(
                    codigo = codigo,
                    nombre = nombre,
                    precio = precio,
                    cantidad = cantidad,
                )

                repository.updateProduct(updatedProduct)

                // Notificar al Fragment que la actualización fue exitosa
                _isUpdated.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                _isUpdated.postValue(false)
            }
        }
    }
}

