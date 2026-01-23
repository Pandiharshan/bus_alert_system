package com.campusbussbuddy.notification

import android.content.Context
import android.location.Location
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat
import com.campusbussbuddy.domain.model.BusLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertManager @Inject constructor(
    private val context: Context,
    private val notificationService: NotificationService
) {
    
    private val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    
    fun monitorBusProximity(
        busLocationFlow: Flow<BusLocation?>,
        studentLocation: Location,
        alertDistanceMeters: Float = 500f
    ): Flow<ProximityAlert> {
        return busLocationFlow.map { busLocation ->
            if (busLocation == null) {
                ProximityAlert.None
            } else {
                val busLocationObj = Location("bus").apply {
                    latitude = busLocation.latitude
                    longitude = busLocation.longitude
                }
                
                val distance = studentLocation.distanceTo(busLocationObj)
                
                when {
                    distance <= alertDistanceMeters -> {
                        val estimatedArrival = calculateEstimatedArrival(distance, busLocation.speed)
                        ProximityAlert.BusApproaching(busLocation.busId, estimatedArrival, distance)
                    }
                    else -> ProximityAlert.None
                }
            }
        }
    }
    
    fun triggerBusArrivalAlert(busId: String, estimatedArrival: String) {
        // Show notification
        notificationService.showBusArrivalAlert(busId, estimatedArrival)
        
        // Vibrate device
        if (vibrator.hasVibrator()) {
            val pattern = longArrayOf(0, 500, 200, 500) // Wait, vibrate, wait, vibrate
            vibrator.vibrate(pattern, -1) // -1 means don't repeat
        }
    }
    
    fun triggerBoardingConfirmation(busId: String) {
        notificationService.showBoardingConfirmation(busId)
        
        // Short vibration for confirmation
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(200)
        }
    }
    
    private fun calculateEstimatedArrival(distanceMeters: Float, speedMps: Float): String {
        return if (speedMps > 0) {
            val timeSeconds = distanceMeters / speedMps
            val minutes = (timeSeconds / 60).toInt()
            when {
                minutes <= 0 -> "Less than 1 minute"
                minutes == 1 -> "1 minute"
                else -> "$minutes minutes"
            }
        } else {
            "Unknown"
        }
    }
}

sealed class ProximityAlert {
    object None : ProximityAlert()
    data class BusApproaching(
        val busId: String,
        val estimatedArrival: String,
        val distanceMeters: Float
    ) : ProximityAlert()
}