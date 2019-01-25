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
    private lateinit var transcript: MutableLiveData<MutableList<Transcript>>
    private var year = 0
    private var semester = 0

    init {
        repository = TranscriptRepository(application)
    }

    fun getTranscript(onError: () -> Unit): LiveData<MutableList<Transcript>> {
        transcript = repository.getTranscript(onError)
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
        repository.getTranscript(onError)
        Log.d(TAG, "refresh: year: $year  semester: $semester")
        transcript.postValue(repository.filter(year, semester))
    }

    override fun onCleared() {
        super.onCleared()
        repository.getNetworkDisposable()?.dispose()
    }
}