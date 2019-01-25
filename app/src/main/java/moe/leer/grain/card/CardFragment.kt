package moe.leer.grain.card


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.chaychan.viewlib.NumberRunningTextView
import kotlinx.android.synthetic.main.fragment_card.*
import moe.leer.grain.App
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.R
import moe.leer.grain.base.BaseFragment
import moe.leer.grain.model.ECard
import moe.leer.grain.model.User
import moe.leer.grain.toast
import java.util.*


class CardFragment : BaseFragment() {
    override var layoutId: Int
        get() = R.layout.fragment_card
        set(value) {}

    private lateinit var viewModel: ECardViewModel
    private val adapter = ECardAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()
        initView()
    }

    private fun initView() {
        ecardRV.layoutManager = LinearLayoutManager(context)
        val controller = AnimationUtils.loadLayoutAnimation(activity, R.anim.anim_layout_fall_down)
        ecardRV.layoutAnimation = controller

        refreshLayout.setOnRefreshListener {
            refresh()
        }
        errorPage.setOnClickListener {
            refresh()
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
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
                && App.getApplication(requireContext().applicationContext).isLogin
            ) {
                adapter.submitList(it)
            }
        })
        refreshLayout.isRefreshing = true

        // refresh from network
        if (App.getApplication(requireContext().applicationContext).isLogin) {
            refresh()
        } else {
            refreshLayout.isRefreshing = false
            toast(R.string.hint_login)
        }

    }

    /**
     *  Refresh card list, and always set refreshLayout.isRefreshing false
     */
    private fun refresh() {
        viewModel.refresh(
            afterRefresh = {
                Log.d(TAG, "initData: refresh finish")
                Handler().postDelayed({
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        refreshLayout.isRefreshing = false
                    }
                }, 100)
            },
            onError = {
                refreshLayout.isRefreshing = false
                toast(R.string.hint_check_network)
            }
        )
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
        Glide.with(this)
            .load(R.mipmap.witch)
            .into(errorImage)
        if (show) {
            errorPage.visibility = View.VISIBLE
            ecardRV.visibility = View.INVISIBLE
        } else {
            errorPage.visibility = View.GONE
            ecardRV.visibility = View.VISIBLE
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
