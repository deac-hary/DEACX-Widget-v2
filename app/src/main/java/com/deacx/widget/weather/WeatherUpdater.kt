package com.deacx.widget.weather

import android.content.Context
import com.deacx.widget.data.local.deacxDataStore
import com.deacx.widget.data.local.setWeatherEmoji
import com.deacx.widget.location.LocationHelper

object WeatherUpdater {

    suspend fun update(context: Context) {

        val location = LocationHelper.getLastLocation(context) ?: return

        val repository = WeatherRepository()

        runCatching {
            repository.getWeather(
                latitude = location.first,
                longitude = location.second
            )
        }.onSuccess { weather ->
            context.deacxDataStore.setWeatherEmoji(weather.emoji)
        }
    }
}
