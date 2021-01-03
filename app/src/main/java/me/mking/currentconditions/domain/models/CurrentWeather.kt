package me.mking.currentconditions.domain.models

data class CurrentWeather(
    val location: String,
    val condition: String,
    val temperature: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val iconUrl: String,
    val updated: Long
) {
    companion object {
        val Empty = CurrentWeather(
            location = "Unknown",
            condition = "Unknown",
            temperature = 0.0,
            windSpeed = 0.0,
            windDirection = 0.0,
            iconUrl = "Unknown",
            updated = 0L
        )
    }
}
