package com.pawan.hirejetpack.data.local

/**
 * [BookmarkEntity] — Room's on-disk row shape for "this job id is
 * bookmarked."
 *
 * Staff note: this is deliberately NOT [com.pawan.hirejetpack.domain.Job].
 * Room entities are a PERSISTENCE-layer concern — table name, primary
 * keys, column types — and shouldn't leak into `domain` or
 * `presentation`. That's why [RoomBookmarkRepository] maps this straight
 * to a `Set<String>` before returning anything upward; nothing above the
 * `data` layer ever sees a `BookmarkEntity`. Only `jobId` is stored —
 * Room requires a primary key even for a table this simple.
 */
@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val jobId: String
)