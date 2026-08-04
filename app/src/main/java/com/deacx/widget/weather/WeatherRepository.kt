package com.deacx.widget.weather

data class WeatherInfo(
    val temperature: Int,
    val emoji: String
)

class WeatherRepository {

    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): WeatherInfo {

        val response = RetrofitClient.api.getCurrentWeather(
            latitude = latitude,
            longitude = longitude
        )

        val current = response.current

        return WeatherInfo(
            temperature = current.temperature_2m.toInt(),
            emoji = weatherEmoji(current.weather_code)
        )
    }

    private fun weatherEmoji(code: Int): String {
        return when (code) {
            0 -> "☀️"
            1 -> "🌤️"
            2 -> "⛅"
            3 -> "☁️"

            45, 48 -> "🌫️"

            51, 53, 55,
            61, 63, 65,
            80, 81, 82 -> "🌧️"

            71, 73, 75,
            77, 85, 86 -> "❄️"

            95, 96, 99 -> "⛈️"

            else -> "🌡️"
        }
    }
}
