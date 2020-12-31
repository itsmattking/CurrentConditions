package me.mking.currentconditions.data.repositories

import me.mking.currentconditions.data.providers.DateTimeProvider
import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import me.mking.currentconditions.domain.repositories.CurrentWeatherRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CachedCurrentWeatherRepository @Inject constructor(
    private val localCurrentWeatherRepository: LocalCurrentWeatherRepository,
    private val remoteCurrentWeatherRepository: RemoteCurrentWeatherRepository,
    private val dateTimeProvider: DateTimeProvider
) : CurrentWeatherRepository {

    override suspend fun getCurrentWeather(currentWeatherInput: CurrentWeatherInput): CurrentWeather {
        var localResult = localCurrentWeatherRepository.getCurrentWeather(currentWeatherInput)
        if (dateTimeProvider.nowInEpochSeconds() - localResult.updated > TimeUnit.DAYS.toSeconds(1)) {
            val remoteResult = remoteCurrentWeatherRepository.getCurrentWeather(currentWeatherInput)
            insertCurrentWeather(remoteResult)
            localResult = remoteResult
        }
        return localResult
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeather) {
        localCurrentWeatherRepository.insertCurrentWeather(currentWeather)
    }
}