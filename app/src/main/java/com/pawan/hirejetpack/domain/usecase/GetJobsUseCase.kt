package com.pawan.hirejetpack.domain.usecase

import com.pawan.hirejetpack.data.JobRepository
import com.pawan.hirejetpack.domain.Job

/**
 * [GetJobsUseCase] — domain business logic for retrieving and looking up jobs.
 */
class GetJobsUseCase(
    private val jobRepository: JobRepository = JobRepository
) {
    operator fun invoke(): List<Job> {
        return jobRepository.getJobs()
    }

    fun getJobById(id: String): Job? {
        return jobRepository.getJobById(id)
    }
}
