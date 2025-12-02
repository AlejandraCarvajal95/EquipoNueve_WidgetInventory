package com.univalle.widgetinventory.model

import com.univalle.widgetinventory.data.FirestoreConstants.ProductFields

data class ProductsFS(
    val codigo: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int
) {
    companion object {

        fun ProductsFS.toFirebaseMap(): Map<String, Any> {
            return mapOf(
                ProductFields.FIELD_CODIGO to this.codigo, // Mapea Kotlin 'id' a Firestore 'id_prod'
                ProductFields.FIELD_NOMBRE to this.nombre,
                ProductFields.FIELD_PRECIO to this.precio,
                ProductFields.FIELD_CANTIDAD to this.cantidad
            )
        }

        fun fromFirebaseMap(map: Map<String, Any>): ProductsFS? {
            val codigo = map[ProductFields.FIELD_CODIGO] as? Long
            val nombre = map[ProductFields.FIELD_NOMBRE] as? String
            val precio = map[ProductFields.FIELD_PRECIO] as? Double
            val cantidad = map[ProductFields.FIELD_CANTIDAD] as? Long

            if (codigo == null || nombre == null || precio == null || cantidad == null) {
                return null
            }

            return ProductsFS(
                codigo = codigo.toInt(),
                nombre = nombre,
                precio = precio,
                cantidad = cantidad.toInt()
            )
        }

    }
}