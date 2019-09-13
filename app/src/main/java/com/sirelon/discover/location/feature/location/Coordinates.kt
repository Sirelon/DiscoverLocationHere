package com.sirelon.discover.location.feature.location

import android.location.Location

/**
 *  Helper class, which hold cooridonates.
 *  (I don't want to use LatLng class, 'cause it available with google map,  but I want to have more abstraction)
 */
class Coordinates(val latitude: Double, val longitude: Double) {

    constructor(location: Location) : this(location.latitude, location.longitude)

}