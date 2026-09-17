package com.pawan.hirejetpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.pawan.hirejetpack.data.ThemeRepository
import com.pawan.hirejetpack.presentation.navigation.AppNavigation
import com.pawan.hirejetpack.presentation.ui.theme.HirejetpackTheme

// ============================================================================
// MAIN ACTIVITY ENTRY POINT
// ============================================================================

/**
 * KEYWORD: [ComponentActivity]
 * Base class for activities that use Jetpack Compose and modern Android architecture.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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









