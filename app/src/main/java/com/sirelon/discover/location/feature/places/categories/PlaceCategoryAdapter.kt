package com.sirelon.discover.location.feature.places.categories

import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.sirelon.discover.location.R
import com.sirelon.discover.location.feature.places.entites.PlaceCategoryDiffCallback
import com.sirelon.discover.location.feature.places.entites.PlaceCategory
import com.sirelon.discover.location.utils.inflate
import java.util.Random


/**
 * Created on 2019-09-12 13:09 for DiscoverLocationHere.
 */
class PlaceCategoryAdapter(val clickCallback: (placeCategory: PlaceCategory) -> Unit) :
    ListAdapter<PlaceCategory, PlaceCategoryViewHolder>(
        PlaceCategoryDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceCategoryViewHolder {
        val slithyWhite = ContextCompat.getColor(parent.context, R.color.slithyWhite)
        val slithyBlack = ContextCompat.getColor(parent.context, R.color.slithyBlack)

        fun getContrastColor(color: Int): Int {
            val y =
                ((299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000).toDouble()
            return if (y >= 128) slithyBlack else slithyWhite
        }

        val viewHolder =
            PlaceCategoryViewHolder(parent.inflate(R.layout.place_category_item))
        val rnd = Random()
        val color = Color.argb(230, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        viewHolder.itemView.setBackgroundColor(color)

        viewHolder.placeCategoryName.setTextColor(getContrastColor(color))

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