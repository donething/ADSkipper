package net.donething.android.adskipper.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import net.donething.android.adskipper.utils.Debug
import net.donething.android.adskipper.tools.InAppAD
import net.donething.android.adskipper.tools.LaunchAD

class MyAccessibilityService : AccessibilityService() {
    companion object {
        private val TAG = MyAccessibilityService::class.java.name
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Debug.log(Debug.D, "无障碍事件被触发")
        // Debug.log(Debug.D, "无障碍事件的类型：${AccessibilityEvent.eventTypeToString(event.eventType)}")

        // 跳过系统应用和本应用
        /*
        if (Utils.isSysApp(this, event.packageName) || event.packageName == BuildConfig.APPLICATION_ID) {
            Debug.log(Debug.D, TAG, "跳过系统应用和本应用")
            return
        }
         */

        event.eventType

        val eventNode = event.source
        if (eventNode == null) {
            Debug.log(Debug.D, TAG, "事件源为null")
            return
        }

        val root = rootInActiveWindow
        if (root == null) {
            Debug.log(Debug.D, TAG, "获取的激活的窗口为null")
            return
        }

        // 跳过启动广告
        LaunchAD.skipAD(root)

        // 去除应用内的广告
        InAppAD.hideAD(root)

        // 回收节点实例来重用
        eventNode.recycle()
        root.recycle()
    }

    override fun onInterrupt() {}
}