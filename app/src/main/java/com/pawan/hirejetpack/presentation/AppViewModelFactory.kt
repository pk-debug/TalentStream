package com.pawan.hirejetpack.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pawan.hirejetpack.HireJetpackApp
import com.pawan.hirejetpack.data.ApplicationRepository
import com.pawan.hirejetpack.data.BookmarkRepository
import com.pawan.hirejetpack.presentation.state.ApplicationsViewModel
import com.pawan.hirejetpack.presentation.state.HomeViewModel
import com.pawan.hirejetpack.presentation.state.JobDetailViewModel
import com.pawan.hirejetpack.presentation.state.SavedJobsViewModel

/**
 * [appViewModelFactory] — one [ViewModelProvider.Factory] that constructs
 * every ViewModel in this app needing constructor dependencies.
 */
fun appViewModelFactory(
    bookmarkRepository: BookmarkRepository,
    applicationRepository: ApplicationRepository
): ViewModelProvider.Factory =
    viewModelFactory {
        initializer { HomeViewModel(bookmarkRepository) }
        initializer { SavedJobsViewModel(bookmarkRepository) }
        initializer { ApplicationsViewModel(bookmarkRepository, applicationRepository) }
        initializer {
            val savedStateHandle = this.createSavedStateHandle()
            JobDetailViewModel(
                savedStateHandle = savedStateHandle,
                bookmarkRepository = bookmarkRepository,
                applicationRepository = applicationRepository
            )
        }
    }

/**
 * [rememberAppViewModelFactory] — Composable convenience wrapper.
 */
@Composable
fun rememberAppViewModelFactory(): ViewModelProvider.Factory {
    val context = LocalContext.current
    val appContainer = (context.applicationContext as HireJetpackApp).appContainer
    val bookmarkRepository = appContainer.bookmarkRepository
    val applicationRepository = appContainer.applicationRepository

    return remember(bookmarkRepository, applicationRepository) {
        appViewModelFactory(bookmarkRepository, applicationRepository)
    }
}
