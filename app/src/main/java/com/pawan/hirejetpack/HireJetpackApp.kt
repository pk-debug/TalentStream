package com.pawan.hirejetpack

import android.app.Application
import android.os.Looper
import com.pawan.hirejetpack.di.AppContainer
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

/**
 * [HireJetpackApp] — custom [Application] subclass; the root of manual
 * dependency injection for this app.
 */
class HireJetpackApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
        prewarmFlutterEngineWhenIdle()
    }

    /**
     * Cold start optimization:
     * Pre-warming the [FlutterEngine] is deferred using [android.os.MessageQueue.IdleHandler].
     * This guarantees that FlutterEngine creation and Dart execution never block the UI thread
     * during the critical startup path (Application & Activity launch, first frame rendering).
     *
     * It only executes once the Main Looper queue is completely idle after the initial frame
     * is already on screen, keeping cold start time as fast as possible while still having
     * the engine warm and cached before the user navigates to Analytics.
     */
    private fun prewarmFlutterEngineWhenIdle() {
        Looper.myQueue().addIdleHandler {
            ensureFlutterEngineInitialized(this)
            false // Run once and remove from idle queue
        }
    }

    companion object {
        const val FLUTTER_ENGINE_ID = "analytics_engine_id"
        private const val CHANNEL_NAME = "com.pawan.hirejetpack/analytics"

        /**
         * Ensures the FlutterEngine is created and cached. Safe to call from
         * [AnalyticsScreen] as a fallback or from the idle handler.
         */
        fun ensureFlutterEngineInitialized(application: Application): FlutterEngine {
            val cache = FlutterEngineCache.getInstance()
            cache.get(FLUTTER_ENGINE_ID)?.let { return it }

            val flutterEngine = FlutterEngine(application)
            flutterEngine.dartExecutor.executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )

            cache.put(FLUTTER_ENGINE_ID, flutterEngine)

            MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NAME)
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

            return flutterEngine
        }
    }
}
