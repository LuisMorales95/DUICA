package com.mezda.aciud.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mezda.aciud.R
import com.mezda.aciud.ui.MainActivity

class MapsFragment : Fragment() {
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
        val place = LatLng(
            (liftingInfo.latitude ?: "0.0").toDouble(),
            (liftingInfo.longitude ?: "0.0").toDouble()
        )
        googleMap.addMarker(
            MarkerOptions().position(place)
                .title("${liftingInfo.name} ${liftingInfo.paternal_surname} ${liftingInfo.maternal_surname}\n ${liftingInfo.number} ${liftingInfo.street}")
        )
        val zoom = CameraUpdateFactory.zoomTo(15f)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place))
        googleMap.animateCamera(zoom)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}