package com.pawan.hirejetpack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * [AppDatabase] — the app's single Room database instance.
 *
 * Staff note: [INSTANCE] + double-checked-locking [getInstance] is the
 * classic manual singleton pattern for "build this expensive object
 * exactly once, hand out the same instance to everyone." This is EXACTLY
 * what Hilt's `@Provides @Singleton` annotation generates for you — if
 * this app added Hilt, this companion object would collapse into:
 *
 * ```
 * @Module
 * @InstallIn(SingletonComponent::class)
 * object DatabaseModule {
 *     @Provides
 *     @Singleton
 *     fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
 *         Room.databaseBuilder(context, AppDatabase::class.java, "hirejetpack.db").build()
 * }
 * ```
 *
 * Being able to point at this class and say "this IS what @Provides
 * @Singleton does, written by hand" is a much stronger interview answer
 * than reciting the annotation's definition.
 */
@Database(entities = [BookmarkEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hirejetpack.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}