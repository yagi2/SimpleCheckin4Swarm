package com.yagi2.swirm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                .getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        val tokyo = LatLng(35.681168, 139.76052)

        map.addMarker(MarkerOptions().position(tokyo).title("Marker in Tokyo"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(tokyo, 15.toFloat()))
    }
}
