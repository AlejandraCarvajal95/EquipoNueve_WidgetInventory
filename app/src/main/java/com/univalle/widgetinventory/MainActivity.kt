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
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        // obtener el NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Si la sesión está iniciada, ir a Home
        if (isLoggedIn) {
            navController.navigate(R.id.homeFragment)
        }

        // Si la Activity fue iniciada desde el widget para abrir login, navegar a loginFragment
        val openLogin = intent?.getBooleanExtra("open_login", false) ?: false
        if (openLogin) {
            navController.navigate(R.id.loginFragment)
        }

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