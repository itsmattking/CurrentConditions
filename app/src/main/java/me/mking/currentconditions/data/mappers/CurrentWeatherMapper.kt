package me.mking.currentconditions.data.mappers

import me.mking.currentconditions.data.models.CurrentWeatherEntity
import me.mking.currentconditions.data.models.OpenWeatherApiResponse
import me.mking.currentconditions.data.providers.DateTimeProvider
import me.mking.currentconditions.domain.models.CurrentWeather
import javax.inject.Inject

class CurrentWeatherMapper @Inject constructor(
    private val dateTimeProvider: DateTimeProvider
) {
    fun mapTo(openWeatherApiResponse: OpenWeatherApiResponse): CurrentWeather {
        if (openWeatherApiResponse.weather.isEmpty()) {
            return CurrentWeather.Empty
        }
        return CurrentWeather(
            location = openWeatherApiResponse.name,
            condition = openWeatherApiResponse.weather[0].description,
            iconUrl = "https://openweathermap.org/img/w/${openWeatherApiResponse.weather[0].icon}.png",
            windSpeed = openWeatherApiResponse.wind.speed,
            windDirection = openWeatherApiResponse.wind.deg.toDouble(),
            temperature = openWeatherApiResponse.main.temp,
            updated = dateTimeProvider.nowInEpochSeconds()
        )
    }

    fun mapTo(currentWeatherEntity: CurrentWeatherEntity): CurrentWeather {
        return CurrentWeather(
            location = currentWeatherEntity.location,
            condition = currentWeatherEntity.condition,
            temperature = currentWeatherEntity.temperature,
            windSpeed = currentWeatherEntity.windSpeed,
            windDirection = currentWeatherEntity.windDirection,
            iconUrl = currentWeatherEntity.iconUrl,
            updated = currentWeatherEntity.updated
        )
    }
}