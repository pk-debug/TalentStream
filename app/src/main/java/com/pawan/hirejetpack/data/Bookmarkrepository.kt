package com.pawan.hirejetpack.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.Flow

/**
 * [BookmarkRepository] — the CONTRACT for bookmark persistence, with no
 * mention of Room, SQLite, or any storage mechanism at all.
 *
 * Staff note — this file is the Dependency Inversion Principle (the "D"
 * in SOLID), made concrete: every ViewModel in this app depends on THIS
 * interface, never on [RoomBookmarkRepository] directly. High-level
 * modules (ViewModels) don't depend on low-level modules (Room); both
 * depend on this abstraction instead. The practical payoff: swap
 * [RoomBookmarkRepository] for an in-memory fake in a unit test, or for a
 * server-synced implementation later, and zero ViewModel code changes —
 * only what gets passed into the constructor changes.
 *
 * `suspend fun toggleBookmark` (not a plain function) matters too: Room
 * writes are I/O and must run off the main thread, and Kotlin's `suspend`
 * keyword is what makes that requirement visible at the call site instead
 * of hidden inside the implementation.
 */
interface BookmarkRepository {
    val bookmarkedIds: Flow<Set<String>>
    suspend fun toggleBookmark(jobId: String)
}

/**
 * [BookmarkRepository] — single source of truth for which job ids the
 * user has bookmarked.
 *
 * Staff note: this is an `object` (singleton), same shape as
 * [JobRepository]. Both [com.pawan.hirejetpack.presentation.state.HomeViewModel]
 * and [com.pawan.hirejetpack.presentation.state.JobDetailViewModel] read
 * from this ONE [bookmarkedIds] flow — that's what makes a bookmark
 * toggled on the detail screen instantly visible on the Home feed too,
 * with no manual syncing between the two ViewModels. In a real app this
 * object's internals would be backed by Room/DataStore instead of an
 * in-memory Set, but nothing in either ViewModel would need to change —
 * they only depend on this public Flow-based contract.
 */
//object BookmarkRepository {
//    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
//    val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds.asStateFlow()
//
//    fun toggleBookmark(jobId: String) {
//        _bookmarkedIds.value = if (jobId in _bookmarkedIds.value) {
//            _bookmarkedIds.value - jobId
//        } else {
//            _bookmarkedIds.value + jobId
//        }
//    }
//}