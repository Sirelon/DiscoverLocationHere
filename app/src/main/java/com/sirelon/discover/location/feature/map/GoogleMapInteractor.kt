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
import com.sirelon.discover.location.feature.places.entites.Place

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
        googleMap?.clear()
        markerManager?.clear()
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

    override fun clearAllMarkers() {
        markerManager?.clear()
    }
}

private const val ZOOM_INCREMENT = 2.5f

private class MarkerManager(context: Context, map: GoogleMap) :
    ClusterManager<PlaceClusterMarker>(context, map) {

    private val markers = hashSetOf<PlaceClusterMarker>()

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


    fun clear() {
        markers.clear()
        clearItems()
        cluster()
    }


    /**
     * Funtion for set markers to map. It optimized to not add already markers which are already set
     */
    fun setPlacesMarkerList(list: List<Place>) {
        val items = list.map { PlaceClusterMarker(it) }
        if (markers.isEmpty()) {
            addItems(items)
            markers.addAll(items)
        } else {
            // get all items which is not contains already in a list
            val itemsToAdd = items.filterNot { markers.contains(it) }

            // Get all other items and remove markers
            markers.subtract(itemsToAdd).forEach {
                removeItem(it)
            }
            addItems(itemsToAdd)

            markers.clear()
            markers.addAll(items)
        }

        cluster()
    }
}

// Override hashcode and equals, 'cause I use this class in hashSet, but I don't want to make this class as "data class", 'cause I need to check only id of the place
private class PlaceClusterMarker(val place: Place) : ClusterItem {
    override fun getSnippet(): String? = null

    override fun getTitle() = place.title

    override fun getPosition() = LatLng(place.latitude, place.longtude)

    override fun hashCode() = place.id.hashCode()

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val marker = o as PlaceClusterMarker?

        return place.id == marker?.place?.id
    }
}