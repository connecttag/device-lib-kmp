# ConnectTag Device Library (KMP)

[![JitPack](https://jitpack.io/v/connecttag/device-lib-kmp.svg)](https://jitpack.io/#connecttag/device-lib-kmp)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.20-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/platform-Android%20%7C%20JVM%20%7C%20iOS-orange.svg)](https://kotlinlang.org/docs/multiplatform.html)

A lightweight, production-grade Kotlin Multiplatform library providing comprehensive hardware specifications, operating system details, app build information, battery & security detection, and runtime configuration environments across Android, JVM, and iOS.

## Features

- **Hardware & OS Metadata (`DeviceMetadata`)**: Memory, screen dimensions/density, ABI, security patch, OS release, and uptime.
- **Application Metadata (`AppMetadata`)**: Package name, version code, version name, flavor, installer source, and distribution channel.
- **Security & Environment**: Root detection, emulator heuristics, ADB status, and developer options.
- **Runtime Configuration (`RuntimeConfigEnvironment`)**: App environment snapshots, installation identifiers, and capabilities.
- **Zero Inter-Library Dependencies**: Fully self-contained models and contracts with native iOS (`iosArm64`, `iosSimulatorArm64`), Android, and JVM support.

## Installation

Add JitPack to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
    }
}
```

Add the dependency to your module's `build.gradle.kts`:

```kotlin
commonMain.dependencies {
    implementation("com.github.connecttag:device-lib-kmp:1.0.0")
}
```

## Usage

```kotlin
import org.connecttag.lib.device.PlatformDeviceService
import org.connecttag.lib.device.DefaultPlatformDeviceService

val deviceService: PlatformDeviceService = DefaultPlatformDeviceService()
val snapshot = deviceService.getSnapshot()

println("Platform: ${snapshot.device.platform}")
println("App Version: ${snapshot.app.versionName}")
```

## License

```
Copyright 2026 ConnectTag

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
