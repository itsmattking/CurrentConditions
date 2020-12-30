package me.mking.currentconditions.data.repositories

import me.mking.currentconditions.data.databases.CurrentWeatherDao
import me.mking.currentconditions.data.mappers.CurrentWeatherEntityMapper
import me.mking.currentconditions.data.mappers.CurrentWeatherMapper
import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import me.mking.currentconditions.domain.repositories.CurrentWeatherRepository
import javax.inject.Inject

class LocalCurrentWeatherRepository @Inject constructor(
    private val currentWeatherDao: CurrentWeatherDao,
    private val currentWeatherEntityMapper: CurrentWeatherEntityMapper,
    private val currentWeatherMapper: CurrentWeatherMapper
) : CurrentWeatherRepository {

    override suspend fun getCurrentWeather(currentWeatherInput: CurrentWeatherInput): CurrentWeather {
        val result = currentWeatherDao.getCurrentWeather()
        if (result.isEmpty()) {
            return CurrentWeather.Empty
        }
        return currentWeatherMapper.mapTo(result.first())
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeather) {
        val id = currentWeatherDao.insertCurrentWeather(
            currentWeatherEntityMapper.mapTo(currentWeather)
        )
        currentWeatherDao.clearAllExcept(id)
    }
}