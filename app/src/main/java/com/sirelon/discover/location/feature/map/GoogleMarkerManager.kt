package com.sirelon.discover.location.feature.map

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.sirelon.discover.location.feature.places.entites.Place
import com.sirelon.discover.location.utils.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created on 2019-09-13 00:13 for DiscoverLocationHere.
 */
private const val ZOOM_INCREMENT = 2.5f

class GoogleMarkerManager(context: Context, map: GoogleMap) :
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
            override fun onBeforeClusterItemRendered(
                item: PlaceClusterMarker?,
                markerOptions: MarkerOptions?
            ) {
                val icon = item?.icon
                if (icon != null) {
                    markerOptions?.icon(icon)
                } else {
                    super.onBeforeClusterItemRendered(item, markerOptions)
                }
            }
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
class PlaceClusterMarker(val place: Place) : ClusterItem {

    var icon: BitmapDescriptor? = null

    // I know this is not really correct, but for faster implementation I leave it as is
    init {
        GlobalScope.launch(Dispatchers.Main) {
            val result = kotlin.runCatching {
                val bitmap = ImageLoader.loadAsBitmap(place.imageUrl)
                icon = BitmapDescriptorFactory.fromBitmap(bitmap)
            }
            result.exceptionOrNull()?.printStackTrace()
        }
    }

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