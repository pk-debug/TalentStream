package com.pawan.hirejetpack.presentation.navigation

/**
 * [Screen] — sealed class defining every top-level navigation route.
 *
 * OOP Concept: Type-Safe Abstraction
 * Using a sealed class instead of raw string constants means the compiler
 * catches typos in route names, and every destination is enumerable in
 * one place.
 *
 * Staff note: [Home] and [Profile] used to be separate routes here.
 * They're gone — both are now tabs inside [Main]'s bottom navigation
 * (see [com.pawan.hirejetpack.presentation.ui.main.BottomNavItem]), not
 * destinations the `NavHost` itself knows about. [JobDetail] stays a
 * `NavHost`-level route because it's something you genuinely PUSH and
 * BACK out of, unlike a tab switch.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Main : Screen("main_screen")
    object Analytics : Screen("analytics_screen")

    object JobDetail : Screen("job_detail_screen/{jobId}") {
        fun createRoute(jobId: String) = "job_detail_screen/$jobId"
    }
}