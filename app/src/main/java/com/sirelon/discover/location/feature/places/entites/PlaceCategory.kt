package com.sirelon.discover.location.feature.places.entites

/**
 * Created on 2019-09-12 11:10 for DiscoverLocationHere.
 */
data class PlaceCategory(
    val id: String,
    val name: String,
    val imageUrl: String,
    val children: List<PlaceCategory>?
)