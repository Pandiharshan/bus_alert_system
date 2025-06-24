package com.harshan.busalertsystem.services.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.harshan.busalertsystem.R
import java.util.concurrent.TimeUnit

/*
 * ADD DEPENDENCIES to app/build.gradle.kts:
 * // Location Services
 * implementation("com.google.android.gms:play-services-location:21.2.0")
 * // Firebase
 * implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
 * implementation("com.google.firebase:firebase-firestore")
 *
 * ADD PERMISSIONS to app/src/main/AndroidManifest.xml:
 * <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 *
 * REGISTER SERVICE in app/src/main/AndroidManifest.xml inside the <application> tag:
 * <service android:name=".services.location.DriverLocationService" />
 */

class DriverLocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var firestore: FirebaseFirestore

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private const val TAG = "DriverLocationService"
        private const val NOTIFICATION_CHANNEL_ID = "driver_location_channel"
        private const val NOTIFICATION_ID = 1
        private const val LOCATION_UPDATE_INTERVAL_SECONDS = 5L
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        firestore = FirebaseFirestore.getInstance()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d(TAG, "New location: ${location.latitude}, ${location.longitude}")
                    uploadLocationToFirestore(location)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates()
        return START_STICKY // If the service is killed, it will be automatically restarted.
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            TimeUnit.SECONDS.toMillis(LOCATION_UPDATE_INTERVAL_SECONDS)
        ).build()

        // This check is necessary, but the permission should be granted by the user in the Activity.
        // The service assumes the permission has been granted.
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Location permission has not been granted.", unlikely)
            stopSelf() // Stop service if permission is not available
        }
    }

    private fun uploadLocationToFirestore(location: android.location.Location) {
        val busLocation = hashMapOf(
            "lat" to location.latitude,
            "lng" to location.longitude,
            "speed" to location.speed,
            "lastSeen" to System.currentTimeMillis()
        )

        firestore.collection("buses").document("bus_1")
            .set(busLocation)
            .addOnSuccessListener {
                Log.d(TAG, "Location successfully uploaded to Firestore.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error uploading location to Firestore", e)
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d(TAG, "Service destroyed, location updates stopped.")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null // We don't provide binding, so return null
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Driver Location",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Bus Tracking Active")
            .setContentText("Driver location is being shared")
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your own icon
            .setOngoing(true)
            .build()
    }
}
