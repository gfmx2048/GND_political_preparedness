package com.example.android.politicalpreparedness

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election

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