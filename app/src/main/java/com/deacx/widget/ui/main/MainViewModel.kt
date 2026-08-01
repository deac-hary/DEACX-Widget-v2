package com.deacx.widget.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deacx.widget.domain.repository.PreferencesRepository
import com.deacx.widget.widget.DeacxWidgetProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds a local draft of the display text (drives the live preview
 * instantly) separate from the persisted value, so the real widget only
 * updates when Save is tapped rather than on every keystroke.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _draftText = MutableStateFlow("")
    val draftText: StateFlow<String> = _draftText.asStateFlow()

    private val _isSaved = MutableStateFlow(true)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _saveError = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saveError: SharedFlow<Unit> = _saveError.asSharedFlow()

    init {
        viewModelScope.launch {
            _draftText.value = preferencesRepository.preferences.first().displayText
        }
    }

    fun onDraftTextChanged(text: String) {
        _draftText.value = text
        _isSaved.value = false
    }

    /** Clears the draft; still requires Save to persist, same as any other edit. */
    fun resetToDefault() {
        onDraftTextChanged("")
    }

    fun save() {
        viewModelScope.launch {
            try {
                preferencesRepository.setDisplayText(_draftText.value)
                DeacxWidgetProvider.refreshAll(appContext)
                _isSaved.value = true
            } catch (e: Exception) {
                _saveError.emit(Unit)
            }
        }
    }
}
