package com.d4rk.lowbrightness.core.utils.extensions

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.d4rk.lowbrightness.appContext

fun CharSequence.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Handler(Looper.getMainLooper()).post {
        val toast = Toast.makeText(appContext, this, duration)
        toast.duration = duration
        toast.show()
    }
}
