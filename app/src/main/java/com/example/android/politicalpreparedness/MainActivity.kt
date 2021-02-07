package com.example.android.politicalpreparedness

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.android.politicalpreparedness.databinding.ActivityMainBinding
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import com.example.android.politicalpreparedness.representative.RepresentativeViewModelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mNavController: NavController
    private val viewModel: RepresentativeViewModel by viewModels(){
            RepresentativeViewModelFactory(application)
    }
    private lateinit var binding: ActivityMainBinding

    companion object{
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
    }

    private val resultFromRequestForegroundLocationPermission =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    checkDeviceLocationSettings()
                } else {
                    viewModel.showSnackBar.value = getString(R.string.allow_permission)
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val toolbar = binding.toolbar

        setSupportActionBar(toolbar)

        mNavController = this.findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(mNavController.graph)
        toolbar.setupWithNavController(mNavController, appBarConfiguration)

        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        viewModel.getLocation.observe(this, {
            it?.let {
                viewModel.clearGetLocation()
                checkLocationPermissions()
            }
        })
        viewModel.showSnackBar.observe(this, {
            it?.let { Snackbar.make(binding.llContainer, it, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok) { checkLocationPermissions() }.show()
            }
        })
    }

    private fun checkLocationPermissions() {
        if (!foregroundPermissionEnabled()) {
            askForegroundPermission()
        } else {
            checkDeviceLocationSettings()
        }
    }

    private fun askForegroundPermission()  = resultFromRequestForegroundLocationPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))

    /*
*  Uses the Location Client to check the current state of location settings, and gives the user
*  the opportunity to turn on location services within our app.
*/
    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply { priority = LocationRequest.PRIORITY_LOW_POWER }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this, REQUEST_TURN_DEVICE_LOCATION_ON)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.e("Error getting location settings resolution: ${sendEx.message}")
                }
            } else {
                viewModel.showSnackBar.value = getString(R.string.location_required_error)
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getLocationAfterApproval()
            }
        }
    }

    /*
*  We get the result from asking the user to turn on device location
*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            // We don't rely on the result code, but just check the location setting again
            checkDeviceLocationSettings(false)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAfterApproval(){
        getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val address = viewModel.geoCodeLocation(location, Geocoder(this, Locale.getDefault()))
                        viewModel.setAddress(address)
                    }
                }
                .addOnFailureListener { e -> Timber.e(e) }
    }

}
