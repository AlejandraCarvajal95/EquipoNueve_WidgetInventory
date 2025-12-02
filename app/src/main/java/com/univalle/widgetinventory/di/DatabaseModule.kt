package com.univalle.widgetinventory.di

import android.content.Context
import androidx.room.Room
import com.univalle.widgetinventory.data.AppDatabase
import com.univalle.widgetinventory.data.ProductsDAO
import com.univalle.widgetinventory.utils.Constants.NAME_BD
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            NAME_BD
        ).build()
    }

    @Provides
    @Singleton
    fun provideProductsDAO(database: AppDatabase): ProductsDAO {
        return database.productsDao()
    }
}