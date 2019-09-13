package com.sirelon.discover.location.feature.places

import com.sirelon.discover.location.feature.location.Coordinates
import com.sirelon.discover.location.feature.places.api.PlaceItemResponse
import com.sirelon.discover.location.feature.places.api.PlacesAPI
import com.sirelon.discover.location.feature.places.api.toApiParameter
import com.sirelon.discover.location.feature.places.api.toApiParamter
import com.sirelon.discover.location.feature.places.entites.Place
import com.sirelon.discover.location.feature.places.entites.PlaceCategory

/**
 * Created on 2019-09-12 11:06 for DiscoverLocationHere.
 */
class PopularPlacesRepository(private val placesAPI: PlacesAPI) {

    suspend fun findPopularPlaces(
        coordinates: Coordinates,
        selectedCategories: Set<PlaceCategory>
    ): List<Place> {
        val response = placesAPI.findPopularPlaces(
            location = coordinates.toApiParameter(),
            categories = selectedCategories.toApiParamter(),
            size = 1000
        )

        return response.result.items.map { it.mapToUi() }
    }

    private fun PlaceItemResponse.mapToUi() =
        Place(
            id,
            title,
            icon,
            latitude = position.first(),
            longtude = position.last(),
            isOpen = openningHoursResponse?.isOpen == true,
            distance = distance
        )
}