package com.sirelon.discover.location.feature

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.sirelon.discover.location.feature.base.BaseViewModel
import com.sirelon.discover.location.feature.places.categories.CategoriesRepository

/**
 * Created on 2019-09-12 12:28 for DiscoverLocationHere.
 */
class MapViewModule(private val categoriesRepository: CategoriesRepository) : BaseViewModel() {

    private val locationTrigger = MutableLiveData<Location>()

    val categoriesLiveData = locationTrigger.switchMap { location ->
        liveData(safetyIOContext) {
            val data = categoriesRepository.loadCategories(location)
            emit(data)
        }
    }

    fun onLocationChange(location: Location) {
        if (location != locationTrigger.value) {
            // Trigger only if location was changed
            locationTrigger.postValue(location)
        }
    }

}