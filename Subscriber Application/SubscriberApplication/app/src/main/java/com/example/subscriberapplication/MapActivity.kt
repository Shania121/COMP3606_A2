package com.example.subscriberapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        try {
            databaseHelper = DatabaseHelper(this)

            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        } catch (e: Exception) {
            Log.e("MapActivity", "Error initializing map", e)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadMessagesOnMap()
    }

    private fun loadMessagesOnMap() {
        try {
            val messages = databaseHelper.getAllMessages()
            val latLngList = messages.mapNotNull { parseMessageToLatLng(it) }

            if (latLngList.isNotEmpty()) {
                mMap.addPolyline(PolylineOptions().addAll(latLngList).color(android.graphics.Color.BLUE).width(5f))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.first(), 10f))
            }
        } catch (e: Exception) {
            Log.e("MapActivity", "Error loading messages on map", e)
        }
    }

    private fun parseMessageToLatLng(message: String): LatLng? {
        return try {
            val parts = message.split(", ")
            if (parts.size == 2) {
                LatLng(parts[0].toDouble(), parts[1].toDouble())
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MapActivity", "Error parsing message to LatLng", e)
            null
        }
    }
}
