package org.connecttag.lib.device

/**
 * Interface to provide the current time in milliseconds since epoch.
 */
fun interface ClockProvider {
    fun nowMillis(): Long

    companion object {
        val Default: ClockProvider = ClockProvider {
            kotlin.time.Clock.System.now().toEpochMilliseconds()
        }
    }
}
