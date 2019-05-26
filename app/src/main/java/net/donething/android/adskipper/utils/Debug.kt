package net.donething.android.adskipper.utils

import android.util.Log

enum class Debug {
    V, D, I, W, E;

    companion object {
        fun log(level: Debug, tag: String, msg: String) {
            when (level) {
                V -> Log.v(tag, msg)
                D -> Log.d(tag, msg)
                I -> Log.i(tag, msg)
                W -> Log.w(tag, msg)
                E -> Log.e(tag, msg)
            }
        }
    }
}
