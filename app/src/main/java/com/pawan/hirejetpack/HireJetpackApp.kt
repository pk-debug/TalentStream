package com.pawan.hirejetpack

import android.app.Application
import com.pawan.hirejetpack.di.AppContainer

/**
 * [HireJetpackApp] — custom [Application] subclass; the root of manual
 * dependency injection for this app.
 *
 * Staff note: [appContainer] is created exactly ONCE, in [onCreate],
 * which runs when the app's process starts — and it lives exactly as
 * long as that process does. That's the correct lifetime for a database
 * connection: too short-lived (e.g. per-Activity) and you'd reopen the
 * database repeatedly for no reason; too long doesn't even apply here,
 * since the process itself is the ceiling. Getting dependency LIFETIMES
 * right — not just "provide the thing somewhere" — is most of what DI
 * frameworks are actually solving, more than the wiring syntax.
 *
 * IMPORTANT — this class must be registered in AndroidManifest.xml:
 * ```xml
 * <application
 *     android:name=".HireJetpackApp"
 *     ... >
 * ```
 * Without that line, `context.applicationContext as HireJetpackApp`
 * (used wherever a ViewModel factory is built) will throw a
 * ClassCastException at runtime — Android still uses the default
 * `Application` class unless told otherwise.
 */
class HireJetpackApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}//package com.pawan.hirejetpack
//
//import android.app.Application
//import io.flutter.embedding.engine.FlutterEngine
//import io.flutter.embedding.engine.FlutterEngineCache
//import io.flutter.embedding.engine.dart.DartExecutor
//import io.flutter.plugin.common.MethodChannel
//
//class HireJetpackApp : Application() {
//
//    override fun onCreate() {
//        super.onCreate()
//
//        // 1. Instantiate a FlutterEngine.
//        val flutterEngine = FlutterEngine(this)
//
//        // 2. Start executing Dart code to pre-warm the engine.
//        flutterEngine.dartExecutor.executeDartEntrypoint(
//            DartExecutor.DartEntrypoint.createDefault()
//        )
//
//        // 3. Cache the pre-warmed FlutterEngine
//        FlutterEngineCache
//            .getInstance()
//            .put("analytics_engine_id", flutterEngine)
//
//        // 4. Setup MethodChannel for Analytics Data
//        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "com.pawan.hirejetpack/analytics")
//            .setMethodCallHandler { call, result ->
//                if (call.method == "getAnalyticsData") {
//                    val data = mapOf(
//                        "jobCount" to 1240,
//                        "topCategory" to "Mobile Development (Kotlin/Flutter)"
//                    )
//                    result.success(data)
//                } else {
//                    result.notImplemented()
//                }
//            }
//    }
//}
