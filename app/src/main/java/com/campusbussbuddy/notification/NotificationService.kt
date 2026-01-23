package com.campusbussbuddy.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    private val context: Context
) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        private const val BUS_ALERT_CHANNEL_ID = "bus_alert_channel"
        private const val LOCATION_CHANNEL_ID = "location_channel"
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val busAlertChannel = NotificationChannel(
                BUS_ALERT_CHANNEL_ID,
                "Bus Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for bus arrival alerts"
            }
            
            val locationChannel = NotificationChannel(
                LOCATION_CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background location tracking notifications"
            }
            
            notificationManager.createNotificationChannel(busAlertChannel)
            notificationManager.createNotificationChannel(locationChannel)
        }
    }
    
    fun showBusArrivalAlert(busId: String, estimatedArrival: String) {
        val notification = NotificationCompat.Builder(context, BUS_ALERT_CHANNEL_ID)
            .setContentTitle("Bus Arriving Soon!")
            .setContentText("Bus $busId will arrive in $estimatedArrival")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
            
        notificationManager.notify(busId.hashCode(), notification)
    }
    
    fun showBoardingConfirmation(busId: String) {
        val notification = NotificationCompat.Builder(context, BUS_ALERT_CHANNEL_ID)
            .setContentTitle("Boarding Confirmed")
            .setContentText("Successfully boarded bus $busId")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
            
        notificationManager.notify("boarding_$busId".hashCode(), notification)
    }
    
    fun createLocationTrackingNotification(): android.app.Notification {
        return NotificationCompat.Builder(context, LOCATION_CHANNEL_ID)
            .setContentTitle("Bus Tracking Active")
            .setContentText("Sharing location with students")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()
    }
}