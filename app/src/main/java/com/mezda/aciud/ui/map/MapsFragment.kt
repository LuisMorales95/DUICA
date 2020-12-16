package com.mezda.aciud.ui.map

import android.content.res.Resources.NotFoundException
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.mezda.aciud.R
import com.mezda.aciud.data.RetrofitModule
import com.mezda.aciud.databinding.FragmentMapsBinding
import timber.log.Timber

class MapsFragment : Fragment() {

    lateinit var binding: FragmentMapsBinding
    val args: MapsFragmentArgs by navArgs()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val liftingInfo = args.liftingInfo
        if ((liftingInfo.image ?: "").isNotEmpty()) {
            binding.pictureImageView.visibility = View.VISIBLE
            Glide.with(requireActivity()).load(RetrofitModule.baseUrl + liftingInfo.image?.replace("~/", "")).into(binding.pictureImageView)
        }
        val place = LatLng(
                (liftingInfo.latitude ?: "0.0").toDouble(),
                (liftingInfo.longitude ?: "0.0").toDouble()
        )
        // Add a marker in Sydney and move the camera
        try {
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(),
                            R.raw.style_json
                    )
            )
            if (!success) Timber.e("Style parsing failed.")
        } catch (e: NotFoundException) {
            Timber.e("Can't find style. Error: $e")
        }
        googleMap.addMarker(MarkerOptions().position(place).title(
                "${liftingInfo.name} ${liftingInfo.paternal_surname} ${liftingInfo.maternal_surname}\n ${liftingInfo.number} ${liftingInfo.street}"
        ))
        val camera = CameraPosition.Builder().target(place).zoom(15f).bearing(0f).tilt(30f).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))

        /*googleMap.addMarker(
                MarkerOptions().position(place)
                        .title("${liftingInfo.name} ${liftingInfo.paternal_surname} ${liftingInfo.maternal_surname}\n ${liftingInfo.number} ${liftingInfo.street}")
        )
        val cameraPosition = CameraPosition.Builder().target(Place_LngLat).zoom(15f).bearing(0f).tilt(30f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        val zoom = CameraUpdateFactory.zoomTo(15f)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place))
        googleMap.animateCamera(zoom)*/
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}