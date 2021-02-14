package com.mezda.aciud.ui.lifting_flow.preview_info

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentPreviewInfoBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModel
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModelProvider
import com.mezda.aciud.utils.dialogs.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PreviewInfoFragment : BaseFragment(), View.OnClickListener {

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var binding : FragmentPreviewInfoBinding
    @Inject
    lateinit var factory: LiftingFlowViewModelProvider
    private val viewModel by navGraphViewModels<LiftingFlowViewModel>(R.id.liftingGraph) {
        factory
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preview_info, container, false)

        loadingDialog = LoadingDialog(requireActivity())
        viewModel.messages.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.messageShown()
            }
        })
        viewModel.loading.observe(viewLifecycleOwner,{
            if (it){
                loadingDialog.apply {
                    show()
                    binding.apply {
                        this.messageText.text = "Enviando"
                    }
                }
            } else {
                loadingDialog.dismiss()
            }
        })

        viewModel.registerSuccess.observe(viewLifecycleOwner, {
            if (it.get() == true) {
                launchDirection(PreviewInfoFragmentDirections.actionPartyInfoFragmentToSearchFragment())
                viewModel.registerSuccessful()
            }
        })

        Glide.with(requireContext()).load(R.drawable.profile_picture).circleCrop().into(binding.profilePictureImage)
        with(viewModel.userInfo){
            binding.nameText.text = name
            binding.paternalSurnameText.text = paternal_last_name
            binding.maternalSurnameText.text = maternal_last_name
            binding.phoneNumberText.text = phone_number
            picture_uri?.let {
                if (it != "null") {
                    Glide.with(requireContext()).load(Uri.parse(it)).circleCrop().into(binding.profilePictureImage)
                }
            }
        }
        with(viewModel.directionInfo) {
            binding.streetText.text = street
            binding.streetNumberText.text = street_number.toString()
            binding.localityText.text = locality
            binding.sectionText.text = section
            binding.suburbText.text = suburb
        }
        with(viewModel.geolocationinfo){
            binding.longitudeText.text = longitude.toString()
            binding.latitudeText.text = latitude.toString()
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }

        with(viewModel.occupationInfo){
            binding.professionText.text = profession
            binding.supportTypeText.text = supportTypes
            binding.observationsText.text = observation
        }

        with(viewModel.partyInfo) {
            binding.flagText.text = this.flag
            binding.sympathizerText.text = sympathizerStatus(this.status_4t)
        }
        binding.sendButton.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            binding.sendButton.id -> {
                viewModel.sendLifting()
            }
        }
    }

    private fun sympathizerStatus(status4t: Int?): String {
        return when(status4t) {
            1 -> {"NO"}
            2 -> {"SI"}
            3 -> {"FAN DESTACADO"}
            else -> {"NO"}
        }
    }


    private val callback = OnMapReadyCallback { googleMap ->
        viewModel.geolocationinfo.apply {
            val place = LatLng(latitude, longitude)
            googleMap.addMarker(MarkerOptions().position(place).title("Esta aqui!"))
            val camera = CameraPosition.Builder().target(place).zoom(17f).bearing(0f).tilt(30f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
        }
    }


}