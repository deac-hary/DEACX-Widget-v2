package com.deacx.widget.widget.schedule

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.deacx.widget.widget.greeting.GreetingPeriod
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

private const val TAG = "WidgetUpdateScheduler"

/**
 * Schedules exactly one deferred update for the next greeting-period
 * boundary instead of polling. WorkManager (not AlarmManager) is used
 * deliberately: this update is never time-critical, so letting the OS
 * batch it with other pending work under Doze is strictly better for
 * battery than forcing an exact wake-up would be.
 */
object WidgetUpdateScheduler {

    private const val UNIQUE_WORK_NAME = "deacx_widget_update"

    fun scheduleNext(context: Context, now: LocalDateTime = LocalDateTime.now()) {
        try {
            val nextBoundary = GreetingPeriod.nextBoundary(now)
            val delay = Duration.between(now, nextBoundary)

            val request = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        } catch (e: Exception) {
            // A missed schedule isn't fatal — the widget just goes stale
            // until the next onUpdate/onEnabled/Save re-arms it.
            Log.e(TAG, "Failed to schedule next widget update", e)
        }
    }

    fun cancel(context: Context) {
        try {
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel scheduled widget update", e)
        }
    }
}
