package com.deacx.widget

import android.app.Application
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

/**
 * Application root. Annotating with @HiltAndroidApp triggers Hilt's code
 * generation and creates the top-level (SingletonComponent) DI container
 * that every other component in the app hangs off of.
 */
@HiltAndroidApp
class DeacxApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Applies system dynamic color to every Activity's theme automatically
        // (buttons, text field cursor/selection, etc.) on API 31+; no-op below
        // that. The widget's own RemoteViews theming is separate (see
        // values-v31/colors.xml) since RemoteViews can't use this mechanism.
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
