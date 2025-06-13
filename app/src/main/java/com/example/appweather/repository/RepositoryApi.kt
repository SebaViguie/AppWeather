package com.example.appweather.repository

import com.example.appweather.repository.models.Ciudad
import com.example.appweather.repository.models.Clima
import com.example.appweather.repository.models.ForecastDTO
import com.example.appweather.repository.models.ListForecast
import kotlinx.serialization.json.Json
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import com.example.appweather.data.model.WeatherResponse


class RepositoryApi : IRepository {

    private val apiKey = "0ee76513a41f5eaa1735576bc7900335"

    private val cliente = HttpClient(){
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        val respuesta = cliente.get("https://api.openweathermap.org/geo/1.0/direct"){
            parameter("q",ciudad)
            parameter("limit",100)
            parameter("appid",apiKey)
            parameter("lang", "es")
        }

        if (respuesta.status == HttpStatusCode.OK){
            val ciudades = respuesta.body<List<Ciudad>>()
            return ciudades
        }else{
            throw Exception()
        }
    }

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        val respuesta = cliente.get("https://api.openweathermap.org/data/2.5/weather"){
            parameter("lat",lat)
            parameter("lon",lon)
            parameter("units","metric")
            parameter("appid",apiKey)
            parameter("lang", "es")
        }
        if (respuesta.status == HttpStatusCode.OK){
            val clima = respuesta.body<Clima>()
            return clima
        }else{
            throw Exception()
        }
    }

    override suspend fun traerPronostico(nombre: String): List<ListForecast> {
        val respuesta = cliente.get("https://api.openweathermap.org/data/2.5/forecast"){
            parameter("q",nombre)
            parameter("units","metric")
            parameter("appid",apiKey)
            parameter("lang", "es")
        }
        if (respuesta.status == HttpStatusCode.OK){
            val forecast = respuesta.body<ForecastDTO>()
            return forecast.list
        }else{
            throw Exception()
        }
    }

    override suspend fun buscarCiudadPorCoordenadas(lat: Double, lon: Double): List<Ciudad> {
        val respuesta = cliente.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("appid", apiKey)
            parameter("lang", "es")
            parameter("units", "metric")
        }

        if (respuesta.status == HttpStatusCode.OK) {
            val weather = respuesta.body<WeatherResponse>()
            return listOf(
                Ciudad(
                    name = weather.name,
                    lat = weather.coord.lat,
                    lon = weather.coord.lon,
                    country = weather.sys.country
                )
            )
        } else {
            throw Exception("No se pudo obtener la ciudad por coordenadas")
        }
    }
}