package moe.leer.grain.card

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import moe.leer.grain.db.ECardDatabase
import moe.leer.grain.db.ECardLocalCache
import moe.leer.grain.model.ECard
import java.util.*
import java.util.concurrent.Executors

/**
 *
 * Created by leer on 1/18/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class ECardViewModel(application: Application) :
    AndroidViewModel(application) {

    val TAG = "ECardViewModel"
    private val repository: ECardRepository

    init {
        val context = application.applicationContext
        val db = ECardDatabase.getInstance(context)
        repository = ECardRepository(ECardLocalCache(db.eCardDao(), Executors.newSingleThreadExecutor()), context)
    }

    val cardList: LiveData<PagedList<ECard>>
        get() = repository.get()

    fun cardListByDate(from: Date, to: Date): LiveData<PagedList<ECard>> = repository.getByDate(from, to)
}