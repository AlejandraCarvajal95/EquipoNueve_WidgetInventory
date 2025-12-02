package com.univalle.widgetinventory.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.univalle.widgetinventory.R
import android.util.Log
import com.univalle.widgetinventory.repository.ProductRepository
import com.univalle.widgetinventory.repository.ProductRepositoryFS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class WidgetProvider : AppWidgetProvider() {

    companion object {
        private const val PREFS_NAME = "widget_prefs"
        private const val PREF_MASKED = "masked_"
        private const val ACTION_TOGGLE = "com.univalle.widgetinventory.ACTION_TOGGLE"
        private const val ACTION_MANAGE = "com.univalle.widgetinventory.ACTION_MANAGE"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // For each widget, update content (possibly async)
        for (appWidgetId in appWidgetIds) {
            Log.d("WidgetProvider", "onUpdate for appWidgetId=$appWidgetId")
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d("WidgetProvider", "onReceive action=${intent.action}")

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (intent.action == ACTION_TOGGLE) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            if (appWidgetId != -1) {
                // Check if user is logged in; if not, open login flow
                val appPrefs = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
                val isLoggedIn = appPrefs.getBoolean("is_logged_in", false)
                if (!isLoggedIn) {
                    Log.d("WidgetProvider", "User not logged in -> launching LoginActivity")
                    // Marcar que venimos del widget
                    appPrefs.edit().putBoolean("opened_from_widget", true).apply()
                    val i = Intent(context, com.univalle.widgetinventory.view.LoginActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(i)
                } else {
                    val key = PREF_MASKED + appWidgetId
                    val masked = prefs.getBoolean(key, true)
                    prefs.edit().putBoolean(key, !masked).apply()
                    Log.d("WidgetProvider", "Toggled masked for widget $appWidgetId -> ${!masked}")
                    // update this widget
                    val mgr = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, mgr, appWidgetId)
                }
            }
        } else if (intent.action == ACTION_MANAGE) {
            // Launch MainActivity to manage inventory
            Log.d("WidgetProvider", "Received ACTION_MANAGE - launching MainActivity")
            val appPrefs = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
            val isLoggedIn = appPrefs.getBoolean("is_logged_in", false)
            
            if (isLoggedIn) {
                val i = Intent(context, com.univalle.widgetinventory.MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(i)
            } else {
                appPrefs.edit().putBoolean("opened_from_widget", true).apply()
                val i = Intent(context, com.univalle.widgetinventory.view.LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(i)
            }
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val maskedKey = PREF_MASKED + appWidgetId
        val masked = prefs.getBoolean(maskedKey, true)

        // Choose layout size based on current widget width
        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val minWidth = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) ?: 0
        val layoutId = when {
            minWidth >= 300 -> R.layout.widget_layout_large
            minWidth >= 220 -> R.layout.widget_layout_medium
            else -> R.layout.widget_layout
        }

        // Set up the RemoteViews
        val views = RemoteViews(context.packageName, layoutId)

        // Default: show masked state by default (closed eye)
        views.setTextViewText(R.id.tv_balance, "$****")
        views.setImageViewResource(R.id.iv_eye, R.drawable.ic_eye_closed)

        // Setup pending intents for eye toggle
        val toggleIntent = Intent(context, WidgetProvider::class.java).apply {
            action = ACTION_TOGGLE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val togglePI = PendingIntent.getBroadcast(context, appWidgetId, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.iv_eye, togglePI)

        // Setup pending intents for manage button
        val manageIntent = Intent(context, WidgetProvider::class.java).apply {
            action = ACTION_MANAGE
        }
        val managePI = PendingIntent.getBroadcast(
            context,
            appWidgetId + 100000,
            manageIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.iv_manage, managePI)
        views.setOnClickPendingIntent(R.id.tv_manage, managePI)

        // Check if user is logged in
        val appPrefs = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
        val isLoggedIn = appPrefs.getBoolean("is_logged_in", false)

        // Load products and compute total asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isLoggedIn && !masked) {
                    // Usuario logueado y quiere ver el saldo
                    val database = com.univalle.widgetinventory.data.FirebaseClient()
                    val repo = ProductRepositoryFS(database)
                    val products = repo.getProducts()
                    val total = products.fold(0.0) { acc, p -> acc + (p.precio * p.cantidad) }
                    Log.d("WidgetProvider", "Loaded ${products.size} products for widget $appWidgetId, total=$total")
                    val formatted = formatAmount(total)

                    views.setTextViewText(R.id.tv_balance, "$" + formatted)
                    views.setImageViewResource(R.id.iv_eye, R.drawable.ic_eye_open)
                } else {
                    // Mostrar asteriscos (saldo oculto)
                    views.setTextViewText(R.id.tv_balance, "$****")
                    views.setImageViewResource(R.id.iv_eye, R.drawable.ic_eye_closed)
                }

            } catch (e: Exception) {
                Log.e("WidgetProvider", "Error loading products for widget $appWidgetId", e)
                // On error keep masked/default
                views.setTextViewText(R.id.tv_balance, "$****")
                views.setImageViewResource(R.id.iv_eye, R.drawable.ic_eye_closed)
            } finally {
                // Apply update on UI thread
                val mgr = AppWidgetManager.getInstance(context)
                mgr.updateAppWidget(appWidgetId, views)
            }
        }
    }

    private fun formatAmount(value: Double): String {
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val df = DecimalFormat("#,##0.00", symbols)
        return df.format(value)
    }
}
