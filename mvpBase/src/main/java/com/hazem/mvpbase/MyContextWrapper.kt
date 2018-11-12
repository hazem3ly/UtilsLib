package com.hazem.mvpbase

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Build

import java.util.Locale
import android.os.LocaleList


class MyContextWrapper(base: Context?) : ContextWrapper(base) {
    companion object {

        val PREFS_NAME = "utils_lib_data"
        val PREF_LANGUAGE = "language"

        fun wrap(context: Context?, language: String): ContextWrapper? {
            context?.let {
                var newcontext = context

                val config = newcontext.resources.configuration

                val sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    getSystemLocale(config)
                } else {
                    getSystemLocaleLegacy(config)
                }
                if (language != "" && sysLocale.language != language) {
                    saveLanguage(context, language)
                    val locale = Locale(language)
                    Locale.setDefault(locale)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        setSystemLocale(config, locale)
                        val localeList = LocaleList(locale)
                        LocaleList.setDefault(localeList)
                        config.locales = localeList
                    } else {
                        setSystemLocale(config, locale)
                    }
                }
                newcontext = newcontext.createConfigurationContext(config)
                return MyContextWrapper(newcontext)
            }
            return MyContextWrapper(context)
        }

        fun getSystemLocaleLegacy(config: Configuration): Locale {
            return config.locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun getSystemLocale(config: Configuration): Locale {
            return config.locales.get(0)
        }

        fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
            config.locale = locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun setSystemLocale(config: Configuration, locale: Locale) {
            config.setLocale(locale)
        }


        fun saveLanguage(context: Context, languageCode: String) {
            val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            editor.putString(PREF_LANGUAGE, languageCode)
            editor.apply()
        }

        fun getCurrentLanguage(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lang = prefs.getString(PREF_LANGUAGE, "en") ?: "en"
            if (lang.isEmpty()) return "en"
            return lang
        }

        fun restartApplication(context: Context) {
            val i = context.packageManager.getLaunchIntentForPackage(context.packageName)
            i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(i)
        }

        fun changeAppLanguageAndRestart(context: Context, language: String) {
            saveLanguage(context, language)
            restartApplication(context)
        }

    }
}
