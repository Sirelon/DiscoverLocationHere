package com.sirelon.discover.location.feature

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sirelon.discover.location.R
import com.sirelon.discover.location.feature.location.Coordinates
import com.sirelon.discover.location.feature.location.LocationListener
import com.sirelon.discover.location.feature.map.GoogleMapInteractor
import com.sirelon.discover.location.feature.map.MapInteractor
import com.sirelon.discover.location.feature.places.categories.CategorySelectionDialog
import com.sirelon.discover.location.feature.places.categories.PlaceCategoryAdapter
import com.sirelon.discover.location.feature.places.list.ListOfPlacesFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.places_categories_screen.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val LOCATION_REQUEST_CODE = 121
private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
private const val ACTION_CHANGE_VIEW_ID = 123;

class MainActivity : AppCompatActivity() {

    private val mapInteractor: MapInteractor = GoogleMapInteractor()

    private val viewModel by viewModel<MainViewModule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(activityToolbar)

        val mapFragment = mapInteractor.initWithActivity(this)
        replaceFragment(mapFragment, false)

        if (isLocationGranted()) {
            onLocationPermissionGranted()
        } else {
            // Show rationale and request permission.
            onLocationPermissionDenied()
        }

        val placesAdapter = PlaceCategoryAdapter {
            val children = it.children ?: listOf(it)
            if (children.size > 1) {
                CategorySelectionDialog.getInstance(it).show(supportFragmentManager, "Selection")
            } else {
                // No sense to show dialog only for one category
                viewModel.changeSelection(it, children)
            }
        }
        with(categoriesList) {
            itemAnimator = DefaultItemAnimator()
            layoutManager = GridLayoutManager(context, 3)
            adapter = placesAdapter
        }

        viewModel.categoriesLiveData.observe(this) {
            if (it.isNullOrEmpty()) {
                categoriesEmptyView.visibility = View.VISIBLE
            } else {
                categoriesEmptyView.visibility = View.GONE
            }
            placesAdapter.submitList(it)
        }

        viewModel.placesLiveData.observe(this) {
            it?.let { mapInteractor.showMarkers(it) }
        }

        actionResetAll.setOnClickListener {
            mapInteractor.clearAllMarkers()
            viewModel.resestSelection()
        }

        actionSearchHere.setOnClickListener {
            // Get current position of map and trigger view model to refresh categories and places
            mapInteractor.getCurrentCoordinates()?.let { viewModel.onLocationChange(it) }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            // Update toolbar menu each time, when we change fragment
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu ?: return super.onCreateOptionsMenu(menu)

        val title: String
        @DrawableRes
        val icon: Int
        // Depends on which screen visible righ now - change item title and item icon
        if (currentFragmentIsList()) {
            title = "Show as map"
            icon = R.drawable.ic_map
        } else {
            title = "Show as list"
            icon = R.drawable.ic_view_list
        }

        menu.add(0, ACTION_CHANGE_VIEW_ID, 0, title)
            .setIcon(icon)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == ACTION_CHANGE_VIEW_ID) {
            if (currentFragmentIsList()) {
                // Simple pop Our back stack, 'cause list view fragment is added to it
                onBackPressed()
            } else {
                // show list
                // I don't want to replace map fragment. Just Show List above map.
                addFragment(ListOfPlacesFragment())
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            val indexOfLocationPermission =
                permissions.indexOfFirst { it == LOCATION_PERMISSION }
            if (grantResults[indexOfLocationPermission] == PackageManager.PERMISSION_GRANTED) {
                onLocationPermissionGranted()
            } else {
                onLocationPermissionDenied()
            }
        }
    }

    private fun onLocationPermissionGranted() {
        mapInteractor.onLocationPermissionGranted()

        var shouldFollowUser = true

        val locationListener =
            LocationListener(this) {

                if (shouldFollowUser) {
                    mapInteractor.showLocation(it.latitude, it.longitude)
                    // Just once. If you want to follow user -- make it true
                    shouldFollowUser = false
                    viewModel.onLocationChange(Coordinates(it))
                }
            }
        lifecycle.addObserver(locationListener)
    }

    private fun onLocationPermissionDenied() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                LOCATION_PERMISSION
            )
        ) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(
                activityRootContainer,
                "For determinate where are you, we need location permission",
                Snackbar.LENGTH_LONG
            ).setAction("Give") {
                requestLocationPermission()
            }.show()
        } else {
            // Permission has not been granted yet. Request it directly.
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(LOCATION_PERMISSION),
            LOCATION_REQUEST_CODE
        )
    }

    private fun isLocationGranted() =
        ContextCompat.checkSelfPermission(
            this,
            LOCATION_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun replaceFragment(fragment: Fragment, withBackStack: Boolean) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).apply {
            if (withBackStack) {
                addToBackStack(null)
            }
        }.commit()
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun currentFragmentIsList() =
        supportFragmentManager.findFragmentById(R.id.fragmentContainer) is ListOfPlacesFragment

}
