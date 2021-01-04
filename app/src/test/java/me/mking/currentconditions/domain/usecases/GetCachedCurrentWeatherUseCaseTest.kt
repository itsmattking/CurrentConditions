package me.mking.currentconditions.domain.usecases

import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import me.mking.currentconditions.data.TestData
import me.mking.currentconditions.data.providers.DateTimeProvider
import me.mking.currentconditions.data.repositories.LocalCurrentWeatherRepository
import me.mking.currentconditions.data.repositories.RemoteCurrentWeatherRepository
import me.mking.currentconditions.data.repositories.RemoteRepositoryException
import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class GetCachedCurrentWeatherUseCaseTest {

    private companion object {
        val SOME_CURRENT_DATE_TIME = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 5)
            set(Calendar.YEAR, 2021)
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 38)
            set(Calendar.SECOND, 22)
        }.timeInMillis / 1000
        val SOME_CURRENT_WEATHER_INPUT = CurrentWeatherInput(
            latitude = 51.5,
            longitude = 0.1,
            unitType = CurrentWeatherInput.UnitType.METRIC,
            maxAge = TimeUnit.MINUTES.toSeconds(10)
        )
        val SOME_CURRENT_WEATHER_NOT_PAST_AGE = TestData.SOME_CURRENT_WEATHER.copy(
            updated = SOME_CURRENT_DATE_TIME - TimeUnit.MINUTES.toSeconds(5)
        )
        val SOME_CURRENT_WEATHER_PAST_AGE = TestData.SOME_CURRENT_WEATHER.copy(
            updated = SOME_CURRENT_DATE_TIME - TimeUnit.MINUTES.toSeconds(15)
        )
    }

    private val mockLocalCurrentWeatherRepository: LocalCurrentWeatherRepository = mockk {
        coEvery { getCurrentWeather(any()) } returns CurrentWeather.Empty
        coEvery { insertCurrentWeather(any()) } returns Unit
    }

    private val mockRemoteCurrentWeatherRepository: RemoteCurrentWeatherRepository = mockk {
        coEvery { getCurrentWeather(any()) } returns TestData.SOME_CURRENT_WEATHER
    }

    private val mockDateTimeProvider: DateTimeProvider = mockk {
        every { nowInEpochSeconds() } returns SOME_CURRENT_DATE_TIME
    }

    private val subject = GetCachedCurrentWeatherUseCase(
        localCurrentWeatherRepository = mockLocalCurrentWeatherRepository,
        remoteCurrentWeatherRepository = mockRemoteCurrentWeatherRepository,
        mockDateTimeProvider
    )

    @Test
    fun givenSubjectAndLocalRepositoryReturnsEmpty_whenExecuteFlow_thenRemoteRepositoryIsCalled() =
        runBlocking {
            val result = subject.executeFlow(SOME_CURRENT_WEATHER_INPUT).toList()
            coVerify {
                mockRemoteCurrentWeatherRepository.getCurrentWeather(
                    SOME_CURRENT_WEATHER_INPUT
                )
            }
            coVerify { mockLocalCurrentWeatherRepository.insertCurrentWeather(TestData.SOME_CURRENT_WEATHER) }
            Truth.assertThat(result.size).isEqualTo(2)
            Truth.assertThat(result[0]).isInstanceOf(DataResult.Partial::class.java)
            Truth.assertThat((result[0] as DataResult.Partial).data)
                .isInstanceOf(CurrentWeather.Empty::class.java)
            Truth.assertThat(result[1]).isInstanceOf(DataResult.Success::class.java)
            Truth.assertThat((result[1] as DataResult.Success).data)
                .isEqualTo(TestData.SOME_CURRENT_WEATHER)
        }

    @Test
    fun givenSubjectAndLocalRepositoryReturnsResultExceedingAge_whenExecuteFlow_thenRemoteRepositoryIsCalled() =
        runBlocking {
            coEvery { mockLocalCurrentWeatherRepository.getCurrentWeather(any()) } returns SOME_CURRENT_WEATHER_PAST_AGE
            val result = subject.executeFlow(SOME_CURRENT_WEATHER_INPUT).toList()
            coVerify {
                mockRemoteCurrentWeatherRepository.getCurrentWeather(
                    SOME_CURRENT_WEATHER_INPUT
                )
            }
            coVerify { mockLocalCurrentWeatherRepository.insertCurrentWeather(TestData.SOME_CURRENT_WEATHER) }
            Truth.assertThat(result.size).isEqualTo(2)
            Truth.assertThat(result[0]).isInstanceOf(DataResult.Partial::class.java)
            Truth.assertThat((result[0] as DataResult.Partial).data)
                .isEqualTo(SOME_CURRENT_WEATHER_PAST_AGE)
            Truth.assertThat(result[1]).isInstanceOf(DataResult.Success::class.java)
            Truth.assertThat((result[1] as DataResult.Success).data)
                .isEqualTo(TestData.SOME_CURRENT_WEATHER)
        }

    @Test
    fun givenSubjectAndLocalRepositoryReturnsResultNotExceedingAge_whenExecuteFlow_thenRemoteRepositoryIsNotCalled() =
        runBlocking {
            coEvery { mockLocalCurrentWeatherRepository.getCurrentWeather(any()) } returns SOME_CURRENT_WEATHER_NOT_PAST_AGE
            val result = subject.executeFlow(SOME_CURRENT_WEATHER_INPUT).toList()
            coVerify(exactly = 0) {
                mockRemoteCurrentWeatherRepository.getCurrentWeather(
                    SOME_CURRENT_WEATHER_INPUT
                )
            }
            coVerify(exactly = 0) { mockLocalCurrentWeatherRepository.insertCurrentWeather(TestData.SOME_CURRENT_WEATHER) }
            Truth.assertThat(result.size).isEqualTo(1)
            Truth.assertThat(result[0]).isInstanceOf(DataResult.Success::class.java)
            Truth.assertThat((result[0] as DataResult.Success).data)
                .isEqualTo(SOME_CURRENT_WEATHER_NOT_PAST_AGE)
        }

    @Test
    fun givenSubjectAndRemoteRepositoryThrowsRemoteException_whenExecuteFlow_thenResultIsLocalResultSuccess() =
        runBlocking {
            coEvery { mockLocalCurrentWeatherRepository.getCurrentWeather(any()) } returns SOME_CURRENT_WEATHER_PAST_AGE
            coEvery { mockRemoteCurrentWeatherRepository.getCurrentWeather(any()) } throws RemoteRepositoryException(
                "Something happened"
            )
            val result = subject.executeFlow(SOME_CURRENT_WEATHER_INPUT).toList()
            Truth.assertThat(result.size).isEqualTo(2)
            Truth.assertThat(result[0]).isInstanceOf(DataResult.Partial::class.java)
            Truth.assertThat((result[0] as DataResult.Partial).data)
                .isEqualTo(SOME_CURRENT_WEATHER_PAST_AGE)
            Truth.assertThat(result[1]).isInstanceOf(DataResult.Success::class.java)
            Truth.assertThat((result[1] as DataResult.Success).data)
                .isEqualTo(SOME_CURRENT_WEATHER_PAST_AGE)
        }

    @Test
    fun givenSubjectAndRemoteRepositoryThrowsOtherException_whenExecuteFlow_thenResultIsError() =
        runBlocking {
            coEvery { mockLocalCurrentWeatherRepository.getCurrentWeather(any()) } returns SOME_CURRENT_WEATHER_PAST_AGE
            coEvery { mockRemoteCurrentWeatherRepository.getCurrentWeather(any()) } throws NullPointerException()
            val result = subject.executeFlow(SOME_CURRENT_WEATHER_INPUT).toList()
            Truth.assertThat(result.size).isEqualTo(2)
            Truth.assertThat(result[0]).isInstanceOf(DataResult.Partial::class.java)
            Truth.assertThat((result[0] as DataResult.Partial).data)
                .isEqualTo(SOME_CURRENT_WEATHER_PAST_AGE)
            Truth.assertThat(result[1]).isInstanceOf(DataResult.Error::class.java)
        }

    @Test
    fun givenSubjectAndLocalRepositoryThrowsException_whenExecuteFlow_thenResultIsError() =
        runBlocking {
            coEvery { mockLocalCurrentWeatherRepository.getCurrentWeather(any()) } throws NullPointerException()
            val result = subject.executeFlow(SOME_CURRENT_WEATHER_INPUT).toList()
            Truth.assertThat(result.size).isEqualTo(1)
            Truth.assertThat(result[0]).isInstanceOf(DataResult.Error::class.java)
        }
}