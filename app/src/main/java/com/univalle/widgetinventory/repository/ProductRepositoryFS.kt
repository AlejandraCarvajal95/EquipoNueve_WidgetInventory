package com.univalle.widgetinventory.repository

import com.univalle.widgetinventory.data.FirebaseClient
import com.univalle.widgetinventory.data.FirestoreConstants.COLLECTION_PRODUCTS
import com.univalle.widgetinventory.model.ProductsFS
import com.univalle.widgetinventory.model.ProductsFS.Companion.toFirebaseMap
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class ProductRepositoryFS @Inject constructor(private val firebase: FirebaseClient) {

    private val productsCollection = firebase.db.collection(COLLECTION_PRODUCTS)

    suspend fun createProduct(product : ProductsFS) = runCatching {
        // Obtener el userId del usuario actual
        val currentUserId = firebase.auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
        
        // Crear el producto con el userId del usuario actual
        val productWithUser = product.copy(userId = currentUserId)
        val dataToSave = productWithUser.toFirebaseMap()

        productsCollection.add(dataToSave).await()

    }.isSuccess

    suspend fun getProducts(): List<ProductsFS> {
        // Obtener el userId del usuario actual
        val currentUserId = firebase.auth.currentUser?.uid ?: return emptyList()
        
        // Filtrar productos solo del usuario actual
        val result = productsCollection
            .whereEqualTo("UserId", currentUserId)
            .get()
            .await()

        return result.documents
            .mapNotNull { documentSnapshot ->
                val documentMap = documentSnapshot.data
                if (documentMap != null) {
                    ProductsFS.fromFirebaseMap(documentMap)
                } else {
                    null
                }
            }.toList()

    }

    suspend fun getProductByCode(codigo: Int): ProductsFS? {
        // Obtener el userId del usuario actual
        val currentUserId = firebase.auth.currentUser?.uid ?: return null
        
        val querySnapshot = productsCollection
            .whereEqualTo("Codigo", codigo)
            .whereEqualTo("UserId", currentUserId)
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            return null
        }

        val documentSnapshot = querySnapshot.documents.firstOrNull()

        val documentMap = documentSnapshot?.data ?: return null

        return ProductsFS.fromFirebaseMap(documentMap)
    }

    suspend fun updateProduct(product: ProductsFS) = runCatching {
        // Obtener el userId del usuario actual
        val currentUserId = firebase.auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
        
        // 1. 🔍 BUSCAR EL DOCUMENTO POR EL CAMPO 'Codigo' Y UserId
        // La búsqueda debe basarse en el valor único del campo, no en el ID del documento.
        val querySnapshot = productsCollection
            .whereEqualTo("Codigo", product.codigo)
            .whereEqualTo("UserId", currentUserId)
            .get()
            .await()

        val documentSnapshot = querySnapshot.documents.firstOrNull()

        if (documentSnapshot == null) {

            throw NoSuchElementException("El producto con código ${product.codigo} no existe y no puede ser actualizado.")
        }

        val actualDocumentId = documentSnapshot.id
        
        // Asegurarse de que el userId se mantenga
        val productWithUser = product.copy(userId = currentUserId)
        val dataToUpdate = productWithUser.toFirebaseMap()

        productsCollection.document(actualDocumentId)
            .set(dataToUpdate)
            .await()

    }.isSuccess


    suspend fun deleteProduct(codigo: Int) = runCatching {
        // Obtener el userId del usuario actual
        val currentUserId = firebase.auth.currentUser?.uid ?: return@runCatching
        
        val querySnapshot = productsCollection
            .whereEqualTo("Codigo", codigo)
            .whereEqualTo("UserId", currentUserId)
            .get()
            .await()

        val documentSnapshot = querySnapshot.documents.firstOrNull()

        if (documentSnapshot == null) {
            return@runCatching
        }
        val actualDocumentId = documentSnapshot.id

        productsCollection.document(actualDocumentId)
            .delete()
            .await()

    }.isSuccess
}