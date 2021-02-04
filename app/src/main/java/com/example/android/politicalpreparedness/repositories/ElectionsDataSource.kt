package com.example.android.politicalpreparedness.repositories

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.Result

/**
 * Created by Vasilis viktoratos on 4/2/2021.
 * Main entry point for accessing elections data.
 */
interface ElectionsDataSource {
    fun getElections(): LiveData<List<Election>?>
    suspend fun saveElection(election: Election)
    suspend fun getElection(id: String): Result<Election>
    suspend fun deleteAllElections()
    suspend fun refreshElectionsOnline()
    fun electionsStatus():LiveData<ElectionsApiStatus?>
}