package me.mking.currentconditions.data.mappers

import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import me.mking.currentconditions.data.models.*
import me.mking.currentconditions.data.providers.DateTimeProvider
import me.mking.currentconditions.domain.models.CurrentWeather
import org.junit.Test
import java.util.*

class CurrentWeatherMapperTest {

    private companion object {
        val SOME_NOW_IN_EPOCH_SECONDS = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 4)
            set(Calendar.YEAR, 2021)
            set(Calendar.HOUR, 10)
            set(Calendar.MINUTE, 21)
            set(Calendar.SECOND, 7)
        }.timeInMillis / 1000 // 1609755667
        val SOME_WEATHER_RESPONSE = WeatherResponse(
            id = "1",
            main = "",
            description = "Cloudy",
            icon = "01n"
        )
        val SOME_MAIN_RESPONSE = MainResponse(
            temp = 4.5
        )
        val SOME_WIND_RESPONSE = WindResponse(
            speed = 2.3,
            deg = 14
        )
        val SOME_OPEN_WEATHER_API_RESPONSE = OpenWeatherApiResponse(
            weather = listOf(SOME_WEATHER_RESPONSE),
            main = SOME_MAIN_RESPONSE,
            wind = SOME_WIND_RESPONSE,
            dt = SOME_NOW_IN_EPOCH_SECONDS,
            name = "London"
        )
        val SOME_CURRENT_WEATHER_ENTITY = CurrentWeatherEntity(
            id = 1,
            location = "London",
            condition = "Cloudy",
            temperature = 4.5,
            windSpeed = 2.3,
            windDirection = 14.0,
            updated = SOME_NOW_IN_EPOCH_SECONDS,
            iconUrl = "https://openweathermap.org/img/w/01n.png"
        )
    }

    private val mockDateTimeProvider: DateTimeProvider = mockk {
        every { nowInEpochSeconds() } returns SOME_NOW_IN_EPOCH_SECONDS
    }

    private val subject: CurrentWeatherMapper = CurrentWeatherMapper(mockDateTimeProvider)

    @Test
    fun givenSubject_whenMapToCurrentWeatheFromOpenWeatherApiResponse_thenResultMatches() {
        val result = subject.mapTo(SOME_OPEN_WEATHER_API_RESPONSE)
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
        val result = subject.mapTo(SOME_OPEN_WEATHER_API_RESPONSE.copy(weather = emptyList()))
        Truth.assertThat(result).isInstanceOf(CurrentWeather.Empty::class.java)
    }

    @Test
    fun givenSubject_whenMapTo_thenResultMatches() {
        val result = subject.mapTo(SOME_CURRENT_WEATHER_ENTITY)
        Truth.assertThat(result.location).isEqualTo("London")
        Truth.assertThat(result.condition).isEqualTo("Cloudy")
        Truth.assertThat(result.temperature).isEqualTo(4.5)
        Truth.assertThat(result.windSpeed).isEqualTo(2.3)
        Truth.assertThat(result.windDirection).isEqualTo(14.0)
        Truth.assertThat(result.iconUrl).isEqualTo("https://openweathermap.org/img/w/01n.png")
        Truth.assertThat(result.updated).isEqualTo(1609755667)
    }
}