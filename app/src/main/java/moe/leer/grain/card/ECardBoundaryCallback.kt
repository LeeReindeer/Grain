package moe.leer.grain.card

import android.content.Context
import android.util.Log
import androidx.paging.PagedList
import moe.leer.grain.Constant.SP_LAST_REQUEST_PAGE
import moe.leer.grain.Constant.SP_NAME
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.db.ECardLocalCache
import moe.leer.grain.getSP
import moe.leer.grain.getSPEdit
import moe.leer.grain.model.ECard

/**
 *
 * Created by leer on 1/22/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class ECardBoundaryCallback(
    private val cache: ECardLocalCache,
    private val context: Context
) : PagedList.BoundaryCallback<ECard>() {

    val TAG = "ECardBoundaryCallback"

    // keep the last requested page in SP. When the request is successful, increment the page number.
    // page limit 10
    private var lastRequestedPage
        get() = context.getSP(SP_NAME).getInt(SP_LAST_REQUEST_PAGE, 1)
        set(value) = context.getSPEdit(SP_NAME) {
            putInt(SP_LAST_REQUEST_PAGE, value)
            apply()
        }

    override fun onZeroItemsLoaded() {
        requestAndSave()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ECard) {
        requestAndSave()
    }

    private fun requestAndSave() {
        Log.d(TAG, "requestAndSave: request page $lastRequestedPage")
        FuckSchoolApi.getInstance(context).getECardList(lastRequestedPage, FuckSchoolApi.NETWORK_PAGE_SIZE)
            .subscribe(object : NetworkObserver<ArrayList<ECard>?>(context) {
                override fun onNetworkNotAvailable() {
                }

                override fun onNext(items: ArrayList<ECard>) {
                    // save to database
                    cache.insert(items)
                    lastRequestedPage++
                }
            })
    }
}