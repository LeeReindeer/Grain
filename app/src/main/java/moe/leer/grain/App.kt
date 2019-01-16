package moe.leer.grain

import android.app.Application
import android.content.Context

/**
 *
 * Created by leer on 1/15/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class App : Application() {
    private val TAG = "App"

    var isLogin
        get() = this.getSharedPreferences(USER_SP, Context.MODE_PRIVATE).getBoolean("isLogin", false)
        set(value) = this.getSharedPreferences(USER_SP, Context.MODE_PRIVATE)
            .edit()
            .putBoolean("isLogin", value)
            .apply()

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        const val USER_SP = "Repository"

        fun getApplication(context: Context): App = context as App
    }
}