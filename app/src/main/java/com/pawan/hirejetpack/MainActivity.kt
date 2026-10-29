package com.pawan.hirejetpack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.pawan.hirejetpack.data.ThemeRepository
import com.pawan.hirejetpack.presentation.navigation.AppNavigation
import com.pawan.hirejetpack.presentation.ui.theme.HirejetpackTheme

// ============================================================================
// MAIN ACTIVITY ENTRY POINT
// ============================================================================

/**
 * [MainActivity] — entry point of the UI.
 *
 * Cold start optimization:
 * - Uses [installSplashScreen] to handle window transition smoothly from the
 *   OS starting window into Compose with zero blank screen flicker.
 * - Extends [FragmentActivity] for Flutter add-to-app fragment interop.
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate()
        installSplashScreen()

        super.onCreate(savedInstanceState)

        /**
         * KEYWORD: [setContent]
         * Composable bridge function that sets the Activity's root view to a Compose tree
         * replacing traditional XML layout inflation (`setContentView`).
         */
        setContent {
            val isDarkMode by ThemeRepository.isDarkMode.collectAsState()
            
            HirejetpackTheme(darkTheme = isDarkMode) {
                AppNavigation()
            }
        }
    }
}









