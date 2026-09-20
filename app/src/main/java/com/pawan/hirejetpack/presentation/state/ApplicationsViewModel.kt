package com.pawan.hirejetpack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawan.hirejetpack.data.ApplicationsRepository
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
 * shared repositories: which jobs were applied to, and which are
 * bookmarked (so the same [com.pawan.hirejetpack.presentation.ui.home.JobCard]
 * can render correctly here too).
 *
 * Staff note — the constructor changed from "no parameters" to
 * `(bookmarkRepository: BookmarkRepository)`. That single change is the
 * whole point of today's feature: this class now depends on an
 * INTERFACE, supplied from outside, instead of reaching out to a global
 * `object` singleton by name. Nothing else about this class's logic
 * changed — `combine` and `stateIn` still work exactly as before, because
 * [BookmarkRepository.bookmarkedIds] is still just a `Flow<Set<String>>`
 * either way.
 */
class ApplicationsViewModel(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val allJobs: List<Job> = JobRepository.getJobs()

    val uiState: StateFlow<ApplicationsUiState> = combine(
        ApplicationsRepository.appliedJobIds,
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