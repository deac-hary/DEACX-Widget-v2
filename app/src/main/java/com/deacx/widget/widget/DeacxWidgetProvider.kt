package com.deacx.widget.widget

import com.deacx.widget.weather.WeatherUpdater
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.deacx.widget.widget.render.DeacxWidgetRenderer
import com.deacx.widget.widget.schedule.WidgetUpdateScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "DeacxWidgetProvider"

/**
 * Renders greeting + date + custom text and delegates all scheduling to
 * WidgetUpdateScheduler. Rendering reads DataStore and the instance's
 * current size, so every lifecycle callback here runs via goAsync() + a
 * coroutine rather than blocking the broadcast dispatch.
 */
class DeacxWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        runAsync(context) {
    WeatherUpdater.update(context)
    pushUpdate(context, appWidgetManager, appWidgetIds)
    WidgetUpdateScheduler.scheduleNext(context)
}
}

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        // Same single render path as every other trigger — just scoped to
        // the one resized instance, so it picks up the new size immediately
        // instead of waiting for the next scheduled boundary.
        runAsync(context) {
            pushUpdate(context, appWidgetManager, intArrayOf(appWidgetId))
        }
    }

    override fun onEnabled(context: Context) {
        WidgetUpdateScheduler.scheduleNext(context)
    }

    override fun onDisabled(context: Context) {
        WidgetUpdateScheduler.cancel(context)
    }

    private fun runAsync(context: Context, block: suspend () -> Unit) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                block()
            } catch (e: Exception) {
                Log.e(TAG, "Widget update failed", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        /** Pushes fresh content to each given instance, sized to what that
         *  instance currently has room for. */
        suspend fun pushUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            appWidgetIds.forEach { id ->
                val heightDp = appWidgetManager.getAppWidgetOptions(id)
                    .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, Int.MAX_VALUE)
                val views = DeacxWidgetRenderer.buildRemoteViews(context, availableHeightDp = heightDp)
                appWidgetManager.updateAppWidget(id, views)
            }
        }

        /** Looks up placed instances and pushes fresh content. Used by the
         *  scheduled worker and by the config screen's Save action so both
         *  paths share one lookup + push implementation. Returns false if
         *  no instances are currently placed. */
        suspend fun refreshAll(context: Context): Boolean {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(context, DeacxWidgetProvider::class.java)
            )
            if (ids.isEmpty()) return false
            pushUpdate(context, appWidgetManager, ids)
            return true
        }
    }
}
