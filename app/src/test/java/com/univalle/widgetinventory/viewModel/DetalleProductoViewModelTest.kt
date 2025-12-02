package com.univalle.widgetinventory.viewModel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.univalle.widgetinventory.model.ProductEntity
import com.univalle.widgetinventory.model.ProductsFS
import com.univalle.widgetinventory.repository.ProductRepository
import com.univalle.widgetinventory.repository.ProductRepositoryFS
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.mockito.kotlin.verify
import org.mockito.Mockito.*
import org.mockito.kotlin.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleProductoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: ProductRepositoryFS
    private lateinit var application: Application

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mock(ProductRepositoryFS::class.java)
        application = mock(Application::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarProducto publica el producto obtenido del repositorio`() = runTest(dispatcher) {
        val codigo = 123
        val entity = ProductsFS(codigo = codigo, nombre = "ProductoPrueba", precio = 9.99, cantidad = 3)
        whenever(repository.getProductByCode(codigo)).thenReturn(entity)

        val vm = DetalleProductoViewModel(application, repository)
        assertNull(vm.producto.value)

        vm.cargarProducto(codigo)
        // Avanzar corrutinas pendientes
        advanceUntilIdle()

        assertEquals(entity, vm.producto.value)
    }

    @Test
    fun `eliminarProducto invoca deleteProduct en el repositorio`() = runTest(dispatcher) {
        val codigo = 77
        val vm = DetalleProductoViewModel(application, repository)

        vm.eliminarProducto(codigo)
        advanceUntilIdle()

        verify(repository).deleteProduct(codigo)
    }
}
