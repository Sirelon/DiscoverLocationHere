package com.sirelon.discover.location.feature

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.sirelon.discover.location.feature.base.BaseViewModel
import com.sirelon.discover.location.feature.location.Coordinates
import com.sirelon.discover.location.feature.places.PopularPlacesRepository
import com.sirelon.discover.location.feature.places.categories.CategoriesRepository
import com.sirelon.discover.location.feature.places.entites.Place
import com.sirelon.discover.location.feature.places.entites.PlaceCategory
import kotlinx.coroutines.launch

/**
 *
 * Created on 2019-09-12 12:28 for DiscoverLocationHere.
 */
class MainViewModel(
    // For load categories
    private val categoriesRepository: CategoriesRepository,
    // For load places
    private val popularPlacesRepository: PopularPlacesRepository
) : BaseViewModel() {

    private val locationTrigger = MutableLiveData<Coordinates>()

    val allCategoriesLiveData = locationTrigger.switchMap { location ->
        liveData(safetyIOContext) {
            val data = categoriesRepository.loadCategories(location)
            emit(data)
        }
    }

    val placesLiveData = MutableLiveData<List<Place>>()

    val goToCoordinatesLiveData = MutableLiveData<Coordinates>()

    private var selectedCategories = mutableSetOf<PlaceCategory>()

    fun onLocationChange(location: Coordinates) {
        if (location != locationTrigger.value) {
            locationTrigger.value = location
            // Trigger only if location was changed
            loadPopularPlaces()
        }
    }

    fun loadCategoryById(id: String): PlaceCategory? {
        return allCategoriesLiveData.value?.find { it.id == id }
    }

    fun changeSelection(parent: PlaceCategory, itemsToAdd: List<PlaceCategory>) {
        // Items to be unselected
        val unselectedList = parent.children?.subtract(itemsToAdd) ?: emptySet()
        // Merge current selected values with new set of them
        selectedCategories.removeAll(unselectedList)
        selectedCategories.addAll(itemsToAdd)
        loadPopularPlaces()
    }

    /**
     * returns all selected categories by user
     */
    fun getSelectedItems() = selectedCategories.toList()

    fun resestSelection() {
        selectedCategories.clear()
    }

    /**
     * Combine Latest ;)
     */
    private fun loadPopularPlaces() {
        val coordinates = locationTrigger.value ?: return
        if (selectedCategories.isEmpty()) return

        viewModelScope.launch(safetyIOContext) {
            val data = popularPlacesRepository.findPopularPlaces(coordinates, selectedCategories)
            placesLiveData.postValue(data)
        }
    }

    fun showPlace(place: Place) {
        goToCoordinatesLiveData.postValue(Coordinates(place.latitude, place.longtude))
    }
}