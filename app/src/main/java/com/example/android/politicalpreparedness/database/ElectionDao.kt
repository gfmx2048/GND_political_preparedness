package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    /**
     * Insert an election in the database. If the election already exists, replace it.
     *
     * @param election the election to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveElection(election: Election)

    /**
     * Insert all elections.
     *
     * @param election the elections to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg election: Election)

    /**
     * @return all elections.
     */
    @Query("SELECT * FROM election_table")
    fun getElections(): LiveData<List<Election>?>

    /**
     * @param electionId the id of the election
     * @return the election object with the electionId
     */
    @Query("SELECT * FROM election_table where id = :electionId")
    suspend fun getElectionsById(electionId: String): Election?

    /**
     * Delete election by id
     */
    @Query("DELETE FROM election_table where id = :electionId")
    suspend fun deleteElections(electionId: String)

    /**
     * Delete all elections.
     */
    @Query("DELETE FROM election_table")
    suspend fun deleteAllElections()

}