package me.mking.currentconditions.presentation.viewmodels

import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import me.mking.currentconditions.data.providers.CurrentLocation
import me.mking.currentconditions.data.providers.CurrentLocationProvider
import me.mking.currentconditions.data.providers.NetworkStatusProvider
import me.mking.currentconditions.domain.usecases.DataResult
import me.mking.currentconditions.domain.usecases.GetCachedCurrentWeatherUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class CurrentConditionsViewModelTest {

    private val stateObserver: Observer<CurrentConditionsViewState> = mockk {
        every { onChanged(any()) } returns Unit
    }

    private val mockGetCachedCurrentWeatherUseCase: GetCachedCurrentWeatherUseCase = mockk {
        every { executeFlow(any()) } returns flowOf(DataResult.Success(mockk()))
    }

    private val mockCurrentLocationProvider: CurrentLocationProvider = mockk {
        coEvery { currentLocation() } returns CurrentLocation.Available(51.5, 0.1)
    }

    private val mockCurrentConditionsViewStateMapper: CurrentConditionsViewStateMapper = mockk {
        every { mapTo(any(), any()) } returns CurrentConditionsViewState.Ready(mockk(), false)
    }

    private val mockNetworkStatusProvider: NetworkStatusProvider = mockk {
        every { isConnected() } returns true
    }

    @Test
    fun givenSubject_whenLoad_thenStateIsReady() = runBlockingTest {
        val subject = givenSubject()
        subject.load()
        verify(exactly = 1) {
            stateObserver.onChanged(withArg {
                Truth.assertThat(it).isInstanceOf(CurrentConditionsViewState.Ready::class.java)
            })
        }
    }

    @Test
    fun givenSubjectAndStateIsReady_whenReload_thenMaxAgeArgumentIsSet() = runBlockingTest {
        val subject = givenSubject()
        subject.load()
        coVerify {
            mockGetCachedCurrentWeatherUseCase.executeFlow(withArg {
                Truth.assertThat(it.maxAge).isGreaterThan(0L)
            })
        }
    }

    @Test
    fun givenSubjectAndLocationIsNotAvailable_whenLoad_thenStateIsLocationNotAvailable() =
        runBlockingTest {
            val subject = givenSubjectAndLocationIsNotAvailable()
            subject.load()
            verify(exactly = 1) {
                stateObserver.onChanged(withArg {
                    Truth.assertThat(it)
                        .isInstanceOf(CurrentConditionsViewState.LocationNotAvailable::class.java)
                })
            }
        }

    @Test
    fun givenSubjectAndStateIsReady_whenReload_thenMaxAgeArgumentIsZero() = runBlockingTest {
        val subject = givenSubjectStateIsReady()
        subject.reload()
        coVerify {
            mockGetCachedCurrentWeatherUseCase.executeFlow(withArg {
                Truth.assertThat(it.maxAge).isEqualTo(0L)
            })
        }
    }

    @Test
    fun givenSubjectAndNetworkStatusIsConnected_whenReload_thenOfflineFalsePassedToMapper() = runBlockingTest {
        val subject = givenSubject()
        subject.reload()
        verify {
            mockCurrentConditionsViewStateMapper.mapTo(any(), false)
        }
    }

    @Test
    fun givenSubjectAndNetworkStatusIsNotConnected_whenReload_thenOfflineTruePassedToMapper() = runBlockingTest {
        val subject = givenSubjectAndNetworkStatusIsNotConnected()
        subject.reload()
        verify {
            mockCurrentConditionsViewStateMapper.mapTo(any(), true)
        }
    }

    private fun givenSubject(): CurrentConditionsViewModel {
        val subject = CurrentConditionsViewModel(
            getCachedCurrentWeatherUseCase = mockGetCachedCurrentWeatherUseCase,
            currentLocationProvider = mockCurrentLocationProvider,
            currentConditionsViewStateMapper = mockCurrentConditionsViewStateMapper,
            networkStatusProvider = mockNetworkStatusProvider
        )
        subject.state.observeForever(stateObserver)
        return subject
    }

    private fun givenSubjectAndLocationIsNotAvailable(): CurrentConditionsViewModel {
        coEvery { mockCurrentLocationProvider.currentLocation() } returns CurrentLocation.NotAvailable
        return givenSubject()
    }

    private fun givenSubjectAndNetworkStatusIsNotConnected(): CurrentConditionsViewModel {
        coEvery { mockNetworkStatusProvider.isConnected() } returns false
        return givenSubject()
    }

    private fun givenSubjectStateIsReady(): CurrentConditionsViewModel {
        val subject = CurrentConditionsViewModel(
            getCachedCurrentWeatherUseCase = mockGetCachedCurrentWeatherUseCase,
            currentLocationProvider = mockCurrentLocationProvider,
            currentConditionsViewStateMapper = mockCurrentConditionsViewStateMapper,
            networkStatusProvider = mockNetworkStatusProvider
        )
        subject.load()
        subject.state.observeForever(stateObserver)
        return subject
    }
}