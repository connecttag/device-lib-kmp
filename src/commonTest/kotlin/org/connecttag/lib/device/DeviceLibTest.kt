package org.connecttag.lib.device

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DeviceLibTest {

    private val json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
    }

    @Test
    fun testAppMetadataSerialization() {
        val app = AppMetadata(
            versionName = "2.1.0",
            versionCode = 42L,
            packageName = "com.connecttag.app",
            appFlavor = "production",
            buildType = "release",
            installerPackageName = "com.android.vending",
            distributionChannel = "google_play",
        )

        val encoded = json.encodeToString(app)
        val decoded = json.decodeFromString<AppMetadata>(encoded)

        assertEquals("2.1.0", decoded.versionName)
        assertEquals(42L, decoded.versionCode)
        assertEquals("com.connecttag.app", decoded.packageName)
        assertEquals("google_play", decoded.distributionChannel)
    }

    @Test
    fun testDeviceMetadataSerialization() {
        val device = DeviceMetadata(
            platform = "android",
            deviceId = "test-device-uuid-1234",
            softwareId = "test-software-id",
            manufacturer = "Samsung",
            brand = "Galaxy",
            model = "S24 Ultra",
            device = "e3q",
            product = "e3qx",
            hardware = "qcom",
            board = "kalama",
            osName = "Android",
            osVersion = "14",
            apiLevel = 34,
            locale = "ar-YE",
            timeZone = "Asia/Aden",
            isRooted = false,
            batteryLevel = 88,
            isCharging = true,
        )

        val encoded = json.encodeToString(device)
        val decoded = json.decodeFromString<DeviceMetadata>(encoded)

        assertEquals("android", decoded.platform)
        assertEquals("Samsung", decoded.manufacturer)
        assertEquals("S24 Ultra", decoded.model)
        assertEquals(34, decoded.apiLevel)
        assertEquals(88, decoded.batteryLevel)
        assertTrue(decoded.isCharging)
        assertFalse(decoded.isRooted)
    }

    @Test
    fun testDeviceRuntimeSnapshot() {
        val service = DefaultPlatformDeviceService()
        val snapshot = service.getSnapshot("custom-id-999")

        assertEquals("custom-id-999", snapshot.device.deviceId)
        assertEquals("1.0.0", snapshot.app.versionName)
        assertEquals("org.connecttag.app", snapshot.app.packageName)

        val encoded = json.encodeToString(snapshot)
        val decoded = json.decodeFromString<DeviceRuntimeSnapshot>(encoded)
        assertEquals(snapshot, decoded)
    }

    @Test
    fun testRuntimeConfigEnvironmentFallback() {
        val fallback = RuntimeConfigEnvironment.Fallback
        assertEquals("unknown", fallback.appId)
        assertEquals(Int.MAX_VALUE, fallback.versionCode)
        assertFalse(fallback.debug)
        assertEquals(0L, fallback.now.toEpochMilliseconds())
    }

    @Test
    fun testClockProviderDefault() {
        val clock = ClockProvider.Default
        val now = clock.nowMillis()
        assertTrue(now > 0L)
    }

    @Test
    fun testAppBuildCapabilitiesDefaults() {
        val caps = object : AppBuildCapabilities {}
        assertFalse(caps.debugAccountAccessBypassEnabled)
        assertTrue(caps.accountFeaturesEnabled)
        assertTrue(caps.locationEnabled)
        assertEquals("", caps.apiBaseUrl)
    }
}
