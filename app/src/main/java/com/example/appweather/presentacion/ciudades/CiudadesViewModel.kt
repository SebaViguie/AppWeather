package com.example.appweather.presentacion.ciudades

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appweather.data.DataStoreManager
import com.example.appweather.data.api.RetrofitInstance
import com.example.appweather.repository.IRepository
import com.example.appweather.repository.models.Ciudad
import com.example.appweather.router.IRouter
import com.example.appweather.router.Ruta
import kotlinx.coroutines.launch

class CiudadesViewModel(
    private val context: Context,
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
                val respuesta = RetrofitInstance.api.getCurrentWeather(
                    lat = lat,
                    lon = lon,
                    apiKey = "API_KEY",
                    units = "metric",
                    lang = "es"
                )

                val ciudad = Ciudad(
                    name = respuesta.name,
                    lat = respuesta.coord.lat,
                    lon = respuesta.coord.lon,
                    country = respuesta.sys.country
                )

                ciudades = listOf(ciudad)
                uiState = CiudadesEstado.Resultado(ciudades)

            } catch (exception: Exception) {
                uiState = CiudadesEstado.Error(exception.message ?: "error desconocido")
            }
        }
    }

    private fun seleccionar(ciudad: Ciudad) {
        viewModelScope.launch {
            DataStoreManager.saveCity(context, ciudad)

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
            DataStoreManager.selectedCityFlow(context).collect { ciudad ->
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
    private val context: Context,
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
