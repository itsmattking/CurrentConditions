package me.mking.currentconditions.data.providers

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FusedCurrentLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : CurrentLocationProvider {

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    override suspend fun currentLocation(): CurrentLocation = suspendCoroutine { task ->
        LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener {
            task.resume(CurrentLocation.Available(it.latitude, it.longitude))
        }.addOnFailureListener {
            task.resume(CurrentLocation.NotAvailable)
        }.addOnCanceledListener {
            task.resume(CurrentLocation.NotAvailable)
        }
    }
}