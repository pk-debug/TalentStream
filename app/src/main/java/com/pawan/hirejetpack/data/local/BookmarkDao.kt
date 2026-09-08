package com.pawan.hirejetpack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * [BookmarkDao] — declares WHAT queries exist; Room's annotation
 * processor generates the actual SQLite implementation at compile time.
 * You never write a class that implements this interface yourself.
 *
 * KEYWORD: a DAO query returning [Flow]
 * This is a genuinely different mechanism from the `MutableStateFlow`s
 * used everywhere else in this app. Room wires this Flow up to SQLite's
 * own change-notification system: any INSERT/DELETE/UPDATE that touches
 * the `bookmarks` table causes THIS Flow to emit a fresh list
 * automatically — no manual "now go refresh the UI" call anywhere. Worth
 * being able to say explicitly in an interview: "Room Flow queries are
 * reactive by observing table invalidation, not by me pushing updates."
 *
 * `getAll()` is NOT `suspend` — reading a Flow doesn't block, you collect
 * it. `insert`/`delete` ARE `suspend` — those are one-shot writes that
 * genuinely need to complete before moving on, off the main thread.
 */
@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks")
    fun getAll(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE jobId = :jobId)")
    suspend fun isBookmarked(jobId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE jobId = :jobId")
    suspend fun delete(jobId: String)
}