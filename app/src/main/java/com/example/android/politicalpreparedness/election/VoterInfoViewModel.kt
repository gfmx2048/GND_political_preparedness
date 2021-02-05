package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.SingleLiveEvent
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repositories.ElectionsDataSource
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val dataSource: ElectionsDataSource) : ViewModel() {

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    var currentElection: Election? = null

    val voterInfo = dataSource.getVoterInfo()
    val voterApiStatus = dataSource.voterStatus()

    fun clearVoterStatus(){
        dataSource.clearVoterStatus()
    }

    //the internal mutableLiveData
    private val _url = MutableLiveData<String?>()
    //the external immutable LiveData
    val url: LiveData<String?>
        get() = _url

    //the internal mutableLiveData
    private val _electionId = MutableLiveData<Int>()

    val savedElection = Transformations.switchMap(_electionId) {
        dataSource.getElectionByIdLD(it)
    }

    fun searchElectionId(id:Int){
        _electionId.value = id
    }

    fun handleFollowUnFollow(){
        viewModelScope.launch {
            if(savedElection.value != null)
            dataSource.deleteElectionById(savedElection.value!!.id)
            else {
                currentElection?.let {
                    dataSource.saveElection(it)
                }
            }
        }
    }

    fun setUrlToOpen(url:String){
        _url.value = url
    }

    fun clearUrl() {
       _url.value = null
    }

    fun getVoterInfo(election: Election)= viewModelScope.launch{
        dataSource.refreshVoterInfo(election)
    }

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs
}