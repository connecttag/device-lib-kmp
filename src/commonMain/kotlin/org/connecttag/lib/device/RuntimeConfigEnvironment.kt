package org.connecttag.lib.device

import kotlin.time.Instant

data class RuntimeConfigEnvironment(
    val appId: String,
    val versionCode: Int,
    val debug: Boolean,
    val language: String? = null,
    val country: String? = null,
    val now: Instant = Instant.fromEpochMilliseconds(0L),
    /** Stable, app-scoped installation discriminator used only for local rollout bucketing. */
    val installationId: String? = null,
) {
    companion object {
        val Fallback = RuntimeConfigEnvironment(
            appId = "unknown",
            versionCode = Int.MAX_VALUE,
            debug = false,
            now = Instant.fromEpochMilliseconds(0L),
        )
    }
}

interface RuntimeConfigEnvironmentProvider {
    suspend fun snapshot(): RuntimeConfigEnvironment
    fun capabilities(): AppBuildCapabilities?
}

fun interface RuntimeInstallationIdProvider {
    suspend fun installationId(): String
}
