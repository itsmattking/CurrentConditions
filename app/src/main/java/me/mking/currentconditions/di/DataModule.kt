package me.mking.currentconditions.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.mking.currentconditions.data.providers.CalendarDateTimeProvider
import me.mking.currentconditions.data.providers.CurrentLocationProvider
import me.mking.currentconditions.data.providers.DateTimeProvider
import me.mking.currentconditions.data.providers.FusedCurrentLocationProvider
import me.mking.currentconditions.data.repositories.CachedCurrentWeatherRepository
import me.mking.currentconditions.domain.repositories.CurrentWeatherRepository

@Module
@InstallIn(ApplicationComponent::class)
abstract class DataModule {
    @Binds
    abstract fun provideCurrentWeatherRepository(
        cachedCurrentWeatherRepository: CachedCurrentWeatherRepository
    ): CurrentWeatherRepository

    @Binds
    abstract fun provideCurrentLocationProvider(
        fusedCurrentLocationProvider: FusedCurrentLocationProvider
    ): CurrentLocationProvider

    @Binds
    abstract fun provideDateTimeProvider(
        calendarDateTimeProvider: CalendarDateTimeProvider
    ): DateTimeProvider
}