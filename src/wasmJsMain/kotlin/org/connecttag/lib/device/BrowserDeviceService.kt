package org.connecttag.lib.device

import kotlin.js.JsFun

@JsFun("() => navigator.language || ''") private external fun browserLanguage(): String
@JsFun("() => Intl.DateTimeFormat().resolvedOptions().timeZone || ''") private external fun browserTimezone(): String
@JsFun("() => window.screen.width") private external fun browserWidth(): Int
@JsFun("() => window.screen.height") private external fun browserHeight(): Int
@JsFun("() => window.devicePixelRatio || 1") private external fun browserDensity(): Double
@JsFun("() => navigator.userAgent || ''") private external fun browserUserAgent(): String
@JsFun("() => crypto.randomUUID()") private external fun browserUuid(): String
@JsFun("(key) => localStorage.getItem(key) || ''") private external fun readId(key: String): String
@JsFun("(key,value) => localStorage.setItem(key,value)") private external fun saveId(key: String,value: String)

class BrowserDeviceService(private val appMetadata: AppMetadata) : PlatformDeviceService {
    private val deviceId: String by lazy {
        val key = "${appMetadata.packageName}.deviceId"
        readId(key).ifBlank { browserUuid().also { saveId(key,it) } }
    }
    override fun getAppMetadata(): AppMetadata = appMetadata
    override fun getDeviceMetadata(overrideDeviceId: String?): DeviceMetadata = DeviceMetadata(
        platform="web",deviceId=overrideDeviceId ?: deviceId,
        model=browserUserAgent(),locale=browserLanguage(),timeZone=browserTimezone(),
        screenWidthPx=browserWidth(),screenHeightPx=browserHeight(),
        screenDensityDpi=(browserDensity()*160).toInt()
    )
}
