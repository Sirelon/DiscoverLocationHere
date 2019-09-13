package com.sirelon.discover.location.feature.places.list

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sirelon.discover.location.R
import com.sirelon.discover.location.feature.MainViewModel
import kotlinx.android.synthetic.main.list_of_places_screen.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created on 2019-09-12 23:30 for DiscoverLocationHere.
 */
class ListOfPlacesFragment : Fragment(R.layout.list_of_places_screen) {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val placesAdapter = PlacesAdapter {
            viewModel.showPlace(it)
            // Hide fragment
            activity?.onBackPressed()
        }

        with(placesList) {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context)
            adapter = placesAdapter
        }

        viewModel.placesLiveData.observe(this) {
            if (it.isNullOrEmpty()) {
                placesEmptyView.visibility = View.VISIBLE
            } else {
                placesEmptyView.visibility = View.GONE
            }
            placesAdapter.submitList(it)
        }
    }

    override fun getEnterTransition() = Slide(Gravity.BOTTOM)

    // When NOT Popping the back stack
    override fun getExitTransition() = Slide(Gravity.BOTTOM)

    // When Popping the back stack
    // Need to be TransitionSet from FRAMEWORK
//    java.lang.ClassCastException: androidx.transition.TransitionSet cannot be cast to android.transition.Transition
    override fun getReturnTransition() = TransitionSet().addTransition(Slide(Gravity.BOTTOM))
}