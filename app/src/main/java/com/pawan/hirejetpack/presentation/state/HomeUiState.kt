package com.pawan.hirejetpack.presentation.state

import com.pawan.hirejetpack.domain.Job

/**
 * [HomeUiState] — Single Source of Truth for the Home screen.
 *
 * Noob note: this is "everything the Home screen needs in order to draw
 * itself" bundled into one object.
 * Staff note: exposing this wrapper (instead of a bare
 * `StateFlow<List<Job>>`) means adding `error: String?` later doesn't
 * change the shape consumers collect — one state object, one collector,
 * no risk of `isLoading` and `jobs` updating on different frames.
 */
data class HomeUiState(
    val jobs: List<Job> = emptyList(),
    val searchQuery: String = "",
    val bookmarkedIds: Set<String> = emptySet(),
    val selectedTags: Set<String> = emptySet(),
    val availableTags: List<String> = emptyList(),
    val isLoading: Boolean = false
)