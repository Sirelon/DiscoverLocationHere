package com.sirelon.discover.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

private const val LOCATION_REQUEST_CODE = 121
private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                LOCATION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onLocationPermissionGranted()
        } else {
            // Show rationale and request permission.
            onLocationPermissionDenied()
        }
    }

    private fun onLocationPermissionDenied() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION)) {
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
        ActivityCompat.requestPermissions(this, arrayOf(LOCATION_PERMISSION), LOCATION_REQUEST_CODE)
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
        // TODO
    }
}
