package net.donething.android.adskipper.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import net.donething.android.adskipper.entity.AppInfo
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    /**
     * 判断是否为系统应用
     */
    fun isSysApp(appInfo: ApplicationInfo): Boolean {
        return appInfo.flags and ApplicationInfo.FLAG_SYSTEM > 0
    }

    /**
     * 判断是否为系统应用
     */
    fun isSysApp(ctx: Context, pkname: CharSequence): Boolean {
        return Utils.isSysApp(ctx.packageManager.getApplicationInfo(pkname.toString(), 0))
    }

    /**
     * 获取应用名
     */
    fun getAppLabel(ctx: Context, pkname: CharSequence): String {
        val packageManager = ctx.packageManager
        val packageInfo = packageManager.getPackageInfo(pkname.toString(), PackageManager.GET_META_DATA)
        return packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
    }

    /**
     * 弹出对话框
     */
    fun buildDialog(
        ctx: Context, title: String, msg: String,
        posText: String, posMethod: DialogInterface.OnClickListener? = null,
        negText: String? = null, negMethod: DialogInterface.OnClickListener? = null
    ): Dialog {
        val builder = AlertDialog.Builder(ctx)
        builder.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(posText, posMethod)
            .setNegativeButton(negText, negMethod)
        return builder.create()
    }

    /**
     * 获取已安装应用列表
     */
    fun scanInstalledApps(ctx: Context): List<AppInfo> {
        val pm = ctx.packageManager
        val appInfos = ArrayList<AppInfo>()
        try {
            val packageInfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES)
            for (info in packageInfos) {
                //过滤掉系统app
                if (isSysApp(info.applicationInfo)) continue
                val appInfo = AppInfo(
                    info.packageName,
                    info.applicationInfo.loadIcon(pm),
                    getAppLabel(ctx, info.packageName),
                    pm.getLaunchIntentForPackage(info.packageName)?.component?.className ?: "未知的包名：${info.packageName}"
                )
                appInfos.add(appInfo)
            }
        } catch (t: Throwable) {
            Debug.log(Debug.E, "获取应用列表出错")
        }

        return appInfos
    }

    /**
     * dp转px
     * ImageView的maxWidth默认单位为px，要设置dp的值，需要先调用此方法转换
     * 参考：https://stackoverflow.com/a/35803372/8179418
     */
    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp * density)
    }

    /**
     * 格式化时间
     */
    fun dateString(date: Date = Date()): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
    }
}
