package com.univalle.widgetinventory.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.univalle.widgetinventory.model.ProductEntity

@Dao
interface ProductsDAO {
    @Query("SELECT * FROM productos")
    suspend fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM productos WHERE id = :idBuscado")
    suspend fun getProductoByID(idBuscado: Int): ProductEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductEntity)

    @Update
    suspend fun updateProducto(producto: ProductEntity)

    @Query("DELETE FROM productos WHERE id = :idAEliminar")
    suspend fun deleteProducto(idAEliminar: Int)
}