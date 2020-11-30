package com.mezda.aciud.ui.lifting

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentLiftingBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LiftingFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLiftingBinding
    private val liftingViewModel by viewModels<LiftingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lifting, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        liftingViewModel.localities.observe(viewLifecycleOwner, {
            binding.localitySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it.toTypedArray())
        })

        liftingViewModel.suburb.observe(viewLifecycleOwner,{
            binding.suburbSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it.toTypedArray())
        })

        liftingViewModel.supervisor.observe(viewLifecycleOwner, {
            Timber.e("supervisor ${Gson().toJson(it)}")
            binding.supervisorText.text = it?.name
        })

        liftingViewModel.operator.observe(viewLifecycleOwner, {
            Timber.e("operator ${Gson().toJson(it)}")
            binding.operatorText.text = it?.name
        })

        binding.localitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                liftingViewModel.onLocalitySelected(p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.gpsLocationButton.setOnClickListener(this)
        binding.registerButton.setOnClickListener(this)

        liftingViewModel.onGetSupervisor()
        liftingViewModel.onGetLocalities()
        liftingViewModel.onGetOperator()

        return binding.root
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.gpsLocationButton.id -> {

            }
            binding.registerButton.id -> {

            }
        }
    }
}