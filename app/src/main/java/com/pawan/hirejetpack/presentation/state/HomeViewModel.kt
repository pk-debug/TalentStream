package com.pawan.hirejetpack.presentation.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawan.hirejetpack.data.BookmarkRepository
import com.pawan.hirejetpack.data.JobRepository
import com.pawan.hirejetpack.domain.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * [HomeViewModel] — owns the job list state shown on the Home tab: search
 * filtering AND bookmark status, combined into one [HomeUiState].
 *
 * Staff note — the constructor changed from "no parameters" to
 * `(bookmarkRepository: BookmarkRepository)`. That single change is the
 * whole point of today's feature: this class now depends on an
 * INTERFACE, supplied from outside, instead of reaching out to a global
 * `object` singleton by name. Nothing else about this class's logic
 * changed — `combine`, `debounce`, `distinctUntilChanged`, `stateIn` all
 * work exactly as before, because [BookmarkRepository.bookmarkedIds] is
 * still just a `Flow<Set<String>>` either way.
 */
class HomeViewModel(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val allJobs: List<Job> = JobRepository.getJobs()
    private val availableTags: List<String> = allJobs.flatMap { it.tags }.distinct().sorted()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())

    val uiState: StateFlow<HomeUiState> = combine(
        _searchQuery.debounce(300).distinctUntilChanged(),
        _selectedTags,
        bookmarkRepository.bookmarkedIds
    ) { query, selectedTags, bookmarkedIds ->
        buildUiState(query = query, selectedTags = selectedTags, bookmarkedIds = bookmarkedIds)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(jobs = allJobs, availableTags = availableTags)
    )

    val recentSearches: StateFlow<List<String>> = _recentSearches

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank() && query !in _recentSearches.value) {
            // Logic to add to history could be here or on "Enter" / selection
            // For now, let's just update the query
        }
    }

    fun onSearchTriggered(query: String) {
        if (query.isNotBlank()) {
            val current = _recentSearches.value.toMutableList()
            current.remove(query)
            current.add(0, query)
            _recentSearches.value = current.take(5)
        }
    }

    fun onClearSearch() {
        _searchQuery.value = ""
    }

    fun toggleTag(tag: String) {
        val current = _selectedTags.value
        _selectedTags.value = if (tag in current) current - tag else current + tag
    }

    /**
     * `toggleBookmark` is now `suspend` underneath (it hits Room), so this
     * function launches it in [viewModelScope] rather than calling it
     * directly — the UI-facing method signature (`fun toggleBookmark(jobId:
     * String)`, no `suspend`) stays exactly the same as before Room
     * existed, which is why [com.pawan.hirejetpack.presentation.ui.home.HomeScreen]
     * needed zero changes for this feature.
     */
    fun toggleBookmark(jobId: String) {
        viewModelScope.launch {
            bookmarkRepository.toggleBookmark(jobId)
        }
    }

    private fun buildUiState(
        query: String,
        selectedTags: Set<String>,
        bookmarkedIds: Set<String>
    ): HomeUiState {
        val filtered = allJobs.filter { job ->
            val matchesQuery = if (query.isBlank()) true else {
                job.title.contains(query, ignoreCase = true) ||
                        job.company.contains(query, ignoreCase = true) ||
                        job.location.contains(query, ignoreCase = true) ||
                        job.tags.any { it.contains(query, ignoreCase = true) }
            }
            val matchesTags = if (selectedTags.isEmpty()) true else {
                selectedTags.any { tag -> job.tags.contains(tag) }
            }
            matchesQuery && matchesTags
        }
        return HomeUiState(
            jobs = filtered,
            searchQuery = query,
            bookmarkedIds = bookmarkedIds,
            selectedTags = selectedTags,
            availableTags = availableTags
        )
    }
}

//package com.pawan.hirejetpack.presentation.state
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.pawan.hirejetpack.data.BookmarkRepository
//import com.pawan.hirejetpack.data.JobRepository
//import com.pawan.hirejetpack.domain.Job
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.flow.debounce
//import kotlinx.coroutines.flow.distinctUntilChanged
//import kotlinx.coroutines.flow.stateIn
//
///**
// * [HomeViewModel] — owns the job list state shown on the Home screen:
// * search filtering AND bookmark status, combined into one [HomeUiState].
// *
// * Staff note: [uiState] is built from TWO independent reactive sources —
// * the debounced search query (owned by this ViewModel) and
// * [BookmarkRepository.bookmarkedIds] (owned by the data layer, shared
// * with the Job Detail screen). [combine] is the Flow operator for exactly
// * this situation: "recompute my output whenever EITHER input changes,"
// * without manually tracking which one changed or re-deriving state by
// * hand on every emission.
// */
//class HomeViewModel : ViewModel() {
//
//    private val allJobs: List<Job> = JobRepository.getJobs()
//
//    private val _searchQuery = MutableStateFlow("")
//
//    val uiState: StateFlow<HomeUiState> = combine(
//        _searchQuery.debounce(300).distinctUntilChanged(),
//        BookmarkRepository.bookmarkedIds
//    ) { query, bookmarkedIds ->
//        buildUiState(query = query, bookmarkedIds = bookmarkedIds)
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = HomeUiState(jobs = allJobs)
//    )
//
//    fun onSearchQueryChanged(query: String) {
//        _searchQuery.value = query
//    }
//
//    fun onClearSearch() {
//        _searchQuery.value = ""
//    }
//
//    /**
//     * Delegates straight to the shared repository — this ViewModel does
//     * not own bookmark state, it only triggers a change to it.
//     */
//    fun toggleBookmark(jobId: String) {
//        BookmarkRepository.toggleBookmark(jobId)
//    }
//
//    private fun buildUiState(query: String, bookmarkedIds: Set<String>): HomeUiState {
//        val filtered = if (query.isBlank()) {
//            allJobs
//        } else {
//            allJobs.filter { job ->
//                job.title.contains(query, ignoreCase = true) ||
//                        job.company.contains(query, ignoreCase = true) ||
//                        job.location.contains(query, ignoreCase = true) ||
//                        job.tags.any { it.contains(query, ignoreCase = true) }
//            }
//        }
//        return HomeUiState(jobs = filtered, searchQuery = query, bookmarkedIds = bookmarkedIds)
//    }
//}