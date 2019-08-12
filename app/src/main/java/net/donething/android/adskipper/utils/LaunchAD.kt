package net.donething.android.adskipper.utils

import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import net.donething.android.adskipper.MyApp
import net.donething.android.adskipper.accessibility.AccessibilityUtil

object LaunchAD {
    private val TAG = LaunchAD::class.java.name

    /**
     * 跳过启动广告
     */
    fun skipAD(nodeInfo: AccessibilityNodeInfo) {
        // 功能未启用，直接返回
        if (!PrefsHelper.enable) {
            Debug.log(Debug.D, TAG, "跳过启动广告的功能未启用")
            return
        }

        // 跳过已被排除的应用
        if (PrefsHelper.isExcludedApp(nodeInfo.packageName.toString())) {
            Debug.log(Debug.D, TAG, "跳过已被排除的应用：${nodeInfo.packageName}")
            return
        }

        findClickText(nodeInfo)
    }

    /**
     * 查找跳过广告的按钮，并点击
     */
    private fun findClickText(nodeInfo: AccessibilityNodeInfo) {
        // findAccessibilityNodeInfosByText()使用类似"contains"的方式，而不是"equals"的方式来比较
        val nodes = nodeInfo.findAccessibilityNodeInfosByText("跳过")
        if (nodes == null || nodes.isEmpty()) return

        // 记录广告文本、广告控件类型到日志文件
        for (node in nodes) {
            // 为调试模式时，只检测TextView类型
            if (!PrefsHelper.isDebugMode && node.className != TextView::class.java.name) return

            // 如果需要更详细的比较，可以在这里使用：node.text=="some string"
            // 去除广告文本中的空格、换行
            val text = node.text.toString().replace(" ", "").replace("\n", "")

            // 不要用可有选项（"?"），否则匹配的内容太多，会误判
            // val pattern = """(${Constants.AD_TEXT})\d?(${Constants.AD_UNIT})?"""
            if (Regex("""(${Constants.AD_TEXT})""").matches(text) ||
                Regex("""(${Constants.AD_TEXT})\d""").matches(text) ||
                Regex("""\d(${Constants.AD_TEXT})""").matches(text) ||
                Regex("""(${Constants.AD_TEXT})\d(${Constants.AD_UNIT})""").matches(text) ||
                Regex("""(${Constants.AD_TEXT})\(\d\)""").matches(text)
            ) {
                val label = Utils.getAppLabel(MyApp.app, node.packageName)
                val time = Utils.dateString(formatter = "MM-dd HH:mm:ss")
                // val type = AccessibilityEvent.eventTypeToString(event.eventType)
                val log = """$time "$label"的广告文本："$text"，控件名："${node.className}""""
                Debug.log(Debug.I, TAG, log)
                PrefsHelper.appendADLog(log)

                Debug.log(Debug.D, TAG, """开始跳过"$label"的广告""")
                if (AccessibilityUtil.clickClickable(node)) {
                    break
                }
            }
        }
    }
}