package com.univalle.widgetinventory.viewModel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.univalle.widgetinventory.model.ProductEntity
import com.univalle.widgetinventory.model.ProductsFS
import com.univalle.widgetinventory.repository.ProductRepository
import com.univalle.widgetinventory.repository.ProductRepositoryFS
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.mockito.Mockito.*
import org.mockito.kotlin.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class WidgetAppModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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
    fun `init carga productos y publica balance formateado`() = runTest(dispatcher) {
        val lista = listOf(
            ProductsFS(1, "A", 1000.5, 2), // 2001.0
            ProductsFS(2, "B", 10.0, 3)    //   30.0
        )                                        // 2031.0
        whenever(repository.getProducts()).thenReturn(lista)

        val vm = WidgetAppModel(application, repository)
        // Balance inicial enmascarado por defecto
        assertEquals("$****", vm.balance.value)
        assertTrue(vm.masked.value == true)

        // Avanzar corrutinas de init
        advanceUntilIdle()

        // Aunque masked siga true, el ViewModel establece el balance numérico tras cargar
        assertEquals("$2.031,00", vm.balance.value)
    }

    @Test
    fun `toggleMasked alterna entre oculto y visible`() = runTest(dispatcher) {
        whenever(repository.getProducts()).thenReturn(listOf(ProductsFS(1, "A", 100.0, 1)))
        val vm = WidgetAppModel(application, repository)
        advanceUntilIdle()

        // Desenmascarar
        vm.toggleMasked()
        assertTrue(vm.masked.value == false)
        assertEquals("$100,00", vm.balance.value)

        // Enmascarar de nuevo
        vm.toggleMasked()
        assertTrue(vm.masked.value == true)
        assertEquals("$****", vm.balance.value)
    }

    @Test
    fun `error al cargar establece total 0 y balance $0,00`() = runTest(dispatcher) {
        whenever(repository.getProducts()).thenAnswer { throw RuntimeException("DB error") }
        val vm = WidgetAppModel(application, repository)

        advanceUntilIdle()
        assertEquals("$0,00", vm.balance.value)
    }
}

