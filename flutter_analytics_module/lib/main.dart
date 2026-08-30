import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(const JobAnalyticsApp());

class JobAnalyticsApp extends StatelessWidget {
  const JobAnalyticsApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        colorSchemeSeed: Colors.deepPurple,
      ),
      home: const AnalyticsDashboard(),
    );
  }
}

class AnalyticsDashboard extends StatefulWidget {
  const AnalyticsDashboard({super.key});

  @override
  State<AnalyticsDashboard> createState() => _AnalyticsDashboardState();
}

class _AnalyticsDashboardState extends State<AnalyticsDashboard> {
  static const platform = MethodChannel('com.pawan.hirejetpack/analytics');
  
  String _jobCount = "0";
  String _topCategory = "Loading...";

  @override
  void initState() {
    super.initState();
    _fetchNativeData();
  }

  Future<void> _fetchNativeData() async {
    try {
      final Map<dynamic, dynamic> result = await platform.invokeMethod('getAnalyticsData');
      setState(() {
        _jobCount = result['jobCount']?.toString() ?? "0";
        _topCategory = result['topCategory'] ?? "None";
      });
    } on PlatformException catch (e) {
      setState(() {
        _topCategory = "Failed to load data: '${e.message}'.";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Job Market Analytics', style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              "Market Overview",
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 24),
            _buildStatCard(
              "Total Active Jobs",
              _jobCount,
              Icons.work,
              Colors.blue,
            ),
            const SizedBox(height: 16),
            _buildStatCard(
              "Top Trending Category",
              _topCategory,
              Icons.trending_up,
              Colors.green,
            ),
            const SizedBox(height: 32),
            const Text(
              "Hiring Trends",
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),
            Expanded(
              child: Container(
                decoration: BoxDecoration(
                  color: Colors.grey[100],
                  borderRadius: BorderRadius.circular(16),
                ),
                child: const Center(
                  child: Text("Interactive Chart Placeholder"),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatCard(String title, String value, IconData icon, Color color) {
    return Card(
      elevation: 0,
      color: color.withOpacity(0.1),
      shape: RoundedCornerShape(16),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Row(
          children: [
            Icon(icon, size: 40, color: color),
            const SizedBox(width: 20),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title, style: const TextStyle(fontSize: 14, color: Colors.black54)),
                Text(value, style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: color)),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

// Extension for RoundedCornerShape equivalent in simple Dart
class RoundedCornerShape extends RoundedRectangleBorder {
  RoundedCornerShape(double radius) : super(borderRadius: BorderRadius.circular(radius));
}
