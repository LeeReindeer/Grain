package moe.leer.grain

import android.util.Log
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import okhttp3.Cookie
import okhttp3.HttpUrl

/**
 *
 * Created by leer on 1/29/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class PersistentCookieJarWrapper(
    private val cookieCache: CookieCache,
    private val cookiePersistor: CookiePersistor
) : ClearableCookieJar {

    private val TAG = "CookieJarWrapper"

    private val cookieJar: ClearableCookieJar

    init {
        cookieJar = PersistentCookieJar(cookieCache, cookiePersistor)
    }

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        cookieCache.addAll(cookies)
        //save all cookie to simulate session login
        Log.d(TAG, "saveFromResponse: cookie size: " + cookies.size)
        cookiePersistor.saveAll(cookies)
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        return cookieJar.loadForRequest(url)
    }

    override fun clearSession() {
        cookieJar.clearSession()
    }

    override fun clear() {
        cookieJar.clear()
    }
}