package moe.leer.grain.card

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.db.ECardDatabase
import moe.leer.grain.db.ECardLocalCache
import moe.leer.grain.model.ECard
import moe.leer.grain.model.User
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

    // fetch the first page again to get new item, also refresh user info
    fun refresh(afterRefresh: () -> Unit) {
        repository.refreshUser()
        repository.fetch(FuckSchoolApi.DEFAULT_PAGE_SIZE) {
            afterRefresh()
        }
    }

    fun refreshUserInfo(): LiveData<User?> {
        return repository.userData
    }

    fun cardListByDate(from: Date, to: Date): LiveData<PagedList<ECard>> = repository.getByDate(from, to)
}