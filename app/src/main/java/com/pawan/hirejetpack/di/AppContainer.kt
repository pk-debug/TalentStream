package com.pawan.hirejetpack.di

import android.content.Context
import com.pawan.hirejetpack.data.ApplicationRepository
import com.pawan.hirejetpack.data.BookmarkRepository
import com.pawan.hirejetpack.data.DefaultJobRepository
import com.pawan.hirejetpack.data.JobRepository
import com.pawan.hirejetpack.data.RoomApplicationRepository
import com.pawan.hirejetpack.data.RoomBookmarkRepository
import com.pawan.hirejetpack.data.local.AppDatabase
import com.pawan.hirejetpack.domain.usecase.ApplyToJobUseCase
import com.pawan.hirejetpack.domain.usecase.GetJobsUseCase
import com.pawan.hirejetpack.domain.usecase.GetSavedJobsUseCase
import com.pawan.hirejetpack.domain.usecase.ToggleBookmarkUseCase

/**
 * [AppContainer] — manual Dependency Injection container.
 *
 * Startup optimization: Dependencies like [database], [bookmarkRepository],
 * and [applicationRepository] are initialized using `by lazy` so that expensive
 * disk I/O, Room SQLite reflection, and open helpers are deferred until first use,
 * keeping app cold start fast.
 */
class AppContainer(private val context: Context) {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    val jobRepository: JobRepository by lazy {
        DefaultJobRepository()
    }

    val bookmarkRepository: BookmarkRepository by lazy {
        RoomBookmarkRepository(database.bookmarkDao())
    }

    val applicationRepository: ApplicationRepository by lazy {
        RoomApplicationRepository(database.applicationDao())
    }

    // Domain Use Cases
    val getJobsUseCase: GetJobsUseCase by lazy {
        GetJobsUseCase(jobRepository)
    }

    val toggleBookmarkUseCase: ToggleBookmarkUseCase by lazy {
        ToggleBookmarkUseCase(bookmarkRepository)
    }

    val applyToJobUseCase: ApplyToJobUseCase by lazy {
        ApplyToJobUseCase(applicationRepository)
    }

    val getSavedJobsUseCase: GetSavedJobsUseCase by lazy {
        GetSavedJobsUseCase(bookmarkRepository, jobRepository)
    }
}
