package com.au.a4x4vehiclehirefraser.helper

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.au.a4x4vehiclehirefraser.R

object Helper {
    fun makeToast(context: Context?, isLong: Boolean, text: String?) {
        val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, text, duration)
        toast.setGravity(Gravity.CENTER, 0, 0)
        val toastView = toast.view
        toastView.setBackgroundResource(R.drawable.toast_formatting)
        toast.show()
    }
}