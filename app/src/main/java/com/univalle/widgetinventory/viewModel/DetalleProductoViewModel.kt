package com.univalle.widgetinventory.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.univalle.widgetinventory.repository.ProductRepository
import com.univalle.widgetinventory.model.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleProductoViewModel @Inject constructor(
    application: Application,
    private val repository: ProductRepository
) : AndroidViewModel(application) {

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