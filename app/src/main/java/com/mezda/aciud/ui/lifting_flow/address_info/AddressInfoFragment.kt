package com.mezda.aciud.ui.lifting_flow.address_info

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.mezda.aciud.R
import com.mezda.aciud.data.models.Locality
import com.mezda.aciud.databinding.FragmentAddressInfoBinding
import com.mezda.aciud.hideSoftKeyBoard
import com.mezda.aciud.showAutoCompleteOptions
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModel
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModelProvider
import com.mezda.aciud.utils.SuburbAutoCompleteValidator
import com.mezda.aciud.utils.SuburbFocusListener
import com.mezda.aciud.utils.section.SectionAutoCompleteValidator
import com.mezda.aciud.utils.section.SectionFocusListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddressInfoFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var factory: LiftingFlowViewModelProvider
    private val viewModel by navGraphViewModels<LiftingFlowViewModel>(R.id.liftingGraph) {
        factory
    }
    private lateinit var binding: FragmentAddressInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_address_info,
            container,
            false
        )

        viewModel.messages.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.messageShown()
            }
        })

        binding.streetText.editText?.setText(viewModel.directionInfo.street)
        binding.streetNumberText.editText?.setText(viewModel.directionInfo.street_number.toString())
        binding.localityTextView.text = Locality.getDefault().nameLocality
        binding.sectionSpinner.visibility = View.GONE
        binding.sectionAutoComplete.isEnabled = false

        viewModel.suburb.observe(viewLifecycleOwner, {
            val adapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                it.toTypedArray()
            )
            /*
            binding.suburbAutoComplete.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
            binding.suburbAutoComplete.setAdapter(adapter)
            binding.suburbAutoComplete.validator = SuburbAutoCompleteValidator(it)
            binding.suburbAutoComplete.onFocusChangeListener = SuburbFocusListener()
            */
            showAutoCompleteOptions(
                binding.suburbAutoComplete,
                adapter,
                arrayOf(InputFilter.AllCaps()),
                SuburbAutoCompleteValidator(it),
                SuburbFocusListener()
            )
            binding.suburbAutoComplete.setText(viewModel.getSuburb())
        })


        binding.sectionButton.setOnClickListener(this)
        viewModel.section.observe(viewLifecycleOwner) {
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                it.toTypedArray()
            )
            if (it.isEmpty()) {
                binding.sectionAutoComplete.isEnabled = false
                binding.sectionAutoComplete.setText("")
                return@observe
            }
            binding.sectionAutoComplete.isEnabled = true
            showAutoCompleteOptions(
                binding.sectionAutoComplete,
                adapter,
                validator = SectionAutoCompleteValidator(it),
                focusListener = SectionFocusListener()
            )
            /*
            binding.sectionAutoComplete.setAdapter(adapter)
            binding.sectionAutoComplete.validator = SectionAutoCompleteValidator(it)
            binding.sectionAutoComplete.onFocusChangeListener = SectionFocusListener()
            */
            binding.sectionAutoComplete.setText(viewModel.getSection())
        }

        viewModel.onGetSectionsAll()
        viewModel.onLocalityByDefault()
        binding.nextButton.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.nextButton.id -> {
                if (validateRequiredInfo()) {
                    val success = viewModel.saveDirectionInfo(
                        binding.streetText.editText?.text.toString(),
                        binding.streetNumberText.editText?.text.toString(),
                        binding.sectionAutoComplete.text.toString(),
                        binding.suburbAutoComplete.text.toString()
                    )
                    if (success) launchDirection(AddressInfoFragmentDirections.actionAddressInfoFragmentToGeoLocationInfoFragment())
                } else {
                    toast("Información Incompleta")
                }
            }
            binding.sectionButton.id -> {
                if (binding.suburbAutoComplete.text.toString().isNotEmpty()) {
                    viewModel.onGetSections(binding.suburbAutoComplete.text.toString())
                }
            }
        }
    }

    private fun validateRequiredInfo(): Boolean {
        return when {
            /*binding.streetText.editText?.text.toString().isEmpty() -> {
                binding.streetText.error = "Requerido"
                false
            }
            binding.sectionAutoComplete.text.toString().isEmpty() -> {
                binding.sectionAutoComplete.error = "Requerido"
                false
            }
            binding.suburbAutoComplete.text.toString().isEmpty() -> {
                binding.streetText.error = "Requerido"
                false
            }*/
            else -> true
        }
    }
}