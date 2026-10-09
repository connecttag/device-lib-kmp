package org.connecttag.lib.device

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import kotlin.time.Instant

open class AndroidRuntimeConfigEnvironmentProvider(
    private val context: Context,
    private val clock: ClockProvider = ClockProvider.Default,
    private val capabilities: AppBuildCapabilities = object : AppBuildCapabilities {},
    private val installationIdProvider: RuntimeInstallationIdProvider = RuntimeInstallationIdProvider { "" },
) : RuntimeConfigEnvironmentProvider {

    override suspend fun snapshot(): RuntimeConfigEnvironment {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }

        return RuntimeConfigEnvironment(
            appId = context.packageName,
            versionCode = getVersionCode(context),
            debug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
            language = locale?.language ?: "en",
            country = locale?.country?.takeIf(String::isNotBlank),
            installationId = installationIdProvider.installationId(),
            now = Instant.fromEpochMilliseconds(clock.nowMillis()),
        )
    }

    override fun capabilities(): AppBuildCapabilities = capabilities

    private fun getVersionCode(context: Context): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (_: Exception) {
            0
        }
    }
}
