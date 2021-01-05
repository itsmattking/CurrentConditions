package me.mking.currentconditions.presentation.viewmodels

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.mking.currentconditions.data.providers.CurrentLocation
import me.mking.currentconditions.data.providers.CurrentLocationProvider
import me.mking.currentconditions.data.providers.NetworkStatusProvider
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import me.mking.currentconditions.domain.usecases.GetCachedCurrentWeatherUseCase
import java.util.concurrent.TimeUnit

class CurrentConditionsViewModel @ViewModelInject constructor(
    private val getCachedCurrentWeatherUseCase: GetCachedCurrentWeatherUseCase,
    private val currentLocationProvider: CurrentLocationProvider,
    private val currentConditionsViewStateMapper: CurrentConditionsViewStateMapper,
    private val networkStatusProvider: NetworkStatusProvider
) : ViewModel() {

    private val _state: MutableLiveData<CurrentConditionsViewState> = MutableLiveData()
    val state: LiveData<CurrentConditionsViewState> = _state

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    fun load() = viewModelScope.launch {
        loadLocationAndWeather()
    }

    fun reload() = viewModelScope.launch {
        loadLocationAndWeather(forceRefresh = true)
    }

    private suspend fun loadLocationAndWeather(forceRefresh: Boolean = false) {
        when (val location = currentLocationProvider.currentLocation()) {
            is CurrentLocation.Available -> loadWeather(location, forceRefresh)
            CurrentLocation.NotAvailable -> _state.value =
                CurrentConditionsViewState.LocationNotAvailable
        }
    }

    private suspend fun loadWeather(
        location: CurrentLocation.Available,
        forceRefresh: Boolean = false
    ) {
        getCachedCurrentWeatherUseCase.executeFlow(
            CurrentWeatherInput(
                latitude = location.latitude,
                longitude = location.longitude,
                unitType = CurrentWeatherInput.UnitType.METRIC,
                maxAge = if (forceRefresh) 0L else TimeUnit.DAYS.toSeconds(1)
            )
        ).collect {
            _state.value = currentConditionsViewStateMapper.mapTo(it, isOffline = !networkStatusProvider.isConnected())
        }
    }
}