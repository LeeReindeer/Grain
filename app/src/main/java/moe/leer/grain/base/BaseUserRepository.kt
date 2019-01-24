package moe.leer.grain.base

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.reactivex.disposables.Disposable
import moe.leer.grain.Constant
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.getSP
import moe.leer.grain.getSPEdit
import moe.leer.grain.model.User

/**
 *
 * Created by leer on 1/24/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
abstract class BaseUserRepository(private val context: Context) {
    val TAG = javaClass.simpleName

    var userDisposable: Disposable? = null

    protected val _userData: MutableLiveData<User?> = MutableLiveData()
    open val userData: LiveData<User?>
        get() {
            refreshAndSaveUser()
            return _userData
        }


    fun loadFromCache(): User {
        var oldUser = User(0, "")
        try {
            val userStr = context.getSP(Constant.SP_NAME).getString(Constant.SP_USER_INFO, "")
            if (!userStr.isNullOrEmpty()) {
                oldUser = Gson().fromJson<User>(
                    userStr,
                    User::class.java
                )
                Log.d(TAG, "loadFromCache: $oldUser")
            }
        } catch (ignore: JsonSyntaxException) {
        }
        return oldUser
    }

    open fun refreshAndSaveUser() {

        _userData.value = loadFromCache()

        val observer = object : NetworkObserver<User>(context) {
            override fun onNetworkNotAvailable() {
                val oldUser = loadFromCache()
                if (oldUser.id != 0) {
                    _userData.postValue(oldUser)
                } else {
                    _userData.value = null
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                _userData.value = null
            }

            override fun onNext(user: User) {
                _userData.value = user
                context.getSPEdit(Constant.SP_NAME) {
                    putString(Constant.SP_USER_INFO, Gson().toJson(user))
                    apply()
                }
            }
        }
        userDisposable = observer

        FuckSchoolApi.getInstance(context).getUserInfo()
            .subscribe(observer)
    }

}