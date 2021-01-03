package com.mezda.aciud.ui.lifting_flow.ocupation_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentOccupationBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModel
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OccupationInfoFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var factory: LiftingFlowViewModelProvider
    private val viewModel by navGraphViewModels<LiftingFlowViewModel>(R.id.liftingGraph) {
        factory
    }

    lateinit var binding: FragmentOccupationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_occupation, container, false)

        viewModel.profession.observe(viewLifecycleOwner) {
            binding.professionSpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )
            val professionPosition = viewModel.getProfessionIndex()
            if (professionPosition != 0) binding.professionSpinner.setSelection(professionPosition)
        }

        viewModel.supportType.observe(viewLifecycleOwner) {
            binding.supportTypeSpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )
            val supportTypePosition = viewModel.getSupportTypeIndex()
            if (supportTypePosition != 0) binding.supportTypeSpinner.setSelection(supportTypePosition)
        }

        viewModel.onGetProfessions()
        viewModel.onGetSupportType()

        binding.nextButton.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.nextButton.id -> {
                if (validateRequiredInfo()) {
                    viewModel.saveOccupationInfo(
                            binding.professionSpinner.selectedItemPosition,
                            binding.supportTypeSpinner.selectedItemPosition,
                            binding.observationsTextArea.text.toString()
                    )
                    launchDirection(OccupationInfoFragmentDirections.actionOccupationInfoFragmentToPartyInfoFragment())
                } else {
                    toast("Información Requerida")
                }

            }
        }
    }

    private fun validateRequiredInfo(): Boolean {
        return when {
            binding.professionSpinner.selectedItemPosition == 0 -> {
                toast("Profession faltante")
                false
            }
            binding.supportTypeSpinner.selectedItemPosition == 0 -> {
                toast("Tipo de Apoyo faltante")
                false
            }
            else -> true
        }
    }
}
