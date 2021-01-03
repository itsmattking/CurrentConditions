package me.mking.currentconditions.domain.usecases

import me.mking.currentconditions.data.providers.DateTimeProvider
import me.mking.currentconditions.data.repositories.LocalCurrentWeatherRepository
import me.mking.currentconditions.data.repositories.RemoteCurrentWeatherRepository
import me.mking.currentconditions.data.repositories.RemoteRepositoryException
import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import javax.inject.Inject

class GetCachedCurrentWeatherUseCase @Inject constructor(
    private val localCurrentWeatherRepository: LocalCurrentWeatherRepository,
    private val remoteCurrentWeatherRepository: RemoteCurrentWeatherRepository,
    private val dateTimeProvider: DateTimeProvider
) {
    suspend fun execute(currentWeatherInput: CurrentWeatherInput): DataResult<CurrentWeather> {
        var localResult: CurrentWeather = CurrentWeather.Empty
        return try {
            localResult = localCurrentWeatherRepository.getCurrentWeather(currentWeatherInput)
            if (dateTimeProvider.nowInEpochSeconds() - localResult.updated > currentWeatherInput.maxAge) {
                val remoteResult =
                    remoteCurrentWeatherRepository.getCurrentWeather(currentWeatherInput)
                localCurrentWeatherRepository.insertCurrentWeather(remoteResult)
                localResult = remoteResult
            }
            DataResult.Success(localResult)
        } catch (exception: Exception) {
            when (exception) {
                is RemoteRepositoryException -> DataResult.Success(localResult)
                else -> DataResult.Error("Unable to fetch weather")
            }
        }
    }
}

