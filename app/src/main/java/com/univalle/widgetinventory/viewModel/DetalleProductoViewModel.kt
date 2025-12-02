package com.univalle.widgetinventory.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.univalle.widgetinventory.model.ProductsFS
import com.univalle.widgetinventory.repository.ProductRepositoryFS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleProductoViewModel @Inject constructor(
    application: Application,
    private val repository: ProductRepositoryFS
) : AndroidViewModel(application) {
    private val _producto = MutableLiveData<ProductsFS?>()
    val producto: LiveData<ProductsFS?> = _producto

    private val _isDeleted = MutableLiveData<Boolean>()
    val isDeleted: LiveData<Boolean> = _isDeleted

    fun cargarProducto(codigo: Int) {
        viewModelScope.launch {
            try {
                val productoCargado = repository.getProductByCode(codigo)
                _producto.postValue(productoCargado)

            } catch (e: Exception) {
                e.printStackTrace()
                _producto.postValue(null)
            }
        }
    }

    fun eliminarProducto(codigo: Int) {
        viewModelScope.launch {
            try {
                val success = repository.deleteProduct(codigo)

                _isDeleted.postValue(success)

            } catch (e: Exception) {
                e.printStackTrace()
                _isDeleted.postValue(false)
            }
        }
    }
}