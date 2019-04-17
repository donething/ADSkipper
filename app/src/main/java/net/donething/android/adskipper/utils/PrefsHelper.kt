package net.donething.android.adskipper.utils

import net.donething.android.adskipper.MyApp
import java.io.File

object PrefsHelper {
    // 广告信息（广告文本、控件）的记录文件
    private val adInfoFile = File("${MyApp.app.filesDir}/ad_info.log")

    fun appendADInfo(text: String) {
        // 不存在则先创建
        if (!adInfoFile.exists()) {
            adInfoFile.createNewFile()
        }
        adInfoFile.appendText("$text\n")
    }

    fun readADInfo(): List<String> {
        if (!adInfoFile.exists()) return listOf()
        return adInfoFile.readLines()
    }
}