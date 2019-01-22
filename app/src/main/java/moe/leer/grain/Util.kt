package moe.leer.grain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 * Created by leer on 1/17/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
object Util {
    fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }

        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)

        return bitmap
    }

    fun showKeyboard(context: Context, view: EditText) {
        view.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    fun closeKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(view.windowToken, 0)
//        imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    val formatWithSecond = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
    val formatIn24 = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    fun getTimeDate(dateStr: String): Date? {
        try {
            return formatWithSecond.parse(dateStr)
        } catch (ignore: ParseException) {
        }
        return null
    }

    fun getTimeString(date: Date): String {
        return formatIn24.format(date)
    }
}