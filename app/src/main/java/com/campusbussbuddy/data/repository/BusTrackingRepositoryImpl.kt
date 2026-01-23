package com.campusbussbuddy.data.repository

import com.campusbussbuddy.domain.model.BusLocation
import com.campusbussbuddy.domain.usecase.BusTrackingRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusTrackingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BusTrackingRepository {

    override suspend fun updateBusLocation(location: BusLocation): Result<Unit> {
        return try {
            val locationData = mapOf(
                "lat" to location.latitude,
                "lng" to location.longitude,
                "speed" to location.speed,
                "lastSeen" to location.lastSeen
            )
            
            firestore.collection("buses")
                .document(location.busId)
                .set(locationData)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getBusLocation(busId: String): Flow<BusLocation?> = callbackFlow {
        val listener = firestore.collection("buses")
            .document(busId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val location = snapshot?.let { doc ->
                    if (doc.exists()) {
                        BusLocation(
                            busId = busId,
                            latitude = doc.getDouble("lat") ?: 0.0,
                            longitude = doc.getDouble("lng") ?: 0.0,
                            speed = doc.getDouble("speed")?.toFloat() ?: 0f,
                            lastSeen = doc.getLong("lastSeen") ?: System.currentTimeMillis()
                        )
                    } else null
                }
                
                trySend(location)
            }
            
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun startTrip(busId: String): Result<Unit> {
        return try {
            firestore.collection("buses")
                .document(busId)
                .update("isActive", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun stopTrip(busId: String): Result<Unit> {
        return try {
            firestore.collection("buses")
                .document(busId)
                .update("isActive", false)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}