package com.pawan.hirejetpack.presentation.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pawan.hirejetpack.presentation.state.ApplicationsViewModel
import com.pawan.hirejetpack.presentation.state.HomeViewModel
import com.pawan.hirejetpack.presentation.state.LoginViewModel
import com.pawan.hirejetpack.presentation.state.SavedJobsViewModel
import com.pawan.hirejetpack.presentation.ui.applications.ApplicationsScreen
import com.pawan.hirejetpack.presentation.ui.home.HomeScreenContent
import com.pawan.hirejetpack.presentation.ui.profile.ProfileScreenContent
import com.pawan.hirejetpack.presentation.ui.savedjobs.SavedJobsScreen

/**
 * [MainScreen] — the LinkedIn-style shell: ONE Scaffold (top bar + bottom
 * nav) wrapping four swappable tabs.
 *
 * Staff note — the load-bearing decision in this file: [homeViewModel],
 * [savedJobsViewModel], and [applicationsViewModel] are all created HERE,
 * at MainScreen's level, via `viewModel()` — NOT inside each tab's own
 * composable. Because `viewModel()`'s lifetime is tied to where it's
 * first called from in the composition/nav-graph, creating them here
 * means they live as long as MainScreen does, i.e. as long as the user is
 * anywhere inside the bottom-nav shell. Switching Home -> Saved -> Home
 * does NOT recreate HomeViewModel or reset its search text, because
 * `selectedTab` changing only swaps which Composable is drawn — it never
 * removes MainScreen from composition. Had these `viewModel()` calls
 * lived inside `HomeScreenContent` itself instead, the same logic would
 * still hold (Compose still wouldn't recreate them on a tab swap, since
 * `HomeScreenContent` briefly leaves composition but MainScreen's call
 * site persists) — but keeping them here, visible in one place, makes the
 * shared-lifetime intent obvious at a glance instead of implicit.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    loginViewModel: LoginViewModel,
    onJobClick: (String) -> Unit,
    onLogout: () -> Unit,
    onAnalyticsClick: () -> Unit
) {
    val homeViewModel: HomeViewModel = viewModel()
    val savedJobsViewModel: SavedJobsViewModel = viewModel()
    val applicationsViewModel: ApplicationsViewModel = viewModel()

    var selectedTab by remember { mutableStateOf(BottomNavItem.Home) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(selectedTab.title) })
        },
        bottomBar = {
            NavigationBar {
                BottomNavItem.entries.forEach { item ->
                    NavigationBarItem(
                        selected = selectedTab == item,
                        onClick = { selectedTab = item },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                BottomNavItem.Home -> HomeScreenContent(
                    viewModel = homeViewModel,
                    onJobClick = onJobClick
                )

                BottomNavItem.Saved -> SavedJobsScreen(
                    viewModel = savedJobsViewModel,
                    onJobClick = onJobClick
                )

                BottomNavItem.Applications -> ApplicationsScreen(
                    viewModel = applicationsViewModel,
                    onJobClick = onJobClick
                )

                BottomNavItem.Profile -> ProfileScreenContent(
                    viewModel = loginViewModel,
                    onLogout = onLogout,
                    onAnalyticsClick = onAnalyticsClick
                )
            }
        }
    }
}