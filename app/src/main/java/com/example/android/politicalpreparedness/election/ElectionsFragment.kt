package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.setDisplayHomeAsUpEnabled
import com.example.android.politicalpreparedness.setTitle

class ElectionsFragment: Fragment() {

    private val viewModel: ElectionsViewModel by viewModels {
        ElectionsViewModelFactory(requireActivity().application)
    }
    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        //TODO: Add ViewModel values and create ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election,container,false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.rvUpcoming.adapter = ElectionListAdapter(ElectionListener { viewModel.onElectionClicked(it) })
        binding.rvSaved.adapter = ElectionListAdapter(ElectionListener { viewModel.onElectionClicked(it) })

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters
        subscribeToLiveData()
        return binding.root
    }

    private fun subscribeToLiveData() {
        viewModel.selectedElection.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.clearSelectedElection()
                this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id,it.division))
            }
        })
    }

}