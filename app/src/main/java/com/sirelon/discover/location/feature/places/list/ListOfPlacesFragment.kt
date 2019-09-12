package com.sirelon.discover.location.feature.places.list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sirelon.discover.location.R
import com.sirelon.discover.location.feature.MainViewModule
import com.sirelon.discover.location.feature.base.BaseFragment
import kotlinx.android.synthetic.main.list_of_places_screen.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created on 2019-09-12 23:30 for DiscoverLocationHere.
 */
class ListOfPlacesFragment : BaseFragment(R.layout.list_of_places_screen) {

    private val viewModel by sharedViewModel<MainViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val placesAdapter = PlacesAdapter()

        with(placesList) {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context)
            adapter = placesAdapter
        }

        viewModel.placesLiveData.observe(this) {
            placesAdapter.submitList(it)
        }
    }

}