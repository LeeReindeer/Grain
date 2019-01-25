package moe.leer.grain

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import moe.leer.grain.Constant.SP_NAME

/**
 *
 * Created by leer on 1/15/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class App : Application() {

    private val TAG = "App"
    lateinit var localeManager: LocaleManager

    var isLogin
        get() = this.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean("isLogin", false)
        set(value) = this.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean("isLogin", value)
            .apply()

    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context) {
        localeManager = LocaleManager(base)
        super.attachBaseContext(localeManager.updateLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        localeManager.updateLocale(this)
        Log.d(TAG, "onConfigurationChanged: ${newConfig?.locale?.language}")
    }

    companion object {
        fun getApplication(context: Context): App = context as App
    }
}