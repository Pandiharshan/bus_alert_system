package com.campusbussbuddy.domain.usecase

import com.campusbussbuddy.domain.model.BusLocation
import kotlinx.coroutines.flow.Flow

interface BusTrackingRepository {
    suspend fun updateBusLocation(location: BusLocation): Result<Unit>
    fun getBusLocation(busId: String): Flow<BusLocation?>
    suspend fun startTrip(busId: String): Result<Unit>
    suspend fun stopTrip(busId: String): Result<Unit>
}

class UpdateBusLocationUseCase(private val repository: BusTrackingRepository) {
    suspend operator fun invoke(location: BusLocation): Result<Unit> {
        return repository.updateBusLocation(location)
    }
}

class GetBusLocationUseCase(private val repository: BusTrackingRepository) {
    operator fun invoke(busId: String): Flow<BusLocation?> {
        return repository.getBusLocation(busId)
    }
}

class StartTripUseCase(private val repository: BusTrackingRepository) {
    suspend operator fun invoke(busId: String): Result<Unit> {
        return repository.startTrip(busId)
    }
}

class StopTripUseCase(private val repository: BusTrackingRepository) {
    suspend operator fun invoke(busId: String): Result<Unit> {
        return repository.stopTrip(busId)
    }
}