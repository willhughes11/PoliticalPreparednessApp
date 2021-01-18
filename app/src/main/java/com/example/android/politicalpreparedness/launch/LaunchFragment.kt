package com.example.android.politicalpreparedness.launch

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentLaunchBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.upcomingElectionsButton.setOnClickListener { navigateToElections() }
        binding.representativesButton.setOnClickListener{ navigateToRepresentatives() }

        return binding.root
    }

    private fun navigateToElections() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchToElections())
    }

    private fun navigateToRepresentatives() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchToRepresentative())
    }

}
