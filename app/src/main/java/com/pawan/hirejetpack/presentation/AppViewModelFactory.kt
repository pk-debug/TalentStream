package com.pawan.hirejetpack.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.SavedStateHandle
import com.pawan.hirejetpack.HireJetpackApp
import com.pawan.hirejetpack.data.BookmarkRepository
import com.pawan.hirejetpack.presentation.state.ApplicationsViewModel
import com.pawan.hirejetpack.presentation.state.HomeViewModel
import com.pawan.hirejetpack.presentation.state.JobDetailViewModel
import com.pawan.hirejetpack.presentation.state.SavedJobsViewModel

/**
 * [appViewModelFactory] — one [ViewModelProvider.Factory] that knows how
 * to construct every ViewModel in this app needing a constructor
 * dependency.
 */
fun appViewModelFactory(bookmarkRepository: BookmarkRepository): ViewModelProvider.Factory =
    viewModelFactory {
        initializer { HomeViewModel(bookmarkRepository) }
        initializer { SavedJobsViewModel(bookmarkRepository) }
        initializer { ApplicationsViewModel(bookmarkRepository) }
        initializer {
            // Use SavedStateHandle.createHandle(CreationExtras)
            val savedStateHandle = SavedStateHandle.createHandle(this)
            JobDetailViewModel(
                savedStateHandle = savedStateHandle,
                bookmarkRepository = bookmarkRepository
            )
        }
    }

/**
 * [rememberAppViewModelFactory] — Composable convenience wrapper.
 */
@Composable
fun rememberAppViewModelFactory(): ViewModelProvider.Factory {
    val context = LocalContext.current
    val bookmarkRepository = (context.applicationContext as HireJetpackApp)
        .appContainer.bookmarkRepository
    return remember(bookmarkRepository) { appViewModelFactory(bookmarkRepository) }
}
