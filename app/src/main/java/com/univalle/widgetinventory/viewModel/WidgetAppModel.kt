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
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import javax.inject.Inject

// ViewModel responsible for widget fragment state and computing the total inventory
@HiltViewModel
class WidgetAppModel @Inject constructor(
	application: Application,
	private val repository: ProductRepositoryFS
) : AndroidViewModel(application) {

	private val _balance = MutableLiveData<String>("$****")
	val balance: LiveData<String> = _balance

	private val _masked = MutableLiveData<Boolean>(true)
	val masked: LiveData<Boolean> = _masked

	private var rawTotal: Double = 0.0

	init {
		// Load products and compute total
		loadTotal()
	}

	private fun formatAmount(value: Double): String {
		// Format with '.' as thousand separator and ',' as decimal separator
		val symbols = DecimalFormatSymbols().apply {
			groupingSeparator = '.'
			decimalSeparator = ','
		}
		val df = DecimalFormat("#,##0.00", symbols)
		return df.format(value)
	}

	private fun computeTotal(products: List<ProductsFS>): Double {
		return products.fold(0.0) { acc, p -> acc + (p.precio * p.cantidad) }
	}

	private fun loadTotal() {
		viewModelScope.launch {
			try {
				val products = repository.getProducts()
				rawTotal = computeTotal(products)
				_balance.postValue("$" + formatAmount(rawTotal))
			} catch (e: Exception) {
				// On error, default to 0
				rawTotal = 0.0
				_balance.postValue("$" + formatAmount(rawTotal))
			}
		}
	}

	fun toggleMasked() {
		val new = _masked.value != true
		_masked.value = new
		if (new) {
			_balance.value = "$****"
		} else {
			_balance.value = "$" + formatAmount(rawTotal)
		}
	}
}