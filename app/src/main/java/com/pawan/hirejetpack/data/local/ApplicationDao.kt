package com.pawan.hirejetpack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * [ApplicationDao] — Room Data Access Object for job applications.
 */
@Dao
interface ApplicationDao {

    @Query("SELECT * FROM applications ORDER BY appliedAt DESC")
    fun getAll(): Flow<List<ApplicationEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM applications WHERE jobId = :jobId)")
    suspend fun isApplied(jobId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ApplicationEntity)

    @Query("DELETE FROM applications WHERE jobId = :jobId")
    suspend fun delete(jobId: String)
}
