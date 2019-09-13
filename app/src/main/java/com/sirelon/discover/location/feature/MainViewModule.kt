package com.sirelon.discover.location.feature

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.sirelon.discover.location.feature.base.BaseViewModel
import com.sirelon.discover.location.feature.location.Coordinates
import com.sirelon.discover.location.feature.places.PlacesRepository
import com.sirelon.discover.location.feature.places.entites.PlaceCategory

/**
 * Created on 2019-09-12 12:28 for DiscoverLocationHere.
 */
class MainViewModule(private val placesRepository: PlacesRepository) : BaseViewModel() {

    private val locationTrigger = MutableLiveData<Coordinates>()

    val categoriesLiveData = locationTrigger.switchMap { location ->
        liveData(safetyIOContext) {
            val data = placesRepository.loadCategories(location)
            emit(data)
        }
    }

    private val selectedItemsTrigger = MutableLiveData<Set<PlaceCategory>>()

    val placesLiveData = selectedItemsTrigger.switchMap { selectedCategories ->
        liveData(safetyIOContext) {
            val location = locationTrigger.value
            if (location != null && !selectedCategories.isNullOrEmpty()) {
                val data = placesRepository.findPopularPlaces(location, selectedCategories)
                emit(data)
            } else {
                emit(null)
            }
        }
    }

    init {

    }

    fun onLocationChange(location: Coordinates) {
        if (location != locationTrigger.value) {
            // Trigger only if location was changed
            locationTrigger.postValue(location)
        }
    }

    fun loadCategoryById(id: String): PlaceCategory? {
        return categoriesLiveData.value?.find { it.id == id }
    }

    fun changeSelection(parent: PlaceCategory, itemsToAdd: List<PlaceCategory>) {
        // Items to be unselected
        val unselectedList = parent.children?.subtract(itemsToAdd) ?: emptySet()
        // Merge current selected values with new set of them
        val selectedCategories = itemsToAdd.toMutableSet() + (getSelectedItems() - unselectedList)
        selectedItemsTrigger.postValue(selectedCategories)
    }

    /**
     * returns all selected categories by user
     */
    fun getSelectedItems() = selectedItemsTrigger.value ?: emptySet()

    fun resestSelection() {
        selectedItemsTrigger.postValue(null)
    }
}