package com.jbubb.diary

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "diary_settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val FONT_SIZE_KEY = intPreferencesKey("font_size")
        const val DEFAULT_FONT_SIZE = 16
    }

    val fontSizeFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[FONT_SIZE_KEY] ?: DEFAULT_FONT_SIZE
        }

    suspend fun setFontSize(fontSize: Int) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = fontSize
        }
    }
}