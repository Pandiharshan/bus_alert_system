package com.campusbussbuddy.domain.model

data class Bus(
    val id: String,
    val driverId: String = "",
    val routeName: String = "",
    val isActive: Boolean = false
)

data class BusLocation(
    val busId: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Float = 0f,
    val lastSeen: Long = System.currentTimeMillis()
)

data class BoardingRecord(
    val studentId: String,
    val busId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String
)