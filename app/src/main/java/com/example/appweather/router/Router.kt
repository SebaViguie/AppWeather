package com.example.appweather.router

import android.annotation.SuppressLint
import androidx.navigation.NavHostController

class Router(
    private val navHostController: NavHostController
): IRouter {
    @SuppressLint("DefaultLocale")
    override fun navegar(ruta: Ruta) {
        when(ruta){
            Ruta.Ciudades -> navHostController.navigate(ruta.id)

            is Ruta.Clima -> {
                val route = String.format(format="%s?lat=%f&lon=%f&nombre=%s",ruta.id,ruta.lat,ruta.lon,ruta.nombre)
                navHostController.navigate(route)
            }
        }
    }
}