package me.mking.currentconditions.data.services

import me.mking.currentconditions.data.models.OpenWeatherApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenWeatherApiService {
    @GET("data/2.5/weather")
    suspend fun weatherByLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appId") appId: String,
        @Query("units") units: String
    ): OpenWeatherApiResponse
}