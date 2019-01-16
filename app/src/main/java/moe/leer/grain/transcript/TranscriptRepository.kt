package moe.leer.grain.transcript

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.reactivex.disposables.Disposable
import moe.leer.grain.App
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.model.Transcript
import moe.leer.grain.model.TranscriptResponse

/**
 *
 * Created by leer on 1/15/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 *
 *
 * Get transcript from Internet or shared preference
 */
class TranscriptRepository(val context: Context) {

    private val TAG = "TranscriptRepository"
    private val sp: SharedPreferences
    private var transcriptList: MutableList<Transcript> = ArrayList()
    private val originData: MutableList<Transcript> = ArrayList()
    private val yearSet: HashSet<String> = HashSet()

    private lateinit var disposable: Disposable

    fun getNetworkDisposable() = disposable


    init {
        sp = context.getSharedPreferences(App.USER_SP, Context.MODE_PRIVATE)
    }

    private val api = FuckSchoolApi.getInstance(context)

    fun getTranscript(): MutableList<Transcript> {
        val observer = object : NetworkObserver<MutableList<Transcript>?>(context) {
            override fun onNetworkNotAvailable() {
                //try get from SP
                if ((context as App).isLogin) {
                    val transcriptJson = sp.getString("Repository", "")!!
                    Log.d(TAG, "network not available, getTranscript: from sp")
                    if (!transcriptJson.isEmpty()) {
                        try {
                            Log.d(TAG, "getTranscript: from sp: $transcriptJson")
                            val transcriptFromSP = Gson().fromJson(transcriptJson, TranscriptResponse::class.java)

                            originData.clear()
                            originData.addAll(transcriptFromSP.transcriptList!!)

                            transcriptList = transcriptFromSP.transcriptList
                        } catch (e: JsonSyntaxException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun onNext(transcriptList: MutableList<Transcript>) {
                originData.clear()
                originData.addAll(transcriptList)
                this@TranscriptRepository.transcriptList = transcriptList
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                originData.clear()
                transcriptList.clear()
            }
        }
        api.getTranscript()?.subscribe(observer)
        disposable = observer

        /*
        val netAvailable = context.isNetworkAvailable()
        // load from net
        var getFromNetSucceed = false
        if (netAvailable) {

            disposable = api.getTranscript().subscribeBy(
                onNext = { response ->
                    Log.d(TAG, "onResponse: code: ${response.code()}")
                    val resp = response.body()?.string()
                    if (resp?.startsWith("{") == true) {
                        Log.d(TAG, "onResponse: $resp")
                        val transcriptResponse = Gson().parseJson<TranscriptResponse>(resp)

                        originData.clear()
                        originData.addAll(transcriptResponse?.transcriptList!!)
                        transcript.postValue(transcriptResponse.transcriptList)
                        sp.edit().putString("Repository", resp).apply()
                        getFromNetSucceed = true
                    } else {
                        Log.d(TAG, "onResponse: getTranscript failed")
                    }
                },
                onComplete = {},
                onError = {}
            )
//            api.getTranscriptAsync(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    Log.d(TAG, "onResponse: code: ${response.code()}")
//                    val resp = response.body()?.string()
//                    Log.i(TAG, "onResponse: $resp");
//                    if (resp?.startsWith("{") == true) {
//                        Log.d(TAG, "onResponse: $resp")
//                        val transcriptResponse = Gson().parseJson<TranscriptResponse>(resp)
//
//                        originData.clear()
//                        originData.addAll(transcriptResponse?.transcriptList!!)
//                        transcript.postValue(transcriptResponse.transcriptList)
//                        sp.edit().putString("Repository", resp).apply()
//                        getFromNetSucceed = true
//                    } else {
//                        Log.d(TAG, "onResponse: getTranscript failed")
//                    }
//                }
//            })
        }
        // get from internet failed, try to get from sp
        if (!getFromNetSucceed && (context as App).isLogin) {
            val transcriptJson = sp.getString("Repository", "")!!
            Log.d(TAG, "getTranscriptAsync: from sp")
            if (!transcriptJson.isEmpty()) {
                try {
                    Log.d(TAG, "getTranscriptAsync: from sp: $transcriptJson")
                    val transcriptFromSP = Gson().fromJson(transcriptJson, TranscriptResponse::class.java)

                    originData.clear()
                    originData.addAll(transcriptFromSP.transcriptList!!)
                    transcript.postValue(transcriptFromSP.transcriptList)
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }
            }
        }*/
        return transcriptList
    }

    /**
     * Filter data with year and semester, filter year first
     * @param year filter with year: 1, 2, 3 ,4
     * @param semester 1, 2
     */
    fun filter(year: Int, semester: Int): MutableList<Transcript>? {
        return filterSemester(filterYear(originData, year), semester)
    }

    /**
     * @param data data to be filter
     * @param year selected by spinner, can be 1, 2, 3, 4
     */
    fun filterYear(data: MutableList<Transcript>?, year: Int): MutableList<Transcript>? {
        val list = data
        if (list == null || list.isEmpty()) {
            Log.d(TAG, "filterYear: null")
            return null
        }
        // no filter
        if (year == 0) {
            return list
        }
        list.forEach {
            yearSet.add(it.year)
        }
        if (year > yearSet.size) {
            transcriptList.clear()
            return null
        }
        val sortedList = yearSet.asSequence().sortedBy { it }.toList()
        Log.d(TAG, "filterYear: $sortedList")
        return list.asSequence().filter { it.year.equals(sortedList[year - 1]) }.toMutableList()
    }

    fun filterYear(year: Int): MutableList<Transcript>? = filterYear(originData, year)

    /**
     * @param data data to be filter
     * @param semester select by spinner, semester should be 1 or 2
     */
    fun filterSemester(data: MutableList<Transcript>?, semester: Int): MutableList<Transcript>? {
        val list = data
        if (list == null || list.isEmpty()) {
            return null
        }
        if (semester == 0) {
            return list
        }
        return list.asSequence().filter { it.semester == semester }.toMutableList()
    }

    fun filterSemester(semester: Int): MutableList<Transcript>? = filterSemester(originData, semester)

}