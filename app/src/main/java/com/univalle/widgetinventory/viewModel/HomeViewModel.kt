package com.univalle.widgetinventory.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.widgetinventory.repository.ProductRepository
import com.univalle.widgetinventory.model.ProductEntity
import kotlinx.coroutines.launch


class HomeViewModel(application: Application) : AndroidViewModel(application)  {
    private val repository = ProductRepository(application)

    private val _productos = MutableLiveData<MutableList<ProductEntity>>()
    val productos: LiveData<MutableList<ProductEntity>> get() = _productos

    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> = _progresState

    fun getProducts(){
        //Ejecuta la operación de base de datos en un hilo secundario (Coroutines)
        viewModelScope.launch {
            try {
                _productos.value = repository.getAllProducts().toMutableList()
                _progresState.value = false
            } catch (e: Exception) {
                _progresState.value = false
            }
        }
    }
}