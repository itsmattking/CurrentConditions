package me.mking.currentconditions.data.repositories

import me.mking.currentconditions.data.mappers.CurrentWeatherMapper
import me.mking.currentconditions.data.services.OpenWeatherApiService
import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import me.mking.currentconditions.domain.repositories.CurrentWeatherRepository
import okio.IOException
import javax.inject.Inject
import javax.inject.Named

class RemoteCurrentWeatherRepository @Inject constructor(
    private val openWeatherApiService: OpenWeatherApiService,
    @Named("OpenWeatherAppId") private val openWeatherAppId: String,
    private val currentWeatherMapper: CurrentWeatherMapper
) : CurrentWeatherRepository {
    override suspend fun getCurrentWeather(currentWeatherInput: CurrentWeatherInput): CurrentWeather {
        try {
            val result = openWeatherApiService.weatherByLatLon(
                lat = currentWeatherInput.latitude,
                lon = currentWeatherInput.longitude,
                appId = openWeatherAppId,
                units = currentWeatherInput.unitType.unitName
            )
            return currentWeatherMapper.mapTo(result)
        } catch (exception: Exception) {
            when (exception) {
                is IOException -> throw RemoteRepositoryException(
                    exception.message ?: "Unable to fetch remote data"
                )
                else -> throw exception
            }
        }
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeather) = Unit
}

class RemoteRepositoryException(msg: String) : Exception(msg)