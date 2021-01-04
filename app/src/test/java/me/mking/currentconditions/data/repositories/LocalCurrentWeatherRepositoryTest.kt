package me.mking.currentconditions.data.repositories

import com.google.common.truth.Truth
import io.mockk.*
import kotlinx.coroutines.runBlocking
import me.mking.currentconditions.data.TestData
import me.mking.currentconditions.data.databases.CurrentWeatherDao
import me.mking.currentconditions.data.mappers.CurrentWeatherEntityMapper
import me.mking.currentconditions.data.mappers.CurrentWeatherMapper
import me.mking.currentconditions.data.models.CurrentWeatherEntity
import me.mking.currentconditions.domain.models.CurrentWeather
import me.mking.currentconditions.domain.repositories.CurrentWeatherInput
import org.junit.Test

class LocalCurrentWeatherRepositoryTest {
    private companion object {
        const val SOME_INSERTED_ID = 1L
        val SOME_CURRENT_WEATHER_INPUT = CurrentWeatherInput(
            latitude = 51.5,
            longitude = 0.1,
            unitType = CurrentWeatherInput.UnitType.METRIC,
            maxAge = 0L
        )
    }

    private val mockCurrentWeatherDao: CurrentWeatherDao = mockk {
        coEvery { getCurrentWeather() } returns listOf(TestData.SOME_CURRENT_WEATHER_ENTITY)
        coEvery { insertCurrentWeather(any()) } returns SOME_INSERTED_ID
        coEvery { clearAllExcept(any()) } returns Unit
    }

    private val mockCurrentWeatherEntityMapper: CurrentWeatherEntityMapper = mockk {
        every { mapTo(any()) } returns TestData.SOME_CURRENT_WEATHER_ENTITY
    }

    private val mockCurrentWeatherMapper: CurrentWeatherMapper = mockk {
        every { mapTo(ofType<CurrentWeatherEntity>()) } returns TestData.SOME_CURRENT_WEATHER
    }

    private val subject = LocalCurrentWeatherRepository(
        currentWeatherDao = mockCurrentWeatherDao,
        currentWeatherEntityMapper = mockCurrentWeatherEntityMapper,
        currentWeatherMapper = mockCurrentWeatherMapper
    )

    @Test
    fun givenSubject_whenGetCurrentWeather_thenResultIsMapped() = runBlocking {
        val result = subject.getCurrentWeather(SOME_CURRENT_WEATHER_INPUT)
        coVerify { mockCurrentWeatherDao.getCurrentWeather() }
        verify { mockCurrentWeatherMapper.mapTo(TestData.SOME_CURRENT_WEATHER_ENTITY) }
        Truth.assertThat(result).isEqualTo(TestData.SOME_CURRENT_WEATHER)
    }

    @Test
    fun givenSubject_whenGetCurrentWeatherAndDaoResultIsEmpty_thenResultEmpty() = runBlocking {
        coEvery { mockCurrentWeatherDao.getCurrentWeather() } returns emptyList()
        val result = subject.getCurrentWeather(SOME_CURRENT_WEATHER_INPUT)
        coVerify { mockCurrentWeatherDao.getCurrentWeather() }
        verify { mockCurrentWeatherMapper.mapTo(TestData.SOME_CURRENT_WEATHER_ENTITY) wasNot Called }
        Truth.assertThat(result).isInstanceOf(CurrentWeather.Empty::class.java)
    }

    @Test
    fun givenSubject_whenInsertCurrentWeather_thenMapperIsCalledAndClearAllExceptCalledWithCorrectId() = runBlocking {
        subject.insertCurrentWeather(TestData.SOME_CURRENT_WEATHER)
        verify { mockCurrentWeatherEntityMapper.mapTo(TestData.SOME_CURRENT_WEATHER) }
        coVerify { mockCurrentWeatherDao.insertCurrentWeather(TestData.SOME_CURRENT_WEATHER_ENTITY) }
        coVerify { mockCurrentWeatherDao.clearAllExcept(SOME_INSERTED_ID) }
    }
}