package com.sirelon.discover.location.feature.base.map

import android.app.Activity
import androidx.fragment.app.Fragment

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

    fun showLocation(latitude: Double, longitude: Double)
}