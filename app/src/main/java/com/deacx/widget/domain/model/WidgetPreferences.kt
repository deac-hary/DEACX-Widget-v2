package com.deacx.widget.domain.model

/**
 * Single source of truth for user-configurable widget state.
 * Extend this data class as new customization options are approved —
 * every addition flows through PreferencesRepository automatically.
 */
data class WidgetPreferences(
    val displayText: String = ""
)
