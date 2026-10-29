package com.pawan.hirejetpack.domain.usecase

import com.pawan.hirejetpack.data.ApplicationRepository
import kotlinx.coroutines.flow.Flow

/**
 * [ApplyToJobUseCase] — domain use case for submitting and observing job applications.
 */
class ApplyToJobUseCase(
    private val applicationRepository: ApplicationRepository
) {
    val appliedJobIds: Flow<Set<String>> = applicationRepository.appliedJobIds

    suspend operator fun invoke(jobId: String): Result<Unit> {
        if (jobId.isBlank()) {
            return Result.failure(IllegalArgumentException("Job ID cannot be blank"))
        }
        return applicationRepository.applyToJob(jobId)
    }

    suspend fun isApplied(jobId: String): Boolean {
        return applicationRepository.isApplied(jobId)
    }
}
