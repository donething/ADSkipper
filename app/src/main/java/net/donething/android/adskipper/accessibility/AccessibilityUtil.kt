package net.donething.android.adskipper.accessibility

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import net.donething.android.adskipper.utils.Debug

// 辅助功能
// 参考：https://blog.csdn.net/qq_32115439/article/details/80261568
object AccessibilityUtil {
    /**
     * 检查系统设置：是否开启辅助服务
     * @param service 辅助服务
     */
    fun isAccessibilityEnabled(service: Class<*>, cxt: Context): Boolean {
        try {
            val enable = Settings.Secure.getInt(cxt.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED, 0)
            if (enable != 1) return false
            val services =
                Settings.Secure.getString(cxt.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (!TextUtils.isEmpty(services)) {
                val split = TextUtils.SimpleStringSplitter(':')
                split.setString(services)
                while (split.hasNext()) { // 遍历所有已开启的辅助服务名
                    if (split.next().equals(cxt.packageName + "/" + service.name, ignoreCase = true))
                        return true
                }
            }
        } catch (e: Throwable) {//若出现异常，则说明该手机设置被厂商篡改了,需要适配
            Debug.log(Debug.E, "检测是否打开无障碍功能时出错：$e")
        }
        return false
    }

    /**
     * 跳转到系统设置：开启辅助服务
     */
    fun openSetting(ctx: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(intent)
        } catch (e: Throwable) {//若出现异常，则说明该手机设置被厂商篡改了,需要适配
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
            } catch (e2: Throwable) {
                Debug.log(Debug.E, "跳转到无障碍功能界面时出错：$e")
            }

        }
    }
}