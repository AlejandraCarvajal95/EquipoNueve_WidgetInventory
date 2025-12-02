package com.univalle.widgetinventory.viewModel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.univalle.widgetinventory.model.ProductEntity
import com.univalle.widgetinventory.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.*
import org.mockito.kotlin.verify
import org.mockito.Mockito.*
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class AgregarProductoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: ProductRepository
    private lateinit var application: Application

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mock(ProductRepository::class.java)
        application = mock(Application::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insertProduct guarda y publica isSaved true`() = runTest(dispatcher) {
        val vm = AgregarProductoViewModel(application, repository)

        vm.insertProduct(codigo = 10, nombre = "P1", precio = 12.5, cantidad = 3)
        advanceUntilIdle()

        val captor = argumentCaptor<ProductEntity>()
        verify(repository).insertProduct(captor.capture())
        val inserted = captor.firstValue
        assertEquals(10, inserted.codigo)
        assertEquals("P1", inserted.nombre)
        assertEquals(12.5, inserted.precio, 0.0)
        assertEquals(3, inserted.cantidad)

        assertTrue(vm.isSaved.value == true)
    }

    @Test
    fun `insertProduct error publica isSaved false`() = runTest(dispatcher) {
        whenever(repository.insertProduct(org.mockito.kotlin.any())).thenAnswer {
            throw RuntimeException("DB error")
        }
        val vm = AgregarProductoViewModel(application, repository)

        vm.insertProduct(codigo = 11, nombre = "P2", precio = 1.0, cantidad = 1)
        advanceUntilIdle()

        assertTrue(vm.isSaved.value == false)
    }
}

