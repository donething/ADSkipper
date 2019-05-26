package net.donething.android.adskipper.entity

import android.graphics.drawable.Drawable

data class AppInfo(
    val pkname: String,
    val icon: Drawable,
    val label: String,
    // 应用启动activity的类名
    val launch: String,
    val excluded: Boolean
)