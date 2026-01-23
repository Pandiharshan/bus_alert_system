package com.campusbussbuddy.location

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapsManager @Inject constructor() {
    
    private var busMarkers = mutableMapOf<String, Marker>()
    
    fun updateBusLocation(
        map: GoogleMap,
        busId: String,
        latitude: Double,
        longitude: Double,
        title: String = "Bus $busId"
    ) {
        val position = LatLng(latitude, longitude)
        
        val marker = busMarkers[busId]
        if (marker == null) {
            // Create new marker
            val newMarker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(title)
            )
            newMarker?.let { busMarkers[busId] = it }
            
            // Move camera to new location
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
        } else {
            // Update existing marker position
            marker.position = position
            map.animateCamera(CameraUpdateFactory.newLatLng(position))
        }
    }
    
    fun removeBusMarker(busId: String) {
        busMarkers[busId]?.remove()
        busMarkers.remove(busId)
    }
    
    fun clearAllMarkers() {
        busMarkers.values.forEach { it.remove() }
        busMarkers.clear()
    }
    
    fun centerMapOnLocation(map: GoogleMap, latitude: Double, longitude: Double, zoom: Float = 15f) {
        val position = LatLng(latitude, longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoom))
    }
}