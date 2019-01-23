package moe.leer.grain.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 *
 * Created by leer on 1/23/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
abstract class BaseFragment : Fragment() {
    val TAG = javaClass.simpleName

    abstract var layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onResume() {
        super.onResume()
    }
}