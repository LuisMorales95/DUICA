package com.mezda.aciud.data

import android.Manifest
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat


class GPSTracker(var mContext: Context) : Service(), LocationListener {

    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var location // Location
            : Location? = null
    var latitude // Latitude
            = 0.0
    var longitude // Longitude
            = 0.0
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters
    private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1 // 1 minute
            ).toLong()

    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

    init {
        location()
    }

    fun location(): Location? {
        try {
            if (
                ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager?
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                canGetLocation = true
                if (isNetworkEnabled) {

                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        location = locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            location = locationManager!!
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }

    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    fun longitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }
        return longitude
    }

    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    fun showSettingsAlert() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(mContext)
        alertDialog.setTitle("GPS is settings")
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")
        alertDialog.setPositiveButton("Settings",
            DialogInterface.OnClickListener { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                mContext.startActivity(intent)
            })
        alertDialog.setNegativeButton("Cancel", { dialog, _ -> dialog.cancel() })
        alertDialog.show()
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(p0: Location) {
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}