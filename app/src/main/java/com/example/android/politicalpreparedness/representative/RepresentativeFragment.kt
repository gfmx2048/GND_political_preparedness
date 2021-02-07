package com.example.android.politicalpreparedness.representative

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.politicalpreparedness.*
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.repositories.RepresentativeApiStatus
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_representative.view.*
import kotlinx.android.synthetic.main.fragment_voter_info.*

class DetailFragment : Fragment() {

    private val viewModel: RepresentativeViewModel by activityViewModels(){
        RepresentativeViewModelFactory(requireActivity().application)
    }
    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative,container,false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val states = resources.getStringArray(R.array.states)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, states)
        binding.state.adapter = adapter
        binding.state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
               viewModel.selectedCurrentAddress.state = states[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
               //NO OP
            }

        }

        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.rvMyRepresentative.adapter = RepresentativeListAdapter(RepresentativeListener {
                //NO OP
        })

        binding.buttonLocation.setOnClickListener {  viewModel.getLocationF() }
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()

            if(viewModel.selectedCurrentAddress.line1.isEmpty()){
                Toast.makeText(requireContext(),"Add a street address",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(viewModel.selectedCurrentAddress.city.isEmpty()){
                Toast.makeText(requireContext(),"Add a city",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(viewModel.selectedCurrentAddress.zip.isEmpty()){
                Toast.makeText(requireContext(),"Add a zip",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.searchRepresentatives()
        }
        subscribeToLiveData()
    return binding.root
    }

    private fun subscribeToLiveData() {
            viewModel.selectedAddress.observe(viewLifecycleOwner, {
                it?.let {
                    val position = resources.getStringArray(R.array.states).indexOf(it.state)
                    binding.state.setSelection(position)
                    binding.invalidateAll()
                }
            })

        viewModel.representativeInfo.observe(viewLifecycleOwner, {
            it?.let {
                binding.rvMyRepresentative.visibility = View.VISIBLE
                (binding.rvMyRepresentative.adapter as RepresentativeListAdapter).submitList(it)
            }
        })

        viewModel.representativeApiStatus.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.clearRepresentativeStatus()
                when(it){
                    RepresentativeApiStatus.LOADING -> binding.statusLoadingWheel.visibility = View.VISIBLE
                    RepresentativeApiStatus.ERROR -> {
                        binding.statusLoadingWheel.visibility = View.GONE
                        Snackbar.make(binding.mlParent, getString(R.string.rep_info_error), Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry) {
                                viewModel.searchRepresentatives()
                        }.show()
                    }
                    RepresentativeApiStatus.DONE -> binding.statusLoadingWheel.visibility = View.GONE
                }
            }
        })
    }

}