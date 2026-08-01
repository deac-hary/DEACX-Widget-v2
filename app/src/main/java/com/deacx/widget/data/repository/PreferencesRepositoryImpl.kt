package com.deacx.widget.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.deacx.widget.data.local.setDisplayText
import com.deacx.widget.data.local.widgetPreferences
import com.deacx.widget.domain.model.WidgetPreferences
import com.deacx.widget.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    override val preferences: Flow<WidgetPreferences> = dataStore.widgetPreferences

    override suspend fun setDisplayText(text: String) {
        dataStore.setDisplayText(text)
    }
}
