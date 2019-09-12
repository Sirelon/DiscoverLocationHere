package com.sirelon.discover.location.feature.places.categories

/**
 * Created on 2019-09-12 19:19 for DiscoverLocationHere.
 */
data class Place(
    val id: String,
    val title: String,
    val imageUrl: String,
    val latitude: Double,
    val longtude: Double,
    val isOpen: Boolean
)
