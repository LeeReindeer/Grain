package moe.leer.grain

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException


/**
 *
 * Created by leer on 1/14/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */

fun Activity.toast(msg: String?) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String?) {
    Toast.makeText(this.requireContext(), msg, Toast.LENGTH_SHORT).show()
}

inline fun <reified T> Gson.parseJson(json: String): T? {
    try {
        return fromJson(json, T::class.java)
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
}

fun Context.getSP(name: String) = this.getSharedPreferences(name, Context.MODE_PRIVATE)


@SuppressLint("CommitPrefEdits")
fun Context.getSPEdit(name: String, edit: SharedPreferences.Editor.() -> Unit) {
    edit(this.getSharedPreferences(name, Context.MODE_PRIVATE).edit())
}