package moe.leer.grain.card


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_card.*
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.R
import moe.leer.grain.model.ECard
import moe.leer.grain.toast
import java.util.*


class CardFragment : androidx.fragment.app.Fragment() {

    private val TAG = "CardFragment"
    private lateinit var viewModel: ECardViewModel
    private val adapter = ECardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()
//        mockData()
        initView()
    }

    private fun initView() {
        ecardRV.layoutManager = LinearLayoutManager(context)
        refreshLayout.isRefreshing = true
        initCard()
    }

    private fun initCard() {

    }

    private fun initData() {
        viewModel = ViewModelProviders.of(this).get(ECardViewModel::class.java)
        ecardRV.adapter = adapter
        viewModel.cardList.observe(this, Observer<PagedList<ECard>?> {
            showEmptyPage(it == null || it.isEmpty())
            adapter.submitList(it)
        })
    }

    private fun mockData() {
        FuckSchoolApi.getInstance(this.requireContext()).getECardList(1, FuckSchoolApi.NETWORK_PAGE_SIZE)
            .subscribe(object : NetworkObserver<ArrayList<ECard>?>(this.requireContext()) {
                override fun onNetworkNotAvailable() {
                }

                override fun onNext(items: ArrayList<ECard>) {
                    Log.d(TAG, "onNext: get ${items.size} items")
                    Log.d(TAG, "onNext: first item: ${items[0]}")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                }
            })
    }

    private fun showEmptyPage(show: Boolean) {
        if (show) {
            //todo
            refreshLayout.isRefreshing = true
            toast("show empty list")
        } else {
            refreshLayout.isRefreshing = false
        }
    }
}
