package com.sirelon.discover.location.feature.map

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
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
    private var markerManager: MarkerManager? = null

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
        markerManager?.clearItems()
        googleMap?.clear()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.markerManager = MarkerManager(mapFragment.requireContext(), googleMap)

        googleMap.uiSettings?.setAllGesturesEnabled(true)
        googleMap.uiSettings?.isZoomControlsEnabled = true

        googleMap.setPadding(0, padding, 0, 0)

        if (postponedLocationEnabled) {
            onLocationPermissionGranted()
            postponedLocationEnabled = false
        }
    }

    override fun showMarkers(list: List<Place>) {
        markerManager?.setPlacesMarkerList(list)
    }
}

private const val ZOOM_INCREMENT = 2.5f

private class MarkerManager(context: Context, map: GoogleMap) :
    ClusterManager<PlaceClusterMarker>(context, map) {

    init {
        map.setOnCameraIdleListener(this)
        map.setOnMarkerClickListener(this)
        map.setOnInfoWindowClickListener(this)
        setOnClusterClickListener { cluster ->
            val currentZoom = map.cameraPosition.zoom
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    cluster.position,
                    currentZoom + ZOOM_INCREMENT
                )
            )
            true
        }
        renderer = object : DefaultClusterRenderer<PlaceClusterMarker>(context, map, this) {

        }
    }

    fun setPlacesMarkerList(list: List<Place>) {
        addItems(list.map { PlaceClusterMarker(it) })
        cluster()
    }
}

private class PlaceClusterMarker(private val place: Place) : ClusterItem {
    override fun getSnippet(): String? = null

    override fun getTitle() = place.title

    override fun getPosition() = LatLng(place.latitude, place.longtude)
}