package com.pawan.hirejetpack.presentation.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawan.hirejetpack.data.BookmarkRepository
import com.pawan.hirejetpack.data.JobRepository
import com.pawan.hirejetpack.domain.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * [JobDetailViewModel] — owns the state for a single job's detail screen:
 * the job itself, its application status, and its bookmark status.
 *
 * KEYWORD: [SavedStateHandle]
 * Automatically injected by `viewModel()` when this ViewModel is created
 * inside a `composable(...)` destination that declares a nav argument.
 * It's a key-value bag holding the current destination's arguments AND
 * survives process death (unlike a constructor parameter would), which is
 * exactly why we read `jobId` from here instead of passing it straight
 * into the ViewModel's constructor from the Composable.
 *
 * Staff note on bookmarks: this ViewModel does NOT own bookmark state —
 * [BookmarkRepository] does. Here, we just `collect` its
 * [BookmarkRepository.bookmarkedIds] flow inside `init` and mirror
 * whether THIS job's id is in that set into our own `_uiState`. That's
 * the pattern for "read shared state, expose it as part of a
 * screen-specific state class": collect the shared flow, don't copy its
 * value once and forget about it — collecting keeps this screen in sync
 * if the bookmark is toggled from the Home screen while this screen is
 * still open (e.g. after backgrounding and returning).
 */
class JobDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val bookmarkRepository: BookmarkRepository
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
                bookmarkRepository.bookmarkedIds.collect { bookmarkedIds ->
                    val current = _uiState.value
                    if (current is JobDetailUiState.Found) {
                        _uiState.value = current.copy(isBookmarked = job.id in bookmarkedIds)
                    }
                }
            }
        }
    }

    /**
     * Submits an application for the currently loaded job.
     *
     * Noob note: `delay(900)` is standing in for a real network call —
     * swap it for a suspend repository function later and nothing else
     * here changes.
     *
     * Staff note: the guard on `currentState is Found && applicationStatus
     * == Idle` prevents a double-submit if the user manages to tap twice
     * before recomposition disables the button — never rely on the UI
     * alone (like `enabled = false`) to make an action safe; the source
     * of truth should refuse the duplicate too.
     */
    fun applyToJob() {
        val currentState = _uiState.value
        if (currentState !is JobDetailUiState.Found) return
        if (currentState.applicationStatus != ApplicationStatus.Idle) return

        _uiState.value = currentState.copy(applicationStatus = ApplicationStatus.Submitting)

        viewModelScope.launch {
            delay(900.milliseconds) // Simulated network round-trip
            val latest = _uiState.value
            if (latest is JobDetailUiState.Found) {
                _uiState.value = latest.copy(applicationStatus = ApplicationStatus.Applied)
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