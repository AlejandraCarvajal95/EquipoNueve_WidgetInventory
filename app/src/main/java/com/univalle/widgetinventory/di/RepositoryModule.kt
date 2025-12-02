package com.univalle.widgetinventory.di

import android.content.Context
import com.univalle.widgetinventory.data.ProductsDAO
import com.univalle.widgetinventory.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        @ApplicationContext context: Context,
        productsDAO: ProductsDAO
    ): ProductRepository {
        return ProductRepository(context, productsDAO)
    }
}