package com.sirelon.discover.location.feature.map

import android.app.Activity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.sirelon.discover.location.feature.location.Coordinates
import com.sirelon.discover.location.feature.places.entites.Place

/**
 * realization of MapInteractor, which use Google Maps
 *
 * Created on 2019-09-11 21:31 for DiscoverLocationHere.
 */
private const val DEFAULT_ZOOM = 15.0F

class GoogleMapInteractor : OnMapReadyCallback, MapInteractor {

    private val mapFragment = SupportMapFragment.newInstance()
    private var googleMap: GoogleMap? = null
    private var markerManager: GoogleMarkerManager? = null

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
        markerManager?.clear()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.markerManager = GoogleMarkerManager(mapFragment.requireContext(), googleMap)

        googleMap.uiSettings?.setAllGesturesEnabled(true)
        googleMap.uiSettings?.isZoomControlsEnabled = true


        if (postponedLocationEnabled) {
            onLocationPermissionGranted()
            postponedLocationEnabled = false
        }
    }

    override fun showMarkers(list: List<Place>) {
        markerManager?.setPlacesMarkerList(list)
    }

    override fun clearAllMarkers() {
        markerManager?.clear()
    }

    override fun getCurrentCoordinates(): Coordinates? {
        val latLng = googleMap?.cameraPosition?.target
        latLng ?: return null

        return Coordinates(latitude = latLng.latitude, longitude = latLng.longitude)
    }
}