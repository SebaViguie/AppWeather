package com.example.appweather.presentacion.clima.actual

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.appweather.R
import com.example.appweather.presentacion.clima.pronostico.PronosticoEstado
import com.example.appweather.presentacion.clima.pronostico.PronosticoView
import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import com.example.appweather.repository.models.ListForecast
import com.example.appweather.repository.models.MainForecast
import com.example.appweather.repository.models.WeatherForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClimaView(
    modifier: Modifier = Modifier,
    state : ClimaEstado,
    onAction: (ClimaIntencion)->Unit,
    pronosticoState: PronosticoEstado
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onAction(ClimaIntencion.actualizarClima)
    }

    val fondoRes = when (state) {
        is ClimaEstado.Exitoso -> obtenerFondoPorId(state.climaId)
        else -> R.drawable.main
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(fondoRes),
            contentDescription = "Fondo home",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when(state){
                is ClimaEstado.Error -> TextoCentrado(texto = state.mensaje)
                is ClimaEstado.Exitoso -> ClimaContenido(
                    ciudad = state.ciudad,
                    temperatura = state.temperatura,
                    descripcion = state.descripcion,
                    st = state.st,
                    climaId = state.climaId
                )
                ClimaEstado.Vacio -> TextoCentrado(texto = "Cargando...")
                ClimaEstado.Cargando -> TextoCentrado(texto = "Nada que mostrar")
            }
            Spacer(modifier = Modifier.height(32.dp))
            PronosticoView(
                state = pronosticoState
            ) { }

            if (state is ClimaEstado.Exitoso) {
                val context = LocalContext.current
                Button(
                    onClick = {
                        val mensaje = buildString {
                            append("Clima en ${state.ciudad}\n")
                            append("Temperatura: ${state.temperatura}°\n")
                            append("Descripción: ${state.descripcion}\n")
                            append("Sensación térmica: ${state.st}°\n")

                            if (pronosticoState is PronosticoEstado.Exitoso) {
                                append("\nPronóstico próximas horas:\n")
                                pronosticoState.climas.forEach {
                                    val hora = SimpleDateFormat("HH:mm", Locale.getDefault())
                                        .format(Date(it.dt * 1000L))
                                    append("$hora: ${it.main.temp}°\n")
                                }
                            }
                        }

                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, mensaje)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Compartir clima"))
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Compartir")
                }

                Button(
                    onClick = {
                        onAction(ClimaIntencion.CambiarCiudad)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Cambiar ciudad")
                }
            }
        }
    }

}

@Composable
fun TextoCentrado(texto: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = texto,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ClimaContenido(ciudad: String, temperatura: Double, descripcion: String, st:Double, climaId:Long){
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE5ECFF)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ciudad,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${temperatura}°",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = descripcion.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Sensación térmica: ${st}°",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }
    }
}

@DrawableRes
fun obtenerFondoPorId(id: Long): Int {
    return when (id) {
        in 200L..699L -> R.drawable.lluvia
        in 700L..799L -> R.drawable.nublado
        800L -> R.drawable.soleado
        in 801L..804L -> R.drawable.parcialnublado
        else -> R.drawable.main
    }
}

@Preview (showBackground = true)
@Composable
fun PreviewClimaCargando() {
    ClimaView(
        state = ClimaEstado.Cargando,
        onAction = {},
        pronosticoState = PronosticoEstado.Cargando
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewClimaVacio() {
    ClimaView(
        state = ClimaEstado.Vacio,
        onAction = {},
        pronosticoState = PronosticoEstado.Vacio
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewClimaError() {
    ClimaView(
        state = ClimaEstado.Error("No se pudo obtener el clima"),
        onAction = {},
        pronosticoState = PronosticoEstado.Error("No se pudo obtener el pronóstico")
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewClimaExitosoDespejado() {
    ClimaView(
        state = ClimaEstado.Exitoso(
            ciudad = "Buenos Aires",
            temperatura = 28.0,
            descripcion = "Soleado",
            st = 30.0,
            icono = "01d",
            climaId = 800L
        ),
        onAction = {},
        pronosticoState = PronosticoEstado.Exitoso(
            climas = listOf(
                ListForecast(
                    dt = 1719308400L,
                    main = MainForecast(
                        temp = 28.0,
                        feels_like = 30.0,
                        temp_min = 26.0,
                        temp_max = 29.0,
                        pressure = 1010,
                        sea_level = 1010,
                        grnd_level = 1004,
                        humidity = 40,
                        temp_kf = 0.0
                    ),
                    weather = listOf(
                        WeatherForecast(
                            id = 800,
                            main = "Clear",
                            description = "cielo claro",
                            icon = "01d"
                        )
                    )
                )
            )
        )
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewClimaExitosoParcialNublado() {
    ClimaView(
        state = ClimaEstado.Exitoso(
            ciudad = "Buenos Aires",
            temperatura = 22.0,
            descripcion = "Parcialmente nublado",
            st = 23.5,
            icono = "02d",
            climaId = 802L
        ),
        onAction = {},
        pronosticoState = PronosticoEstado.Exitoso(
            climas = listOf(
                ListForecast(
                    dt = 1719308400L,
                    main = MainForecast(
                        temp = 22.0,
                        feels_like = 23.0,
                        temp_min = 21.0,
                        temp_max = 24.0,
                        pressure = 1011,
                        sea_level = 1011,
                        grnd_level = 1005,
                        humidity = 55,
                        temp_kf = 0.0
                    ),
                    weather = listOf(
                        WeatherForecast(
                            id = 802,
                            main = "Clouds",
                            description = "algo nublado",
                            icon = "02d"
                        )
                    )
                )
            )
        )
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewClimaExitosoNublado() {
    ClimaView(
        state = ClimaEstado.Exitoso(
            ciudad = "Buenos Aires",
            temperatura = 20.0,
            descripcion = "Nublado",
            st = 20.0,
            icono = "03d",
            climaId = 700L
        ),
        onAction = {},
        pronosticoState = PronosticoEstado.Exitoso(
            climas = listOf(
                ListForecast(
                    dt = 1719304800L, // 8:00 AM
                    main = MainForecast(
                        temp = 16.5,
                        feels_like = 16.0,
                        temp_min = 15.0,
                        temp_max = 17.0,
                        pressure = 1012,
                        sea_level = 1012,
                        grnd_level = 1006,
                        humidity = 90,
                        temp_kf = 0.0
                    ),
                    weather = listOf(
                        WeatherForecast(
                            id = 200,
                            main = "Rain",
                            description = "lluvia ligera",
                            icon = "03d"
                        )
                    )
                )
            )
        )
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewClimaExitosoLluvia() {
    ClimaView(
        state = ClimaEstado.Exitoso(
            ciudad = "Buenos Aires",
            temperatura = 17.0,
            descripcion = "Lluvia",
            st = 16.0,
            icono = "10d",
            climaId = 200L
        ),
        onAction = {},
        pronosticoState = PronosticoEstado.Exitoso(
            climas = listOf(
                ListForecast(
                    dt = 1719304800L,
                    main = MainForecast(
                        temp = 16.5,
                        feels_like = 16.0,
                        temp_min = 15.0,
                        temp_max = 17.0,
                        pressure = 1012,
                        sea_level = 1012,
                        grnd_level = 1006,
                        humidity = 90,
                        temp_kf = 0.0
                    ),
                    weather = listOf(
                        WeatherForecast(
                            id = 200,
                            main = "Rain",
                            description = "lluvia ligera",
                            icon = "10d"
                        )
                    )
                )
            )
        )
    )
}
