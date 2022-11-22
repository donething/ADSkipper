package net.donething.android.adskipper.tools

import android.view.accessibility.AccessibilityNodeInfo
import net.donething.android.adskipper.accessibility.AccessibilityUtil
import net.donething.android.adskipper.utils.Debug

/**
 * 移除应用内的广告
 */
object InAppAD {
    private val TAG = InAppAD::class.java.name

    fun hideAD(nodeInfo: AccessibilityNodeInfo) {
        when (nodeInfo.packageName) {
            "com.baidu.tieba_mini" -> rmTiebaMiniAD(nodeInfo)
            "com.hupu.games" -> rmHupuAD(nodeInfo)
            // "com.zhihu.android" -> rmZhihu(nodeInfo)
        }
    }

    /**
     * 移除贴吧简版的广告
     */
    private fun rmTiebaMiniAD(nodeInfo: AccessibilityNodeInfo) {
        // 点击后，弹出关闭广告提示框
        clickRmByID(nodeInfo, "com.baidu.tieba_mini:id/mq")

        // 点击提示框后，真正关闭广告
        clickRmByID(nodeInfo, "com.baidu.tieba_mini:id/ma")
    }

    /**
     * 移除虎扑的广告
     */
    private fun rmHupuAD(nodeInfo: AccessibilityNodeInfo) {
        clickRmByID(nodeInfo, "com.hupu.games:id/close_small_adv_r_btn")
        clickRmByID(nodeInfo, "com.hupu.games:id/close_adv_l_btn")
        clickRmByID(nodeInfo, "com.hupu.games:id/clear_video_btn")
    }

    /**
     * 移除知乎广告
     */
    private fun rmZhihu(nodeInfo: AccessibilityNodeInfo) {
        // 弹出关闭广告提示框
        nodeInfo.findAccessibilityNodeInfosByViewId("com.zhihu.android:id/menu").forEach {
            if (it.parent != null &&
                it.parent.childCount >= 1 &&
                it.parent.getChild(0) != null &&
                it.parent.getChild(0).childCount >= 1 &&
                it.parent.getChild(0).getChild(0) != null
            ) {
                Debug.log(Debug.D, TAG, "知乎广告：${it.parent.getChild(0).getChild(0).text}")
                if (it.parent.getChild(0).getChild(0).text == "广告") {
                    AccessibilityUtil.clickClickable(it)
                }
            }
        }
        // 关闭广告
        nodeInfo.findAccessibilityNodeInfosByViewId("com.zhihu.android:id/title").forEach {
            if (it.text == "不感兴趣") {
                AccessibilityUtil.clickClickable(it)
            }
        }
    }


    /**
     * 根据ID查找，点击移除广告
     */
    private fun clickRmByID(nodeInfo: AccessibilityNodeInfo, id: String) {
        nodeInfo.findAccessibilityNodeInfosByViewId(id).forEach {
            AccessibilityUtil.clickClickable(it)
        }
    }
}