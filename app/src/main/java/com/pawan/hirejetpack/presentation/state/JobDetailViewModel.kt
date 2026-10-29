package com.pawan.hirejetpack.presentation.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawan.hirejetpack.data.ApplicationRepository
import com.pawan.hirejetpack.data.BookmarkRepository
import com.pawan.hirejetpack.data.JobRepository
import com.pawan.hirejetpack.domain.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * [JobDetailViewModel] — owns the state for a single job's detail screen:
 * the job itself, its application status, and its bookmark status.
 *
 * Persisted Application Status:
 * Backed by [ApplicationRepository] and [BookmarkRepository] via Room database,
 * ensuring application status and bookmarks survive process death.
 */
class JobDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val bookmarkRepository: BookmarkRepository,
    private val applicationRepository: ApplicationRepository
) : ViewModel() {

    private val jobId: String? = savedStateHandle["jobId"]
    private val job: Job? = jobId?.let { JobRepository.getJobById(it) }

    private val _uiState = MutableStateFlow<JobDetailUiState>(
        if (job != null) JobDetailUiState.Found(job) else JobDetailUiState.NotFound
    )
    val uiState: StateFlow<JobDetailUiState> = _uiState.asStateFlow()

    init {
        if (job != null) {
            viewModelScope.launch {
                combine(
                    bookmarkRepository.bookmarkedIds,
                    applicationRepository.appliedJobIds
                ) { bookmarkedIds, appliedIds ->
                    val isBookmarked = job.id in bookmarkedIds
                    val isApplied = job.id in appliedIds
                    isBookmarked to isApplied
                }.collect { (isBookmarked, isApplied) ->
                    val current = _uiState.value
                    if (current is JobDetailUiState.Found) {
                        _uiState.value = current.copy(
                            isBookmarked = isBookmarked,
                            applicationStatus = if (isApplied) ApplicationStatus.Applied else current.applicationStatus
                        )
                    }
                }
            }
        }
    }

    /**
     * Submits an application for the currently loaded job via a suspend repository call.
     */
    fun applyToJob() {
        val currentState = _uiState.value
        if (currentState !is JobDetailUiState.Found) return
        if (currentState.applicationStatus != ApplicationStatus.Idle) return

        _uiState.value = currentState.copy(applicationStatus = ApplicationStatus.Submitting)

        viewModelScope.launch {
            val result = applicationRepository.applyToJob(currentState.job.id)
            val latest = _uiState.value
            if (latest is JobDetailUiState.Found) {
                _uiState.value = if (result.isSuccess) {
                    latest.copy(applicationStatus = ApplicationStatus.Applied)
                } else {
                    latest.copy(applicationStatus = ApplicationStatus.Idle)
                }
            }
        }
    }

    fun toggleBookmark() {
        job?.let {
            viewModelScope.launch {
                bookmarkRepository.toggleBookmark(it.id)
            }
        }
    }
}
