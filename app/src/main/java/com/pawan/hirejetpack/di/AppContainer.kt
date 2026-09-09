package com.pawan.hirejetpack.di

import android.content.Context
import com.pawan.hirejetpack.data.BookmarkRepository
import com.pawan.hirejetpack.data.RoomBookmarkRepository
import com.pawan.hirejetpack.data.local.AppDatabase

/**
 * [AppContainer] — a hand-rolled dependency container. THIS is manual
 * Dependency Injection: the same core idea Hilt automates, done by hand
 * so the mechanics are visible instead of hidden behind annotations.
 *
 * Staff note — read this class alongside [com.pawan.hirejetpack.HireJetpackApp].
 * Together they answer "what does Hilt actually generate for me?":
 *
 * | This app today (manual)                       | Equivalent with Hilt                          |
 * |------------------------------------------------|-------------------------------------------------|
 * | `AppContainer` class, built by hand             | `@Module @InstallIn(SingletonComponent::class)` |
 * | `HireJetpackApp.appContainer` field             | Generated `Hilt_HireJetpackApp` internals        |
 * | `context.applicationContext as HireJetpackApp`  | `@AndroidEntryPoint` + `hiltViewModel()`         |
 * | `appViewModelFactory(...)` + `viewModel(factory=...)` | `@HiltViewModel` + `hiltViewModel()`       |
 *
 * Being able to draw that table from memory — not just define what Hilt
 * is — is what separates "I've heard of DI" from "I understand DI" in an
 * interview.
 */
class AppContainer(context: Context) {

    private val database: AppDatabase = AppDatabase.getInstance(context)

    val bookmarkRepository: BookmarkRepository = RoomBookmarkRepository(database.bookmarkDao())
}