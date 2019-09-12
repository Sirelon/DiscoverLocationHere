package com.sirelon.discover.location.feature

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sirelon.discover.location.R
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

class MainActivity : AppCompatActivity() {

    private val mapInteractor: MapInteractor = GoogleMapInteractor()

    private val viewModel by viewModel<MainViewModule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            placesAdapter.submitList(it)
        }

        viewModel.placesLiveData.observe(this) {
            it?.let { mapInteractor.showMarkers(it) }
        }

        actionResetAll.setOnClickListener {
            mapInteractor.clearAllMarkers()
            viewModel.resestSelection()
        }

        actionShowAsList.setOnClickListener {
            replaceFragment(ListOfPlacesFragment(), true)
        }
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

    private fun isLocationGranted() =
        ContextCompat.checkSelfPermission(
            this,
            LOCATION_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun onLocationPermissionGranted() {
        // TODO
        mapInteractor.onLocationPermissionGranted()

        val locationListener =
            LocationListener(this) {
                // Uncomment if you want to follow by user
//                mapInteractor.showLocation(it.latitude, it.longitude)
                viewModel.onLocationChange(it)
            }
        lifecycle.addObserver(locationListener)
    }

    private fun replaceFragment(fragment: Fragment, withBackStack: Boolean) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).apply {
            if (withBackStack) {
                addToBackStack(null)
            }
        }.commit()
    }
}
