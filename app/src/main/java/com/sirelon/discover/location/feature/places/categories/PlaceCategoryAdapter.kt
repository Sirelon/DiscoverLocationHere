package com.sirelon.discover.location.feature.places.categories

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sirelon.discover.location.R
import com.sirelon.discover.location.feature.places.entites.PlaceCategory
import com.sirelon.discover.location.feature.places.entites.PlaceCategoryDiffCallback
import com.sirelon.discover.location.utils.ColorUtils
import com.sirelon.discover.location.utils.inflate


/**
 * Created on 2019-09-12 13:09 for DiscoverLocationHere.
 */
class PlaceCategoryAdapter(
    private val colorUtils: ColorUtils,
    private val clickCallback: (placeCategory: PlaceCategory) -> Unit
) : ListAdapter<PlaceCategory, PlaceCategoryViewHolder>(PlaceCategoryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceCategoryViewHolder {

        val viewHolder =
            PlaceCategoryViewHolder(parent.inflate(R.layout.place_category_item))

        val color = colorUtils.randomColor()
        viewHolder.itemView.setBackgroundColor(color)

        viewHolder.placeCategoryName.setTextColor(colorUtils.getContrastColor(color))
        // Hide for now
        viewHolder.itemView.findViewById<View>(R.id.badgeCounter).visibility = View.GONE
        return viewHolder
    }

    override fun onBindViewHolder(holder: PlaceCategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
        holder.itemView.setOnClickListener {
            clickCallback(category)
        }
    }
}