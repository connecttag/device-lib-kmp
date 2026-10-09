package org.connecttag.lib.device

/**
 * Interface for retrieving device and app metadata across platforms.
 */
interface PlatformDeviceService {
    /**
     * Returns a snapshot of the current device hardware and OS metadata.
     *
     * @param overrideDeviceId Optional ID to override the hardware-detected device ID.
     */
    fun getDeviceMetadata(overrideDeviceId: String? = null): DeviceMetadata

    /**
     * Returns metadata about the current application build.
     */
    fun getAppMetadata(): AppMetadata

    /**
     * Returns a combined snapshot of device and app metadata.
     */
    fun getSnapshot(overrideDeviceId: String? = null): DeviceRuntimeSnapshot {
        return DeviceRuntimeSnapshot(
            device = getDeviceMetadata(overrideDeviceId),
            app = getAppMetadata()
        )
    }
}
