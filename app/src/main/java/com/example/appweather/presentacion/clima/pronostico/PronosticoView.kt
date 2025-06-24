package com.example.appweather.presentacion.clima.pronostico

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.appweather.repository.models.ListForecast
import com.example.appweather.repository.models.MainForecast
import com.example.appweather.repository.models.WeatherForecast
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries

@Composable
fun PronosticoView(
    modifier: Modifier = Modifier,
    state : PronosticoEstado,
    onAction: (PronosticoIntencion)->Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onAction(PronosticoIntencion.actualizarClima)
    }
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(state){
            is PronosticoEstado.Error -> ErrorView(mensaje = state.mensaje)
            is PronosticoEstado.Exitoso -> PronosticoContenido(state.climas)
            PronosticoEstado.Vacio -> LoadingView()
            PronosticoEstado.Cargando -> EmptyView()
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun EmptyView(){
    Text(text = "No hay nada que mostrar")
}

@Composable
fun LoadingView(){
    Text(text = "Cargando")
}

@Composable
fun ErrorView(mensaje: String){
    Text(text = mensaje)
}

@Composable
fun PronosticoContenido(climas: List<ListForecast>){
    val modelProducer = remember(climas) { CartesianChartModelProducer() }
    val temperaturas = climas.map { it.main.temp.toFloat() }

    LaunchedEffect(temperaturas) {
        modelProducer.runTransaction {
            columnSeries { series(temperaturas) }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3EFFF)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            if (temperaturas.isNotEmpty()) {
                Text(
                    "Próximos días",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleMedium
                )
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom(),
                    ),
                    modelProducer = modelProducer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(8.dp)
                )
            } else {
                Text("No hay datos de temperatura")
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun PreviewPronosticoCargando() {
    PronosticoView(
        state = PronosticoEstado.Cargando,
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPronosticoVacio() {
    PronosticoView(
        state = PronosticoEstado.Vacio,
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPronosticoError() {
    PronosticoView(
        state = PronosticoEstado.Error("Error al obtener el pronóstico"),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPronosticoExitoso() {
    val fakeForecasts = listOf(
        ListForecast(
            dt = 1L,
            main = MainForecast(
                temp = 23.5,
                feels_like = 25.0,
                temp_min = 21.0,
                temp_max = 26.0,
                pressure = 1013,
                sea_level = 1013,
                grnd_level = 1009,
                humidity = 70,
                temp_kf = 0.5
            ),
            weather = listOf(
                WeatherForecast(
                    id = 800L,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            )
        ),
        ListForecast(
            dt = 2L,
            main = MainForecast(
                temp = 20.0,
                feels_like = 21.0,
                temp_min = 18.0,
                temp_max = 22.0,
                pressure = 1010,
                sea_level = 1010,
                grnd_level = 1006,
                humidity = 75,
                temp_kf = 0.5
            ),
            weather = listOf(
                WeatherForecast(
                    id = 500L,
                    main = "Rain",
                    description = "light rain",
                    icon = "10d"
                )
            )
        )
    )

    PronosticoView(
        state = PronosticoEstado.Exitoso(climas = fakeForecasts),
        onAction = {}
    )
}