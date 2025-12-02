package com.univalle.widgetinventory

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.univalle.widgetinventory.databinding.ActivityMainBinding
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import com.univalle.widgetinventory.widget.WidgetProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        // Verificar sesión
        val sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        
        // Si NO está logueado, ir a LoginActivity
        if (!isLoggedIn) {
            // Verificar si venimos del widget
            val openedFromWidget = sharedPreferences.getBoolean("opened_from_widget", false)
            if (openedFromWidget) {
                // Mantener la bandera para que LoginActivity la procese
                // No hacer nada aquí, solo redirigir
            }
            val intent = Intent(this, com.univalle.widgetinventory.view.LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        
        // Si está logueado, continuar con la navegación normal (HomeFragment por defecto)

        // Force widget update on app start so widgets pick up current DB values
        try {
            val mgr = AppWidgetManager.getInstance(this)
            val cn = ComponentName(this, WidgetProvider::class.java)
            val ids = mgr.getAppWidgetIds(cn)
            if (ids != null && ids.isNotEmpty()) {
                val updateIntent = Intent(this, WidgetProvider::class.java)
                updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                sendBroadcast(updateIntent)
            }
        } catch (_: Exception) {
            // ignore
        }
    }
}