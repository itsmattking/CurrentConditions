package me.mking.currentconditions.presentation.viewmodels

import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import me.mking.currentconditions.data.providers.CurrentLocation
import me.mking.currentconditions.data.providers.CurrentLocationProvider
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
        every { mapTo(any()) } returns CurrentConditionsViewState.Ready(mockk(), false)
    }

    @Test
    fun givenSubject_whenLoad_thenStateIsLoadingThenReady() = runBlockingTest {
        val subject = givenSubject()
        subject.load()
        verifyOrder {
            stateObserver.onChanged(CurrentConditionsViewState.Loading)
            stateObserver.onChanged(withArg {
                Truth.assertThat(it).isInstanceOf(CurrentConditionsViewState.Ready::class.java)
            })
        }
    }

    @Test
    fun givenSubjectAndLocationIsNotAvailable_whenLoad_thenStateIsLocationNotAvailable() = runBlockingTest {
        val subject = givenSubjectAndLocationIsNotAvailable()
        subject.load()
        verifyOrder {
            stateObserver.onChanged(CurrentConditionsViewState.Loading)
            stateObserver.onChanged(withArg {
                Truth.assertThat(it).isInstanceOf(CurrentConditionsViewState.LocationNotAvailable::class.java)
            })
        }
    }

    @Test
    fun givenSubjectAndStateIsReady_whenReload_thenStateIsReadyIsRefreshing() = runBlockingTest {
        val subject = givenSubjectStateIsReady()
        subject.reload()
        verifyOrder {
            stateObserver.onChanged(withArg {
                Truth.assertThat(it).isInstanceOf(CurrentConditionsViewState.Ready::class.java)
                Truth.assertThat((it as CurrentConditionsViewState.Ready).isRefreshing).isTrue()
            })
            stateObserver.onChanged(withArg {
                Truth.assertThat(it).isInstanceOf(CurrentConditionsViewState.Ready::class.java)
                Truth.assertThat((it as CurrentConditionsViewState.Ready).isRefreshing).isFalse()
            })
        }
    }

    private fun givenSubject(): CurrentConditionsViewModel {
        val subject = CurrentConditionsViewModel(
            getCachedCurrentWeatherUseCase = mockGetCachedCurrentWeatherUseCase,
            currentLocationProvider = mockCurrentLocationProvider,
            currentConditionsViewStateMapper = mockCurrentConditionsViewStateMapper
        )
        subject.state.observeForever(stateObserver)
        return subject
    }

    private fun givenSubjectAndLocationIsNotAvailable(): CurrentConditionsViewModel {
        coEvery { mockCurrentLocationProvider.currentLocation() } returns CurrentLocation.NotAvailable
        return givenSubject()
    }

    private fun givenSubjectStateIsReady(): CurrentConditionsViewModel {
        val subject = CurrentConditionsViewModel(
            getCachedCurrentWeatherUseCase = mockGetCachedCurrentWeatherUseCase,
            currentLocationProvider = mockCurrentLocationProvider,
            currentConditionsViewStateMapper = mockCurrentConditionsViewStateMapper
        )
        subject.load()
        subject.state.observeForever(stateObserver)
        return subject
    }
}