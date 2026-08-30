package com.pawan.hirejetpack

import android.app.Application
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

class HireJetpackApp : Application() {
    
    override fun onCreate() {
        super.onCreate()

        // 1. Instantiate a FlutterEngine.
        val flutterEngine = FlutterEngine(this)

        // 2. Start executing Dart code to pre-warm the engine.
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        // 3. Cache the pre-warmed FlutterEngine
        FlutterEngineCache
            .getInstance()
            .put("analytics_engine_id", flutterEngine)

        // 4. Setup MethodChannel for Analytics Data
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "com.pawan.hirejetpack/analytics")
            .setMethodCallHandler { call, result ->
                if (call.method == "getAnalyticsData") {
                    val data = mapOf(
                        "jobCount" to 1240,
                        "topCategory" to "Mobile Development (Kotlin/Flutter)"
                    )
                    result.success(data)
                } else {
                    result.notImplemented()
                }
            }
    }
}
