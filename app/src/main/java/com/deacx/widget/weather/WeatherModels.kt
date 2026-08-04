package com.deacx.widget.weather

data class WeatherResponse(
    val current: CurrentWeather
)

data class CurrentWeather(
    val temperature_2m: Double,
    val weather_code: Int
)
