package me.mking.currentconditions.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
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

    fun executeFlow(currentWeatherInput: CurrentWeatherInput): Flow<DataResult<CurrentWeather>> {
        return flow {
            var localResult = CurrentWeather.Empty
            try {
                localResult = localCurrentWeatherRepository.getCurrentWeather(currentWeatherInput)
                emit(DataResult.Success(localResult))
                if (dateTimeProvider.nowInEpochSeconds() - localResult.updated > currentWeatherInput.maxAge) {
                    val remoteResult =
                        remoteCurrentWeatherRepository.getCurrentWeather(currentWeatherInput)
                    localCurrentWeatherRepository.insertCurrentWeather(remoteResult)
                    emit(DataResult.Success(remoteResult))
                }
            } catch (exception: Exception) {
                when (exception) {
                    is RemoteRepositoryException -> emit(DataResult.Success(localResult))
                    else -> emit(DataResult.Error<CurrentWeather>("Unable to fetch weather"))
                }
            }
        }.catch {
            emit(DataResult.Error("Some problem occurred"))
        }
    }
}

