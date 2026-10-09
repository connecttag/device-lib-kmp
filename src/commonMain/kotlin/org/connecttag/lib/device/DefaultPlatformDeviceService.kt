package org.connecttag.lib.device

/**
 * Default portable implementation of [PlatformDeviceService] for generic platforms and testing.
 */
open class DefaultPlatformDeviceService(
    private val appMetadata: AppMetadata = AppMetadata(
        versionName = "1.0.0",
        versionCode = 1L,
        packageName = "org.connecttag.app",
    ),
    private val defaultDeviceId: String = "kmp-device-001",
    private val platformName: String = "generic",
) : PlatformDeviceService {

    override fun getDeviceMetadata(overrideDeviceId: String?): DeviceMetadata {
        val id = overrideDeviceId ?: defaultDeviceId
        return DeviceMetadata(
            platform = platformName,
            deviceId = id,
            softwareId = id,
            manufacturer = "Generic",
            brand = "Generic",
            model = "KMP Device",
            device = "kmp",
            product = "kmp-platform",
            hardware = "unknown",
            board = "unknown",
            osName = platformName,
            osVersion = "1.0",
            apiLevel = 1,
            locale = "en-US",
            timeZone = "UTC",
        )
    }

    override fun getAppMetadata(): AppMetadata = appMetadata
}
