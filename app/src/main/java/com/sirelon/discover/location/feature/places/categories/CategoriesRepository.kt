package com.sirelon.discover.location.feature.places.categories

import android.location.Location

/**
 * Created on 2019-09-12 11:06 for DiscoverLocationHere.
 */
class CategoriesRepository(private val placesAPI: PlacesAPI) {

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
//        val categoriesMap = categoriesResponse.groupBy { it.within.isEmpty() }
//        val parentCategories = categoriesMap[true]?. // Categories without parent

//        val children = categoriesMap[false]

    }

    private fun CategoryItemResponse.mapToUi(children: Set<CategoryItemResponse>? = null): PlaceCategory {
        return PlaceCategory(
            id = id,
            name = title,
            imageUrl = icon,
            children = children?.map { it.mapToUi() })
    }
}

/**
 * Just comma between lat and lng
 */
fun Location.toApiParameter() = "$latitude,$longitude"