package com.harshan.busalertsystem.ui.student

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.harshan.busalertsystem.R

class StudentMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var busMarker: Marker? = null
    private lateinit var busRef: DocumentReference

    companion object {
        private const val TAG = "StudentMapActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize Firestore reference
        val firestore = FirebaseFirestore.getInstance()
        busRef = firestore.collection("buses").document("bus_1")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Set initial camera position (e.g., a default location)
        val defaultLocation = LatLng(0.0, 0.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

        // Add a listener for real-time updates
        listenForBusLocationUpdates()
    }

    private fun listenForBusLocationUpdates() {
        busRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val lat = snapshot.getDouble("lat")
                val lng = snapshot.getDouble("lng")

                if (lat != null && lng != null) {
                    val busLocation = LatLng(lat, lng)
                    updateBusMarker(busLocation)
                }
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    private fun updateBusMarker(location: LatLng) {
        if (busMarker == null) {
            // Create the marker if it doesn't exist
            busMarker = mMap.addMarker(MarkerOptions().position(location).title("Bus Location"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
        } else {
            // Move the existing marker smoothly
            busMarker?.position = location
            mMap.animateCamera(CameraUpdateFactory.newLatLng(location))
        }
    }
}
