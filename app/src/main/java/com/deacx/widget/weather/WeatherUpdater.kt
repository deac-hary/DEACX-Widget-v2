package com.deacx.widget.weather

import android.content.Context
import android.util.Log
import com.deacx.widget.data.local.deacxDataStore
import com.deacx.widget.data.local.setWeatherEmoji
import com.deacx.widget.location.LocationHelper

object WeatherUpdater {

    private const val TAG = "WeatherUpdater"

    suspend fun update(context: Context) {

        Log.d(TAG, "Weather update started")

        val location = LocationHelper.getLastLocation(context)

        if (location == null) {
            Log.e(TAG, "Location is NULL")
            return
        }

        Log.d(TAG, "Location = ${location.first}, ${location.second}")

        val repository = WeatherRepository()

        runCatching {
            repository.getWeather(
                latitude = location.first,
                longitude = location.second
            )
        }.onSuccess { weather ->

            Log.d(TAG, "Weather emoji = ${weather.emoji}")

            context.deacxDataStore.setWeatherEmoji(weather.emoji)

            Log.d(TAG, "Emoji saved successfully")
        }.onFailure { e ->

            Log.e(TAG, "Weather API failed", e)
        }
    }
}
