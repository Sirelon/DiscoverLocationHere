package com.sirelon.discover.location.feature.places.entites

import androidx.recyclerview.widget.DiffUtil

/**
 * Created on 2019-09-12 17:00 for DiscoverLocationHere.
 */
object PlaceCategoryDiffCallback : DiffUtil.ItemCallback<PlaceCategory>() {

    override fun areItemsTheSame(oldItem: PlaceCategory, newItem: PlaceCategory) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PlaceCategory, newItem: PlaceCategory) =
        oldItem == newItem
}

object PlaceDiffCallback : DiffUtil.ItemCallback<Place>() {

    override fun areItemsTheSame(oldItem: Place, newItem: Place) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Place, newItem: Place) =
        oldItem == newItem
}