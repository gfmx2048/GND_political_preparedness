package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.repositories.ElectionsApiStatus
import com.example.android.politicalpreparedness.setDisplayHomeAsUpEnabled
import com.example.android.politicalpreparedness.setTitle
import com.google.android.material.snackbar.Snackbar

class ElectionsFragment: Fragment() {

    private val viewModel: ElectionsViewModel by viewModels {
        ElectionsViewModelFactory(requireActivity().application)
    }
    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election,container,false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.rvUpcoming.adapter = ElectionListAdapter(ElectionListener { viewModel.onElectionClicked(it) })
        binding.rvSaved.adapter = ElectionListAdapter(ElectionListener { viewModel.onElectionClicked(it) })

        subscribeToLiveData()
        return binding.root
    }

    private fun subscribeToLiveData() {
        viewModel.selectedElection.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.clearSelectedElection()
                this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it))
            }
        })

        viewModel.refreshStatusOfElections.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.clearSelectedElection()
                when(it){
                    ElectionsApiStatus.LOADING -> binding.statusLoadingWheel.visibility = View.VISIBLE
                    ElectionsApiStatus.ERROR -> {
                        binding.statusLoadingWheel.visibility = View.GONE
                        Snackbar.make(binding.clParentElections, getString(R.string.elections_error), Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry) {
                            viewModel.refreshOnlineElections()
                        }.show()
                    }
                    ElectionsApiStatus.DONE -> binding.statusLoadingWheel.visibility = View.GONE
                }
            }
        })
    }

}