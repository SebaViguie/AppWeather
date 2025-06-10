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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.appweather.R

@Composable
fun ClimaView(
    modifier: Modifier = Modifier,
    state : ClimaEstado,
    onAction: (ClimaIntencion)->Unit
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
            Spacer(modifier = Modifier.height(100.dp))
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
    Column {
        Text(text = ciudad, style = MaterialTheme.typography.titleMedium)
        Text(text = "${temperatura}°", style = MaterialTheme.typography.titleLarge)
        Text(text = descripcion, style = MaterialTheme.typography.bodyMedium)
        Text(text = "Sensación Térmica: ${st}°", style = MaterialTheme.typography.bodyMedium)
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
