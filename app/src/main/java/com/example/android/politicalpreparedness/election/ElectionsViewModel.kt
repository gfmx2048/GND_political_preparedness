package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repositories.ElectionsDataSource
import kotlinx.coroutines.launch


class ElectionsViewModel(private val dataSource: ElectionsDataSource): ViewModel() {

    val upcomingElections = dataSource.getUpcomingElections()
    val savedElections = dataSource.getElections()
    val refreshStatusOfElections = dataSource.electionsStatus()

    //the internal mutableLiveData
    private val _selectedElection = MutableLiveData<Election?>()
    //the external immutable LiveData
    val selectedElection: LiveData<Election?>
        get() = _selectedElection

    //Refresh elections and save them in db, then observe db elections data
    init {
       refreshOnlineElections()
    }

    fun refreshOnlineElections() = viewModelScope.launch{
        dataSource.refreshElectionsOnline()
    }

    fun onElectionClicked(election: Election) {
            _selectedElection.value = election
    }

    fun clearSelectedElection(){
        _selectedElection.value = null
    }
}