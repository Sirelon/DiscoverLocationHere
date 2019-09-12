package com.sirelon.discover.location.feature.places.categories

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sirelon.discover.location.feature.MainViewModule
import com.sirelon.discover.location.feature.places.entites.PlaceCategory
import kotlinx.android.synthetic.main.category_selection_screen.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created on 2019-09-12 16:44 for DiscoverLocationHere.
 */
class CategorySelectionDialog : DialogFragment() {

    private val viewModel by sharedViewModel<MainViewModule>()

    companion object {
        private const val PLACE_CATEGORY_ID_ARG = ".placeCategoryIdArg"

        fun getInstance(placeCategory: PlaceCategory): DialogFragment {
            return CategorySelectionDialog().apply {
                arguments = bundleOf(PLACE_CATEGORY_ID_ARG to placeCategory.id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            com.sirelon.discover.location.R.layout.category_selection_screen,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectionAdapter = CategorySelectionAdapter()

        with(categorySelectionList) {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context)
            adapter = selectionAdapter
        }

        // Should be AFTER set adapter
        val tracker = SelectionTracker.Builder<Long>(
            "categorySelection",
            categorySelectionList,
            StableIdKeyProvider(categorySelectionList),
            CategorySelectionDetailsLookup(categorySelectionList),
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        selectionAdapter.tracker = tracker

        val parentId =
            arguments?.getString(PLACE_CATEGORY_ID_ARG)!! // I want crash if such dialog is going to open incorrect
        val parentPlaceCategory = viewModel.loadCategoryById(parentId)!!

        val title = "Categories from ${parentPlaceCategory.name}"
        selectionTitle.text = title
        val categoriesChildren = parentPlaceCategory.children
        selectionAdapter.submitList(categoriesChildren)

        preselectItems(categoriesChildren, tracker)

        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            @SuppressLint("SetTextI18n")
            override fun onSelectionChanged() {
                val sizeOfSelection = tracker.selection.size()
                selectionTitle.text = "$title ($sizeOfSelection)"
            }
        })

        actionConfirm.setOnClickListener {
            val selectedItems = tracker.selection.mapNotNull { categoriesChildren?.get(it.toInt()) }
            viewModel.changeSelection(parentPlaceCategory, selectedItems)

            // Bla
            dismiss()
        }

        actionSelectAll.setOnClickListener {
            val selection = (0 until selectionAdapter.itemCount).map { it.toLong() }

            tracker.setItemsSelected(selection, true)
        }
    }

    private fun preselectItems(
        categoriesChildren: List<PlaceCategory>?,
        tracker: SelectionTracker<Long>
    ) {
        val selectedItems = viewModel.getSelectedItems()
        // Find already selected items and map them to List of ids
        val alreadySelectedItems = categoriesChildren?.mapIndexedNotNull { index, placeCategory ->
            if (selectedItems.contains(placeCategory)) index.toLong()
            else null
        }

        if (alreadySelectedItems != null) {
            tracker.setItemsSelected(alreadySelectedItems, true)
        }
    }

    class CategorySelectionDetailsLookup(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y) ?: return null
            return (recyclerView.getChildViewHolder(view) as CategorySelectionAdapter.SelectionCategoryViewHolder).getItemDetails()
        }
    }
}