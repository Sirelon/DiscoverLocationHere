package com.sirelon.discover.location.feature.places.categories

import androidx.annotation.Keep
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created on 2019-09-12 10:52 for DiscoverLocationHere.
 */
@Keep
interface PlacesAPI {

    @GET("categories/places")
    suspend fun loadCategories(
        @Query("at")
        location: String
    ): CategoriesResponse
}