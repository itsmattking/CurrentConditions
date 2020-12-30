package me.mking.currentconditions.data.mappers

import me.mking.currentconditions.data.models.CurrentWeatherEntity
import me.mking.currentconditions.domain.models.CurrentWeather
import javax.inject.Inject

class CurrentWeatherEntityMapper @Inject constructor() {
    fun mapTo(currentWeather: CurrentWeather): CurrentWeatherEntity {
        return CurrentWeatherEntity(
            location = currentWeather.location,
            condition = currentWeather.condition,
            temperature = currentWeather.temperature,
            windSpeed = currentWeather.windSpeed,
            windDirection = currentWeather.windDirection,
            iconUrl = currentWeather.iconUrl,
            updated = currentWeather.updated
        )
    }
}