package com.sirelon.discover.location.feature

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.sirelon.discover.location.R
import com.sirelon.discover.location.feature.location.LocationListener
import com.sirelon.discover.location.feature.map.GoogleMapInteractor
import com.sirelon.discover.location.feature.map.MapInteractor
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val LOCATION_REQUEST_CODE = 121
private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

class MainActivity : AppCompatActivity() {

    private val mapInteractor: MapInteractor =
        GoogleMapInteractor()

    private val viewModule by viewModel<MapViewModule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = mapInteractor.initWithActivity(this)
        replaceFragment(mapFragment)

        if (isLocationGranted()) {
            onLocationPermissionGranted()
        } else {
            // Show rationale and request permission.
            onLocationPermissionDenied()
        }

        viewModule.categoriesLiveData.observe(this) {
            Log.d("Sirelon", "CATEGORIES are $it")
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
                mapInteractor.showLocation(it.latitude, it.longitude)
                viewModule.onLocationChange(it)
            }
        lifecycle.addObserver(locationListener)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }
}
