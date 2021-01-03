package me.mking.currentconditions.presentation.viewmodels

import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.usecases.DataResult
import java.util.*
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.roundToInt

class CurrentConditionsViewStateMapper @Inject constructor() {

    private companion object {
        const val METERS_TO_MPH_MULTIPLIER = 2.237
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

    fun mapTo(result: DataResult<CurrentWeather>): CurrentConditionsViewState {
        return when (result) {
            is DataResult.Error -> CurrentConditionsViewState.Error
            is DataResult.Success -> CurrentConditionsViewState.Ready(mapCurrentWeather(result.data))
        }
    }

    private fun mapCurrentWeather(currentWeather: CurrentWeather): CurrentWeatherViewState {
        return CurrentWeatherViewState(
            condition = currentWeather.condition.capitalize(Locale.UK),
            temperature = String.format("%s\u00B0c", currentWeather.temperature.roundToInt()),
            windSpeed = String.format(
                "%smph",
                floor(currentWeather.windSpeed * METERS_TO_MPH_MULTIPLIER).toInt()
            ),
            windDirection = mapToWindDirection(currentWeather.windDirection),
            iconUrl = currentWeather.iconUrl
        )
    }

    private fun mapToWindDirection(windDirectionInDegrees: Double): String {
        return DIRECTIONS[(floor((windDirectionInDegrees / 45.0) + 0.5) % 8).toInt()]
    }
}