package com.campusbussbuddy.data.local.dao

import androidx.room.*
import com.campusbussbuddy.data.local.entity.BusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusDao {
    
    @Query("SELECT * FROM buses")
    fun getAllBuses(): Flow<List<BusEntity>>
    
    @Query("SELECT * FROM buses WHERE id = :busId")
    fun getBusById(busId: String): Flow<BusEntity?>
    
    @Query("SELECT * FROM buses WHERE isActive = 1")
    fun getActiveBuses(): Flow<List<BusEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBus(bus: BusEntity)
    
    @Update
    suspend fun updateBus(bus: BusEntity)
    
    @Delete
    suspend fun deleteBus(bus: BusEntity)
    
    @Query("DELETE FROM buses")
    suspend fun clearAllBuses()
}