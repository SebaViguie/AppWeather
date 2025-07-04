package com.example.appweather.presentacion.clima

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.appweather.presentacion.clima.actual.ClimaView
import com.example.appweather.presentacion.clima.actual.ClimaViewModel
import com.example.appweather.presentacion.clima.actual.ClimaViewModelFactory
import com.example.appweather.presentacion.clima.pronostico.PronosticoView
import com.example.appweather.presentacion.clima.pronostico.PronosticoViewModel
import com.example.appweather.presentacion.clima.pronostico.PronosticoViewModelFactory
import com.example.appweather.router.Router
import com.example.appweather.repository.RepositoryApi

@Composable
fun ClimaPage(
    navHostController: NavHostController,
    lat : Float,
    lon : Float,
    nombre: String
){
    val context = LocalContext.current.applicationContext

    val viewModel : ClimaViewModel = viewModel(
        factory = ClimaViewModelFactory(
            repositorio = RepositoryApi(),
            router = Router(navHostController),
            lat = lat,
            lon = lon,
            nombre = nombre,
            context = context
        )
    )
    val pronosticoViewModel : PronosticoViewModel = viewModel(
        factory = PronosticoViewModelFactory(
            repositorio = RepositoryApi(),
            router = Router(navHostController),
            nombre = nombre
        )
    )

    Column {
        ClimaView(
            state = viewModel.uiState,
            onAction = { intencion ->
                viewModel.ejecutar(intencion)
            },
            pronosticoState = pronosticoViewModel.uiState
        )
        PronosticoView(
            state = pronosticoViewModel.uiState,
            onAction = { intencion ->
                pronosticoViewModel.ejecutar(intencion)
            }
        )
    }
}