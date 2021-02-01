package com.example.android.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass

/**
 * kind	- string	- Identifies what kind of resource this is. Value: the fixed string "civicinfo#electionsQueryResponse".
 * elections[] - list	- A list of available elections
 */
@JsonClass(generateAdapter = true)
data class ElectionResponse(
        val kind: String,
        val elections: List<Election>
)