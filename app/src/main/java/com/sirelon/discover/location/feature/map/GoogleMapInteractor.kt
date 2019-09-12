package com.sirelon.discover.location.feature.map

import android.app.Activity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sirelon.discover.location.R
import com.sirelon.discover.location.feature.places.categories.Place

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

    private var padding = 0

    override fun initWithActivity(activity: Activity): Fragment {
        mapFragment.getMapAsync(this)
        padding = activity.resources.getDimensionPixelSize(R.dimen.peek_height) / 2
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

        googleMap?.uiSettings?.setAllGesturesEnabled(true)
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        googleMap?.setPadding(0, padding, 0, 0)

        if (postponedLocationEnabled) {
            onLocationPermissionGranted()
            postponedLocationEnabled = false
        }
    }

    override fun showMarkers(list: List<Place>) {
        val markers = list.map {
            val position = LatLng(it.latitude, it.longtude)
            MarkerOptions().position(position).title(it.title)
        }

        markers.forEach {
            googleMap?.addMarker(it)
        }
    }
}