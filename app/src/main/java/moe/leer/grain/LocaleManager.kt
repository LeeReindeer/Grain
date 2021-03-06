package moe.leer.grain

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import java.util.*


/**
 *
 * Created by leer on 1/24/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class LocaleManager(private val context: Context) {

    private val TAG = "LocaleManage"

    companion object {
        const val EN_LANG = "en"
        const val ZH_LANG = "zh"
        const val LANGUAGE_KEY = "key_language"
    }

    fun updateLocale(context: Context): Context {
        val lang = getLanguage()
        Log.d(TAG, "getLanguage: ${lang}")
        return updateLocale(context, lang)
    }

    fun getLanguage(): String {
        return when (context.getSP(Constant.SP_SETTING_NAME).getString(LANGUAGE_KEY, EN_LANG)) {
            "0" -> EN_LANG
            "1" -> ZH_LANG
            else -> EN_LANG
        }
    }


    /**
     * Update locale and return new context of that
     */
    fun updateLocale(context: Context, language: String): Context {
        var newContext = context
        val locale = Locale(language)
        Locale.setDefault(locale)

        val res = newContext.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        newContext = newContext.createConfigurationContext(config)
        return newContext
    }

}