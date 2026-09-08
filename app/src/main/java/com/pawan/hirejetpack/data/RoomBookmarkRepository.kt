package com.pawan.hirejetpack.data

import com.pawan.hirejetpack.data.local.BookmarkDao
import com.pawan.hirejetpack.data.local.BookmarkEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [RoomBookmarkRepository] — the real, disk-backed implementation of
 * [BookmarkRepository].
 *
 * Staff note: `dao.getAll()` returns `Flow<List<BookmarkEntity>>`; this
 * class's job is entirely to translate that into the
 * `Flow<Set<String>>` shape [BookmarkRepository] promises, so
 * `BookmarkEntity` never has to be imported by anything outside `data`.
 * `map` here is doing the SAME kind of transformation work as `map` in
 * [com.pawan.hirejetpack.presentation.state.SavedJobsViewModel] — Flow
 * operators aren't special to any one layer, they're a general tool used
 * consistently top to bottom in this codebase.
 */
class RoomBookmarkRepository(
    private val dao: BookmarkDao
) : BookmarkRepository {

    override val bookmarkedIds: Flow<Set<String>> =
        dao.getAll().map { entities -> entities.map { it.jobId }.toSet() }

    override suspend fun toggleBookmark(jobId: String) {
        if (dao.isBookmarked(jobId)) {
            dao.delete(jobId)
        } else {
            dao.insert(BookmarkEntity(jobId))
        }
    }
}