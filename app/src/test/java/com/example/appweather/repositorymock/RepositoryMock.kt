package com.example.appweather.repositorymock

import com.example.appweather.repository.IRepository
import com.example.appweather.repository.models.*

open class RepositorioMock : IRepository {

    val cordoba = Ciudad(name = "Cordoba", lat = -31.4, lon = -64.2, country = "AR")
    val bsAs = Ciudad(name = "Buenos Aires", lat = -34.6, lon = -58.4, country = "AR")
    val laPlata = Ciudad(name = "La Plata", lat = -34.9, lon = -57.9, country = "AR")

    val ciudades = listOf(cordoba, bsAs, laPlata)

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        if (ciudad == "error") throw Exception("Error simulado en búsqueda")
        return ciudades.filter { it.name.contains(ciudad, ignoreCase = true) }
    }

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        return Clima(
            name = "Cordoba",
            main = Main(
                temp = 22.5,
                feels_like = 21.0,
                temp_min = 18.0,
                temp_max = 25.3,
                pressure = 1013,
                humidity = 60,
            ),
            weather = listOf(
                Weather(
                    id = 801,
                    main = "Nubes",
                    description = "algo nublado",
                    icon = "02d"
                )
            ),
            base = "mock",
            coord = Coord(lat = lat.toDouble(), lon = lon.toDouble()),
            wind = Wind(speed = 3.2, deg = 180),
            clouds = Clouds(all = 40)
        )
    }

    override suspend fun traerPronostico(nombre: String): List<ListForecast> {
        return listOf(
            ListForecast(
                dt = 1234567890,
                main = MainForecast(
                    temp = 20.0,
                    feels_like = 19.0,
                    temp_min = 18.0,
                    temp_max = 22.0,
                    pressure = 1012,
                    sea_level = 1012,
                    grnd_level = 1000,
                    humidity = 70,
                    temp_kf = 1.5
                ),
                weather = listOf(
                    WeatherForecast(
                        id = 500,
                        main = "Lluvia",
                        description = "lluvia",
                        icon = "10d"
                    )
                )
            )
        )
    }

    override suspend fun buscarCiudadPorCoordenadas(lat: Double, lon: Double): List<Ciudad> {
        TODO("Not yet implemented")
    }
}

class RepositorioMockError : IRepository {

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        throw Exception("Error al cargar ciudades")
    }

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        throw Exception("Error al traer clima")
    }

    override suspend fun traerPronostico(nombre: String): List<ListForecast> {
        throw Exception("Error al traer pronóstico")
    }

    override suspend fun buscarCiudadPorCoordenadas(lat: Double, lon: Double): List<Ciudad> {
        TODO("Not yet implemented")
    }
}
