package com.univalle.widgetinventory.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.univalle.widgetinventory.repository.ProductRepository
import com.univalle.widgetinventory.model.ProductEntity
import kotlinx.coroutines.launch

class DetalleProductoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProductRepository(application)

    private val _producto = MutableLiveData<ProductEntity>()
    val producto: LiveData<ProductEntity> = _producto

    fun cargarProducto(codigo: Int) {
        viewModelScope.launch {
            val productoCargado = repository.getProductByID(codigo)
            _producto.postValue(productoCargado)
        }
    }

    fun eliminarProducto(codigo: Int) {
        viewModelScope.launch {
            repository.deleteProduct(codigo)
        }
    }

}