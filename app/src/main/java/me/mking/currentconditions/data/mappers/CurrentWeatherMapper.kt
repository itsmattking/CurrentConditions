package me.mking.currentconditions.data.mappers

import me.mking.currentconditions.data.models.CurrentWeatherEntity
import me.mking.currentconditions.data.models.OpenWeatherApiResponse
import me.mking.currentconditions.domain.models.CurrentWeather
import javax.inject.Inject

class CurrentWeatherMapper @Inject constructor() {
    fun mapTo(openWeatherApiResponse: OpenWeatherApiResponse): CurrentWeather {
        if (openWeatherApiResponse.weather.isEmpty()) {
            return CurrentWeather.Empty
        }
        return CurrentWeather(
            location = openWeatherApiResponse.name,
            condition = openWeatherApiResponse.weather[0].description,
            iconUrl = "https://openweathermap.org/img/w/${openWeatherApiResponse.weather[0].icon}.png",
            windSpeed = openWeatherApiResponse.wind.speed.toString(),
            windDirection = openWeatherApiResponse.wind.deg.toString(),
            temperature = openWeatherApiResponse.main.temp,
            updated = openWeatherApiResponse.dt
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