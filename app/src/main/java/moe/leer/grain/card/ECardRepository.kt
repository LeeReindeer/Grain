package moe.leer.grain.card

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.base.BaseUserRepository
import moe.leer.grain.db.ECardLocalCache
import moe.leer.grain.model.ECard
import java.util.*

/**
 *
 * Created by leer on 1/18/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class ECardRepository(private val cache: ECardLocalCache, private val context: Context) : BaseUserRepository(context) {

    /**
     * Fetch [size] ( the size of items will change to >= 100) card items from network
     * Used to fetch a chunk of data and index by date in Service,
     * It will reduce time to load data in further scroll
     */
    fun fetch(size: Int, fetchFinished: () -> Unit, onError: () -> Unit) {
        val requestTimes = (size / FuckSchoolApi.NETWORK_PAGE_SIZE) + 1
        for (page in 1..requestTimes) {
            requestAndSave(page, fetchFinished, onError)
        }
    }

    /**
     * Fetch 100 items from server and save to local database
     */
    private fun requestAndSave(pageIndex: Int, finished: () -> Unit, error: () -> Unit) {
        FuckSchoolApi.getInstance(context).getECardList(pageIndex, FuckSchoolApi.NETWORK_PAGE_SIZE)
            .subscribe(object : NetworkObserver<ArrayList<ECard>?>(context) {
                override fun onNetworkNotAvailable() {
                    error()
                }

                override fun onNext(items: ArrayList<ECard>) {
                    // save to database
                    cache.insert(items)
                    finished()
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    error()
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
}