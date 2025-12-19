package com.example.smartdesk.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SensorManagerWrapper(context: Context) {

    private val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT)

    private var listener: SensorEventListener? = null
    private var isRunning = false

    val luxFlow: Flow<Float> = callbackFlow {
        val l = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    trySend(event.values[0])
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        listener = l
        start()

        awaitClose {
            stop()
        }
    }

    fun start() {
        if (!isRunning && lightSensor != null) {
            sm.registerListener(
                listener,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            isRunning = true
        }
    }

    fun stop() {
        if (isRunning) {
            listener?.let { sm.unregisterListener(it) }
            isRunning = false
        }
    }

    fun hasLightSensor(): Boolean = lightSensor != null
}