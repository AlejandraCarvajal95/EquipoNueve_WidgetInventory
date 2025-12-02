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

        val dataToSave = product.toFirebaseMap()

        productsCollection.add(dataToSave).await()

    }.isSuccess

    suspend fun getProducts(): List<ProductsFS> {

        val result = productsCollection.get().await()

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

        val querySnapshot = productsCollection
            .whereEqualTo("Codigo", codigo)
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

        // 1. 🔍 BUSCAR EL DOCUMENTO POR EL CAMPO 'Codigo'
        // La búsqueda debe basarse en el valor único del campo, no en el ID del documento.
        val querySnapshot = productsCollection
            .whereEqualTo("Codigo", product.codigo)
            .get()
            .await()

        val documentSnapshot = querySnapshot.documents.firstOrNull()

        if (documentSnapshot == null) {

            throw NoSuchElementException("El producto con código ${product.codigo} no existe y no puede ser actualizado.")
        }

        val actualDocumentId = documentSnapshot.id

        val dataToUpdate = product.toFirebaseMap()

        productsCollection.document(actualDocumentId)
            .set(dataToUpdate)
            .await()

    }.isSuccess


    suspend fun deleteProduct(codigo: Int) = runCatching {

        val querySnapshot = productsCollection
            .whereEqualTo("Codigo", codigo)
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