package com.yagi2.swirm

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.foursquare.android.nativeoauth.FoursquareOAuth
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private val REQUEST_4SQ_CONNECT = 100
    private val REQUEST_4SQ_TOKEN_EXCHANGE = 200

    private lateinit var currentLatLng: LatLng
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMap()

        //TODO DEBUGコード、AccessTokenが存在しないならダイアログを出して誘導する
        startActivityForResult(FoursquareOAuth.getConnectIntent(this, getString(R.string.client_id)), REQUEST_4SQ_CONNECT)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_4SQ_CONNECT -> {
                val code = FoursquareOAuth.getAuthCodeFromResult(resultCode, data)
                startActivityForResult(FoursquareOAuth.getTokenExchangeIntent(this, getString(R.string.client_id), getString(R.string.client_secret), code.code), REQUEST_4SQ_TOKEN_EXCHANGE)
            }
            REQUEST_4SQ_TOKEN_EXCHANGE -> {
                val token = FoursquareOAuth.getTokenFromResult(resultCode, data)
                //TODO アクセストークンはSharedPreferenceに保存しておく
                Toast.makeText(this, "AccessToken : ${token.accessToken}", Toast.LENGTH_LONG).show()
            }
        }
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
