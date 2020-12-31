package me.mking.currentconditions.presentation.viewmodels

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.mking.currentconditions.data.providers.CurrentLocation
import me.mking.currentconditions.data.providers.CurrentLocationProvider
import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import me.mking.currentconditions.domain.usecases.DataResult
import me.mking.currentconditions.domain.usecases.GetCachedCurrentWeatherUseCase

class CurrentConditionsViewModel @ViewModelInject constructor(
    private val getCachedCurrentWeatherUseCase: GetCachedCurrentWeatherUseCase,
    private val currentLocationProvider: CurrentLocationProvider
) : ViewModel() {

    private val _state: MutableLiveData<CurrentConditionsViewState> = MutableLiveData()
    val state: LiveData<CurrentConditionsViewState> = _state

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    fun load() = viewModelScope.launch {
        _state.value = CurrentConditionsViewState.Loading
        when (val location = currentLocationProvider.currentLocation()) {
            is CurrentLocation.Available -> loadWeather(location)
            CurrentLocation.NotAvailable -> _state.value =
                CurrentConditionsViewState.LocationNotAvailable
        }
    }

    private suspend fun loadWeather(location: CurrentLocation.Available) {
        val result = getCachedCurrentWeatherUseCase.execute(
            CurrentWeatherInput(
                latitude = location.latitude,
                longitude = location.longitude,
                unitType = CurrentWeatherInput.UnitType.METRIC
            )
        )
        _state.value = when (result) {
            is DataResult.Error -> CurrentConditionsViewState.Error
            is DataResult.Success -> CurrentConditionsViewState.Ready(result.data)
        }
    }
}

sealed class CurrentConditionsViewState {
    object Loading : CurrentConditionsViewState()
    data class Ready(val currentWeather: CurrentWeather) : CurrentConditionsViewState()
    object Error : CurrentConditionsViewState()
    object LocationNotAvailable : CurrentConditionsViewState()
}