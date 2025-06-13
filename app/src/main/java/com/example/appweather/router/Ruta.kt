package com.example.appweather.router

sealed class Ruta(val id: String) {
    data object Ciudades: Ruta("ciudades")
    data class Clima(val lat: Double, val lon: Double, val nombre:String): Ruta("clima")
}