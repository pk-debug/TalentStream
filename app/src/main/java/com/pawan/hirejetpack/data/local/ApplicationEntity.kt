package com.pawan.hirejetpack.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [ApplicationEntity] — represents a job that the user has applied to,
 * persisted in Room SQLite storage so applications survive process death.
 */
@Entity(tableName = "applications")
data class ApplicationEntity(
    @PrimaryKey
    val jobId: String,
    val appliedAt: Long = System.currentTimeMillis()
)
