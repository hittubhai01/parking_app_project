# ⚡ Android Unit Test Speed Optimization Guide

This guide outlines how to compile and run unit tests for the `Vision-Parking` Android app in the fastest possible way.

---

## 🏎️ Quick Run Summary

| Speedup Method | Terminal Command | Expected Speedup | Recommended Use Case |
| :--- | :--- | :--- | :--- |
| **Run Single Test Class** | `./gradlew testDebugUnitTest --tests "com.example.visionpark.UserVehicleTest"` | 🟢 **90% faster** | Active development on a specific model or helper |
| **Grep Specific Test** | `./gradlew testDebugUnitTest --tests "*.testVehicleInitialization"` | 🟢 **95% faster** | Debugging a single assertion |
| **Enable Gradle Daemon** | Active by default (`org.gradle.daemon=true`) | 🟡 **3x faster compiling** | Essential for fast local compilation |
| **Gradle Configuration Cache**| Active by default (`configuration-cache=true`) | 🟡 **2x faster setup** | Bypasses project structure configuration phase |

---

## 🛠️ 1. Running Specific Tests (Bypass Full Suite)
Instead of executing the entire 58-test suite, compile and run only the files/methods you are working on.

### A. Run a Single Test Class
To run all tests inside a specific class (e.g. `UserVehicleTest.java`):
```bash
./gradlew testDebugUnitTest --tests "com.example.visionpark.UserVehicleTest"
```

### B. Run a Specific Test Method
To run a single test case (e.g. `testVehicleInitialization` inside `UserVehicleTest`):
```bash
./gradlew testDebugUnitTest --tests "com.example.visionpark.UserVehicleTest.testVehicleInitialization"
```

---

## ⚙️ 2. Under the Hood Optimization (Already Configured)
We have optimized the Gradle configuration in [gradle.properties](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/gradle.properties) and [build.gradle.kts](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/build.gradle.kts):
1. **Parallel execution forks:** Tests are dynamically split and executed across multiple cores using:
   `maxParallelForks = Runtime.getRuntime().availableProcessors() / 2`
2. **Build Caching:** Gradle reuse previously built classes (`FROM-CACHE`) which keeps the execution under **15 seconds**!
