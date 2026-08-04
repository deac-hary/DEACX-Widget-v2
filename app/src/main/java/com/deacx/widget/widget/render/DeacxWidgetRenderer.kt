package com.deacx.widget.widget.render

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.deacx.widget.R
import com.deacx.widget.data.local.deacxDataStore
import com.deacx.widget.data.local.widgetPreferences
import com.deacx.widget.widget.greeting.GreetingPeriod
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

/**
 * Builds the widget's RemoteViews content for "right now" at a given size.
 * Reads the one DataStore field it needs directly (no Hilt — AppWidgetProvider
 * /Worker can't use constructor injection) via the shared deacxDataStore delegate.
 */
object DeacxWidgetRenderer {

    /** Below this, date and custom text drop out to keep the greeting readable
     *  and unclipped at very small widget sizes. */
    private const val COMPACT_HEIGHT_THRESHOLD_DP = 100

    suspend fun buildRemoteViews(
        context: Context,
        now: LocalDateTime = LocalDateTime.now(),
        availableHeightDp: Int = Int.MAX_VALUE
    ): RemoteViews {
        val preferences = context.deacxDataStore.widgetPreferences.first()
        val customText = preferences.displayText
        val weatherEmoji = preferences.weatherEmoji
        val isCompact = availableHeightDp < COMPACT_HEIGHT_THRESHOLD_DP

        return RemoteViews(context.packageName, R.layout.widget_deacx).apply {
            setTextViewText(R.id.widget_greeting_text, context.getString(greetingTextRes(GreetingPeriod.current(now.toLocalTime()))))

            if (isCompact) {
                setViewVisibility(R.id.widget_custom_text, View.GONE)
                setViewVisibility(R.id.widget_date_text, View.GONE)
            } else {
                setViewVisibility(R.id.widget_date_text, View.VISIBLE)
                val dateText = buildString {
   	        append(now.format(WidgetDateFormat.formatter))

                if (weatherEmoji.isNotBlank()) {
                append(" ")
                append(weatherEmoji)
    }
}

                setTextViewText(
                R.id.widget_date_text,
                dateText
)

                if (customText.isBlank()) {
                    setViewVisibility(R.id.widget_custom_text, View.GONE)
                } else {
                    setTextViewText(R.id.widget_custom_text, customText)
                    setViewVisibility(R.id.widget_custom_text, View.VISIBLE)
                }
            }
        }
    }

    /** Public so the config screen's live preview can resolve the same text. */
    fun greetingTextRes(period: GreetingPeriod): Int = when (period) {
        GreetingPeriod.MORNING -> R.string.greeting_morning
        GreetingPeriod.AFTERNOON -> R.string.greeting_afternoon
        GreetingPeriod.EVENING -> R.string.greeting_evening
        GreetingPeriod.NIGHT, GreetingPeriod.LATE_NIGHT -> R.string.greeting_night
    }
}
