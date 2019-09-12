package com.sirelon.discover.location.feature.places.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sirelon.discover.location.R
import com.sirelon.discover.location.feature.places.entites.Place
import com.sirelon.discover.location.feature.places.entites.PlaceDiffCallback
import com.sirelon.discover.location.utils.inflate
import com.sirelon.discover.location.utils.loadUrl
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.place_item.*

/**
 * Created on 2019-09-12 16:57 for DiscoverLocationHere.
 */
class PlacesAdapter : ListAdapter<Place, PlacesAdapter.PlaceViewHolder>(
    PlaceDiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlaceViewHolder(
            (parent.inflate(
                R.layout.place_item
            ))
        )

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = getItem(position)
        holder.placeDistance.text = "Distance ${place.distance}km"
        holder.placeName.text = place.title
        holder.placeIcon.loadUrl(place.imageUrl)
    }

    class PlaceViewHolder(override val containerView: View) : LayoutContainer,
        RecyclerView.ViewHolder(containerView)
}