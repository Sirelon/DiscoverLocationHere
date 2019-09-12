package com.sirelon.discover.location.feature.places.api

import androidx.annotation.Keep
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created on 2019-09-12 10:52 for DiscoverLocationHere.
 */
private const val LOCATION_QUERY = "at"

@Keep
interface PlacesAPI {

    @GET("categories/places")
    suspend fun loadCategories(
        @Query(LOCATION_QUERY)
        location: String
    ): CategoriesResponse

    @GET("discover/explore")
    suspend fun findPopularPlaces(
        @Query(LOCATION_QUERY)
        location: String,
        @Query("cat")
        categories: String,
        @Query("size")
        size: Int
    ): PlacesResponse
}