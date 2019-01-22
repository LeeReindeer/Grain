package moe.leer.grain.db

import android.util.Log
import androidx.paging.DataSource
import moe.leer.grain.model.ECard
import java.util.*
import java.util.concurrent.Executor

/**
 *
 * Created by leer on 1/22/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 *
 * Call method in ECardDao in Executor
 */
class ECardLocalCache(
    private val cardDao: ECardDao,
    private val ioExecutor: Executor
) {
    fun insert(items: ArrayList<ECard>) {
        ioExecutor.execute {
            Log.d("ECardLocalCache", "insert ${items.size} items")
            cardDao.insert(items)
        }
    }

    fun get(): DataSource.Factory<Int, ECard> {
        return cardDao.get()
    }

    fun getItemsByDate(from: Date, to: Date): DataSource.Factory<Int, ECard> {
        return cardDao.getItemsByDate(from, to)
    }

}