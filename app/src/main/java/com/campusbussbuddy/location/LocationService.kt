package com.campusbussbuddy.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.campusbussbuddy.domain.model.BusLocation
import com.campusbussbuddy.domain.usecase.UpdateBusLocationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {
    
    @Inject
    lateinit var locationManager: LocationManager
    
    @Inject
    lateinit var updateBusLocationUseCase: UpdateBusLocationUseCase
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var busId: String? = null
    
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "location_service_channel"
        private const val NOTIFICATION_ID = 1
        const val EXTRA_BUS_ID = "bus_id"
        const val ACTION_START_TRACKING = "start_tracking"
        const val ACTION_STOP_TRACKING = "stop_tracking"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TRACKING -> {
                busId = intent.getStringExtra(EXTRA_BUS_ID)
                startLocationTracking()
            }
            ACTION_STOP_TRACKING -> {
                stopLocationTracking()
            }
        }
        return START_STICKY
    }
    
    private fun startLocationTracking() {
        val currentBusId = busId ?: return
        
        startForeground(NOTIFICATION_ID, createNotification())
        
        locationManager.getLocationUpdates()
            .onEach { location ->
                val busLocation = BusLocation(
                    busId = currentBusId,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    speed = location.speed
                )
                updateBusLocationUseCase(busLocation)
            }
            .catch { e ->
                // Handle location errors
            }
            .launchIn(serviceScope)
    }
    
    private fun stopLocationTracking() {
        serviceScope.cancel()
        stopForeground(true)
        stopSelf()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Bus Tracking Active")
            .setContentText("Sharing location with students")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}