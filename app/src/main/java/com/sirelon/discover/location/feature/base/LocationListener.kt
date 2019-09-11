package com.sirelon.discover.location.feature.base

import android.content.Context
import android.location.Location
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.sirelon.discover.location.utils.logInfo

/**
 * Created on 2019-09-11 21:55 for DiscoverLocationHere.
 */
class LocationListener(context: Context, private val callback: (location: Location) -> Unit) :
    LifecycleObserver {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            "FUSED LOCATION GET LOCATION $locationResult".logInfo()
            locationResult ?: return
            val location = locationResult.lastLocation
            callback(location)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}