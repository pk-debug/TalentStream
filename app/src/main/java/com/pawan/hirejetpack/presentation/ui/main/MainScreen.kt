package com.pawan.hirejetpack.presentation.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pawan.hirejetpack.presentation.rememberAppViewModelFactory
import com.pawan.hirejetpack.presentation.state.ApplicationsViewModel
import com.pawan.hirejetpack.presentation.state.HomeViewModel
import com.pawan.hirejetpack.presentation.state.LoginUiState
import com.pawan.hirejetpack.presentation.state.LoginViewModel
import com.pawan.hirejetpack.presentation.state.SavedJobsViewModel
import com.pawan.hirejetpack.presentation.ui.applications.ApplicationsScreen
import com.pawan.hirejetpack.presentation.ui.home.AppDrawerContent
import com.pawan.hirejetpack.presentation.ui.home.HomeScreenContent
import com.pawan.hirejetpack.presentation.ui.profile.ProfileScreenContent
import com.pawan.hirejetpack.presentation.ui.savedjobs.SavedJobsScreen
import kotlinx.coroutines.launch

/**
 * [MainScreen] — top-level shell combining:
 * - Slide-out [ModalNavigationDrawer] (Home, Saved Jobs, Applications, Profile, Logout)
 * - Material 3 [Scaffold] with [TopAppBar] (including drawer trigger)
 * - Persistent [NavigationBar] bottom tabs
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    loginViewModel: LoginViewModel,
    onJobClick: (String) -> Unit,
    onLogout: () -> Unit,
    onAnalyticsClick: () -> Unit
) {
    val factory = rememberAppViewModelFactory()
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val savedJobsViewModel: SavedJobsViewModel = viewModel(factory = factory)
    val applicationsViewModel: ApplicationsViewModel = viewModel(factory = factory)

    var selectedTab by remember { mutableStateOf(BottomNavItem.Home) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val loginState by loginViewModel.uiState.collectAsState()
    val currentUser = (loginState as? LoginUiState.Success)?.user

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                user = currentUser,
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    scope.launch { drawerState.close() }
                },
                onLogoutClick = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedTab.title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Open navigation drawer")
                        }
                    }
                )
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
}
