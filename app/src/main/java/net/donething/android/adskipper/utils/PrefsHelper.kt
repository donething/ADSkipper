package net.donething.android.adskipper.utils

import android.content.Context
import android.preference.PreferenceManager
import net.donething.android.adskipper.MyApp
import java.io.File

object PrefsHelper {
    private val TAG = PrefsHelper::class.java.name

    private const val LOG_MAX_SIZE = 200
    // 广告信息（广告文本、控件）的记录文件
    private val adInfoFile = File("${MyApp.app.filesDir}/ad_info.log")
    private val prefs = PreferenceManager.getDefaultSharedPreferences(MyApp.app)
    private val appsPrefs = MyApp.app.getSharedPreferences("exclude_apps", Context.MODE_PRIVATE)

    /**
     * 保存广告信息的日志
     * 最大条数：LOG_MAX_SIZE
     */
    fun appendADLog(text: String) {
        // 不存在则先创建
        if (!adInfoFile.exists()) {
            adInfoFile.createNewFile()
        }
        val logs = adInfoFile.readLines().toMutableList()
        if (logs.size >= LOG_MAX_SIZE) {
            logs.removeAt(0)
        }
        logs.add(text)

        // 写入日志到文件
        adInfoFile.writeText(logs.joinToString("\n"))
    }

    /**
     * 读取保存广告信息的日志
     */
    fun readADLog(): List<String> {
        if (!adInfoFile.exists()) return listOf()
        return adInfoFile.readLines()
    }

    /**
     * 清除日志
     */
    fun clearADLog(): Boolean {
        if (!adInfoFile.exists()) return true
        try {
            return adInfoFile.delete()
        } catch (e: Exception) {
            Debug.log(Debug.E, TAG, "删除日志出错：$e")
        }
        return false
    }

    /**
     * 是否启用功能
     */
    var enable: Boolean
        get() {
            return prefs.getBoolean("enable", false)
        }
        set(value) {
            prefs.edit().putBoolean("enable", value).apply()
        }

    /**
     * 是否为调试模式
     */
    val isDebugMode: Boolean
        get() {
            return prefs.getBoolean("debug_mode", false)
        }

    /**
     * 应用是否被排除
     */
    fun isExcludedApp(pkname: String): Boolean {
        return appsPrefs.getBoolean(pkname, false)
    }

    /**
     * 设置应用是否被排除
     */
    fun setExcludedApp(pkname: String, excluded: Boolean) {
        if (excluded) {
            appsPrefs.edit().putBoolean(pkname, excluded).apply()
            return
        }
        appsPrefs.edit().remove(pkname).apply()
    }
}