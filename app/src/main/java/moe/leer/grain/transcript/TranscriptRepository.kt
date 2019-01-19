package moe.leer.grain.transcript

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.reactivex.disposables.Disposable
import moe.leer.grain.App
import moe.leer.grain.Constant
import moe.leer.grain.Constant.SP_FETCH_TRANSCRIPT_TIME
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.getSPEdit
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
    private var transcriptList: MutableLiveData<MutableList<Transcript>> = MutableLiveData()
    private val originData: MutableList<Transcript> = ArrayList()
    private val yearSet: HashSet<String> = HashSet()

    private var disposable: Disposable? = null

    fun getNetworkDisposable() = disposable


    init {
        sp = context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE)
    }

    private val api = FuckSchoolApi.getInstance(context)

    /**
     * Fill data from Shared preference if user login
     * when:
     * FETCH_TIMEOUT not timeout or
     * network error
     */
    fun getDataFromSP() {
        val transcriptJson = sp.getString(Constant.SP_TRANSCRIPT, "")!!
        Log.d(TAG, "network not available, getTranscript: from sp")
        if (!transcriptJson.isEmpty()) {
            try {
                Log.d(TAG, "getTranscript: from sp: $transcriptJson")
                val transcriptFromSP = Gson().fromJson(transcriptJson, TranscriptResponse::class.java)

                originData.clear()
                originData.addAll(transcriptFromSP.transcriptList!!)
                transcriptList.postValue(transcriptFromSP.transcriptList)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
        }
    }

    fun getTranscript(): MutableLiveData<MutableList<Transcript>> {
//        if (System.currentTimeMillis() - context.getSP(Constant.SP_NAME).getLong(
//                SP_FETCH_TRANSCRIPT_TIME,
//                0L
//            ) < FETCH_TIMEOUT
//        ) { // Get data from SP
//            getDataFromSP()
//            return transcriptList
//        }
        // Update last fetch time
        context.getSPEdit(Constant.SP_NAME) {
            putLong(SP_FETCH_TRANSCRIPT_TIME, 0)
            apply()
        }

        val observer = object : NetworkObserver<MutableList<Transcript>?>(context) {
            override fun onNetworkNotAvailable() {
                //try get from SP
                if ((context as App).isLogin) {
                    getDataFromSP()
                }
            }

            override fun onNext(transcriptList: MutableList<Transcript>) {
                originData.clear()
                originData.addAll(transcriptList)
                this@TranscriptRepository.transcriptList.postValue(transcriptList)
                // save to SP
                sp.edit().putString(Constant.SP_TRANSCRIPT, Gson().toJson(TranscriptResponse(transcriptList)))
                    .apply()
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                originData.clear()
                transcriptList.value?.clear()
            }
        }
        api.getTranscript().subscribe(observer)
        disposable = observer

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
            transcriptList.value?.clear()
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