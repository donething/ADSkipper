package net.donething.android.adskipper.utils

import android.util.Log

enum class Debug {
    V, D, I, W, E, A;

    companion object {
        fun log(level: Debug, msg: String) {
            when (level) {
                E -> Log.e(Constants.TAG, msg)
                else -> Log.d(Constants.TAG, msg)
            }
        }
    }
}
