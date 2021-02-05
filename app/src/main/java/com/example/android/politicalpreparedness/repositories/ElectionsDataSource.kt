package com.example.android.politicalpreparedness.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.Result
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

/**
 * Created by Vasilis viktoratos on 4/2/2021.
 * Main entry point for accessing elections data.
 */
interface ElectionsDataSource {
    fun getElections(): LiveData<List<Election>?>
    fun getUpcomingElections(): LiveData<List<Election>?>
    suspend fun saveElection(election: Election)
    suspend fun getElection(id: Int): Result<Election>
    fun getElectionByIdLD(id:Int): LiveData<Election?>
    suspend fun deleteAllElections()
    suspend fun deleteElectionById(id:Int)
    suspend fun refreshElectionsOnline()
    fun electionsStatus():LiveData<ElectionsApiStatus?>
    fun clearElectionStatus()
    fun voterStatus(): LiveData<VoterApiStatus?>
    fun clearVoterStatus()
    suspend fun refreshVoterInfo(election: Election)
    fun getVoterInfo(): LiveData<VoterInfoResponse?>
}