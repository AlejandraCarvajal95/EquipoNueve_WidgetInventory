package com.univalle.widgetinventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.univalle.widgetinventory.model.ProductEntity
import com.univalle.widgetinventory.utils.Constants.NAME_BD

@Database(entities = [ProductEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productsDao(): ProductsDAO

    companion object{
        // Migración de versión 1 a 2 para agregar el campo userId
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Agregar la columna userId con valor por defecto vacío
                database.execSQL("ALTER TABLE productos ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
            }
        }
        
        fun getDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                NAME_BD
            )
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration() // Como alternativa, elimina y recrea la BD si la migración falla
            .build()
        }
    }
}