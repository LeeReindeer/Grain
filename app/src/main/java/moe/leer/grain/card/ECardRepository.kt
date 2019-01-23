package moe.leer.grain.card

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import moe.leer.grain.Constant
import moe.leer.grain.Constant.SP_NAME
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.db.ECardLocalCache
import moe.leer.grain.getSP
import moe.leer.grain.model.ECard
import moe.leer.grain.model.User
import java.util.*

/**
 *
 * Created by leer on 1/18/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class ECardRepository(private val cache: ECardLocalCache, private val context: Context) {
    val TAG = "ECardRepository"

    private val _userData: MutableLiveData<User?> = MutableLiveData()
    val userData: LiveData<User?>
        get() {
            refreshUser()
            return _userData
        }

    /**
     * Fetch [size] ( the size of items will change to >= 100) card items from network
     * Used to fetch a chunk of data and index by date in Service,
     * It will reduce time to load data in further scroll
     */
    fun fetch(size: Int, fetchFinished: () -> Unit) {
        val requestTimes = (size / FuckSchoolApi.NETWORK_PAGE_SIZE) + 1
        for (page in 1..requestTimes) {
            requestAndSave(page, fetchFinished)
        }
    }

    /**
     * Fetch 100 items from server and save to local database
     */
    private fun requestAndSave(pageIndex: Int, finished: () -> Unit) {
        FuckSchoolApi.getInstance(context).getECardList(pageIndex, FuckSchoolApi.NETWORK_PAGE_SIZE)
            .subscribe(object : NetworkObserver<ArrayList<ECard>?>(context) {
                override fun onNetworkNotAvailable() {
                }

                override fun onNext(items: ArrayList<ECard>) {
                    // save to database
                    cache.insert(items)
                    finished()
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                }
            })
    }

    fun get(): LiveData<PagedList<ECard>> {
        val dataSource = cache.get()
        val boundaryCallback = ECardBoundaryCallback(cache, context)
        return LivePagedListBuilder(dataSource, FuckSchoolApi.DEFAULT_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()
    }

    fun getByDate(from: Date, to: Date): LiveData<PagedList<ECard>> {
        TODO()
        val dataSource = cache.getItemsByDate(from, to)
        val boundaryCallback = ECardBoundaryCallback(cache, context)
        return LivePagedListBuilder(dataSource, FuckSchoolApi.DEFAULT_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()
    }

    fun refreshUser() {
        var oldUser = User(0, "")
        try {
            val userStr = context.getSP(SP_NAME).getString(Constant.SP_USER_INFO, "")
           if (!userStr.isNullOrEmpty()) {
               oldUser = Gson().fromJson<User>(
                   userStr,
                   User::class.java
               )
           }
        } catch (ignore: JsonSyntaxException) {
        }
        _userData.value = oldUser

        FuckSchoolApi.getInstance(context).getUserInfo()
            .subscribe(object : NetworkObserver<User>(context) {
                override fun onNetworkNotAvailable() {
                    _userData.value = null
                }

                override fun onNext(user: User) {
                    _userData.value = user
                }
            })
    }

}