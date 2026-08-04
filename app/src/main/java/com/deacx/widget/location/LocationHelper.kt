package com.deacx.widget.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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

        val client = LocationServices
            .getFusedLocationProviderClient(context)

        return suspendCancellableCoroutine { continuation ->

            client.lastLocation
                .addOnSuccessListener { location ->
                    if (location == null) {
                        continuation.resume(null)
                    } else {
                        continuation.resume(
                            Pair(
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }
}
