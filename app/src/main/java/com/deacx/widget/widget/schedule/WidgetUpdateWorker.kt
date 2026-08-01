package com.deacx.widget.widget.schedule

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deacx.widget.widget.DeacxWidgetProvider
import java.io.IOException

private const val TAG = "WidgetUpdateWorker"

/**
 * Fires once per greeting-period boundary. Pushes fresh content to every
 * placed widget instance, then re-arms the scheduler for the following
 * boundary. If no instances remain, it exits without rescheduling — a
 * belt-and-suspenders backstop alongside onDisabled()'s cancellation.
 */
class WidgetUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val hadWidgets = DeacxWidgetProvider.refreshAll(applicationContext)
            if (hadWidgets) {
                WidgetUpdateScheduler.scheduleNext(applicationContext)
            }
            Result.success()
        } catch (e: IOException) {
            Log.w(TAG, "Transient failure reading preferences, will retry", e)
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected failure, not retrying", e)
            Result.failure()
        }
    }
}
