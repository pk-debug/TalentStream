package com.pawan.hirejetpack.domain.usecase

import com.pawan.hirejetpack.data.BookmarkRepository
import kotlinx.coroutines.flow.Flow

/**
 * [ToggleBookmarkUseCase] — domain use case for toggling and observing bookmarks.
 */
class ToggleBookmarkUseCase(
    private val bookmarkRepository: BookmarkRepository
) {
    val bookmarkedIds: Flow<Set<String>> = bookmarkRepository.bookmarkedIds

    suspend operator fun invoke(jobId: String) {
        bookmarkRepository.toggleBookmark(jobId)
    }
}
