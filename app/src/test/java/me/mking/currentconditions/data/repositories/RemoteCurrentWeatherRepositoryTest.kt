package me.mking.currentconditions.data.repositories

import com.google.common.truth.Truth
import io.mockk.*
import kotlinx.coroutines.runBlocking
import me.mking.currentconditions.data.mappers.CurrentWeatherMapper
import me.mking.currentconditions.data.mappers.TestData
import me.mking.currentconditions.data.services.OpenWeatherApiService
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import okio.IOException
import org.junit.Test

class RemoteCurrentWeatherRepositoryTest {

    private companion object {
        const val SOME_OPEN_WEATHER_APP_ID = "abcd1234"
        val SOME_CURRENT_WEATHER_INPUT = CurrentWeatherInput(
            latitude = 51.5,
            longitude = 0.1,
            unitType = CurrentWeatherInput.UnitType.METRIC,
            maxAge = 0L
        )
    }

    private val mockOpenWeatherApiService: OpenWeatherApiService = mockk {
        coEvery {
            weatherByLatLon(
                any(),
                any(),
                any(),
                any()
            )
        } returns TestData.SOME_OPEN_WEATHER_API_RESPONSE
    }

    private val mockCurrentWeatherMapper: CurrentWeatherMapper = mockk {
        every { mapTo(TestData.SOME_OPEN_WEATHER_API_RESPONSE) } returns TestData.SOME_CURRENT_WEATHER
    }

    private val subject: RemoteCurrentWeatherRepository = RemoteCurrentWeatherRepository(
        mockOpenWeatherApiService,
        SOME_OPEN_WEATHER_APP_ID,
        mockCurrentWeatherMapper
    )

    @Test
    fun givenSubject_whenGetCurrentWeather_thenApiServiceAndMapperAreCalledWithCorrectArguments() =
        runBlocking {
            subject.getCurrentWeather(SOME_CURRENT_WEATHER_INPUT)
            coVerify {
                mockOpenWeatherApiService.weatherByLatLon(
                    51.5,
                    0.1,
                    SOME_OPEN_WEATHER_APP_ID,
                    "metric"
                )
            }
            verify { mockCurrentWeatherMapper.mapTo(TestData.SOME_OPEN_WEATHER_API_RESPONSE) }
        }

    @Test
    fun givenSubject_whenGetCurrentWeatherThrowsIOException_thenRemoteRepositoryExceptionIsThrown() =
        runBlocking {
            coEvery {
                mockOpenWeatherApiService.weatherByLatLon(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } throws IOException("IO Error")
            var thrownException: Exception? = null
            try {
                subject.getCurrentWeather(SOME_CURRENT_WEATHER_INPUT)
            } catch (exception: Exception) {
                thrownException = exception
            }
            Truth.assertThat(thrownException).isInstanceOf(RemoteRepositoryException::class.java)
        }

    @Test
    fun givenSubject_whenGetCurrentWeatherThrowsOtherException_thenSameExceptionIsThrown() =
        runBlocking {
            coEvery {
                mockOpenWeatherApiService.weatherByLatLon(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } throws NullPointerException()
            var thrownException: Exception? = null
            try {
                subject.getCurrentWeather(SOME_CURRENT_WEATHER_INPUT)
            } catch (exception: Exception) {
                thrownException = exception
            }
            Truth.assertThat(thrownException).isInstanceOf(NullPointerException::class.java)
        }
}