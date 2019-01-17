package moe.leer.grain.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import moe.leer.grain.FuckSchoolApi

/**
 *
 * Created by leer on 1/17/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "LoginViewModel"
    private val repository: LoginRepository
    private val statusLiveData: MutableLiveData<Int> = MutableLiveData()

    init {
        repository = LoginRepository(application.applicationContext, this)
        statusLiveData.value = FuckSchoolApi.LOGIN_PROCESS
    }

    fun doLogin(id: Int, password: String) {
        repository.doLogin(id, password)
    }

    fun getLoginStatus() = statusLiveData

}