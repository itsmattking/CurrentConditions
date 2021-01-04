package me.mking.currentconditions.data.mappers

import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import me.mking.currentconditions.data.TestData
import me.mking.currentconditions.data.providers.DateTimeProvider
import me.mking.currentconditions.domain.models.CurrentWeather
import org.junit.Test

class CurrentWeatherMapperTest {

    private val mockDateTimeProvider: DateTimeProvider = mockk {
        every { nowInEpochSeconds() } returns TestData.SOME_NOW_IN_EPOCH_SECONDS
    }

    private val subject: CurrentWeatherMapper = CurrentWeatherMapper(mockDateTimeProvider)

    @Test
    fun givenSubject_whenMapToCurrentWeatherFromOpenWeatherApiResponse_thenResultMatches() {
        val result = subject.mapTo(TestData.SOME_OPEN_WEATHER_API_RESPONSE)
        Truth.assertThat(result.location).isEqualTo("London")
        Truth.assertThat(result.temperature).isEqualTo(4.5)
        Truth.assertThat(result.condition).isEqualTo("Cloudy")
        Truth.assertThat(result.windSpeed).isEqualTo(2.3)
        Truth.assertThat(result.windDirection).isEqualTo(14.0)
        Truth.assertThat(result.updated).isEqualTo(1609755667)
        Truth.assertThat(result.iconUrl).isEqualTo("https://openweathermap.org/img/w/01n.png")
    }

    @Test
    fun givenSubject_whenMapToCurrentWeatheFromOpenWeatherApiResponseWithNoWeather_thenResultIsEmpty() {
        val result =
            subject.mapTo(TestData.SOME_OPEN_WEATHER_API_RESPONSE.copy(weather = emptyList()))
        Truth.assertThat(result).isInstanceOf(CurrentWeather.Empty::class.java)
    }

    @Test
    fun givenSubject_whenMapTo_thenResultMatches() {
        val result = subject.mapTo(TestData.SOME_CURRENT_WEATHER_ENTITY)
        Truth.assertThat(result.location).isEqualTo("London")
        Truth.assertThat(result.condition).isEqualTo("Cloudy")
        Truth.assertThat(result.temperature).isEqualTo(4.5)
        Truth.assertThat(result.windSpeed).isEqualTo(2.3)
        Truth.assertThat(result.windDirection).isEqualTo(14.0)
        Truth.assertThat(result.iconUrl).isEqualTo("https://openweathermap.org/img/w/01n.png")
        Truth.assertThat(result.updated).isEqualTo(1609755667)
    }
}