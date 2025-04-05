package com.example.taq_c.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taq_c.data.model.Alert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAlert(alert: Alert): Long

    @Delete
    suspend fun deleteAlert(alert: Alert): Int

    @Query("SELECT * FROM Alert")
    fun getAllAlert(): Flow<List<Alert>>
}