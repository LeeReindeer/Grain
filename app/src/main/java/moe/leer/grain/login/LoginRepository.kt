package moe.leer.grain.login

import android.content.Context
import android.util.Log
import io.reactivex.disposables.Disposable
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.model.User

/**
 *
 * Created by leer on 1/17/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class LoginRepository(val context: Context, val viewModel: LoginViewModel) {
    val TAG = "LoginRepository"

    private val api = FuckSchoolApi.getInstance(context)
    lateinit var disposable: Disposable

    fun doLogin(id: Int, password: String) {
        api.getExecution().subscribe(object : NetworkObserver<String>(context) {
            override fun onNetworkNotAvailable() {
                viewModel.getLoginStatus().value = FuckSchoolApi.LOGIN_NET_ERROR
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                viewModel.getLoginStatus().value = FuckSchoolApi.LOGIN_NET_ERROR
            }

            override fun onNext(executionStr: String) {
                val observer = object : NetworkObserver<Int>(context) {
                    override fun onNetworkNotAvailable() {
                        viewModel.getLoginStatus().value = FuckSchoolApi.LOGIN_NET_ERROR
                    }

                    override fun onNext(status: Int) {
                        Log.d(TAG, "onNext: $status")
                        viewModel.getLoginStatus().value = status
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        viewModel.getLoginStatus().value = FuckSchoolApi.LOGIN_NET_ERROR
                    }
                }
                disposable = observer
                api.login(User(id, password), executionStr).subscribe(observer)
            }
        })

    }
}