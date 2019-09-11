package com.sirelon.discover.location.feature.base.map

import android.app.Activity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * realization of MapInteractor, which use Google Maps
 *
 * Created on 2019-09-11 21:31 for DiscoverLocationHere.
 */
private const val DEFAULT_ZOOM = 9.0F

class GoogleMapInteractor : OnMapReadyCallback, MapInteractor {

    private val mapFragment = SupportMapFragment.newInstance()
    private var googleMap: GoogleMap? = null

    // In case when onLocationPermissionGranted method called before google map initialized
    private var postponedLocationEnabled = false

    override fun initWithActivity(activity: Activity): Fragment {
        mapFragment.getMapAsync(this)
        return mapFragment
    }

    override fun onLocationPermissionGranted() {
        if (googleMap == null) {
            postponedLocationEnabled = true
        }
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
    }

    override fun showLocation(latitude: Double, longitude: Double) {
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(latitude, longitude), DEFAULT_ZOOM
            )
        )
    }

    override fun release() {
        googleMap?.clear()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        this.googleMap?.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        this.googleMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        googleMap?.uiSettings?.setAllGesturesEnabled(true)
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        if (postponedLocationEnabled) {
            onLocationPermissionGranted()
            postponedLocationEnabled = false
        }
    }
}