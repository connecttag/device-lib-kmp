package org.connecttag.lib.device

/**
 * Android-specific app information configuration.
 */
data class AndroidAppInfo(
    val versionName: String,
    val versionCode: Long,
    val packageName: String,
    val appFlavor: String = "default",
    val buildType: String = "unknown",
)
