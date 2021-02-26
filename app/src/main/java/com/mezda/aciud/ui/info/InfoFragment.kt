package com.mezda.aciud.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.mezda.aciud.R
import com.mezda.aciud.data.RetrofitModule
import com.mezda.aciud.databinding.FragmentPreviewInfoBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.users.UserViewModel
import com.mezda.aciud.utils.dialogs.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class InfoFragment: BaseFragment() {

    private lateinit var binding: FragmentPreviewInfoBinding
    private val viewModel by viewModels<InfoViewModel>()
    private val args: InfoFragmentArgs by navArgs()
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preview_info,container,false)
        binding.sendButton.visibility = View.GONE
        binding.infoValidationCard.visibility = View.GONE

        loadingDialog = LoadingDialog(requireContext())

        viewModel.loading.observe(viewLifecycleOwner, {
            if (it.get() == true) {
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

        Glide.with(requireContext()).load(R.drawable.profile_picture).circleCrop().into(binding.profilePictureImage)

        viewModel.liftingInfo.observe(viewLifecycleOwner, {
            Timber.e(Gson().toJson(it[0]))
            val lifting = it[0]
            binding.nameText.text = lifting.name
            binding.paternalSurnameText.text = lifting.parental
            binding.maternalSurnameText.text = lifting.maternal
            binding.phoneNumberText.text = lifting.phone
            if (lifting.image != "null" && !lifting.image.contains("fotolimpia.Jpeg")) {
                Glide.with(requireContext()).load(RetrofitModule.baseUrl + lifting.image.replace("~/", "")).circleCrop().into(binding.profilePictureImage)
            }

            binding.streetText.text = lifting.street
            binding.streetNumberText.text = lifting.number
            binding.localityText.text = "COATZACOALCOS"
            binding.sectionText.text = lifting.seccion.toString()
            binding.suburbText.text = lifting.colony

            binding.longitudeText.text = lifting.longitude
            binding.latitudeText.text = lifting.latitude
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback(lifting.latitude, lifting.longitude))

            binding.professionText.text = lifting.profession
            binding.flagText.text = lifting.flag
            binding.observationsText.text = lifting.observation
            binding.sympathizerText.text = sympathizerStatus(lifting.simpathizer.toInt())
        })

        viewModel.getLiftingInfo(args.liftingId)

        return binding.root
    }

    private fun sympathizerStatus(status4t: Int?): String {
        return when (status4t) {
            1 -> {
                "NO"
            }
            2 -> {
                "SI"
            }
            3 -> {
                "FAN DESTACADO"
            }
            else -> {
                "NO"
            }
        }
    }

    private fun callback(lat:String, long: String) = OnMapReadyCallback { googleMap ->
            val place = LatLng(lat.toDouble(), long.toDouble())
            googleMap.addMarker(MarkerOptions().position(place))
            val camera = CameraPosition.Builder().target(place).zoom(17f).bearing(0f).tilt(30f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
    }
}