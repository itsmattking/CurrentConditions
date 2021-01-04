package me.mking.currentconditions.data.mappers

import me.mking.currentconditions.data.models.*
import me.mking.currentconditions.domain.models.CurrentWeather
import java.util.*

object TestData {
    val SOME_NOW_IN_EPOCH_SECONDS = Calendar.getInstance().apply {
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.DAY_OF_MONTH, 4)
        set(Calendar.YEAR, 2021)
        set(Calendar.HOUR, 10)
        set(Calendar.MINUTE, 21)
        set(Calendar.SECOND, 7)
    }.timeInMillis / 1000 // 1609755667
    val SOME_WEATHER_RESPONSE = WeatherResponse(
        id = "1",
        main = "",
        description = "Cloudy",
        icon = "01n"
    )
    val SOME_MAIN_RESPONSE = MainResponse(
        temp = 4.5
    )
    val SOME_WIND_RESPONSE = WindResponse(
        speed = 2.3,
        deg = 14
    )
    val SOME_OPEN_WEATHER_API_RESPONSE = OpenWeatherApiResponse(
        weather = listOf(SOME_WEATHER_RESPONSE),
        main = SOME_MAIN_RESPONSE,
        wind = SOME_WIND_RESPONSE,
        dt = SOME_NOW_IN_EPOCH_SECONDS,
        name = "London"
    )
    val SOME_CURRENT_WEATHER_ENTITY = CurrentWeatherEntity(
        id = 1,
        location = "London",
        condition = "Cloudy",
        temperature = 4.5,
        windSpeed = 2.3,
        windDirection = 14.0,
        updated = SOME_NOW_IN_EPOCH_SECONDS,
        iconUrl = "https://openweathermap.org/img/w/01n.png"
    )
    val SOME_CURRENT_WEATHER = CurrentWeather(
        location = "London",
        condition = "Cloudy",
        temperature = 4.5,
        windSpeed = 2.3,
        windDirection = 12.4,
        iconUrl = "https://openweathermap.org/img/w/01n.png",
        updated = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 4)
            set(Calendar.YEAR, 2021)
            set(Calendar.HOUR, 10)
            set(Calendar.MINUTE, 21)
            set(Calendar.SECOND, 7)
        }.timeInMillis / 1000 // 1609755667
    )
}