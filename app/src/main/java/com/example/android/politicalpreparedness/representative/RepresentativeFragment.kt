package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class RepresentativeFragment : Fragment() {

    companion object {
        //DONE: Add Constant for Location request
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    //DONE: Establish bindings
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var representativeAdapter: RepresentativeListAdapter

    //DONE: Declare ViewModel
    private val viewModel: RepresentativeViewModel by lazy {

        val application = requireNotNull(this.activity).application
        val viewModelFactory =  RepresentativeViewModel.Factory(application)
        ViewModelProvider(this,viewModelFactory)
                .get(RepresentativeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.address = Address("", "", "", "", "")


        val states = resources.getStringArray(R.array.states)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, states)
        binding.state.adapter = adapter

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions(it)
        }

        //DONE: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.getRepresentatives()
        }

        //DONE: Define and assign Representative adapter
        representativeAdapter = RepresentativeListAdapter()
        binding.representativesRecyclerView.adapter = representativeAdapter

        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //DONE: Populate Representative adapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer { representatives ->
            representativeAdapter.submitList(representatives)

        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //DONE: Handle location permission result to get location on permission granted
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocation()
                }
            }
        }
    }

    private fun checkLocationPermissions(view: View): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            true
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                getLocation()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            }
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        //DONE: Check if permission is already granted and return (true = granted, false = denied/other)
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //DONE: Get location from LocationServices
        val locationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                    //DONE: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
                        val address = geoCodeLocation(location)
                        viewModel.address.value = address

                        val states = resources.getStringArray(R.array.states)
                        val selectedStateIndex = states.indexOf(address.state)
                        binding.state.setSelection(selectedStateIndex)

                        viewModel.getRepresentatives()
                    }
                }
                .addOnFailureListener { e -> e.printStackTrace() }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}