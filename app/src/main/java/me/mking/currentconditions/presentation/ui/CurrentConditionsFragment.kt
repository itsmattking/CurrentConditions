package me.mking.currentconditions.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import me.mking.currentconditions.R
import me.mking.currentconditions.presentation.viewmodels.CurrentConditionsViewModel
import me.mking.currentconditions.presentation.viewmodels.CurrentConditionsViewState

@AndroidEntryPoint
class CurrentConditionsFragment : Fragment() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            checkForLocationPermissions()
        }

    companion object {
        fun newInstance() = CurrentConditionsFragment()
    }

    private val viewModel: CurrentConditionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_conditions_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner) { handleState(it) }
        checkForLocationPermissions()
    }

    private fun handleState(state: CurrentConditionsViewState) {
        when (state) {
            CurrentConditionsViewState.Loading -> Unit
            is CurrentConditionsViewState.Ready -> Log.d(
                "CurrentConditions",
                state.currentWeather.condition
            )
            CurrentConditionsViewState.Error -> Unit
            CurrentConditionsViewState.LocationNotAvailable -> Unit
        }
    }

    private fun checkForLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            viewModel.load()
        }
    }
}