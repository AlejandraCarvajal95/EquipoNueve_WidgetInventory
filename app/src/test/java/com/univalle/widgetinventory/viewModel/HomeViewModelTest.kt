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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: ProductRepository
    private lateinit var application: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mock(ProductRepository::class.java)
        application = mock(Application::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getProducts actualiza progresState y productos correctamente`() = runTest(dispatcher) {
        // Datos simulados
        val lista = listOf(
            ProductEntity(codigo = 1, nombre = "Producto A", precio = 10.0, cantidad = 2),
            ProductEntity(codigo = 2, nombre = "Producto B", precio = 5.5, cantidad = 4)
        )
        whenever(repository.getAllProducts()).thenReturn(lista)

        val viewModel = HomeViewModel(application, repository)

        // Estado inicial
        assertFalse(viewModel.progresState.value ?: false)
        assertEquals(null, viewModel.productos.value)

        viewModel.getProducts()

        // Progreso inicia
        assertTrue(viewModel.progresState.value == true)

        // Avanzar toda la cola (incluye delay)
        advanceUntilIdle()

        // Verificar actualización final
        assertFalse(viewModel.progresState.value ?: true)
        val productos = viewModel.productos.value
        assertNotNull(productos)
        assertEquals(2, productos!!.size)
        assertEquals("Producto A", productos[0].nombre)
        assertEquals(10.0, productos[0].precio, 0.0)
    }

    @Test
    fun `getProducts maneja excepcion y oculta progreso`() = runTest(dispatcher) {
        whenever(repository.getAllProducts()).thenThrow(RuntimeException("DB error"))
        val viewModel = HomeViewModel(application, repository)

        viewModel.getProducts()
        // Progreso inicia
        assertTrue(viewModel.progresState.value == true)
        // Avanzar toda la cola (incluye delay y catch)
        advanceUntilIdle()
        // Debe haberse puesto en false en el catch
        assertFalse(viewModel.progresState.value ?: true)
        // Lista debe seguir nula
        assertEquals(null, viewModel.productos.value)
    }
}
