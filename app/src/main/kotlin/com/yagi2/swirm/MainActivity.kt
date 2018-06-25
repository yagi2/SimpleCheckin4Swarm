package com.yagi2.swirm

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var currentLatLng: LatLng
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMap()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        getCurrentLocationWithPermissionCheck()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onLocationChanged(location: Location) {
        currentLatLng = LatLng(location.latitude, location.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.5f))
    }

    private fun initMap() {
        val mapFragment = SupportMapFragment.newInstance(GoogleMapOptions().apply {
            compassEnabled(false)
            zoomGesturesEnabled(false)
            rotateGesturesEnabled(false)
            scrollGesturesEnabled(false)
            tiltGesturesEnabled(false)
        })

        supportFragmentManager.beginTransaction()
                .add(R.id.map_container, mapFragment)
                .commit()

        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getCurrentLocation() {
        googleMap.isMyLocationEnabled = true

        MapsInitializer.initialize(this)

        (getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L ,0.toFloat(), this)

        (getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L ,0.toFloat(), this)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }
}
