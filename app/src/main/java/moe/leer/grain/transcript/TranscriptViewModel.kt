package moe.leer.grain.transcript

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import moe.leer.grain.model.Transcript

/**
 *
 * Created by leer on 1/15/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
//@Deprecated("use MVP instead")
class TranscriptViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "TranscriptViewModel"
    private val repository: TranscriptRepository
    // this livedata is different from repository one
    private var transcript: MutableLiveData<MutableList<Transcript>> = MutableLiveData()
    private var year = 0
    private var semester = 0

    init {
        repository = TranscriptRepository(application)
    }

    fun getTranscript(onError: () -> Unit): LiveData<MutableList<Transcript>> {
        val list = repository.getTranscript(onError = onError).value
        if (list != null) {
            transcript.value!!.addAll(list)
        }
//        transcript = repository.getTranscript(onError = onError)
        return transcript
    }

    fun filterYear(year: Int) {
        this.year = year
        transcript.postValue(repository.filter(year, semester))
    }

    fun filterSemester(semester: Int) {
        this.semester = semester
        transcript.postValue(repository.filter(year, semester))
    }


    fun refresh(onError: () -> Unit) {
        repository.getTranscript(onError = onError)
        Log.d(TAG, "refresh: year: $year  semester: $semester")
        transcript.postValue(repository.filter(year, semester))
    }

    fun refresh(onCompleted: () -> Unit, onError: () -> Unit) {
        repository.getTranscript({
            Log.d(TAG, "refresh: year: $year  semester: $semester")
            transcript.postValue(repository.filter(year, semester))
            onCompleted()
        }, onError)
    }

    override fun onCleared() {
        super.onCleared()
        repository.getNetworkDisposable()?.dispose()
    }
}