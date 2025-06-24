package com.example.appweather.repositorymock

import com.example.appweather.repository.models.Ciudad
import com.example.appweather.repository.models.Clima
import com.example.appweather.repository.models.ListForecast
import kotlinx.coroutines.delay

class RepositoryMockDelay : RepositorioMock(){
    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        delay(100) // nos permite ver el estado Cargando en el test
        return super.buscarCiudad(ciudad)
    }
    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        delay(100)
        return super.traerClima(lat, lon)
    }
    override suspend fun traerPronostico(nombre: String): List<ListForecast> {
        delay(100)
        return super.traerPronostico(nombre)
    }
    override suspend fun buscarCiudadPorCoordenadas(lat: Double, lon: Double): List<Ciudad> {
        delay(100)
        return super.buscarCiudadPorCoordenadas(lat, lon)
    }
}