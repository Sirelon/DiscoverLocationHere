package com.sirelon.discover.location.feature.places.categories

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ListAdapter
import com.sirelon.discover.location.R
import com.sirelon.discover.location.utils.inflate

/**
 * Created on 2019-09-12 16:57 for DiscoverLocationHere.
 */
class CategorySelectionAdapter :
    ListAdapter<PlaceCategory, CategorySelectionAdapter.SelectionCategoryViewHolder>(
        PlaceCategoryDiffCallback
    ) {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SelectionCategoryViewHolder((parent.inflate(R.layout.place_category_item_selection)))

    override fun onBindViewHolder(holder: SelectionCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.isActivated = tracker?.isSelected(keyFromPosition(position)) == true
        holder.itemView.setOnClickListener {
            tracker?.select(keyFromPosition(position))
        }
    }

    override fun getItemId(position: Int) = keyFromPosition(position)

    private fun keyFromPosition(position: Int) = position.toLong()

    class SelectionCategoryViewHolder(view: View) : PlaceCategoryViewHolder(view) {

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
            }
    }
}