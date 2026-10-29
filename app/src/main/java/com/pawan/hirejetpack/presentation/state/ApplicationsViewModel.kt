package com.pawan.hirejetpack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawan.hirejetpack.data.ApplicationRepository
import com.pawan.hirejetpack.data.BookmarkRepository
import com.pawan.hirejetpack.data.JobRepository
import com.pawan.hirejetpack.domain.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * [ApplicationsViewModel] — the Applications tab's state, combining TWO
 * shared repositories: which jobs were applied to (persisted in Room),
 * and which are bookmarked.
 */
class ApplicationsViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val applicationRepository: ApplicationRepository
) : ViewModel() {

    private val allJobs: List<Job> = JobRepository.getJobs()

    val uiState: StateFlow<ApplicationsUiState> = combine(
        applicationRepository.appliedJobIds,
        bookmarkRepository.bookmarkedIds
    ) { appliedIds, bookmarkedIds ->
        ApplicationsUiState(
            jobs = allJobs.filter { it.id in appliedIds },
            bookmarkedIds = bookmarkedIds
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ApplicationsUiState()
    )

    fun toggleBookmark(jobId: String) {
        viewModelScope.launch {
            bookmarkRepository.toggleBookmark(jobId)
        }
    }
}
