package me.mking.currentconditions.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.mking.currentconditions.data.services.OpenWeatherApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(ApplicationComponent::class)
object ServicesModule {
    @Provides
    fun providesOpenWeatherApiService(
        okHttpClient: OkHttpClient
    ): OpenWeatherApiService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/")
            .build()
            .create(OpenWeatherApiService::class.java)
    }
}