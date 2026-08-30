package com.pawan.hirejetpack.presentation.ui.analytics

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import io.flutter.embedding.android.FlutterFragment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Market Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // We embed the FlutterFragment using AndroidView
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { context ->
                val activity = context as FragmentActivity
                val fragmentManager = activity.supportFragmentManager
                
                // Check if fragment already exists
                var flutterFragment = fragmentManager.findFragmentByTag("flutter_fragment") as? FlutterFragment
                
                if (flutterFragment == null) {
                    flutterFragment = FlutterFragment.withCachedEngine("analytics_engine_id")
                        .destroyEngineWithFragment(false) // We want to keep the engine warmed in Application
                        .build()
                    
                    fragmentManager.beginTransaction()
                        .add(android.R.id.content, flutterFragment, "flutter_fragment")
                        .commit()
                }
                
                // Return a dummy view because the fragment is added to android.R.id.content
                // In a real Compose + Fragment setup, you might use a FragmentContainerView
                android.view.View(context)
            }
        )
    }
}
