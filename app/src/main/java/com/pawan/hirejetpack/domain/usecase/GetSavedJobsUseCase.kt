package com.pawan.hirejetpack.domain.usecase

import com.pawan.hirejetpack.data.BookmarkRepository
import com.pawan.hirejetpack.data.JobRepository
import com.pawan.hirejetpack.domain.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [GetSavedJobsUseCase] — domain use case combining bookmarked IDs and job listings.
 */
class GetSavedJobsUseCase(
    private val bookmarkRepository: BookmarkRepository,
    private val jobRepository: JobRepository = JobRepository
) {
    operator fun invoke(): Flow<List<Job>> {
        val allJobs = jobRepository.getJobs()
        return bookmarkRepository.bookmarkedIds.map { bookmarkedIds ->
            allJobs.filter { it.id in bookmarkedIds }
        }
    }
}
