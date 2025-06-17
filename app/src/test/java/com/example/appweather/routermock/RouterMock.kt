package com.example.appweather.routermock

import com.example.appweather.router.IRouter
import com.example.appweather.router.Ruta

class RouterMock : IRouter {
    var ultimaRuta: Ruta? = null
    override fun navegar(ruta: Ruta) {
        ultimaRuta = ruta
        println("navegar a : ${ ruta.id }" )
    }
}