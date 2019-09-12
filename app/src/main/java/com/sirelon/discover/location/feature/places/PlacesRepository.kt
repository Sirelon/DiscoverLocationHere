package com.sirelon.discover.location.feature.places

import android.location.Location
import com.sirelon.discover.location.feature.places.api.CategoryItemResponse
import com.sirelon.discover.location.feature.places.api.PlaceItemResponse
import com.sirelon.discover.location.feature.places.api.PlacesAPI
import com.sirelon.discover.location.feature.places.entites.Place
import com.sirelon.discover.location.feature.places.entites.PlaceCategory

/**
 * Created on 2019-09-12 11:06 for DiscoverLocationHere.
 */
class PlacesRepository(private val placesAPI: PlacesAPI) {

    /**
     * Load categories from network by location
     */
    suspend fun loadCategories(location: Location): List<PlaceCategory> {
        val response = placesAPI.loadCategories(location.toApiParameter())
        val categoriesResponse = response.categories

        val map = mutableMapOf<String, MutableSet<CategoryItemResponse>>()

        categoriesResponse.forEach { itemResponse ->
            itemResponse.within.forEach {
                val children = map.getOrPut(it, { mutableSetOf() })
                children.add(itemResponse)
            }
        }

        val uiItems = map.mapNotNull { entry ->
            val parentId = entry.key
            val items = entry.value
            val parent = categoriesResponse.find { it.id == parentId } // find ParentCategory
            parent?.mapToUi(items)
        }

        return uiItems
    }

    suspend fun findPopularPlaces(
        location: Location,
        selectedCategories: Set<PlaceCategory>
    ): List<Place> {
        val response = placesAPI.findPopularPlaces(
            location = location.toApiParameter(),
            categories = selectedCategories.toApiParamter(),
            size = 1000
        )

        return response.result.items.map { it.mapToUi() }
    }

    private fun CategoryItemResponse.mapToUi(children: Set<CategoryItemResponse>? = null): PlaceCategory {
        return PlaceCategory(
            id = id,
            name = title,
            imageUrl = icon,
            children = children?.map { it.mapToUi() })
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

/**
 * Just comma between lat and lng
 */
fun Location.toApiParameter() = "$latitude,$longitude"

fun Collection<PlaceCategory>.toApiParamter() = joinToString(",") { it.id }