package me.mking.currentconditions.data.providers

interface CurrentLocationProvider {
    suspend fun currentLocation(): CurrentLocation
}

sealed class CurrentLocation {
    data class Available(
        val latitude: Double,
        val longitude: Double
    ) : CurrentLocation()

    object NotAvailable : CurrentLocation()
}