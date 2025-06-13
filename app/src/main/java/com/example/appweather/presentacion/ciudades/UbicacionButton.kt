package com.example.appweather.presentacion.ciudades

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.appweather.util.obtenerUbicacion

@Composable
fun UbicacionButton(
    modifier: Modifier = Modifier,
    onLocationObtained: (Double, Double) -> Unit,
    iconTint: Color = Color(0xFF3F51B5)
) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
            if (granted) {
                obtenerUbicacion(context, onLocationObtained)
            } else {
                Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    IconButton(
        onClick = {
            if (hasPermission) {
                obtenerUbicacion(context, onLocationObtained)
            } else {
                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Obtener ubicación",
            modifier = Modifier.size(48.dp),
            tint = iconTint

        )
    }
}