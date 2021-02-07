package com.example.android.politicalpreparedness.representative

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.SingleLiveEvent
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repositories.ElectionsDataSource
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class RepresentativeViewModel(private val dataSource: ElectionsDataSource): ViewModel() {
    //the internal mutableLiveData
    private val _getLocation = MutableLiveData<Boolean?>()
    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()

    //the external immutable LiveData
    val getLocation: LiveData<Boolean?>
        get() = _getLocation

    fun getLocationF() {_getLocation.value = true}
    fun clearGetLocation(){ _getLocation.value = null }

    init {
        Timber.d("ViewModel Init")
    }

    var selectedCurrentAddress = Address("","","","","")

    //the internal mutableLiveData
    private val _selectedAddress = MutableLiveData<Address?>()
    //the external immutable LiveData
    val selectedAddress: LiveData<Address?>
        get() = _selectedAddress


    val representativeInfo = dataSource.getRepresentativeInfo()
    val representativeApiStatus = dataSource.representativeStatus()


    fun geoCodeLocation(location: Location, geoCoder: Geocoder): Address {
        return geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare?:"", address.subThoroughfare?:"", address.locality?:"", address.adminArea?:"", address.postalCode?:"")
                }
                .first()
    }

    fun setAddress(address: Address) {
        _selectedAddress.value = address
        selectedCurrentAddress = address
    }

    fun searchRepresentatives() {
        viewModelScope.launch {
            dataSource.refreshRepresentatives(selectedCurrentAddress)
        }
    }

    fun clearRepresentativeStatus() {
        dataSource.clearRepresentativeStatus()
    }

}
