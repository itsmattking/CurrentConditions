package me.mking.currentconditions.domain.repositories

import me.mking.currentconditions.domain.models.CurrentWeather

interface CurrentWeatherRepository {
    suspend fun getCurrentWeather(currentWeatherInput: CurrentWeatherInput): CurrentWeather
    suspend fun insertCurrentWeather(currentWeather: CurrentWeather)
}

data class CurrentWeatherInput(
    val latitude: Double,
    val longitude: Double,
    val unitType: UnitType
) {
    enum class UnitType(name: String) {
        METRIC("metric"),
        IMPERIAL("imperial")
    }
}