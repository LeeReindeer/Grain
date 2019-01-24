package moe.leer.grain.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import moe.leer.grain.db.ECardDatabase
import moe.leer.grain.model.User
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 *
 * Created by leer on 1/24/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "ProfileViewModel"

    private val repository: ProfileRepository
    private val db: ECardDatabase
    private val ioExecutor: Executor

    init {
        val context = application.applicationContext
        db = ECardDatabase.getInstance(context)
        repository = ProfileRepository(db, context)
        ioExecutor = Executors.newSingleThreadExecutor()
    }

    fun userData(): LiveData<User?> = repository.userData

    // Clear all data in shared preference and database
    fun deleteAllData() {
        ioExecutor.execute {
            repository.deleteAllData()
        }
    }

    fun nukeData() {
        ioExecutor.execute {
            repository.nukeData()
        }
    }

    fun refresh() {
        repository.refreshAndSaveUser()
    }

    override fun onCleared() {
        super.onCleared()
        repository.userDisposable?.dispose()
    }
}