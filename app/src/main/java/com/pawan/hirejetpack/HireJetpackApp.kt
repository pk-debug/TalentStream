package com.pawan.hirejetpack

import android.app.Application
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

class HireJetpackApp : Application() {
    
    override fun onCreate() {
        super.onCreate()

        // 1. Instantiate a FlutterEngine.
        val flutterEngine = FlutterEngine(this)

        // 2. Start executing Dart code to pre-warm the engine.
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        // 3. Cache the pre-warmed FlutterEngine to be used by FlutterActivity or FlutterFragment.
        FlutterEngineCache
            .getInstance()
            .put("analytics_engine_id", flutterEngine)
    }
}
