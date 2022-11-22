package net.donething.android.adskipper.entity

import android.graphics.drawable.Drawable

data class AppInfo(
    val pkname: String,
    val icon: Drawable,
    val label: String,
    // 启动应用 activity 的类名
    val launch: String,
    // 是否为系统应用
    val isSys: Boolean,
    // 是否为不跳过广告的应用（选择框中打钩的）
    var excluded: Boolean
)