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
class EditarProductoViewModelTest {

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
    fun `cargarProducto publica producto en LiveData`() = runTest(dispatcher) {
        val codigo = 55
        val entity = ProductsFS(codigo, "Edit P", 3.3, 7)
        whenever(repository.getProductByCode(codigo)).thenReturn(entity)

        val vm = EditarProductoViewModel(application, repository)
        assertNull(vm.producto.value)

        vm.cargarProducto(codigo)
        advanceUntilIdle()

        assertEquals(entity, vm.producto.value)
    }

    @Test
    fun `cargarProducto error no actualiza LiveData`() = runTest(dispatcher) {
        val codigo = 66
        whenever(repository.getProductByCode(codigo)).thenAnswer {
            throw RuntimeException("DB error")
        }

        val vm = EditarProductoViewModel(application, repository)
        vm.cargarProducto(codigo)
        advanceUntilIdle()

        assertNull(vm.producto.value)
    }

    @Test
    fun `updateProduct exito actualiza repo y publica isUpdated true`() = runTest(dispatcher) {
        val vm = EditarProductoViewModel(application, repository)

        val editProduct = ProductsFS(codigo = 10, nombre = "Nuevo", precio = 4.0, cantidad = 9)
        whenever(repository.updateProduct(editProduct)).thenReturn(true)
        vm.editarProducto(editProduct)
        advanceUntilIdle()

        val captor = argumentCaptor<ProductsFS>()
        verify(repository).updateProduct(captor.capture())
        val upd = captor.firstValue
        assertEquals(10, upd.codigo)
        assertEquals("Nuevo", upd.nombre)
        assertEquals(4.0, upd.precio, 0.0)
        assertEquals(9, upd.cantidad)
        assertTrue(vm.isUpdated.value == true)
    }

    @Test
    fun `updateProduct error publica isUpdated false`() = runTest(dispatcher) {
        whenever(repository.updateProduct(org.mockito.kotlin.any())).thenAnswer {
            throw RuntimeException("DB error")
        }
        val vm = EditarProductoViewModel(application, repository)

        val editProduct = ProductsFS(codigo = 1, nombre = "X", precio = 1.0, cantidad = 1)

        vm.editarProducto(editProduct)
        advanceUntilIdle()

        assertTrue(vm.isUpdated.value == false)
    }
}

