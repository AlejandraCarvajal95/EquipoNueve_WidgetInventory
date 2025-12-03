package com.univalle.widgetinventory.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.univalle.widgetinventory.model.ProductEntity

@Dao
interface ProductsDAO {
    @Query("SELECT * FROM productos WHERE userId = :userId")
    suspend fun getAll(userId: String): List<ProductEntity>
    
    @Query("SELECT * FROM productos")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM productos WHERE codigo = :codigoBuscado AND userId = :userId")
    suspend fun getProductoByID(codigoBuscado: Int, userId: String): ProductEntity
    
    @Query("SELECT * FROM productos WHERE codigo = :codigoBuscado")
    suspend fun getProductoByCode(codigoBuscado: Int): ProductEntity

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProducto(producto: ProductEntity)

    @Update
    suspend fun updateProducto(producto: ProductEntity)

    @Query("DELETE FROM productos WHERE codigo = :codigoBuscado AND userId = :userId")
    suspend fun deleteProducto(codigoBuscado: Int, userId: String)
    
    @Query("DELETE FROM productos WHERE codigo = :codigoBuscado")
    suspend fun deleteProductByCode(codigoBuscado: Int)
}