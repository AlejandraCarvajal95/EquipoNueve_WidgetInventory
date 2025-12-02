package com.univalle.widgetinventory.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.widgetinventory.model.ProductsFS
import com.univalle.widgetinventory.repository.ProductRepositoryFS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarProductoViewModel @Inject constructor(
    application: Application,
    private val repository: ProductRepositoryFS
) : AndroidViewModel(application) {
    private val _producto = MutableLiveData<ProductsFS?>()
    val producto: LiveData<ProductsFS?> = _producto

    private val _isUpdated = MutableLiveData<Boolean?>()
    val isUpdated: LiveData<Boolean?> = _isUpdated

    var currentProduct: ProductsFS? = null

    fun cargarProducto(codigo: Int) {
        viewModelScope.launch {
            try {
                val productoCargado = repository.getProductByCode(codigo)

                _producto.postValue(productoCargado)

                currentProduct = productoCargado

            } catch (e: Exception) {
                e.printStackTrace()
                _producto.postValue(null)
            }
        }
    }

    fun editarProducto(product: ProductsFS) {
        viewModelScope.launch {
            try {
                val success = repository.updateProduct(product)
                _isUpdated.postValue(success)

                if (success) {
                    currentProduct = product
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isUpdated.postValue(false)
            }
        }
    }
}
