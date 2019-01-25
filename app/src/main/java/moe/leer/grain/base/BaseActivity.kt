package moe.leer.grain.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import moe.leer.grain.LocaleManager
import moe.leer.grain.R
import moe.leer.grain.Util
import java.io.Serializable

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
abstract class BaseActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName

    override fun attachBaseContext(newBase: Context) {
        val localeManager = LocaleManager(newBase)
        super.attachBaseContext(localeManager.updateLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        setFullScreen()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        Util.closeKeyboard(this, this.window.decorView)
    }

    open fun initView() {}

    abstract fun initData()

    inline fun <reified T : Activity> Activity.startActivity(options: Bundle?, vararg args: Pair<String, Any>) {
        val intent = Intent(this, T::class.java)
        intent.putExtras(bundleOf(*args))
        startActivity(intent, options)
    }

    inline fun <reified T : Activity> Activity.startActivityForResult(
        options: Bundle?,
        requestCode: Int,
        vararg args: Pair<String, Any>
    ) {
        val intent = Intent(this, T::class.java)
        intent.putExtras(bundleOf(*args))
        startActivityForResult(intent, requestCode, options)
    }

    protected fun makeViewSquare(view: View) {
        Handler().post {
            val viewParams = view.layoutParams
            viewParams.height = view.width
            view.layoutParams = viewParams
        }
    }

    protected fun setFullScreen() {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = this.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // set statusBar transparent
            window.statusBarColor = this.resources.getColor(R.color.transparent)
        }
    }

    fun toggleInteraction(enable: Boolean) {
        if (enable) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    /**
     *
     * androidx.core.os.bundleOf
     *
     * Returns a new [Bundle] with the given key/value pairs as elements.
     *
     * @throws IllegalArgumentException When a value is not a supported type of [Bundle].
     */
    fun bundleOf(vararg pairs: Pair<String, Any?>) = Bundle(pairs.size).apply {
        for ((key, value) in pairs) {
            when (value) {
                null -> putString(key, null) // Any nullable type will suffice.

                // Scalars
                is Boolean -> putBoolean(key, value)
                is Byte -> putByte(key, value)
                is Char -> putChar(key, value)
                is Double -> putDouble(key, value)
                is Float -> putFloat(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Short -> putShort(key, value)

                // References
                is Bundle -> putBundle(key, value)
                is CharSequence -> putCharSequence(key, value)
                is Parcelable -> putParcelable(key, value)

                // Scalar arrays
                is BooleanArray -> putBooleanArray(key, value)
                is ByteArray -> putByteArray(key, value)
                is CharArray -> putCharArray(key, value)
                is DoubleArray -> putDoubleArray(key, value)
                is FloatArray -> putFloatArray(key, value)
                is IntArray -> putIntArray(key, value)
                is LongArray -> putLongArray(key, value)
                is ShortArray -> putShortArray(key, value)

                // Reference arrays
                is Array<*> -> {
                    val componentType = value::class.java.componentType
                    @Suppress("UNCHECKED_CAST") // Checked by reflection.
                    when {
                        Parcelable::class.java.isAssignableFrom(componentType) -> {
                            putParcelableArray(key, value as Array<Parcelable>)
                        }
                        String::class.java.isAssignableFrom(componentType) -> {
                            putStringArray(key, value as Array<String>)
                        }
                        CharSequence::class.java.isAssignableFrom(componentType) -> {
                            putCharSequenceArray(key, value as Array<CharSequence>)
                        }
                        Serializable::class.java.isAssignableFrom(componentType) -> {
                            putSerializable(key, value)
                        }
                        else -> {
                            val valueType = componentType.canonicalName
                            throw IllegalArgumentException(
                                "Illegal value array type $valueType for key \"$key\""
                            )
                        }
                    }
                }

                // Last resort. Also we must check this after Array<*> as all arrays are serializable.
                is Serializable -> putSerializable(key, value)

                else -> {
                    if (Build.VERSION.SDK_INT >= 18 && value is Binder) {
                        putBinder(key, value)
                    } else if (Build.VERSION.SDK_INT >= 21 && value is Size) {
                        putSize(key, value)
                    } else if (Build.VERSION.SDK_INT >= 21 && value is SizeF) {
                        putSizeF(key, value)
                    } else {
                        val valueType = value.javaClass.canonicalName
                        throw IllegalArgumentException("Illegal value type $valueType for key \"$key\"")
                    }
                }
            }
        }
    }
}