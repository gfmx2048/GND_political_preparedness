package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.repositories.ElectionsApiStatus
import com.example.android.politicalpreparedness.repositories.VoterApiStatus
import com.example.android.politicalpreparedness.setDisplayHomeAsUpEnabled
import com.example.android.politicalpreparedness.setTitle
import com.google.android.material.snackbar.Snackbar

class VoterInfoFragment : Fragment() {

    private val viewModel: VoterInfoViewModel by viewModels {
        VoterInfoViewModelFactory(requireActivity().application)
    }

    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info,container,false)

        viewModel.currentElection = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElection
        viewModel.currentElection?.id?.let { viewModel.searchElectionId(it) }
        viewModel.currentElection?.let { viewModel.getVoterInfo(it) }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        //TODO: Add binding values

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        subscribeToLiveData()
        return binding.root
    }

    private fun subscribeToLiveData() {
        viewModel.showSnackBar.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(binding.clParent, it, Snackbar.LENGTH_INDEFINITE).show()
            }
        })

        viewModel.url.observe(viewLifecycleOwner, {
            it?.let {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
                viewModel.clearUrl()
            }
        })

        viewModel.voterApiStatus.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.clearVoterStatus()
                when(it){
                    VoterApiStatus.LOADING -> binding.statusLoadingWheel.visibility = View.VISIBLE
                    VoterApiStatus.ERROR -> {
                        binding.statusLoadingWheel.visibility = View.GONE
                        Snackbar.make(binding.clParent, getString(R.string.voter_info_error), Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry) {
                            viewModel.currentElection?.let {election->
                                viewModel.getVoterInfo(election)
                            }
                        }.show()
                    }
                    VoterApiStatus.DONE -> binding.statusLoadingWheel.visibility = View.GONE
                }
            }
        })
    }

    //TODO: Create method to load URL intents

}