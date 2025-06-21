package com.example.appweather.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority // Importar Priority

@SuppressLint("MissingPermission")
fun obtenerUbicacionActual(context: Context, onLocationObtained: (Double, Double) -> Unit) {
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context, "Permisos de ubicación no concedidos", Toast.LENGTH_SHORT).show()
        return
    }

    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // Solicitar alta precisión cada 10 segundos
        .setMinUpdateIntervalMillis(5000)
        .build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                onLocationObtained(location.latitude, location.longitude)
                fusedLocationClient.removeLocationUpdates(this)
            } ?: run {
                Toast.makeText(context, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al solicitar actualizaciones de ubicación: ${e.message}", Toast.LENGTH_LONG).show()
        }
}