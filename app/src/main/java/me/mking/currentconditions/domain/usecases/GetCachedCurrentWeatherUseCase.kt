package me.mking.currentconditions.domain.usecases

import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import me.mking.currentconditions.domain.repositories.CurrentWeatherRepository
import javax.inject.Inject

class GetCachedCurrentWeatherUseCase @Inject constructor(
    private val currentWeatherRepository: CurrentWeatherRepository
) {
    suspend fun execute(currentWeatherInput: CurrentWeatherInput): DataResult<CurrentWeather> {
        return try {
            DataResult.Success(currentWeatherRepository.getCurrentWeather(currentWeatherInput))
        } catch (exception: Exception) {
            DataResult.Error("Failed to fetch current weather")
        }
    }
}

