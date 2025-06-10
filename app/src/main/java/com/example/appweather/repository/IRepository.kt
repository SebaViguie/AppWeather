package com.example.appweather.repository

import com.example.appweather.repository.models.Ciudad
import com.example.appweather.repository.models.Clima
import com.example.appweather.repository.models.ListForecast

interface IRepository {
    suspend fun buscarCiudad(ciudad: String): List<Ciudad>
    suspend fun traerClima(lat: Float, lon: Float) : Clima
    suspend fun traerPronostico(nombre: String) : List<ListForecast>
}