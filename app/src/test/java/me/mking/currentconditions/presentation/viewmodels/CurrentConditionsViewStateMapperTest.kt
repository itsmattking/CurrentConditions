package me.mking.currentconditions.presentation.viewmodels

import com.google.common.truth.Truth
import me.mking.currentconditions.data.TestData
import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.usecases.DataResult
import org.junit.Test

class CurrentConditionsViewStateMapperTest {

    private val subject = CurrentConditionsViewStateMapper()

    @Test
    fun givenSubject_whenMapToWithError_thenResultIsError() {
        val result = subject.mapTo(DataResult.Error("Something Happened"))
        Truth.assertThat(result).isInstanceOf(CurrentConditionsViewState.Error::class.java)
    }

    @Test
    fun givenSubject_whenMapToWithPartial_thenResultIsReadyWithIsRefreshingTrue() {
        val result = subject.mapTo(
            DataResult.Partial(
                TestData.SOME_CURRENT_WEATHER
            )
        )
        Truth.assertThat(result).isInstanceOf(CurrentConditionsViewState.Ready::class.java)
        Truth.assertThat((result as CurrentConditionsViewState.Ready).isRefreshing).isTrue()
    }


    @Test
    fun givenSubject_whenMapToWithSuccess_thenResultIsReady() {
        val result = subject.mapTo(
            DataResult.Success(
                TestData.SOME_CURRENT_WEATHER
            )
        )
        Truth.assertThat(result).isInstanceOf(CurrentConditionsViewState.Ready::class.java)

        with((result as CurrentConditionsViewState.Ready)) {
            Truth.assertThat(isOffline).isFalse()
            Truth.assertThat(isRefreshing).isFalse()
            Truth.assertThat(currentWeather.location).isEqualTo("London")
            Truth.assertThat(currentWeather.condition).isEqualTo("Cloudy")
            Truth.assertThat(currentWeather.temperature).isEqualTo("5\u00B0c")
            Truth.assertThat(currentWeather.windSpeed).isEqualTo("5mph")
            Truth.assertThat(currentWeather.windDirection).isEqualTo("North")
            Truth.assertThat(currentWeather.iconUrl).isEqualTo("https://openweathermap.org/img/w/01n.png")
            Truth.assertThat(currentWeather.lastUpdated).isEqualTo("Last updated 4 January 2021 10:21 AM")
        }
    }

    @Test
    fun givenSubject_whenMapToWithSuccessAndOfflineTrue_thenResultIsReadyWithOfflineTrue() {
        val result = subject.mapTo(
            DataResult.Success(
                TestData.SOME_CURRENT_WEATHER
            ),
            isOffline = true
        )
        Truth.assertThat(result).isInstanceOf(CurrentConditionsViewState.Ready::class.java)

        with((result as CurrentConditionsViewState.Ready)) {
            Truth.assertThat(isOffline).isTrue()
        }
    }

    @Test
    fun givenSubject_whenMapToWithEmptyCurrentWeather_thenResultIsReadyWithEmptyState() {
        val result = subject.mapTo(
            DataResult.Success(
                CurrentWeather.Empty
            )
        )
        Truth.assertThat(result).isInstanceOf(CurrentConditionsViewState.Ready::class.java)

        with((result as CurrentConditionsViewState.Ready).currentWeather) {
            Truth.assertThat(location).isEqualTo("Not available")
            Truth.assertThat(condition).isEqualTo("Unknown")
            Truth.assertThat(temperature).isEqualTo("-")
            Truth.assertThat(windSpeed).isEqualTo("-")
            Truth.assertThat(windDirection).isEqualTo("-")
            Truth.assertThat(iconUrl).isEqualTo("")
            Truth.assertThat(lastUpdated).isEqualTo("Last update unknown")
        }
    }

}