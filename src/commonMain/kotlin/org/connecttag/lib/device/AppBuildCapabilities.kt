package org.connecttag.lib.device

interface AppBuildCapabilities {
    val debugAccountAccessBypassEnabled: Boolean get() = false
    val accountFeaturesEnabled: Boolean get() = true
    val accountAuthEnabled: Boolean get() = true
    val accountProfileEnabled: Boolean get() = true
    val accountCartEnabled: Boolean get() = true
    val accountCheckoutEnabled: Boolean get() = true
    val accountOrdersEnabled: Boolean get() = true
    val accountRidesEnabled: Boolean get() = true
    val accountVerificationEnabled: Boolean get() = true
    val locationEnabled: Boolean get() = true
    val apiBaseUrl: String get() = ""
    val apiBackupBaseUrl: String? get() = null
    val apiKey: String get() = ""
    val sslPinningEnabled: Boolean get() = false
    val sslPinningHosts: List<String> get() = emptyList()
    val sslCertificatePins: List<String> get() = emptyList()
}
