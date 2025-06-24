package com.example.appweather.presentacion.clima.actual

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appweather.data.DataStoreManager
import com.example.appweather.repository.IRepository
import com.example.appweather.router.IRouter
import com.example.appweather.router.Ruta
import kotlinx.coroutines.launch

class ClimaViewModel(
    private val context: Context? = null,
    val respositorio: IRepository,
    val router: IRouter,
    val lat : Float,
    val lon : Float,
    val nombre: String
) : ViewModel() {

    var uiState by mutableStateOf<ClimaEstado>(ClimaEstado.Vacio)

    fun ejecutar(intencion: ClimaIntencion){
        when(intencion){
            ClimaIntencion.actualizarClima -> traerClima()
            is ClimaIntencion.CambiarCiudad -> cambiarCiudad()
        }
    }

    fun traerClima() {
        uiState = ClimaEstado.Cargando
        viewModelScope.launch {
            try{
                val clima = respositorio.traerClima(lat = lat, lon = lon)
                uiState = ClimaEstado.Exitoso(
                    ciudad = clima.name ,
                    temperatura = clima.main.temp,
                    descripcion = clima.weather.first().description,
                    st = clima.main.feels_like,
                    icono = clima.weather.first().icon,
                    climaId = clima.weather.first().id
                )
            } catch (exception: Exception){
                uiState = ClimaEstado.Error(exception.localizedMessage ?: "error desconocido")
            }
        }
    }

    fun cambiarCiudad() {
        viewModelScope.launch {
            context?.let {
                DataStoreManager.clearSavedCity(it)
            }
            router.navegar(Ruta.Ciudades)
        }
    }
}

class ClimaViewModelFactory(
    private val context: Context? = null,
    private val repositorio: IRepository,
    private val router: IRouter,
    private val lat: Float,
    private val lon: Float,
    private val nombre: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClimaViewModel::class.java)) {
            return ClimaViewModel(context, repositorio,router,lat,lon,nombre) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}