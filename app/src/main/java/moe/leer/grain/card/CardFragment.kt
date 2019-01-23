package moe.leer.grain.card


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaychan.viewlib.NumberRunningTextView
import kotlinx.android.synthetic.main.fragment_card.*
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.R
import moe.leer.grain.model.ECard
import moe.leer.grain.model.User
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
        initView()
    }

    private fun initView() {
        ecardRV.layoutManager = LinearLayoutManager(context)
        refreshLayout.setOnRefreshListener {
            viewModel.refresh {
                refreshLayout.isRefreshing = false
            }
        }
        initCard()
    }

    private fun initCard() {
        viewModel.refreshUserInfo().observe(this, Observer<User?> {
            if (it == null || it.id == 0) {
                showEmptyCard(true)
            } else {
                showEmptyCard(false)
                cardIdText.text = it.id.toString(10)
                (cardMoneyText as NumberRunningTextView).setContent(it.moneyRem.toString())
                cardNameText.text = it.name
            }
        })
    }

    private fun initData() {
        viewModel = ViewModelProviders.of(this).get(ECardViewModel::class.java)
        ecardRV.adapter = adapter
        viewModel.cardList.observe(this, Observer<PagedList<ECard>?> {
            showEmptyPage(it == null || it.isEmpty())
            adapter.submitList(it)
        })
        refreshLayout.isRefreshing = true
        viewModel.refresh {
            Log.d(TAG, "initData: refresh finish")
            Handler().postDelayed({
                refreshLayout.isRefreshing = false
            }, 500)
        }
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
            toast("show empty list")
        } else {
        }
    }

    private fun showEmptyCard(show: Boolean) {
        if (show) {
            cardIdText.visibility = View.INVISIBLE
            cardNameText.visibility = View.INVISIBLE
            cardMoneyText.visibility = View.INVISIBLE
            cardMoneyLabel.visibility = View.INVISIBLE
        } else {
            cardIdText.visibility = View.VISIBLE
            cardNameText.visibility = View.VISIBLE
            cardMoneyText.visibility = View.VISIBLE
            cardMoneyLabel.visibility = View.VISIBLE
        }
    }
}
