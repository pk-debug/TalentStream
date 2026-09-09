package com.pawan.hirejetpack.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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
 *
 * KEYWORD: [viewModelFactory] / [initializer]
 * This DSL (from `androidx.lifecycle.viewmodel`) is the modern,
 * reflection-free replacement for the old
 * `if (modelClass == X::class.java) ... else if (...)` factory pattern.
 * Each `initializer<T> { }` block is registered against its ViewModel
 * type; when `viewModel(factory = ...)` is called for a specific class,
 * this factory picks the matching block automatically.
 *
 * KEYWORD: [createSavedStateHandle]
 * Available inside any `initializer { }` block because its receiver is
 * `CreationExtras`, which carries the current navigation back-stack
 * entry's arguments. This is how [JobDetailViewModel] still gets its
 * `jobId` — nothing about that mechanism changed by adding a repository
 * dependency alongside it.
 *
 * Staff note: this is the manual equivalent of Hilt's generated
 * `HiltViewModelFactory`, which does this exact "resolve by requested
 * type" dispatch automatically for every `@HiltViewModel`-annotated
 * class — see the comparison table in [com.pawan.hirejetpack.di.AppContainer].
 */
fun appViewModelFactory(bookmarkRepository: BookmarkRepository): ViewModelProvider.Factory =
    viewModelFactory {
        initializer { HomeViewModel(bookmarkRepository) }
        initializer { SavedJobsViewModel(bookmarkRepository) }
        initializer { ApplicationsViewModel(bookmarkRepository) }
        initializer {
            JobDetailViewModel(
                savedStateHandle = createSavedStateHandle(),
                bookmarkRepository = bookmarkRepository
            )
        }
    }

/**
 * [rememberAppViewModelFactory] — Composable convenience wrapper so call
 * sites don't repeat the `LocalContext` + cast + `remember` boilerplate.
 *
 * Staff note: `context.applicationContext as HireJetpackApp` is the exact
 * line that throws `ClassCastException` if `HireJetpackApp` isn't
 * registered in the manifest — see the warning on that class. `remember`
 * (not `remember(Unit)` or unkeyed recomposition) here means this factory
 * is only rebuilt if `bookmarkRepository` itself changes identity, which
 * it never does within one app process — effectively built once per
 * composition, same spirit as the `AppContainer` singleton underneath it.
 */
@Composable
fun rememberAppViewModelFactory(): ViewModelProvider.Factory {
    val context = LocalContext.current
    val bookmarkRepository = (context.applicationContext as HireJetpackApp)
        .appContainer.bookmarkRepository
    return remember(bookmarkRepository) { appViewModelFactory(bookmarkRepository) }
}