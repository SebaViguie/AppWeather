package com.example.appweather.presentacion.ciudades

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appweather.data.DataStoreManager
// import com.example.appweather.data.api.RetrofitInstance // <-- Puedes quitar este import si ya no lo usas aquí
import com.example.appweather.repository.IRepository
import com.example.appweather.repository.models.Ciudad
import com.example.appweather.router.IRouter
import com.example.appweather.router.Ruta
import kotlinx.coroutines.launch
// import com.example.appweather.BuildConfig // <-- Puedes quitar este import si ya no usas la API Key directamente aquí
import retrofit2.HttpException // <-- Mantén este si el Repositorio aún puede lanzar HttpExceptions
import java.io.IOException // <-- Mantén este si el Repositorio aún puede lanzar IOExceptions


class CiudadesViewModel(
    private val context: Context? = null,
    val repositorio: IRepository,
    val router: IRouter
) : ViewModel() {

    var uiState by mutableStateOf<CiudadesEstado>(CiudadesEstado.Vacio)
    var ciudades: List<Ciudad> = emptyList()

    fun ejecutar(intencion: CiudadesIntencion) {
        when (intencion) {
            is CiudadesIntencion.Buscar -> buscar(nombre = intencion.nombre)
            is CiudadesIntencion.Seleccionar -> seleccionar(ciudad = intencion.ciudad)
            is CiudadesIntencion.BuscarPorCoordenadas -> buscarPorCoordenadas(
                lat = intencion.lat,
                lon = intencion.lon
            )
        }
    }

    private fun buscar(nombre: String) {
        uiState = CiudadesEstado.Cargando
        viewModelScope.launch {
            try {
                ciudades = repositorio.buscarCiudad(nombre)
                if (ciudades.isEmpty()) {
                    uiState = CiudadesEstado.Vacio
                } else {
                    uiState = CiudadesEstado.Resultado(ciudades)
                }
            } catch (exception: Exception) {
                uiState = CiudadesEstado.Error(exception.message ?: "error desconocido")
            }
        }
    }

    private fun buscarPorCoordenadas(lat: Double, lon: Double) {
        uiState = CiudadesEstado.Cargando
        viewModelScope.launch {
            try {
                val ciudadesEncontradas = repositorio.buscarCiudadPorCoordenadas(lat, lon)

                if (ciudadesEncontradas.isEmpty()) {
                    uiState = CiudadesEstado.Vacio
                } else {
                    ciudades = ciudadesEncontradas
                    uiState = CiudadesEstado.Resultado(ciudadesEncontradas)

                }

            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    401 -> "Error 401: API Key inválida o no activada. Verifica tu clave en OpenWeatherMap."
                    404 -> "Error 404: No se encontró información del clima para esas coordenadas."
                    else -> "Error HTTP: ${e.code()} - ${e.message() ?: "error desconocido"}"
                }
                uiState = CiudadesEstado.Error(errorMessage)
                e.printStackTrace()
            } catch (e: IOException) {
                uiState = CiudadesEstado.Error("Error de conexión a internet. Revisa tu conexión.")
                e.printStackTrace()
            } catch (exception: Exception) {
                uiState = CiudadesEstado.Error(
                    exception.message ?: "Error desconocido al obtener el clima"
                )
                exception.printStackTrace()
            }
        }
    }

    private fun seleccionar(ciudad: Ciudad) {
        viewModelScope.launch {
            context?.let { ctx ->
                DataStoreManager.saveCity(ctx, ciudad)
            }

            val ruta = Ruta.Clima(
                lat = ciudad.lat,
                lon = ciudad.lon,
                nombre = ciudad.name
            )
            router.navegar(ruta)
        }
    }

    init {
        viewModelScope.launch {
            context?.let { ctx ->
                DataStoreManager.selectedCityFlow(ctx).collect { ciudad ->
                    ciudad?.let {
                        val ruta = Ruta.Clima(
                            lat = it.lat,
                            lon = it.lon,
                            nombre = it.name
                        )
                        router.navegar(ruta)
                    }
                }
            }
        }
    }

    class CiudadesViewModelFactory(
        private val context: Context? = null,
        private val repositorio: IRepository,
        private val router: IRouter
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CiudadesViewModel::class.java)) {
                return CiudadesViewModel(context, repositorio, router) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}