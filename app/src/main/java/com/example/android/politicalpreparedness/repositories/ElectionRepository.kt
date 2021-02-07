package com.example.android.politicalpreparedness.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.*
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber



enum class ElectionsApiStatus{
    LOADING,
    ERROR,
    DONE
}

enum class VoterApiStatus{
    LOADING,
    ERROR,
    DONE
}

enum class RepresentativeApiStatus{
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

    override fun clearElectionStatus() {
        _status.value = null
    }

    //the internal mutableLiveData
    private val _statusVoter = MutableLiveData<VoterApiStatus?>()
    override fun voterStatus() = _statusVoter

    override fun clearVoterStatus() {
        _statusVoter.value = null
    }

    //the internal mutableLiveData
    private val _statusRep = MutableLiveData<RepresentativeApiStatus?>()
    override fun representativeStatus() = _statusRep

    override fun clearRepresentativeStatus() {
        _statusRep.value = null
    }

    //the internal mutableLiveData
    private val _upcomingElections = MutableLiveData<List<Election>?>()
    override fun getUpcomingElections(): LiveData<List<Election>?> = _upcomingElections

    //the internal mutableLiveData
    private val _voterInfo = MutableLiveData<VoterInfoResponse?>()
    override fun getVoterInfo(): LiveData<VoterInfoResponse?> = _voterInfo

    private val _representativeInfo = MutableLiveData<List<Representative>>()
    override fun getRepresentativeInfo(): LiveData<List<Representative>> = _representativeInfo

    override fun getElections(): LiveData<List<Election>?>  = electionDao.getElections()

    override suspend fun saveElection(election: Election) {
       withContext(ioDispatcher){
           election.isSaved = true
           electionDao.saveElection(election)
       }
    }

    override suspend fun getElection(id: Int): Result<Election>  = withContext(ioDispatcher){
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

    override fun getElectionByIdLD(id: Int): LiveData<Election?>  = electionDao.getElectionsByIdLD(id)

    override suspend fun deleteAllElections() {
        withContext(ioDispatcher) {
            electionDao.deleteAllElections()
        }
    }

    override suspend fun deleteElectionById(id: Int) {
        withContext(ioDispatcher) {
            electionDao.deleteElections(id)
        }
    }

    override suspend fun refreshElectionsOnline() = withContext(ioDispatcher) {
        try {
            Timber.d("Refresh Elections")
            _status.postValue(ElectionsApiStatus.LOADING)
            val electionsResponse = CivicsApi.retrofitService.getElectionsAsync()
            _upcomingElections.postValue(electionsResponse.elections)
            _status.postValue(ElectionsApiStatus.DONE)
        } catch (e: Exception) {
            Timber.e("Refresh Elections Online Failed :$e")
            _status.postValue(ElectionsApiStatus.ERROR)
        }
    }

    override suspend fun refreshVoterInfo(election: Election) = withContext(ioDispatcher) {
        try {
            Timber.d("Refresh Voter Info")
            _statusVoter.postValue(VoterApiStatus.LOADING)
            val voterResponse = CivicsApi.retrofitService.getVoterInfoAsync(election.id,if(election.division.state.isEmpty()) election.division.country else "${election.division.country}/${election.division.state}")
            _voterInfo.postValue(voterResponse)
            _statusVoter.postValue(VoterApiStatus.DONE)
        } catch (e: Exception) {
            Timber.e("Refresh VoterInfo Online Failed :$e")
            _statusVoter.postValue(VoterApiStatus.ERROR)
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */
    override suspend fun refreshRepresentatives(address: Address) = withContext(ioDispatcher) {
        try {
            Timber.d("Refresh representatives")
            _statusRep.postValue(RepresentativeApiStatus.LOADING)
            val (offices, officials) = CivicsApi.retrofitService.getRepresentativesAsync(address.toFormattedString())
            _representativeInfo.postValue(offices.flatMap { office -> office.getRepresentatives(officials) })
            _statusRep.postValue(RepresentativeApiStatus.DONE)
        } catch (e: Exception) {
            Timber.e("Refresh representatives Online Failed :$e")
            _statusRep.postValue(RepresentativeApiStatus.ERROR)
        }
    }

}