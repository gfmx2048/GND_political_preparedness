package com.example.android.politicalpreparedness.network


import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

private const val BASE_URL = "https://civicinfo.googleapis.com/civicinfo/v2/"

private val moshi = Moshi.Builder()
        .add(ElectionAdapter())
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(CivicsHttpClient.getClient())
        .baseUrl(BASE_URL)
        .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 *  Key is added by http client
 */

interface CivicsApiService {

    /**
     * Get list of available elections
     * Do not supply a request body with this method.
     */
    @GET("elections")
    suspend fun getElectionsAsync(): ElectionResponse// with the suspend function we don't need deferred<>

    //TODO: Add voterinfo API Call
    /**
     * Looks up information relevant to a voter based on the voter's registered address.
     * Required query parameters: address -> The registered address of the voter to look up.
     */
    @GET("voterinfo")
    suspend fun getVoterInfoAsync(@Query("address")address: String): VoterInfoResponse

    /**
     * Looks up political geography and representative information for a single address
     * optional query parameters: address
     */
    @GET("representatives")
    suspend fun getRepresentativesAsync(@Query("address")address: String?): RepresentativeResponse

}

object CivicsApi {
    val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }
}