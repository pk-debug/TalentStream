package com.pawan.hirejetpack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawan.hirejetpack.data.BookmarkRepository
import com.pawan.hirejetpack.data.JobRepository
import com.pawan.hirejetpack.domain.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * [SavedJobsViewModel] — derives the Saved tab's list purely from
 * [BookmarkRepository], with no local mutable state of its own.
 *
 * Staff note — the constructor changed from "no parameters" to
 * `(bookmarkRepository: BookmarkRepository)`. That single change is the
 * whole point of today's feature: this class now depends on an
 * INTERFACE, supplied from outside, instead of reaching out to a global
 * `object` singleton by name. Nothing else about this class's logic
 * changed — `map` and `stateIn` still work exactly as before, because
 * [BookmarkRepository.bookmarkedIds] is still just a `Flow<Set<String>>`
 * either way.
 */
class SavedJobsViewModel(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val allJobs: List<Job> = JobRepository.getJobs()

    val uiState: StateFlow<SavedJobsUiState> = bookmarkRepository.bookmarkedIds
        .map { bookmarkedIds ->
            SavedJobsUiState(jobs = allJobs.filter { it.id in bookmarkedIds })
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SavedJobsUiState()
        )

    fun toggleBookmark(jobId: String) {
        viewModelScope.launch {
            bookmarkRepository.toggleBookmark(jobId)
        }
    }
}