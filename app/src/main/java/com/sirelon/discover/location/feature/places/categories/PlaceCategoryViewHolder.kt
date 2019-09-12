package com.sirelon.discover.location.feature.places.categories

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sirelon.discover.location.R
import com.sirelon.discover.location.utils.loadUrl

/**
 * Created on 2019-09-12 17:03 for DiscoverLocationHere.
 */
open class PlaceCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val placeCategoryName = view.findViewById<TextView>(R.id.placeCategoryName)
    val placeCategoryIcon = view.findViewById<ImageView>(R.id.placeCategoryIcon)

    fun bind(category: PlaceCategory) {
        placeCategoryName.text = category.name
        placeCategoryIcon.loadUrl(category.imageUrl)
    }

}