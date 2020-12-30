package me.mking.currentconditions.presentation.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import me.mking.currentconditions.domain.usecases.DataResult
import me.mking.currentconditions.domain.usecases.GetCachedCurrentWeatherUseCase

class CurrentConditionsViewModel @ViewModelInject constructor(
    private val getCachedCurrentWeatherUseCase: GetCachedCurrentWeatherUseCase
) : ViewModel() {

    private val _currentConditions: MutableLiveData<CurrentConditionsViewData> = MutableLiveData()
    val currentConditions: LiveData<CurrentConditionsViewData> = _currentConditions

    fun load() = viewModelScope.launch {
        val result = getCachedCurrentWeatherUseCase.execute(
            CurrentWeatherInput(
                latitude = 51.5,
                longitude = 0.0,
                unitType = CurrentWeatherInput.UnitType.METRIC
            )
        )
        _currentConditions.value = when (result) {
            is DataResult.Error -> CurrentConditionsViewData.Error
            is DataResult.Success -> CurrentConditionsViewData.Ready(result.data)
        }
    }
}

sealed class CurrentConditionsViewData {
    object Loading : CurrentConditionsViewData()
    data class Ready(val currentWeather: CurrentWeather) : CurrentConditionsViewData()
    object Error : CurrentConditionsViewData()
}