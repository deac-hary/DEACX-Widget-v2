package com.deacx.widget.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

object LocationHelper {

    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(
        context: Context
    ): Pair<Double, Double>? {

        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            return null
        }

        val client = LocationServices.getFusedLocationProviderClient(context)

        // Try cached location first
        runCatching {
            client.lastLocation.await()
        }.getOrNull()?.let {
            return Pair(it.latitude, it.longitude)
        }

        // Cache is empty -> request a fresh location
        return runCatching {
            client.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await()
        }.getOrNull()?.let {
            Pair(it.latitude, it.longitude)
        }
    }
}
