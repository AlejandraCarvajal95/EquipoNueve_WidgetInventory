package com.univalle.widgetinventory.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.widgetinventory.model.ProductsFS
import com.univalle.widgetinventory.repository.ProductRepositoryFS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val repository: ProductRepositoryFS
) : AndroidViewModel(application)  {

    private val _productos = MutableLiveData<MutableList<ProductsFS>>()
    val productos: LiveData<MutableList<ProductsFS>> get() = _productos

    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> = _progresState

    fun getProducts(){
        //Ejecuta la operación de base de datos en un hilo secundario (Coroutines)
        viewModelScope.launch {
            try {
                _progresState.value = true  // Mostrar ProgressBar
                delay(2000)  // Simular carga de 2 segundos
                _productos.value = repository.getProducts().toMutableList()
                _progresState.value = false  // Ocultar ProgressBar
            } catch (e: Exception) {
                _progresState.value = false
            }
        }
    }
}