package com.deacx.widget.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.deacx.widget.domain.model.WidgetPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val PREFERENCES_DATASTORE_NAME = "deacx_preferences"
private val KEY_DISPLAY_TEXT = stringPreferencesKey("display_text")
private val KEY_WEATHER_EMOJI = stringPreferencesKey("weather_emoji")
/**
 * The single Context.dataStore delegate for the whole app. DataStore
 * throws if two delegates target the same file, so every consumer —
 * Hilt-injected (PreferencesRepositoryImpl) or not (widget rendering,
 * which can't use constructor injection) — reads through this one
 * property and the mapper below instead of declaring their own.
 */
val Context.deacxDataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_DATASTORE_NAME
)

/**
 * Read path shared by the repository and the widget renderer: a corrupted
 * or momentarily unreadable prefs file falls back to defaults instead of
 * crashing the caller. Any other exception is a real bug and still throws.
 */
val DataStore<Preferences>.widgetPreferences: Flow<WidgetPreferences>
    get() = data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it.toWidgetPreferences() }

fun Preferences.toWidgetPreferences(): WidgetPreferences =
    WidgetPreferences(
        displayText = this[KEY_DISPLAY_TEXT] ?: "",
        weatherEmoji = this[KEY_WEATHER_EMOJI] ?: ""
    )

suspend fun DataStore<Preferences>.setDisplayText(text: String) {
    edit { it[KEY_DISPLAY_TEXT] = text }
}
suspend fun DataStore<Preferences>.setWeatherEmoji(emoji: String) {
    edit { it[KEY_WEATHER_EMOJI] = emoji }
}
