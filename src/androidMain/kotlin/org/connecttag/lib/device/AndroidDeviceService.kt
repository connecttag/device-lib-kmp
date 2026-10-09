package org.connecttag.lib.device

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.os.SystemClock
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.accessibility.AccessibilityManager
import com.scottyab.rootbeer.RootBeer
import java.io.File
import java.util.Locale
import java.util.TimeZone

/**
 * Android implementation of [PlatformDeviceService].
 */
open class AndroidDeviceService(
    private val context: Context,
    private val appInfo: AndroidAppInfo,
    private val clockProvider: ClockProvider = ClockProvider.Default,
    private val notificationPermissionStateProvider: () -> String = { "UNKNOWN" },
) : PlatformDeviceService {

    private val stableAppMetadata: AppMetadata by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        val installerPackageName = readInstallerPackageName()
        AppMetadata(
            versionName = appInfo.versionName,
            versionCode = appInfo.versionCode,
            packageName = appInfo.packageName,
            appFlavor = appInfo.appFlavor,
            buildType = appInfo.buildType,
            installerPackageName = installerPackageName,
            distributionChannel = distributionChannel(installerPackageName),
        )
    }

    private val stableDeviceMetadata: DeviceMetadata by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        val softwareId = readAndroidId()
        DeviceMetadata(
            platform = "android",
            deviceId = softwareId,
            softwareId = softwareId,
            manufacturer = Build.MANUFACTURER.orEmpty(),
            brand = Build.BRAND.orEmpty(),
            model = Build.MODEL.orEmpty(),
            device = Build.DEVICE.orEmpty(),
            product = Build.PRODUCT.orEmpty(),
            hardware = Build.HARDWARE.orEmpty(),
            board = Build.BOARD.orEmpty(),
            osName = "Android",
            osVersion = Build.VERSION.RELEASE.orEmpty(),
            apiLevel = Build.VERSION.SDK_INT,
            locale = "",
            timeZone = "",
            supportedAbis = Build.SUPPORTED_ABIS.orEmpty().toList(),
            totalMemoryBytes = getTotalMemory(),
            screenDensityDpi = context.resources.displayMetrics.densityDpi,
            cpuAbi = Build.SUPPORTED_ABIS.firstOrNull().orEmpty(),
            isRooted = isDeviceRooted(),
            isEmulator = isRunningOnEmulator(),
            securityPatch = getSecurityPatch(),
            buildTags = Build.TAGS.orEmpty(),
            kernelVersion = System.getProperty("os.version").orEmpty(),
            playServicesVersion = getPlayServicesVersion(),
        )
    }

    override fun getAppMetadata(): AppMetadata = stableAppMetadata

    override fun getDeviceMetadata(overrideDeviceId: String?): DeviceMetadata {
        val displayMetrics = context.resources.displayMetrics
        val stable = stableDeviceMetadata

        return stable.copy(
            deviceId = overrideDeviceId ?: stable.softwareId,
            locale = Locale.getDefault().toLanguageTag(),
            timeZone = TimeZone.getDefault().id.orEmpty(),
            countryCode = Locale.getDefault().country.orEmpty().uppercase(Locale.ROOT),
            notificationPermissionState = notificationPermissionStateProvider(),
            screenWidthPx = displayMetrics.widthPixels,
            screenHeightPx = displayMetrics.heightPixels,
            isAdbEnabled = isAdbEnabled(),
            isDeveloperModeEnabled = isDeveloperModeEnabled(),
            batteryLevel = getBatteryLevel(),
            isCharging = isDeviceCharging(),
            isPowerSaveMode = isPowerSaveMode(),
            isDataSaverEnabled = isDataSaverEnabled(),
            uptimeMillis = SystemClock.elapsedRealtime(),
            isDarkMode = isDarkMode(),
            freeStoragePercentage = getFreeStoragePercentage(),
            availableRamBytes = getAvailableRam(),
            isLowRam = isLowRam(),
            fontScale = context.resources.configuration.fontScale,
            isMultiWindowActive = isMultiWindowActive(),
            isReducedMotionEnabled = isReducedMotionEnabled(),
            appStandbyBucket = getAppStandbyBucket(),
            orientation = if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) "landscape" else "portrait",
            isAccessibilityActive = isAccessibilityActive(),
            simState = getSimState(),
            chargeType = getChargeType(),
            timezoneOffsetMinutes = TimeZone.getDefault().getOffset(clockProvider.nowMillis()) / 60000,
        )
    }

    private fun isAccessibilityActive(): Boolean {
        return try {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            am.isEnabled
        } catch (_: Exception) {
            false
        }
    }

    private fun getSimState(): String {
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (tm.simState) {
                TelephonyManager.SIM_STATE_ABSENT -> "ABSENT"
                TelephonyManager.SIM_STATE_READY -> "READY"
                TelephonyManager.SIM_STATE_PIN_REQUIRED -> "PIN_REQUIRED"
                TelephonyManager.SIM_STATE_PUK_REQUIRED -> "PUK_REQUIRED"
                TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "NETWORK_LOCKED"
                TelephonyManager.SIM_STATE_UNKNOWN -> "UNKNOWN"
                else -> "OTHER"
            }
        } catch (_: Exception) {
            "UNKNOWN"
        }
    }

    private fun getChargeType(): String {
        return try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            when (intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                BatteryManager.BATTERY_PLUGGED_AC -> "AC"
                BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "WIRELESS"
                else -> "NONE"
            }
        } catch (_: Exception) {
            "NONE"
        }
    }

    private fun getSecurityPatch(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH.orEmpty()
        } else {
            ""
        }
    }

    private fun getAppStandbyBucket(): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                when (usageStatsManager.appStandbyBucket) {
                    UsageStatsManager.STANDBY_BUCKET_ACTIVE -> "ACTIVE"
                    UsageStatsManager.STANDBY_BUCKET_WORKING_SET -> "WORKING_SET"
                    UsageStatsManager.STANDBY_BUCKET_FREQUENT -> "FREQUENT"
                    UsageStatsManager.STANDBY_BUCKET_RARE -> "RARE"
                    UsageStatsManager.STANDBY_BUCKET_RESTRICTED -> "RESTRICTED"
                    else -> "UNKNOWN"
                }
            } else {
                "NOT_APPLICABLE"
            }
        } catch (_: Exception) {
            "UNKNOWN"
        }
    }

    private fun getPlayServicesVersion(): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo("com.google.android.gms", 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (_: Exception) {
            0L
        }
    }

    private fun getAvailableRam(): Long {
        return try {
            val mi = ActivityManager.MemoryInfo()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(mi)
            mi.availMem
        } catch (_: Exception) {
            0L
        }
    }

    private fun isLowRam(): Boolean {
        return try {
            val mi = ActivityManager.MemoryInfo()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(mi)
            mi.lowMemory
        } catch (_: Exception) {
            false
        }
    }

    private fun isMultiWindowActive(): Boolean {
        return false
    }

    private fun isReducedMotionEnabled(): Boolean {
        return try {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.TRANSITION_ANIMATION_SCALE,
                1.0f
            ) == 0.0f
        } catch (_: Exception) {
            false
        }
    }

    private fun getBatteryLevel(): Int {
        return try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level == -1 || scale == -1) -1 else (level * 100 / scale.toFloat()).toInt()
        } catch (_: Exception) {
            -1
        }
    }

    private fun isDeviceCharging(): Boolean {
        return try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        } catch (_: Exception) {
            false
        }
    }

    private fun isPowerSaveMode(): Boolean {
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isPowerSaveMode
        } catch (_: Exception) {
            false
        }
    }

    private fun isDataSaverEnabled(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                connectivityManager.restrictBackgroundStatus ==
                    ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun isDarkMode(): Boolean {
        val uiMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun getFreeStoragePercentage(): Int {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val available = stat.availableBlocksLong
            val total = stat.blockCountLong
            if (total == 0L) -1 else (available * 100 / total).toInt()
        } catch (_: Exception) {
            -1
        }
    }

    private fun isDeviceRooted(): Boolean {
        return try {
            RootBeer(context).isRooted
        } catch (_: Throwable) {
            isDeviceRootedFallback()
        }
    }

    private fun isDeviceRootedFallback(): Boolean {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun isRunningOnEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.PRODUCT.contains("sdk_google")
            || Build.PRODUCT.contains("google_sdk")
            || Build.PRODUCT.contains("sdk")
            || Build.PRODUCT.contains("sdk_x86")
            || Build.PRODUCT.contains("vbox86p")
            || Build.PRODUCT.contains("emulator")
            || Build.PRODUCT.contains("simulator")
    }

    private fun isAdbEnabled(): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) != 0
        } catch (_: Exception) {
            false
        }
    }

    private fun isDeveloperModeEnabled(): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) != 0
        } catch (_: Exception) {
            false
        }
    }

    private fun getTotalMemory(): Long {
        return try {
            val mi = ActivityManager.MemoryInfo()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(mi)
            mi.totalMem
        } catch (_: Exception) {
            0L
        }
    }

    private fun readAndroidId(): String {
        return try {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID,
            ).orEmpty()
        } catch (_: Exception) {
            "unknown"
        }
    }

    @Suppress("DEPRECATION")
    private fun readInstallerPackageName(): String? {
        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.packageManager
                    .getInstallSourceInfo(appInfo.packageName)
                    .installingPackageName
            } else {
                context.packageManager.getInstallerPackageName(appInfo.packageName)
            }
        }.getOrNull()?.trim()?.takeIf(String::isNotEmpty)
    }

    private fun distributionChannel(installerPackageName: String?): String {
        return when (installerPackageName) {
            "com.android.vending" -> "google_play"
            "com.sec.android.app.samsungapps" -> "galaxy_store"
            "com.amazon.venezia" -> "amazon_appstore"
            null -> "unknown"
            else -> "other"
        }
    }
}
