package org.connecttag.lib.device

/**
 * iOS implementation of [PlatformDeviceService].
 */
open class IosDeviceService(
    private val appMetadata: AppMetadata = AppMetadata(
        versionName = "1.0.0",
        versionCode = 1L,
        packageName = "org.connecttag.ios",
    ),
    private val defaultDeviceId: String = "ios-device-001",
) : PlatformDeviceService {

    override fun getDeviceMetadata(overrideDeviceId: String?): DeviceMetadata {
        val id = overrideDeviceId ?: defaultDeviceId
        return DeviceMetadata(
            platform = "ios",
            deviceId = id,
            softwareId = id,
            manufacturer = "Apple",
            brand = "Apple",
            model = "iPhone",
            device = "iPhone",
            product = "iOS",
            hardware = "Apple Silicon",
            board = "Apple",
            osName = "iOS",
            osVersion = "17.0",
            apiLevel = 17,
            locale = "en-US",
            timeZone = "UTC",
            isRooted = false,
            isEmulator = false,
        )
    }

    override fun getAppMetadata(): AppMetadata = appMetadata
}
