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
import moe.leer.grain.model.ECard
import moe.leer.grain.model.Transcript
import moe.leer.grain.model.TranscriptResponse
import moe.leer.grain.model.User
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
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

        const val DEFAULT_PAGE_SIZE = 30
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

    fun getExecution(): Observable<String> {
        return Observable.fromCallable {
            val response = httpClient.newCall(executionRequest).execute()
            if (!response.isSuccessful) {
                ""
            } else {
                val respString = response.body()!!.string()
                val doc = Jsoup.parse(respString)
                val selected = doc.select("input[type=hidden]")
                val executionNode = selected[1]
                val executionStr = executionNode.attr("value")
                Log.d(TAG, "onResponse: execution: $executionStr")
                executionStr
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    @Deprecated("Main thread may stick")
    fun getExecutionStick(): String {
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
        val execution = getExecutionStick()
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
    fun login(user: User, execution: String): Observable<Int> {
//        val execution = getExecutionStick()
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
            Log.d(TAG, "getTranscript: code: ${response.code()}")
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

    /**
     * @param pageIndex page start from 1
     */
    fun getECardList(pageIndex: Int, pageSize: Int): Observable<ArrayList<ECard>?> {
        val form = FormBody.Builder()
            .add("pageIndex", pageIndex.toString())
            .add("pageSize", pageSize.toString())
            .build()
        val request = Request.Builder()
            .url("http://portal.zjou.edu.cn/independent.portal?.cs=ZHxjb20uZWR1LmRrLnN0YXJnYXRlLnBvcnRhbC5jb250YWluZXIuY29yZS5pbXBsLlBvcnRsZXRFbnRpdHlXaW5kb3d8aW50ZS1qeXh4fHZpZXd8bm9ybWFsfHBtX2ludGUtanl4eF9hY3Rpb249cXVlcnlKeXh4fHJtX3xwcm1f")
            .header("User-Agent", USER_AGENT)
            .post(form)
            .build()

        return Observable.fromCallable {
            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful || response.body() == null) {
                ArrayList()
            } else {
                parseCardTable(response.body()!!.string())
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    //fixme not tested
    private fun parseCardTable(responseString: String): ArrayList<ECard>? {
        if (responseString.isEmpty()) {
            return null
        }
        val itemList = ArrayList<ECard>(30)
        val doc = Jsoup.parse(responseString)
        val table = doc.select("table")[0]
        val rows = table.select("tr")
        for (i in 0..rows.size) {
            val cols = rows[i].select("td")
            var money = 0.0
            try {
                money = cols[5].text().toDouble()
            } catch (ignore: NumberFormatException) {
            }
            if (money != 0.0) {
                //FIXME
//                val item = ECard(cols[3].text(), cols[2].text(), money)
//                itemList.add(item)
            }
        }
        return itemList
    }

}