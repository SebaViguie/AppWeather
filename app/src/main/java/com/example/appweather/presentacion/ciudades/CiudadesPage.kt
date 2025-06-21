package com.example.appweather.presentacion.ciudades

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.appweather.router.Router
import com.example.appweather.repository.RepositoryApi
import androidx.compose.ui.platform.LocalContext


@Composable
fun CiudadesPage(
    navHostController: NavHostController
) {
    val context = LocalContext.current.applicationContext

    val viewModel : CiudadesViewModel = viewModel(
        factory = CiudadesViewModelFactory(
            context = context,
            repositorio = RepositoryApi(),
            router = Router(navHostController)
        )
    )
    CiudadesView(
        state = viewModel.uiState,
        onAction = { intencion ->
            viewModel.ejecutar(intencion)
        }
    )
}