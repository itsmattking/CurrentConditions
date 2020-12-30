package me.mking.currentconditions.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import me.mking.currentconditions.R
import me.mking.currentconditions.presentation.viewmodels.CurrentConditionsViewData
import me.mking.currentconditions.presentation.viewmodels.CurrentConditionsViewModel

@AndroidEntryPoint
class CurrentConditionsFragment : Fragment() {

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.currentConditions.observe(viewLifecycleOwner) {
            when (it) {
                CurrentConditionsViewData.Loading -> Unit
                is CurrentConditionsViewData.Ready -> Log.d(
                    "CurrentConditions",
                    it.currentWeather.condition
                )
                CurrentConditionsViewData.Error -> Unit
            }
        }
        viewModel.load()
    }

}