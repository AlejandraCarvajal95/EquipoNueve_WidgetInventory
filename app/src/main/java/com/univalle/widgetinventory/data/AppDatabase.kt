package com.univalle.widgetinventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.univalle.widgetinventory.model.ProductEntity
import com.univalle.widgetinventory.utils.Constants.NAME_BD
@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productsDao(): ProductsDAO

    companion object{
        fun getDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                NAME_BD
            ).build()
        }
    }
}