package com.mezda.aciud.ui.lifting_flow.geolocation_info

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.mezda.aciud.R
import com.mezda.aciud.data.models.GeoLocationInfo
import com.mezda.aciud.databinding.FragmentGeolocationInfoBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModel
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class GeoLocationInfoFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var factory: LiftingFlowViewModelProvider
    private val viewModel by navGraphViewModels<LiftingFlowViewModel>(R.id.liftingGraph) {
        factory
    }

    private lateinit var binding: FragmentGeolocationInfoBinding

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_geolocation_info, container, false)
        binding.nextButton.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            binding.nextButton.id -> {
                if (validateRequiredLocation()){
                    viewModel.saveGeoLocation(latitude, longitude)
                    launchDirection(GeoLocationInfoFragmentDirections.actionGeoLocationInfoFragmentToOccupationInfoFragment())
                } else {
                    toast("Información Requerida")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { map ->
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.setOnMyLocationButtonClickListener {
            val location = map.myLocation
            toast("Location longitude ${location.longitude} latitude ${location.latitude}")
            Timber.e("Location longitude ${location.longitude} latitude ${location.latitude}")
            longitude = location.longitude
            latitude = location.latitude
            false
        }
        map.setOnMyLocationClickListener {
            toast("Location longitude ${it.longitude} latitude ${it.latitude}")
            Timber.e("Location longitude ${it.longitude} latitude ${it.latitude}")

        }
    }

    fun validateRequiredLocation(): Boolean{
        return longitude != 0.0 || latitude != 0.0
    }
}