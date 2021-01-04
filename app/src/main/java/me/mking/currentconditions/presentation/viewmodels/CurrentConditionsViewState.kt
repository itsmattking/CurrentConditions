package me.mking.currentconditions.presentation.viewmodels

sealed class CurrentConditionsViewState {
    object Loading : CurrentConditionsViewState()
    data class Ready(
        val currentWeather: CurrentWeatherViewState,
        val isRefreshing: Boolean = false,
        val isOffline: Boolean = false
    ) : CurrentConditionsViewState()

    object Error : CurrentConditionsViewState()
    object LocationNotAvailable : CurrentConditionsViewState()
}

data class CurrentWeatherViewState(
    val location: String,
    val temperature: String,
    val condition: String,
    val windSpeed: String,
    val windDirection: String,
    val iconUrl: String,
    val lastUpdated: String
)