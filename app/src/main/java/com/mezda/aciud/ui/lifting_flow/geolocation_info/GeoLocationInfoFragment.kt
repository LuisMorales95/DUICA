@file:Suppress("DEPRECATION")

package com.mezda.aciud.ui.lifting_flow.geolocation_info

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentGeolocationInfoBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModel
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class GeoLocationInfoFragment : BaseFragment(), View.OnClickListener {

    companion object {
        const val ZOOM = 19f
    }

    private lateinit var binding: FragmentGeolocationInfoBinding
    @Inject lateinit var factory: LiftingFlowViewModelProvider
    private val viewModel by navGraphViewModels<LiftingFlowViewModel>(R.id.liftingGraph) { factory }

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

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { map ->

        setUpSavedLocation(map)


        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        statusCheck()
        map.setOnMyLocationButtonClickListener {
            val location = map.myLocation
            if (location != null) {

                val loc = LatLng(location.latitude, location.longitude)

                Timber.e("Location longitude ${loc.longitude} latitude ${loc.latitude}")
                toast("Location longitude ${loc.longitude} latitude ${loc.latitude}")

                val camera = CameraPosition.Builder().target(loc).zoom(ZOOM).bearing(0f).tilt(30f).build()
                map.animateCamera(CameraUpdateFactory.newCameraPosition(camera))

                longitude = location.longitude
                latitude = location.latitude
            } else {
                toast("Ubicacion no disponible")
            }
            true
        }
    }

    private fun setUpSavedLocation(googleMap: GoogleMap) {
        val savedLocation = viewModel.getGetLocation()
        if (savedLocation.latitude != 0.0 || savedLocation.longitude != 0.0){
            this.latitude = savedLocation.latitude
            this.longitude = savedLocation.longitude
            val camera = CameraPosition.Builder().target(LatLng(latitude, longitude)).zoom(ZOOM).bearing(0f).tilt(30f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.nextButton.id -> {
                if (validateRequiredLocation()) {
                    viewModel.saveGeoLocation(latitude, longitude)
                    launchDirection(GeoLocationInfoFragmentDirections.actionGeoLocationInfoFragmentToOccupationInfoFragment())
                } else {
                    toast("Información Requerida")
                }
            }
        }
    }

    private fun validateRequiredLocation(): Boolean {
        return longitude != 0.0 || latitude != 0.0
    }

    private fun statusCheck() {
        val manager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ){ _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("No"){ dialog, _ -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}