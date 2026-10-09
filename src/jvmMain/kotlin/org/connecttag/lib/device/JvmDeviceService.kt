package org.connecttag.lib.device

import java.util.Locale
import java.util.TimeZone

/**
 * JVM desktop/server implementation of [PlatformDeviceService].
 */
open class JvmDeviceService(
    private val appMetadata: AppMetadata = AppMetadata(
        versionName = "1.0.0",
        versionCode = 1L,
        packageName = "org.connecttag.jvm",
    ),
    private val defaultDeviceId: String = "jvm-desktop-001",
) : PlatformDeviceService {

    override fun getDeviceMetadata(overrideDeviceId: String?): DeviceMetadata {
        val runtime = Runtime.getRuntime()
        val totalMem = runtime.totalMemory()
        val freeMem = runtime.freeMemory()
        val osName = System.getProperty("os.name").orEmpty()
        val osVersion = System.getProperty("os.version").orEmpty()
        val osArch = System.getProperty("os.arch").orEmpty()
        val id = overrideDeviceId ?: defaultDeviceId

        return DeviceMetadata(
            platform = "jvm",
            deviceId = id,
            softwareId = id,
            manufacturer = System.getProperty("java.vendor").orEmpty(),
            brand = "JVM",
            model = osArch,
            device = "desktop",
            product = "jvm-runtime",
            hardware = osArch,
            board = osName,
            osName = osName,
            osVersion = osVersion,
            apiLevel = parseJavaVersion(),
            locale = Locale.getDefault().toLanguageTag(),
            timeZone = TimeZone.getDefault().id.orEmpty(),
            countryCode = Locale.getDefault().country.orEmpty().uppercase(Locale.ROOT),
            totalMemoryBytes = totalMem,
            availableRamBytes = freeMem,
            cpuAbi = osArch,
            isRooted = false,
            isEmulator = false,
        )
    }

    override fun getAppMetadata(): AppMetadata = appMetadata

    private fun parseJavaVersion(): Int {
        val version = System.getProperty("java.version").orEmpty()
        return runCatching {
            val parts = version.split(".")
            if (parts[0] == "1") parts.getOrNull(1)?.toInt() ?: 8
            else parts[0].takeWhile { it.isDigit() }.toInt()
        }.getOrDefault(17)
    }
}
