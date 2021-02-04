package com.example.android.politicalpreparedness.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber



enum class ElectionsApiStatus{
    LOADING,
    ERROR,
    DONE
}

/**
 * Created by Vasilis viktoratos on 4/2/2021.
 * @param electionDao the dao that does the Room db operations
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class ElectionRepository( private val electionDao: ElectionDao, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ElectionsDataSource {

    //the internal mutableLiveData
    private val _status = MutableLiveData<ElectionsApiStatus?>()
    override fun electionsStatus() = _status

    override fun getElections(): LiveData<List<Election>?>  = electionDao.getElections()

    override suspend fun saveElection(election: Election) {
       withContext(ioDispatcher){
           electionDao.saveElection(election)
       }
    }

    override suspend fun getElection(id: String): Result<Election>  = withContext(ioDispatcher){
        try {
            val election = electionDao.getElectionsById(id)
            if (election != null) {
                return@withContext Result.Success(election)
            } else {
                return@withContext Result.Error("Election not found!")
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e.localizedMessage)
        }
    }

    override suspend fun deleteAllElections() {
        withContext(ioDispatcher) {
            electionDao.deleteAllElections()
        }
    }

    override suspend fun refreshElectionsOnline() = withContext(ioDispatcher) {
        try {
            _status.postValue(ElectionsApiStatus.LOADING)
            val electionsResponse = CivicsApi.retrofitService.getElectionsAsync()
            electionDao.insertAll(*electionsResponse.elections.toTypedArray())
            _status.postValue(ElectionsApiStatus.DONE)
        } catch (e: Exception) {
            Timber.e("Refresh Elections Online Failed :$e")
            _status.postValue(ElectionsApiStatus.ERROR)
        }
    }

}