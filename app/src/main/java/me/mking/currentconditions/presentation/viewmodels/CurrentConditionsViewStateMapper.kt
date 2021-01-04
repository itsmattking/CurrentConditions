package me.mking.currentconditions.presentation.viewmodels

import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.usecases.DataResult
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.roundToInt

class CurrentConditionsViewStateMapper @Inject constructor() {

    private companion object {
        const val METERS_TO_MPH_MULTIPLIER = 2.237
        const val LAST_UPDATED_DATE_FORMAT = "d MMMM yyyy h:mm a"
        val DIRECTIONS = arrayOf(
            "North",
            "North East",
            "East",
            "South East",
            "South",
            "South West",
            "West",
            "North West",
        )
    }

    fun mapTo(result: DataResult<CurrentWeather>, isOffline: Boolean = false): CurrentConditionsViewState {
        return when (result) {
            is DataResult.Error -> CurrentConditionsViewState.Error
            is DataResult.Success -> CurrentConditionsViewState.Ready(
                currentWeather = mapCurrentWeather(result.data),
                isOffline = isOffline
            )
            is DataResult.Partial -> CurrentConditionsViewState.Ready(
                currentWeather = mapCurrentWeather(result.data),
                isRefreshing = true
            )
        }
    }

    private fun mapCurrentWeather(currentWeather: CurrentWeather): CurrentWeatherViewState {
        return CurrentWeatherViewState(
            location = currentWeather.location,
            condition = currentWeather.condition.capitalize(Locale.UK),
            temperature = String.format("%s\u00B0c", currentWeather.temperature.roundToInt()),
            windSpeed = String.format(
                "%smph",
                floor(currentWeather.windSpeed * METERS_TO_MPH_MULTIPLIER).toInt()
            ),
            windDirection = mapToWindDirection(currentWeather.windDirection),
            iconUrl = currentWeather.iconUrl,
            lastUpdated = String.format(
                "Last updated %s",
                SimpleDateFormat(
                    LAST_UPDATED_DATE_FORMAT,
                    Locale.UK
                ).format(currentWeather.updated * 1000)
            )
        )
    }

    private fun mapToWindDirection(windDirectionInDegrees: Double): String {
        return DIRECTIONS[(floor((windDirectionInDegrees / 45.0) + 0.5) % 8).toInt()]
    }
}