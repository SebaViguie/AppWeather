package com.example.appweather.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.appweather.repository.models.Ciudad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object DataStoreManager {
    private val Context.dataStore by preferencesDataStore("config")

    private val SELECTED_CITY = stringPreferencesKey("selected_city")

    suspend fun saveCity(context: Context, ciudad: Ciudad) {
        val json = Json.encodeToString(ciudad)
        context.dataStore.edit { preferences ->
            preferences[SELECTED_CITY] = json
        }
    }

    val selectedCityFlow: (Context) -> Flow<Ciudad?> = { context ->
        context.dataStore.data
            .map { preferences ->
                preferences[SELECTED_CITY]?.let {
                    runCatching { Json.decodeFromString<Ciudad>(it) }.getOrNull()
                }
            }
    }
}