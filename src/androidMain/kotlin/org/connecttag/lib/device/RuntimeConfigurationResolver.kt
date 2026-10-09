package org.connecttag.lib.device

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.View
import java.util.Locale

data class RuntimeConfigurationSnapshot(
    val languageTag: String,
    val languageCode: String,
    val isNightMode: Boolean,
    val isRtlLayout: Boolean,
    val isPortrait: Boolean,
)

object RuntimeConfigurationResolver {

    fun snapshot(context: Context): RuntimeConfigurationSnapshot {
        val locale = localeFor(context)
        return RuntimeConfigurationSnapshot(
            languageTag = locale.toLanguageTag(),
            languageCode = locale.language,
            isNightMode = isNightMode(context.resources.configuration),
            isRtlLayout = isRtlLayout(context.resources.configuration),
            isPortrait = isPortrait(context.resources.configuration),
        )
    }

    fun localeFor(context: Context): Locale = localeFor(context.resources.configuration)

    internal fun localeFor(
        configuration: Configuration,
        useLocaleList: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N,
    ): Locale = if (useLocaleList) {
        configuration.locales.get(0)
            ?: configuration.locales.getFirstMatch(arrayOf(Locale.getDefault().language))
            ?: Locale.getDefault()
    } else {
        @Suppress("DEPRECATION")
        configuration.locale ?: Locale.getDefault()
    }

    fun languageTag(context: Context): String = localeFor(context).toLanguageTag()

    internal fun languageTag(
        configuration: Configuration,
        useLocaleList: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N,
    ): String = localeFor(configuration, useLocaleList).toLanguageTag()

    fun languageCode(context: Context): String = languageCode(context.resources.configuration)

    internal fun languageCode(
        configuration: Configuration,
        useLocaleList: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N,
    ): String = localeFor(configuration, useLocaleList).language

    fun isNightMode(context: Context): Boolean {
        return isNightMode(context.resources.configuration)
    }

    internal fun isNightMode(configuration: Configuration): Boolean {
        return (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    fun isRtlLayout(context: Context): Boolean {
        return isRtlLayout(context.resources.configuration)
    }

    internal fun isRtlLayout(configuration: Configuration): Boolean {
        return configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
    }

    fun isPortrait(context: Context): Boolean {
        return isPortrait(context.resources.configuration)
    }

    internal fun isPortrait(configuration: Configuration): Boolean {
        return configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    fun copyConfiguration(context: Context): Configuration = Configuration(context.resources.configuration)

    fun applyLocale(configuration: Configuration, locale: Locale): Configuration {
        return configuration.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setLocales(android.os.LocaleList(locale))
            } else {
                @Suppress("DEPRECATION")
                this.locale = locale
            }
            setLayoutDirection(locale)
        }
    }

    fun localizedConfiguration(context: Context, locale: Locale): Configuration {
        return applyLocale(copyConfiguration(context), locale)
    }

    fun localizedContext(context: Context, locale: Locale): Context {
        return context.createConfigurationContext(localizedConfiguration(context, locale))
    }
}
