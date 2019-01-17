package moe.leer.grain

import android.content.Context
import android.util.Log
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moe.leer.grain.model.Transcript
import moe.leer.grain.model.TranscriptResponse
import moe.leer.grain.model.User
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import java.util.concurrent.TimeUnit

//@SuppressLint("StaticFieldLeak")
/**
 *
 * Created by leer on 1/14/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class FuckSchoolApi private constructor(val context: Context) {

    private val TAG = "FuckSchoolApi"

    private val httpClient: OkHttpClient


    fun getHttpClient() = httpClient


    companion object {
        private var INSTANCE: FuckSchoolApi? = null
        val USER_AGENT =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1"

        @Synchronized
        fun getInstance(context: Context): FuckSchoolApi {
            if (INSTANCE == null) {
                INSTANCE = FuckSchoolApi(context)
            }
            return INSTANCE!!
        }

        const val LOGIN_NET_ERROR = -1
        const val LOGIN_ID_PASS_ERROR = 0
        const val LOGIN_SUCCESS = 1
        const val LOGIN_PROCESS = 2
    }

    init {
        val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
        httpClient = OkHttpClient().newBuilder()
            .cookieJar(cookieJar)
            .callTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    private val executionRequest = Request.Builder()
        .url("https://my.zjou.edu.cn/cas/login?service=http%3A%2F%2Fportal.zjou.edu.cn%2Findex.portal")
        .header("User-Agent", USER_AGENT)
        .get()
        .build()

    private fun getExecution(): String {
        var executionStr = ""
        if (!context.isNetworkAvailable()) {
            return executionStr
        }
        httpClient.newCall(executionRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                return
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    return
                }
                val respString = response.body()!!.string()
                val dom = Jsoup.parse(respString)
                val selected = dom.select("input[type=hidden]")
                val executionNode = selected[1]
                executionStr = executionNode.attr("value")
                Log.d(TAG, "onResponse: execution: $executionStr")
            }
        })

        // fixme maybe stick in here
        while (executionStr.isEmpty());

        return executionStr
    }

    fun loginAsync(user: User, callback: Callback) {
        val execution = getExecution()
        if (execution.isEmpty()) {
            Log.e(TAG, "getExecution failed");
            return
        }

        val formBody = FormBody.Builder()
            .add("authType", "0")
            .add("username", user.id.toString())
            .add("password", user.password)
            .add("lt", "")
            .add("execution", execution)
            .add("_eventId", "submit")
            .add("randomStr", "")
            .build()
        val loginRequest = Request.Builder()
            .url("https://my.zjou.edu.cn/cas/login?service=http%3A%2F%2Fportal.zjou.edu.cn%2Findex.portal")
            .header("User-Agent", USER_AGENT)
            .post(formBody)
            .build()
        httpClient.newCall(loginRequest).enqueue(callback)
    }


    /**
     * @param user user to login with is and password
     * @return return observable of login status code
     * @see LOGIN_NET_ERROR -1 -> network error
     * @see LOGIN_ID_PASS_ERROR 0 -> password error
     * @see LOGIN_SUCCESS 1 -> success
     */
    fun login(user: User): Observable<Int> {
        val execution = getExecution()
        if (execution.isEmpty()) {
            Log.e(TAG, "getExecution failed");
            return Observable.fromCallable { LOGIN_NET_ERROR }
        }

        val formBody = FormBody.Builder()
            .add("authType", "0")
            .add("username", user.id.toString())
            .add("password", user.password)
            .add("lt", "")
            .add("execution", execution)
            .add("_eventId", "submit")
            .add("randomStr", "")
            .build()
        val loginRequest = Request.Builder()
            .url("https://my.zjou.edu.cn/cas/login?service=http%3A%2F%2Fportal.zjou.edu.cn%2Findex.portal")
            .header("User-Agent", USER_AGENT)
            .post(formBody)
            .build()

        return Observable.fromCallable {
            val response = httpClient.newCall(loginRequest).execute()
            Log.d(TAG, "login: respose code: ${response.code()}")
            val doc = Jsoup.parse(response.body()?.string())
            // password or ID error
            val statusDiv = doc.getElementById("status")
            if (statusDiv != null) {
                LOGIN_ID_PASS_ERROR
            } else { // login succeed, redirect to home page
                LOGIN_SUCCESS
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun logoutAsync(callback: Callback) {
        val logoutRequest = Request.Builder()
            .url("https://my.zjou.edu.cn/cas/logout?service=http://portal.zjou.edu.cn")
            .header("User-Agent", USER_AGENT)
            .get()
            .build()
        httpClient.newCall(logoutRequest).enqueue(callback)
    }

    fun logout(): Observable<Response> {
        val logoutRequest = Request.Builder()
            .url("https://my.zjou.edu.cn/cas/logout?service=http://portal.zjou.edu.cn")
            .header("User-Agent", USER_AGENT)
            .get()
            .build()
        return Observable.fromCallable { httpClient.newCall(logoutRequest).execute() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private val transcriptRequest = Request.Builder()
        .url("http://portal.zjou.edu.cn/independent.portal?.lm=zhxsxfcj&.ms=view&action=cj&.ir=true")
        .header("User-Agent", USER_AGENT)
        .get()
        .build()

    fun getTranscriptAsync(callback: Callback) {
        httpClient.newCall(transcriptRequest).enqueue(callback)
    }

    fun getTranscript(): Observable<MutableList<Transcript>?> {
        return Observable.fromCallable {
            val response = httpClient.newCall(transcriptRequest).execute()
            Log.d(TAG, "onResponse: code: ${response.code()}")
            val resp = response.body()?.string()
            if (resp?.startsWith("{") == true) {
                Log.d(TAG, "onResponse: $resp")
                val transcriptResponse = Gson().parseJson<TranscriptResponse>(resp)
                transcriptResponse?.transcriptList
            } else {
                ArrayList<Transcript>()
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private val gpaRequest = Request.Builder()
        .url("http://portal.zjou.edu.cn/independent.portal?.lm=zhxsxfcj&.ms=view&action=xf&.ir=true")
        .header("User-Agent", USER_AGENT)
        .get()
        .build()

    fun getGpaAsync(callback: Callback) {
        httpClient.newCall(gpaRequest).enqueue(callback)
    }

    fun getGpa(): Observable<Response> {
        return Observable.fromCallable { httpClient.newCall(gpaRequest).execute() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}