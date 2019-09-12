package com.sirelon.discover.location.feature.map

import android.app.Activity
import androidx.fragment.app.Fragment
import com.sirelon.discover.location.feature.places.categories.Place

/**
 * Created on 2019-09-11 21:40 for DiscoverLocationHere.
 */
interface MapInteractor {
    /**
     * init all logic, related to map and return piece of UI (fragment) with Map inside
     *
     * return Fragment with map
     */
    fun initWithActivity(activity: Activity): Fragment

    /**
     * Should be called when we have permission from user for using location
     */
    fun onLocationPermissionGranted()

    /**
     * Should be called when we are finish working with map.
     */
    fun release()

    /**
     * Show map with passed coordinates
     */
    fun showLocation(latitude: Double, longitude: Double)

    /**
     * Show places as markers on a map
     */
    fun showMarkers(list: List<Place>)

    /**
     * Clear all added markers from a map
     */
    fun clearAllMarkers()
}