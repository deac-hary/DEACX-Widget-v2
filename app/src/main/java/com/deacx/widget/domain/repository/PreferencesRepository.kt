package com.deacx.widget.domain.repository

import com.deacx.widget.domain.model.WidgetPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Domain-layer contract for reading and writing widget preferences.
 * The UI and widget layers depend on this interface only — never on
 * DataStore directly — so the storage mechanism can change later
 * without touching either consumer.
 */
interface PreferencesRepository {
    val preferences: Flow<WidgetPreferences>
    suspend fun setDisplayText(text: String)
}
