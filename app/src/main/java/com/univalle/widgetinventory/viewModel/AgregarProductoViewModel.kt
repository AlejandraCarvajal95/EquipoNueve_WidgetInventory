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
class AgregarProductoViewModel @Inject constructor(
    application: Application,
    private val repository: ProductRepository
) : AndroidViewModel(application) {

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> = _isSaved

    fun insertProduct(codigo: Int, nombre: String, precio: Double, cantidad: Int) {
        // Ejecuta la operación de base de datos en un hilo secundario (Coroutines)
        viewModelScope.launch {
            try {
                val newProduct = ProductEntity(
                    codigo = codigo,
                    nombre = nombre,
                    precio = precio,
                    cantidad = cantidad,
                )

                repository.insertProduct(newProduct)

                // Notificar al Fragment que la inserción fue exitosa
                _isSaved.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                _isSaved.postValue(false)
            }
        }
    }
}