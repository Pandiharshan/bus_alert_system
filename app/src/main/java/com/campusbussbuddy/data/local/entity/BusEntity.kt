package com.campusbussbuddy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.campusbussbuddy.domain.model.Bus

@Entity(tableName = "buses")
data class BusEntity(
    @PrimaryKey
    val id: String,
    val driverId: String,
    val routeName: String,
    val isActive: Boolean
)

fun BusEntity.toDomainModel(): Bus {
    return Bus(
        id = id,
        driverId = driverId,
        routeName = routeName,
        isActive = isActive
    )
}

fun Bus.toEntity(): BusEntity {
    return BusEntity(
        id = id,
        driverId = driverId,
        routeName = routeName,
        isActive = isActive
    )
}