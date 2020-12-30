package me.mking.currentconditions.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenWeatherApiResponse(
    @field:Json(name = "weather")
    val weather: List<WeatherResponse>,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "dt")
    val dt: Long,
    @field:Json(name = "main")
    val main: MainResponse,
    @field:Json(name = "wind")
    val wind: WindResponse
)

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "main")
    val main: String,
    @field:Json(name = "description")
    val description: String,
    @field:Json(name = "icon")
    val icon: String
)

@JsonClass(generateAdapter = true)
data class MainResponse(
    @field:Json(name = "temp")
    val temp: Double
)

@JsonClass(generateAdapter = true)
data class WindResponse(
    @field:Json(name = "speed")
    val speed: Double,
    @field:Json(name = "deg")
    val deg: Int
)