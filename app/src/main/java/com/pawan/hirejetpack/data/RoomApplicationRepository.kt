package com.pawan.hirejetpack.data

import com.pawan.hirejetpack.data.local.ApplicationDao
import com.pawan.hirejetpack.data.local.ApplicationEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.milliseconds

/**
 * [RoomApplicationRepository] — disk-backed implementation of [ApplicationRepository]
 * using Room SQLite. Replaces simulated in-memory state with true persistence
 * and realistic suspend network-to-disk application pipeline.
 */
class RoomApplicationRepository(
    private val dao: ApplicationDao
) : ApplicationRepository {

    override val appliedJobIds: Flow<Set<String>> =
        dao.getAll().map { list -> list.map { it.jobId }.toSet() }

    override suspend fun applyToJob(jobId: String): Result<Unit> {
        return try {
            // Simulated network latency for backend submission
            delay(800.milliseconds)
            // Persist to local SQLite database so applications survive process death
            dao.insert(ApplicationEntity(jobId = jobId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isApplied(jobId: String): Boolean {
        return dao.isApplied(jobId)
    }
}
