package com.au.a4x4vehiclehirefraser.helper

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import com.au.a4x4vehiclehirefraser.R
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

object Helper {

    fun Any.toast(context: Context, isLong: Boolean): Toast {
        val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, this.toString(), duration)
        toast.setGravity(Gravity.CENTER, 0, 0)
        val toastView = toast.view
        toastView.setBackgroundResource(R.drawable.toast_formatting)
        return toast.apply { show() }
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }

    fun EditText.validate(message: String, validator: (String) -> Boolean) {
        this.afterTextChanged {
            this.error = if (validator(it)) null else message
        }
        this.error = if (validator(this.text.toString())) null else message
    }

    fun String.textIsEmpty(): Boolean = !this.isNullOrEmpty()

    /**
     * Convert a given date to milliseconds
     */
    fun Date.toMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar.timeInMillis
    }


    fun Date.isSame(to: Date): Boolean {
        val sdf = SimpleDateFormat("yyyMMdd", Locale.getDefault())
        return sdf.format(this) == sdf.format(to)
    }


    fun String.toIntOrZero(): Int {
        var value = 0
        try {
            value = this.toInt()
        } catch (_: Exception) {
        }
        return value
    }


    fun String.toBoolean(): Boolean {
        return this != "" &&
                (this.equals("TRUE", ignoreCase = true)
                        || this.equals("Y", ignoreCase = true)
                        || this.equals("YES", ignoreCase = true))
    }


    fun String.convertToCamelCase(): String {
        var text = ""
        if (this.isNotEmpty()) {
            val words = this.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            words.filterNot { it.isEmpty() }
                .map {
                    it.substring(
                        0,
                        1
                    ).toUpperCase(Locale.getDefault()) + it.substring(1).toLowerCase(Locale.getDefault())
                }
                .forEach { text += "$it " }
        }
        return text.trim { it <= ' ' }
    }

    fun Double.roundTo(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
        return 0.0
    }



}