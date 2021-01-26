package com.mezda.aciud.ui.map

import android.annotation.SuppressLint
import android.content.res.Resources.NotFoundException
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mezda.aciud.R
import com.mezda.aciud.data.RetrofitModule
import com.mezda.aciud.data.models.LiftingInfo
import com.mezda.aciud.databinding.FragmentMapsBinding
import timber.log.Timber


@Suppress("DEPRECATION")
class MapsFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentMapsBinding
    val args: MapsFragmentArgs by navArgs()

    @Suppress("DEPRECATION")
    private val callback = OnMapReadyCallback { googleMap ->
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json)
            )
            if (!success) Timber.e("Style parsing failed.")
        } catch (e: NotFoundException) {
            Timber.e("Can't find style. Error: $e")
        }
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sLocation = args.locations.singleLocation
        if (sLocation) {
            val liftingInfo = args.locations.list[0]
            val place = LatLng(
                (liftingInfo.latitude ?: "0.0").toDouble(),
                (liftingInfo.longitude ?: "0.0").toDouble()
            )
            val fullName = "${liftingInfo.name} ${liftingInfo.paternal_surname}"
            val phone = "${liftingInfo.phone}"
            createMarker(liftingInfo, googleMap, place, fullName, phone)
        } else {
            val liftingList = args.locations.list
            liftingList.forEach {
                val place =
                    LatLng((it.latitude ?: "0.0").toDouble(), (it.longitude ?: "0.0").toDouble())
                val full_name = "${it.name} ${it.paternal_surname}"
                val phone = "${it.phone}"
                createMarker(it, googleMap, place, full_name, phone)
            }
        }

        googleMap.setOnMarkerClickListener { marker ->
            run loop@ {
                args.locations.list.forEach { liftingInfo ->
                    if ((marker.tag as Int) == liftingInfo.idLifting) {
                        binding.pictureImageView.visibility = View.VISIBLE
                        Glide.with(requireContext()).load(buildImageUrlString(liftingInfo)).into(binding.pictureImageView)
                        return@loop
                    }
                }
            }

            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        binding.pictureImageView.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.pictureImageView.id -> {
                binding.pictureImageView.visibility = View.GONE
            }
        }
    }

    private fun createMarker(
        liftingInfo: LiftingInfo,
        googleMap: GoogleMap,
        place: LatLng,
        full_name: String,
        phone: String
    ) {
        if (imagePathExists(liftingInfo)) {
            downloadMarkerImage(liftingInfo, googleMap, place, full_name, phone)
        } else {
            setUpMarker(googleMap, null, place, full_name, phone, liftingInfo.idLifting ?: 0)
        }
    }

    private fun downloadMarkerImage(
        liftingInfo: LiftingInfo,
        googleMap: GoogleMap,
        place: LatLng,
        full_name: String,
        phone: String
    ) {
        Glide.with(requireContext()).asBitmap().load(buildImageUrlString(liftingInfo)).circleCrop()
            .into(object :
                SimpleTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    setUpMarker(
                        googleMap,
                        resource,
                        place,
                        full_name,
                        phone,
                        liftingInfo.idLifting ?: 0
                    )
                }
            })
    }

    private fun buildImageUrlString(it: LiftingInfo) =
        RetrofitModule.baseUrl + it.image?.replace("~/", "")

    private fun imagePathExists(it: LiftingInfo) = it.image != null

    fun setUpMarker(
        googleMap: GoogleMap,
        clientImage: Bitmap?,
        place: LatLng,
        fullName: String,
        phone: String,
        tag: Int
    ) {
        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(place)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getMarkerBitmapFromView(
                            clientImage,
                            fullName,
                            phone
                        )
                    )
                )
        )
        if (clientImage != null) marker.tag = tag
        val camera = CameraPosition.Builder().target(place).zoom(13f).bearing(0f).tilt(30f).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
    }


    @SuppressLint("InflateParams")
    private fun getMarkerBitmapFromView(
        client: Bitmap?,
        name: String,
        phone: String
    ): Bitmap? {

        val customMarkerView = LayoutInflater.from(requireContext()).inflate(
            R.layout.custom_map_pointer,
            null
        )

        val markerImage: ImageView = customMarkerView.findViewById(R.id.marker_image)
        markerImage.setImageResource(R.drawable.ic_map)
        val clientImageView: ImageView = customMarkerView.findViewById<ImageView>(R.id.client_image)
        if (client != null) {
            clientImageView.visibility = View.VISIBLE
            clientImageView.setImageBitmap(client)
        } else clientImageView.visibility = View.GONE
        customMarkerView.findViewById<TextView>(R.id.text_title).apply { text = name }
        customMarkerView.findViewById<TextView>(R.id.text_subtitle).apply { text = phone }

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            0,
            0,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )
        customMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        drawable?.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }
}
