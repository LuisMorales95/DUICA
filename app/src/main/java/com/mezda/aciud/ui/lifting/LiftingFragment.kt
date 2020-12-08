package com.mezda.aciud.ui.lifting

import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.mezda.aciud.R
import com.mezda.aciud.data.GPSTracker
import com.mezda.aciud.data.models.Locality
import com.mezda.aciud.databinding.FragmentLiftingBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.utils.SuburbAutoCompleteValidator
import com.mezda.aciud.utils.SuburbFocusListener
import com.mezda.aciud.utils.section.SectionAutoCompleteValidator
import com.mezda.aciud.utils.section.SectionFocusListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class LiftingFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentLiftingBinding
    private val liftingViewModel by viewModels<LiftingViewModel>()

    private lateinit var gpsTracker: GPSTracker


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        gpsTracker = GPSTracker(requireContext())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lifting, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        liftingViewModel.localities.observe(viewLifecycleOwner, {
            binding.localitySpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )
        })

        liftingViewModel.suburb.observe(viewLifecycleOwner, {
            val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it.toTypedArray())
            binding.suburbAutoComplete.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
            binding.suburbAutoComplete.setAdapter(adapter)
            binding.suburbAutoComplete.validator = SuburbAutoCompleteValidator(it)
            binding.suburbAutoComplete.onFocusChangeListener = SuburbFocusListener()
            /*binding.suburbSpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )*/
        })

        liftingViewModel.section.observe(viewLifecycleOwner) {
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, it.toTypedArray())
//            binding.sectionSpinner.adapter = adapter
            binding.sectionAutoComplete.setAdapter(adapter)
            binding.sectionAutoComplete.validator = SectionAutoCompleteValidator(it)
            binding.sectionAutoComplete.onFocusChangeListener = SectionFocusListener()
        }

        liftingViewModel.supervisor.observe(viewLifecycleOwner, {
            Timber.e("supervisor ${Gson().toJson(it)}")
            binding.supervisorText.text = it?.name
        })

        liftingViewModel.operator.observe(viewLifecycleOwner, {
            Timber.e("operator ${Gson().toJson(it)}")
            binding.operatorText.text = it?.name
        })

        liftingViewModel.registerSuccess.observe(viewLifecycleOwner, {
            if (it.get() == true) {
                popBack()
                liftingViewModel.registerSuccessful()
            }
        })

        binding.localitySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    liftingViewModel.onLocalitySelected(p2)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        binding.gpsLocationButton.setOnClickListener(this)
        binding.registerButton.setOnClickListener(this)

        liftingViewModel.onGetSupervisor()
//        liftingViewModel.onGetLocalities()
        binding.localityTextView.text = Locality.getDefault().nameLocality
        binding.localitySpinner.visibility = View.GONE
        liftingViewModel.onLocalityByDefault()
        liftingViewModel.onGetOperator()
        liftingViewModel.onGetSections()

        gpsTracker.location()
        return binding.root
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.gpsLocationButton.id -> {
                if (gpsTracker.canGetLocation()) {
                    val latitude: Double = gpsTracker.latitude
                    val longitude: Double = gpsTracker.longitude
                    binding.latitudeText.text = "$latitude"
                    binding.longitudeText.text = "$longitude"
                } else {
                    gpsTracker.showSettingsAlert()
                }
            }
            binding.registerButton.id -> {
                if (validateForm()) {
                    liftingViewModel.sendLiftingInfo(
                            binding.nameText.editText?.text.toString(),
                            binding.paternalSurnameText.editText?.text.toString(),
                            binding.maternalSurnameText.editText?.text.toString(),
                            binding.phoneNumberText.editText?.text.toString(),
                            binding.streetText.editText?.text.toString(),
                            binding.streetNumberText.editText?.text.toString(),
                            binding.localitySpinner.selectedItemPosition,
                            binding.suburbAutoComplete.text.toString(),
                            binding.latitudeText.text.toString(),
                            binding.longitudeText.text.toString(),
                            binding.sectionAutoComplete.text.toString()
                    )
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        binding.nameText.isErrorEnabled = false
        binding.paternalSurnameText.isErrorEnabled = false
        binding.maternalSurnameText.isErrorEnabled = false
        binding.phoneNumberText.isErrorEnabled = false
        binding.streetText.isErrorEnabled = false
        binding.streetNumberText.isErrorEnabled = false

        return when {
            binding.nameText.editText?.text.toString().isEmpty() -> {
                binding.nameText.error = "Requerido"
                false
            }
            binding.paternalSurnameText.editText?.text.toString().isEmpty() -> {
                binding.paternalSurnameText.error = "Requerido"
                false
            }
            binding.maternalSurnameText.editText?.text.toString().isEmpty() -> {
                binding.maternalSurnameText.error = "Requerido"
                false
            }
            binding.phoneNumberText.editText?.text.toString().isEmpty() -> {
                binding.phoneNumberText.error = "Requerido"
                false
            }
            binding.streetText.editText?.text.toString().isEmpty() -> {
                binding.streetText.error = "Requerido"
                false
            }
            binding.streetNumberText.editText?.text.toString().isEmpty() -> {
                binding.streetNumberText.error = "Requerido"
                false
            }
            binding.suburbAutoComplete.text.toString().isEmpty() -> {
                binding.streetNumberText.error = "Requerido"
                false
            }
            /*binding.localitySpinner.selectedItemPosition == 0 || binding.suburbSpinner.selectedItemPosition == 0 -> {
                Toast.makeText(requireContext(), "Ubicacion ó Colonia faltante", Toast.LENGTH_SHORT)
                    .show()
                binding.nameText.error = "Requerido"
                false
            }*/
            binding.latitudeText.text.toString().isEmpty() || binding.longitudeText.text.toString()
                .isEmpty() -> {
                Toast.makeText(requireContext(), "Ubicacion GPS Requerida", Toast.LENGTH_SHORT)
                    .show()
                false
            }
            binding.sectionAutoComplete.text.toString().isEmpty() -> {
                binding.sectionAutoComplete.error = "Requerido"
                false
            }
            else -> true
        }
    }

}