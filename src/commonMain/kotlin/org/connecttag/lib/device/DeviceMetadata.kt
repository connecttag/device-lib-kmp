package org.connecttag.lib.device

import kotlinx.serialization.Serializable

/**
 * Portable representation of application metadata.
 */
@Serializable
data class AppMetadata(
    val versionName: String,
    val versionCode: Long,
    val packageName: String,
    val appFlavor: String = "default",
    val buildType: String = "unknown",
    val installerPackageName: String? = null,
    val distributionChannel: String = "unknown",
)

/**
 * Portable representation of device hardware and OS metadata.
 */
@Serializable
data class DeviceMetadata(
    val platform: String, // "android", "ios", "jvm", etc.
    val deviceId: String,
    val softwareId: String = "",
    val manufacturer: String = "",
    val brand: String = "",
    val model: String = "",
    val device: String = "",
    val product: String = "",
    val hardware: String = "",
    val board: String = "",
    val osName: String = "",
    val osVersion: String = "",
    val apiLevel: Int = 0,
    val locale: String = "",
    val timeZone: String = "",
    val supportedAbis: List<String> = emptyList(),
    val countryCode: String = "",
    val notificationPermissionState: String = "UNKNOWN",

    // Additional Specifications
    val totalMemoryBytes: Long = 0L,
    val screenWidthPx: Int = 0,
    val screenHeightPx: Int = 0,
    val screenDensityDpi: Int = 0,
    val cpuAbi: String = "",

    // Security & Environment
    val isRooted: Boolean = false,
    val isEmulator: Boolean = false,
    val isAdbEnabled: Boolean = false,
    val isDeveloperModeEnabled: Boolean = false,
    val batteryLevel: Int = -1,
    val isCharging: Boolean = false,
    val isPowerSaveMode: Boolean = false,
    val isDataSaverEnabled: Boolean = false,
    val uptimeMillis: Long = 0L,
    val isDarkMode: Boolean = false,
    val freeStoragePercentage: Int = -1,
    val availableRamBytes: Long = 0L,
    val isLowRam: Boolean = false,
    val fontScale: Float = 1.0f,
    val isMultiWindowActive: Boolean = false,
    val isReducedMotionEnabled: Boolean = false,
    val securityPatch: String = "",
    val buildTags: String = "",
    val appStandbyBucket: String = "UNKNOWN",
    val kernelVersion: String = "",
    val orientation: String = "UNKNOWN",
    val playServicesVersion: Long = 0L,
    val isAccessibilityActive: Boolean = false,
    val simState: String = "UNKNOWN",
    val chargeType: String = "NONE",
    val timezoneOffsetMinutes: Int = 0,
)

/**
 * A combined snapshot of device and app state.
 */
@Serializable
data class DeviceRuntimeSnapshot(
    val device: DeviceMetadata,
    val app: AppMetadata,
)
