package com.example.appweather.presentacion.ciudades

import com.example.appweather.repository.models.Ciudad

sealed class CiudadesIntencion {
    data class Buscar( val nombre:String ) : CiudadesIntencion()
    data class BuscarPorCoordenadas(val lat: Double, val lon: Double) : CiudadesIntencion()
    data class Seleccionar(val ciudad: Ciudad) : CiudadesIntencion()
}