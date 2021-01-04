package me.mking.currentconditions.data.mappers

import com.google.common.truth.Truth
import me.mking.currentconditions.domain.models.CurrentWeather
import org.junit.Test
import java.util.*

class CurrentWeatherEntityMapperTest {

    private companion object {
        val SOME_CURRENT_WEATHER = CurrentWeather(
            location = "London",
            condition = "Cloudy",
            temperature = 4.5,
            windSpeed = 2.3,
            windDirection = 12.4,
            iconUrl = "https://openweathermap.org/img/w/01n.png",
            updated = Calendar.getInstance().apply {
                set(Calendar.MONTH, Calendar.JANUARY)
                set(Calendar.DAY_OF_MONTH, 4)
                set(Calendar.YEAR, 2021)
                set(Calendar.HOUR_OF_DAY, 10)
                set(Calendar.MINUTE, 21)
                set(Calendar.SECOND, 7)
            }.timeInMillis / 1000 // 1609755667
        )
    }

    private val subject: CurrentWeatherEntityMapper = CurrentWeatherEntityMapper()

    @Test
    fun givenSubject_whenMapTo_thenResultMatches() {
        val result = subject.mapTo(SOME_CURRENT_WEATHER)
        Truth.assertThat(result.location).isEqualTo("London")
        Truth.assertThat(result.condition).isEqualTo("Cloudy")
        Truth.assertThat(result.temperature).isEqualTo(4.5)
        Truth.assertThat(result.windSpeed).isEqualTo(2.3)
        Truth.assertThat(result.windDirection).isEqualTo(12.4)
        Truth.assertThat(result.iconUrl).isEqualTo("https://openweathermap.org/img/w/01n.png")
        Truth.assertThat(result.updated).isEqualTo(1609755667)
    }

}