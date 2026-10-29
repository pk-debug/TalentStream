package com.pawan.hirejetpack.data

import kotlinx.coroutines.flow.Flow

/**
 * [ApplicationRepository] — the contract for managing applied jobs.
 * Implementations persist to disk (e.g. Room SQLite) and provide reactive updates.
 */
interface ApplicationRepository {
    val appliedJobIds: Flow<Set<String>>
    suspend fun applyToJob(jobId: String): Result<Unit>
    suspend fun isApplied(jobId: String): Boolean
}
