package me.mking.currentconditions.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.mking.currentconditions.data.providers.CalendarDateTimeProvider
import me.mking.currentconditions.data.providers.CurrentLocationProvider
import me.mking.currentconditions.data.providers.DateTimeProvider
import me.mking.currentconditions.data.providers.FusedCurrentLocationProvider

@Module
@InstallIn(ApplicationComponent::class)
abstract class DataModule {
    @Binds
    abstract fun provideCurrentLocationProvider(
        fusedCurrentLocationProvider: FusedCurrentLocationProvider
    ): CurrentLocationProvider

    @Binds
    abstract fun provideDateTimeProvider(
        calendarDateTimeProvider: CalendarDateTimeProvider
    ): DateTimeProvider
}