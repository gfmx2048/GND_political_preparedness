package com.example.android.politicalpreparedness

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

/**
 * Created by vasilis viktoratos on 4/2/2021.
 */

@BindingAdapter("electionsAdapter")
fun RecyclerView.bindList(elections: List<Election>?){
    elections?.let {
        visibility = View.VISIBLE
        (adapter as ElectionListAdapter).submitList(it)
    }
}

@BindingAdapter("followText")
fun Button.followText(election: Election?){
    text = if(election?.isSaved == true)
        context.getString(R.string.unfollow)
    else
        context.getString(R.string.follow)
}

@BindingAdapter("handleVisibility")
fun TextView.bindVisibility(voterInfoResponse: VoterInfoResponse?){
    if(voterInfoResponse == null) {
        visibility = View.GONE
        return
    }

    val info = voterInfoResponse.state?.getOrNull(0)?.electionAdministrationBody

    if(info == null) {
        visibility = View.GONE
        return
    }

    when(id){
        R.id.state_locations -> {
            visibility = if(info.votingLocationFinderUrl.isNullOrEmpty()){ View.GONE }else{ View.VISIBLE }
        }
        R.id.state_ballot ->{
            visibility = if(info.ballotInfoUrl.isNullOrEmpty()){ View.GONE }else{ View.VISIBLE }
        }
        R.id.state_correspondence_header->{
            visibility = if(info.correspondenceAddress?.toFormattedString().isNullOrEmpty()){ View.GONE }else{ View.VISIBLE }
        }
        R.id.address->{
            visibility = if(info.correspondenceAddress?.toFormattedString().isNullOrEmpty()){ View.GONE }else{ View.VISIBLE }
            text = info.correspondenceAddress?.toFormattedString()
        }
    }
}