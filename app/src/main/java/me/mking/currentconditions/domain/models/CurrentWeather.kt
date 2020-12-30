package me.mking.currentconditions.domain.models

data class CurrentWeather(
    val location: String,
    val condition: String,
    val temperature: Double,
    val windSpeed: String,
    val windDirection: String,
    val iconUrl: String,
    val updated: Long
) {
    companion object {
        val Empty = CurrentWeather(
            location = "Unknown",
            condition = "Unknown",
            temperature = 0.0,
            windSpeed = "Unknown",
            windDirection = "Unknown",
            iconUrl = "Unknown",
            updated = 0L
        )
    }
}
