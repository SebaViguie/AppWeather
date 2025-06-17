package com.example.appweather

import com.example.appweather.presentacion.ciudades.CiudadesEstado
import com.example.appweather.presentacion.ciudades.CiudadesIntencion
import com.example.appweather.presentacion.ciudades.CiudadesViewModel
import com.example.appweather.presentacion.ciudades.CiudadesViewModelFactory
import com.example.appweather.presentacion.clima.actual.ClimaEstado
import com.example.appweather.presentacion.clima.actual.ClimaIntencion
import com.example.appweather.presentacion.clima.actual.ClimaViewModel
import com.example.appweather.presentacion.clima.actual.ClimaViewModelFactory
import com.example.appweather.presentacion.clima.pronostico.PronosticoEstado
import com.example.appweather.presentacion.clima.pronostico.PronosticoIntencion
import com.example.appweather.presentacion.clima.pronostico.PronosticoViewModel
import com.example.appweather.presentacion.clima.pronostico.PronosticoViewModelFactory
import com.example.appweather.repositorymock.RepositorioMock
import com.example.appweather.repositorymock.RepositorioMockError
import com.example.appweather.repositorymock.RepositoryMockDelay
import com.example.appweather.router.Ruta
import com.example.appweather.routermock.RouterMock
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ExampleUnitTest {

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    val repositorio = RepositorioMock()
    val repositorioError = RepositorioMockError()
    val repositorioDelay = RepositoryMockDelay()
    val router = RouterMock()
    val factory = CiudadesViewModelFactory(repositorio,router)
    val viewModel = factory.create(CiudadesViewModel::class.java)


    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun test_buscarCordoba() = runTest {
        launch(Dispatchers.Main){
            viewModel.ejecutar(
                intencion = CiudadesIntencion.Buscar("cor"))
                delay(1.milliseconds)
                assertEquals(
                CiudadesEstado.Resultado(listOf(repositorio.cordoba)),
                viewModel.uiState)
        }
    }

    @Test
    fun test_buscarBsAs() = runTest {
        launch(Dispatchers.Main){
            viewModel.ejecutar(
                intencion = CiudadesIntencion.Buscar("bue"))
            delay(1.milliseconds)
            assertEquals(
                CiudadesEstado.Resultado(listOf(repositorio.bsAs)),
                viewModel.uiState)
        }
    }

    @Test
    fun test_buscarLaPlata() = runTest {
        launch(Dispatchers.Main){
            viewModel.ejecutar(
                intencion = CiudadesIntencion.Buscar("plat"))
            delay(1.milliseconds)
            assertEquals(
                CiudadesEstado.Resultado(listOf(repositorio.laPlata)),
                viewModel.uiState)
        }
    }

    @Test
    fun test_buscarCiudad_vacio() = runTest(timeout = 3.seconds) {

        launch(Dispatchers.Main){
            viewModel.ejecutar(
                intencion = CiudadesIntencion.Buscar("zzz"))
            delay(1.milliseconds)
            assertEquals(
                CiudadesEstado.Vacio,
                viewModel.uiState)
        }
    }

    @Test
    fun test_buscarCiudad_error() = runTest(timeout = 3.seconds) {

        val factoryError = CiudadesViewModelFactory(repositorioError,router)
        val viewModelError = factoryError.create(CiudadesViewModel::class.java)

        launch(Dispatchers.Main){
            viewModelError.ejecutar(
                intencion = CiudadesIntencion.Buscar("error"))
            delay(1.milliseconds)
            assertEquals(
                CiudadesEstado.Error("Error al cargar ciudades"),
                viewModelError.uiState)
        }
    }

    @Test
    fun test_buscarCiudad_cargando() = runTest(timeout = 3.seconds) {
        val factoryCargando = CiudadesViewModelFactory(repositorioDelay, router)
        val viewModelCargando = factoryCargando.create(CiudadesViewModel::class.java)

        launch(Dispatchers.Main) {
            val job = launch {
                viewModelCargando.ejecutar(CiudadesIntencion.Buscar("cor"))
            }

            delay(10) // Todavía no terminó el delay(100), así que el estado debería estar en Cargando
            assertEquals(CiudadesEstado.Cargando, viewModelCargando.uiState)

            job.join()
        }
    }

    @Test
    fun test_seleccionarCiudad() = runTest(timeout = 3.seconds) {
        val factorySeleccionar = CiudadesViewModelFactory(repositorio, router)
        val viewModelSeleccionar = factorySeleccionar.create(CiudadesViewModel::class.java)
        val ciudadSeleccionada = repositorio.cordoba

        launch(Dispatchers.Main) {
            viewModelSeleccionar.ejecutar(
                intencion = CiudadesIntencion.Seleccionar(ciudadSeleccionada)
            )
            delay(1.milliseconds)

            // Verificamos que la ruta usada por el router fue la correcta
            val rutaEsperada = Ruta.Clima(
                lat = ciudadSeleccionada.lat,
                lon = ciudadSeleccionada.lon,
                nombre = ciudadSeleccionada.name
            )

            assertEquals(rutaEsperada, router.ultimaRuta)
        }
    }

    @Test
    fun test_traerClima() = runTest(timeout = 3.seconds) {
        val cordoba = repositorio.cordoba
        val factoryClima = ClimaViewModelFactory(repositorio,router,cordoba.lat.toFloat(),cordoba.lon.toFloat(),cordoba.name)
        val viewModelClima = factoryClima.create(ClimaViewModel::class.java)

        val climaEsperado = repositorio.traerClima(cordoba.lat.toFloat(), cordoba.lon.toFloat())

        val estadoEsperado = ClimaEstado.Exitoso(
            ciudad = climaEsperado.name,
            temperatura = climaEsperado.main.temp,
            descripcion = climaEsperado.weather.first().description,
            st = climaEsperado.main.feels_like,
            icono = climaEsperado.weather.first().icon,
            climaId = climaEsperado.weather.first().id
        )

        launch(Dispatchers.Main) {
            viewModelClima.ejecutar(ClimaIntencion.actualizarClima)
            delay(1.milliseconds)
            assertEquals(estadoEsperado, viewModelClima.uiState)
        }
    }

    @Test
    fun test_traerClima_error() = runTest(timeout = 3.seconds) {
        val repositorioError = RepositorioMockError()
        val cordoba = repositorio.cordoba
        val factory = ClimaViewModelFactory(repositorioError, router,cordoba.lat.toFloat(),cordoba.lon.toFloat(),cordoba.name)
        val viewModel = factory.create(ClimaViewModel::class.java)

        launch(Dispatchers.Main) {
            viewModel.ejecutar(ClimaIntencion.actualizarClima)
            delay(1.milliseconds)
            assertEquals(
                ClimaEstado.Error("Error al traer clima"),
                viewModel.uiState
            )
        }
    }

    @Test
    fun test_traerClima_cargando() = runTest(timeout = 3.seconds) {
        val repositorioDelay = RepositoryMockDelay()
        val cordoba = repositorioDelay.cordoba
        val factory = ClimaViewModelFactory(repositorioDelay, router, cordoba.lat.toFloat(), cordoba.lon.toFloat(), cordoba.name)
        val viewModel = factory.create(ClimaViewModel::class.java)

        launch(Dispatchers.Main) {
            val job = launch {
                viewModel.ejecutar(ClimaIntencion.actualizarClima)
            }
            delay(10)
            assertEquals(ClimaEstado.Cargando, viewModel.uiState)
            job.join()
        }
    }

    @Test
    fun test_traerClima_vacio() = runTest(timeout = 3.seconds) {
        val cordoba = repositorio.cordoba
        val viewModel = ClimaViewModelFactory(repositorio, router, cordoba.lat.toFloat(), cordoba.lon.toFloat(), cordoba.name)
            .create(ClimaViewModel::class.java)

        assertEquals(ClimaEstado.Vacio, viewModel.uiState)
    }

    @Test
    fun test_traerPronostico_exitoso() = runTest(timeout = 3.seconds) {
        val factory = PronosticoViewModelFactory(repositorio, router, "Cordoba")
        val viewModel = factory.create(PronosticoViewModel::class.java)

        val resultadoEsperado = PronosticoEstado.Exitoso(repositorio.traerPronostico("Cordoba"))

        launch(Dispatchers.Main) {
            viewModel.ejecutar(PronosticoIntencion.actualizarClima)
            delay(1.milliseconds)
            assertEquals(resultadoEsperado, viewModel.uiState)
        }
    }

    @Test
    fun test_traerPronostico_error() = runTest(timeout = 3.seconds) {
        val factory = PronosticoViewModelFactory(repositorioError, router, "Cordoba")
        val viewModel = factory.create(PronosticoViewModel::class.java)

        launch(Dispatchers.Main) {
            viewModel.ejecutar(PronosticoIntencion.actualizarClima)
            delay(1.milliseconds)
            assertEquals(
                PronosticoEstado.Error("Error al traer pronóstico"),
                viewModel.uiState
            )
        }
    }

    @Test
    fun test_traerPronostico_cargando() = runTest(timeout = 3.seconds) {
        val repositorioDelay = RepositoryMockDelay()
        val factory = PronosticoViewModelFactory(repositorioDelay, router, "Cordoba")
        val viewModel = factory.create(PronosticoViewModel::class.java)

        launch(Dispatchers.Main) {
            val job = launch {
                viewModel.ejecutar(PronosticoIntencion.actualizarClima)
            }

            delay(10)
            assertEquals(PronosticoEstado.Cargando, viewModel.uiState)

            job.join()
        }
    }

    @Test
    fun test_traerPronostico_vacio() = runTest(timeout = 3.seconds) {
        val factory = PronosticoViewModelFactory(repositorio, router, "Cordoba")
        val viewModel = factory.create(PronosticoViewModel::class.java)

        assertEquals(PronosticoEstado.Vacio, viewModel.uiState)
    }
}
