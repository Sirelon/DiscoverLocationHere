package com.sirelon.discover.location.feature.places.categories

import com.sirelon.discover.location.feature.location.Coordinates
import com.sirelon.discover.location.feature.places.api.CategoryItemResponse
import com.sirelon.discover.location.feature.places.api.PlacesAPI
import com.sirelon.discover.location.feature.places.api.toApiParameter
import com.sirelon.discover.location.feature.places.entites.PlaceCategory

/**
 *  In theory we can load categories from other source
 */
class CategoriesRepository(private val placesAPI: PlacesAPI) {

    /**
     * Load categories from network by location
     */
    suspend fun loadCategories(coordinates: Coordinates): List<PlaceCategory> {
        val response = placesAPI.loadCategories(coordinates.toApiParameter())
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

    private fun CategoryItemResponse.mapToUi(children: Set<CategoryItemResponse>? = null): PlaceCategory {
        return PlaceCategory(
            id = id,
            name = title,
            imageUrl = icon,
            children = children?.map { it.mapToUi() })
    }
}