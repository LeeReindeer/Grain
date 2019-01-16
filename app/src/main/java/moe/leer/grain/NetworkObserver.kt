package moe.leer.grain

import android.content.Context
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableHelper
import java.util.concurrent.atomic.AtomicReference

/**
 *
 * Created by leer on 1/16/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
abstract class NetworkObserver<T>(val context: Context) : Observer<T>, AtomicReference<Disposable>(), Disposable {

    override fun onSubscribe(d: Disposable) {
        if (!context.isNetworkAvailable()) {
            onNetworkNotAvailable()
            d.dispose()
        }
    }

    abstract fun onNetworkNotAvailable()

    override fun onComplete() {

    }

    override fun onError(e: Throwable) {

    }

    override fun isDisposed(): Boolean {
        return get() == DisposableHelper.DISPOSED
    }

    override fun dispose() {
        DisposableHelper.dispose(this)
    }
}