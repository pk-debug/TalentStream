# Talent Stream — Jetpack Compose Job App

A modern, single-activity Android application built with **Jetpack Compose**, implementing **MVVM Architecture** and **OOP principles**. The app features a **Login Screen**, a **Home Screen** with a **navigation drawer** and a **job listing feed**, and a redesigned **Profile Screen** — all connected with **type-safe navigation**.

---

## 🛠 Features

- **Jetpack Compose UI:** 100% declarative UI with Material 3 components.
- **MVVM Architecture:** Clean separation of UI logic, state, and data.
- **Navigation Drawer:** Slide-out `ModalNavigationDrawer` on the Home screen with a profile summary header, Home/Profile/Logout entries.
- **Job Listing Feed:** Scrollable `LazyColumn` of job cards (title, company, location, salary, skill tags), backed by a mock `JobRepository`.
- **Job Detail Screen:** Tapping a job card navigates to a full detail view via a parameterized route (`job_detail_screen/{jobId}`), with dedicated Loading / Found / Not-Found states.
- **Apply Flow:** Confirmation dialog → simulated submission → "Applied" state, modeled as its own `ApplicationStatus` sub-state (Idle / Submitting / Applied) so the button can never show a contradictory combination.
- **Debounced Search:** Filter the job feed by title, company, location, or skill tag. Search input is debounced (300ms) via Kotlin Flow operators so filtering doesn't run on every single keystroke.
- **Bookmarking:** Save/unsave jobs from either the feed or the detail screen. Bookmark status is shared app-wide via a repository-level `StateFlow`, so toggling it in one place is instantly reflected everywhere.
- **Bookmarks:** Save a job from either the feed (`JobCard`) or the detail screen's top bar. Bookmark state lives in a single shared `BookmarkRepository`, so toggling it on one screen is instantly reflected on the other.
- **Redesigned Profile Screen:** Gradient header, circular initials avatar, role badge, and info displayed in an elevated card.
- **Type-Safe Navigation:** Built with `navigation-compose`, routes defined as a sealed class, backstack cleared correctly on login/logout.
- **Reactive State Management:** Kotlin `StateFlow` per screen, collected safely in Compose via `collectAsState`.
- **OOP Concepts:** Encapsulation, Abstraction, Polymorphism, Single Responsibility Principle (SRP), Composition over Inheritance, and DRY (shared `InitialsAvatar` component).

---

## 🧭 App Flow

```text
Login Screen
     │  (successful login)
     ▼
Home Screen  ──(tap a job card)──▶  Job Detail Screen
     │      │                              │
     │      └──(Back)──────────────────────┘
     │
     └──(drawer → Profile, or profile icon in top bar)──▶  Profile Screen
     │                                                              │
     └──────────────────── Logout (from either screen) ────────────┘
                                        │
                                        ▼
                                  Login Screen
```

---

## 🏗 App Architecture & Structure

```text
com.pawan.hirejetpack/
│
├── domain/                         # Pure data models — no Android imports
│   ├── UserProfile.kt              # Logged-in user's data
│   └── Job.kt                      # One job listing
│
├── data/                           # Data sources
│   ├── JobRepository.kt            # Mock job data + getJobById() lookup (Retrofit-shaped for easy swap later)
│   └── BookmarkRepository.kt       # Single source of truth for bookmarked job ids, shared across screens
│
├── presentation/
│   ├── state/                      # ViewModels + UI state classes, one pair per screen
│   │   ├── LoginUiState.kt         # Idle / Loading / Success / Error
│   │   ├── LoginViewModel.kt
│   │   ├── HomeUiState.kt          # jobs (filtered) + searchQuery + bookmarkedIds + isLoading
│   │   ├── HomeViewModel.kt        # combine(debounced search, BookmarkRepository.bookmarkedIds) -> stateIn
│   │   ├── JobDetailUiState.kt     # Loading / Found (+ ApplicationStatus, isBookmarked) / NotFound
│   │   ├── JobDetailViewModel.kt   # Reads jobId via SavedStateHandle; collects BookmarkRepository to stay in sync
│   │   └── ApplicationStatus.kt    # Idle / Submitting / Applied sub-state for the apply flow
│   │
│   ├── navigation/                 # Routing
│   │   ├── Screen.kt               # Sealed class of routes, incl. parameterized JobDetail
│   │   └── AppNavigation.kt        # NavHost graph wiring screens together
│   │
│   └── ui/                         # Compose screens, grouped by feature
│       ├── components/
│       │   └── InitialsAvatar.kt   # Shared circular avatar (drawer + profile)
│       ├── login/
│       │   └── LoginScreen.kt
│       ├── home/
│       │   ├── HomeScreen.kt       # Drawer + search bar + job feed scaffold (incl. private JobSearchField)
│       │   ├── AppDrawerContent.kt # Drawer panel contents
│       │   └── JobCard.kt          # One row in the feed — clickable, bookmarkable, navigates to detail
│       ├── jobdetail/
│       │   └── JobDetailScreen.kt  # Full job info, bookmark toggle, apply confirmation dialog + status-aware button
│       └── profile/
│           └── ProfileScreen.kt    # Gradient header + info card
│
└── MainActivity.kt                 # Entry point — only hosts setContent { AppNavigation() }
```

---

## 🧩 Key Concepts & Keywords Reference

### 1. Object-Oriented Programming (OOP)

| Concept | Implementation in Code |
| --- | --- |
| **Encapsulation** | `LoginViewModel` and `HomeViewModel` each hold a `private MutableStateFlow` and expose only a `public read-only StateFlow`, preventing the UI from mutating state directly. |
| **Polymorphism** | `LoginUiState` is a `sealed interface`; the UI polymorphically renders different layouts (`Loading`, `Error`, `Success`) based on the concrete instance at runtime. |
| **Single Responsibility (SRP)** | Each ViewModel owns one screen's state; `JobRepository` only knows how to fetch jobs; each Composable file renders exactly one piece of UI. |
| **Composition over Inheritance** | `HomeScreen` is built by composing `ModalNavigationDrawer` + `Scaffold` + `LazyColumn` + `JobCard`, not by extending a base screen class. `InitialsAvatar` is reused via composition in both the drawer and Profile screen. |
| **Type-Safe Abstraction** | `Screen` is a `sealed class` of routes — the compiler catches typos that a raw string route name wouldn't. |
| **DRY** | `InitialsAvatar` and `ProfileInfoRow` exist because the same visual pattern showed up twice — pulled out once, reused everywhere. |
| **Single Source of Truth** | `BookmarkRepository` owns bookmark state, not `HomeViewModel` or `JobDetailViewModel` individually — both screens *observe* the same `StateFlow`, so a bookmark toggled on one screen is correct on the other without any manual syncing between them. |

### 2. Jetpack Compose Keywords

| Keyword | Description |
| --- | --- |
| `@Composable` | Annotation telling the Kotlin compiler that a function transforms data into UI nodes. |
| `setContent` | Bridge function attaching Compose layouts to an Android `ComponentActivity`. |
| `remember` | Caches a value in memory across recompositions. |
| `mutableStateOf` | Creates a reactive state holder; updating it triggers recomposition of dependent UI. |
| `collectAsState` | Collects a `StateFlow`'s emissions into reactive Compose `State`. |
| `rememberNavController` | Creates and retains a `NavHostController` across recompositions. |
| `ModalNavigationDrawer` | Renders a slide-out drawer panel alongside main screen content. |
| `rememberDrawerState` / `DrawerState` | Holds whether the drawer is open or closed; `.open()` / `.close()` are suspend functions. |
| `rememberCoroutineScope` | Provides a coroutine scope tied to the composition, used to call suspend functions (like opening the drawer) from click handlers. |
| `Scaffold` | Material layout structure providing slots for TopBar, BottomBar, and body content. |
| `LazyColumn` | Lazily-rendered scrollable list — only composes items currently visible on screen. |
| `SavedStateHandle` | Key-value bag holding the current destination's navigation arguments; injected automatically into a ViewModel and survives process death. |
| `navArgument` | Declares a named, typed argument (e.g. `jobId: String`) on a `composable(...)` route, enabling parameterized navigation like `job_detail_screen/{jobId}`. |
| `debounce` / `distinctUntilChanged` | Flow operators: `debounce` waits for a pause between emissions before passing one through (used here to avoid filtering on every keystroke); `distinctUntilChanged` skips emissions equal to the previous one. |
| `combine` | Flow operator that recomputes an output whenever ANY of several input flows emit — used to merge the debounced search query with the shared bookmark flow into one `HomeUiState`. |
| `stateIn` | Converts a cold `Flow` into a hot, shareable `StateFlow`, with a `SharingStarted` policy controlling when the upstream work actually runs. |

---

## 🚀 Getting Started

### Prerequisites

* **Android Studio:** Ladybug or newer
* **JDK:** 17+
* **Min SDK:** 24 (Android 7.0)
* **Target SDK:** 34 or higher

### Gradle Dependencies

Ensure your `build.gradle.kts` (Module `:app`) contains the following:

```kotlin
dependencies {
    // Jetpack Compose & Material 3
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")

    // Extended icon set (Menu, AccountCircle, Work, LocationOn, Logout, Home)
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    // ViewModel & Navigation for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.navigation:navigation-compose:2.8.0")
}
```

---

## 💻 How to Run

1. Clone or download this repository.
2. Open the project in **Android Studio**.
3. Confirm the module's `applicationId` / package matches `com.pawan.hirejetpack` (or update it, and the `package` line at the top of every file, to match your own).
4. Perform a **Gradle Sync**.
5. Run the application on an Emulator or connected Physical Device (`Shift + F10`).
6. Enter any sample email and password on the Login screen — this navigates to the Home screen's job feed.
7. Type in the search bar to filter jobs by title, company, location, or skill (filtering kicks in ~300ms after you stop typing).
8. Tap the bookmark icon on any job card (or on the detail screen) to save it — notice the icon stays in sync between the feed and detail screen, since both read the same `BookmarkRepository`.
9. Tap a job card to open its detail screen, then **Apply Now** to try the confirm → submit → applied flow (Back returns to the feed).
10. Tap the menu icon (or the profile icon in the top bar) to open the drawer and reach the Profile screen.

---

## 🔮 Next Steps / Extension Ideas

- Add a "Saved Jobs" screen (filter `BookmarkRepository.bookmarkedIds` against `JobRepository.getJobs()`), reachable from the drawer — the data plumbing already exists.
- Add filter chips (by tag) alongside the text search, so users can narrow by skill with a tap instead of typing.
- Replace the simulated `delay(900)` in `applyToJob()` with a real suspend repository call once a backend exists.
- Persist application status and bookmarks (currently both lost on process death) — back `BookmarkRepository` and application status with DataStore instead of in-memory `Set`/state.
- Replace `JobRepository`'s hardcoded list with a real Retrofit-backed data source — `getJobs()` and `getJobById()` are already shaped for this swap; search would then likely move server-side.
- Introduce a `domain`-layer `UseCase` (e.g. `GetJobsUseCase`, `ApplyToJobUseCase`, `ToggleBookmarkUseCase`) once business rules get more complex than what lives in the ViewModels today.
- If the app grows past a handful of features, consider splitting `domain`/`data` into `core-model`/`core-network` modules, and each `presentation/ui/<feature>` folder into its own `feature-*` Gradle module (see multi-module architecture notes).
