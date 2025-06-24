package com.example.appweather.presentacion.clima.actual

sealed class ClimaIntencion {
    object actualizarClima: ClimaIntencion()
    object CambiarCiudad : ClimaIntencion()
}