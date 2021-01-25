package com.mezda.aciud.ui.lifting_flow.party_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentPartyBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModel
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PartyInfoFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var factory: LiftingFlowViewModelProvider
    private val viewModel by navGraphViewModels<LiftingFlowViewModel>(R.id.liftingGraph) {
        factory
    }

    lateinit var binding: FragmentPartyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_party, container, false)

        viewModel.flag.observe(viewLifecycleOwner, {
            binding.flagSpinner.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                it.toTypedArray()
            )
        })

        viewModel.getFlags()
        binding.nextButton.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.nextButton.id -> {
                if (validateRequiredInfo()) {
                    viewModel.savePartyInfo(
                        getSympathizerOptionValue(),
                        binding.flagSpinner.selectedItemPosition
                    )
                    launchDirection(PartyInfoFragmentDirections.actionPartyInfoFragmentToSearchFragment())
                }
            }
        }
    }

    private fun validateRequiredInfo(): Boolean {
        return when {
            binding.optionsRadio.checkedRadioButtonId == -1 -> {
                toast("Estatus requerido")
                false
            }
            binding.flagSpinner.selectedItemPosition == 0 -> {
                toast("Bandera Requerida")
                false
            }
            else -> true
        }
    }

    private fun getSympathizerOptionValue(): Int {
        return when (binding.optionsRadio.checkedRadioButtonId) {
            binding.noRadio.id -> 1
            binding.yesRadio.id -> 2
            binding.fanRadio.id -> 3
            else -> 1
        }
    }
}