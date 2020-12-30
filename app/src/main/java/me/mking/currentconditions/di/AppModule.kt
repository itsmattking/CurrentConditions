package me.mking.currentconditions.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.mking.currentconditions.BuildConfig
import me.mking.currentconditions.data.databases.CurrentWeatherDao
import me.mking.currentconditions.data.databases.CurrentWeatherDatabase
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Named("OpenWeatherAppId")
    fun provideOpenWeatherAppId() = BuildConfig.OPENWEATHER_APP_ID

    @Provides
    fun provideCurrentWeatherDatabase(
        @ApplicationContext applicationContext: Context
    ): CurrentWeatherDatabase {
        return Room.databaseBuilder(
            applicationContext,
            CurrentWeatherDatabase::class.java, "current-weather-db"
        ).build()
    }

    @Provides
    fun provideCurrentWeatherDao(appDatabase: CurrentWeatherDatabase): CurrentWeatherDao {
        return appDatabase.currentWeatherDao()
    }

}