package net.donething.android.adskipper.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import net.donething.android.adskipper.BuildConfig
import net.donething.android.adskipper.utils.Constants
import net.donething.android.adskipper.utils.Debug
import net.donething.android.adskipper.utils.PrefsHelper
import net.donething.android.adskipper.utils.Utils

class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Debug.log(Debug.D, "无障碍事件被触发")
        if (event == null) {
            Debug.log(Debug.D, "事件为null")
            return
        }

        // 跳过系统应用和本应用
        if (Utils.isSysApp(this, event.packageName) || event.packageName == BuildConfig.APPLICATION_ID) {
            Debug.log(Debug.D, "跳过系统应用和本应用")
            return
        }

        val eventNode = event.source
        if (eventNode == null) {
            Debug.log(Debug.D, "事件源为null")
            return
        }

        // 只检测TextView类型
        if (eventNode.className != TextView::class.java.name) {
            Debug.log(Debug.D, "事件源的类不为TextView")
            return
        }

        val root = rootInActiveWindow
        if (root == null) {
            Debug.log(Debug.D, "获取的激活的窗口为null")
            return
        }

        findClickText(root, "跳过")

        // 回收节点实例来重用
        eventNode.recycle()
        root.recycle()
    }

    private fun findClickText(nodeInfo: AccessibilityNodeInfo, txt: String) {
        // findAccessibilityNodeInfosByText()使用类似"contain"的方式，而不是"equals"的方式来比较
        val nodes = nodeInfo.findAccessibilityNodeInfosByText(txt)
        if (nodes == null || nodes.isEmpty())
            return
        // 记录广告文本、广告控件类型到日志文件
        for (node in nodes) {
            // 如果需要更详细的比较，可以在这里使用：node.text=="some string"
            // 去除广告文本中的空格、换行
            val text = node.text.toString().replace(" ", "").replace("\n", "")

            // 不要用可有选项（"?"），否则匹配的内容太多，会误判
            // val pattern = """(${Constants.AD_TEXT})\d?(${Constants.AD_UNIT})?"""
            if (Regex("""(${Constants.AD_TEXT})""").matches(text) ||
                Regex("""(${Constants.AD_TEXT})\d""").matches(text) ||
                Regex("""\d(${Constants.AD_TEXT})""").matches(text) ||
                Regex("""(${Constants.AD_TEXT})\d(${Constants.AD_UNIT})""").matches(text)
            ) {
                val label = Utils.getAppLabel(this, node.packageName)
                val log = """${Utils.dateString()} "$label"的广告文本："$text"，控件名："${node.className}""""
                Debug.log(Debug.I, log)
                PrefsHelper.appendADInfo(log)
                if (clickClickable(node)) {
                    break
                }
            }
        }
    }

    /**
     * 向上查找可点击的控件，并点击
     */
    private fun clickClickable(node: AccessibilityNodeInfo?): Boolean {
        node ?: return false
        if (node.isClickable) {
            Debug.log(Debug.D, "开始跳过 ${Utils.getAppLabel(this, node.packageName)} 的广告")
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }
        return clickClickable(node.parent)
    }

    override fun onInterrupt() {}
}
