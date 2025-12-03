package com.univalle.widgetinventory.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductEntity(
    @PrimaryKey val codigo: Int = 0,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val userId: String = "" // ID del usuario dueño del producto
)