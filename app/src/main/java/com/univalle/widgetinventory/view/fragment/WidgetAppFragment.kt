package com.univalle.widgetinventory.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.univalle.widgetinventory.R
import com.univalle.widgetinventory.viewModel.WidgetAppModel
import dagger.hilt.android.AndroidEntryPoint

// Fragment that shows the widget UI inside the app (implements the requested UI criteria)
@AndroidEntryPoint
class WidgetAppFragment : Fragment() {

	private lateinit var viewModel: WidgetAppModel

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_widget_app, container, false)

		viewModel = ViewModelProvider(this).get(WidgetAppModel::class.java)

		val tvBalance = view.findViewById<TextView>(R.id.tvBalance)
		val ivEye = view.findViewById<ImageView>(R.id.ivEye)
		val ivManage = view.findViewById<ImageView>(R.id.ivManage)
		val tvManage = view.findViewById<TextView>(R.id.tvManage)

		// Observe balance and masked state
		viewModel.balance.observe(viewLifecycleOwner, Observer { b ->
			// balance LiveData already contains formatted string (with $)
			tvBalance.text = if (viewModel.masked.value == true) "$****" else (b ?: "$0,00")
		})

		viewModel.masked.observe(viewLifecycleOwner, Observer { masked ->
			if (masked == true) {
				tvBalance.text = "$****"
				ivEye.setImageResource(R.drawable.ic_eye_open)
				ivEye.contentDescription = "Mostrar saldo"
			} else {
				// show actual balance
				ivEye.setImageResource(R.drawable.ic_eye_closed)
				ivEye.contentDescription = "Ocultar saldo"
				tvBalance.text = viewModel.balance.value ?: "$0,00"
			}
		})

		ivEye.setOnClickListener {
			viewModel.toggleMasked()
		}

		// Navigate to login when manage clicked
		val navigateToLogin = View.OnClickListener {
			try {
				val intent = android.content.Intent(requireContext(), com.univalle.widgetinventory.view.LoginActivity::class.java)
				intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
				startActivity(intent)
				requireActivity().finish()
			} catch (e: Exception) {
				// ignore if navigation fails
			}
		}

		ivManage.setOnClickListener(navigateToLogin)
		tvManage.setOnClickListener(navigateToLogin)

		return view
	}
}