package net.donething.android.adskipper.task

import android.content.pm.PackageManager
import android.os.AsyncTask
import android.widget.BaseAdapter
import net.donething.android.adskipper.MyApp
import net.donething.android.adskipper.entity.AppInfo
import net.donething.android.adskipper.fragments.AppsFragment
import net.donething.android.adskipper.interfaces.OnDataSendToActivity
import net.donething.android.adskipper.utils.PrefsHelper
import java.text.Collator

class GetAppsTask(
    private val appsList: MutableList<AppInfo>, private val adapter: BaseAdapter, fg: AppsFragment
) : AsyncTask<String, AppInfo, String>() {
    companion object {
        // 任务是否正在进行，是的话将不执行任务，避免交叉更改appsList出现的异常
        var isDoing = false
    }

    // 临时保存应用信息
    private val tmpAppsList = mutableListOf<AppInfo>()
    private val dataSendToActivity = fg as OnDataSendToActivity

    // 被排除的应用排在前面，再按中文名称排序
    // 参考：https://juejin.im/entry/58ccb36b570c3500589c8809
    private val com = Collator.getInstance(java.util.Locale.CHINA)
    private val c1: Comparator<AppInfo> = Comparator { o1, o2 ->
        if (o2.excluded == o1.excluded) {
            com.compare(o1.label, o2.label)
        } else {
            if (o1.excluded) {
                -1
            } else {
                1
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPreExecute() {
        isDoing = true
        appsList.clear()
        tmpAppsList.clear()

    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: String?): String {
        val ctx = MyApp.app
        val appsInfos = ctx.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        appsInfos.forEach {
            //过滤掉系统app
            // if (Utils.isSysApp(it)) return@forEach
            val appInfo = AppInfo(
                it.packageName,
                it.loadIcon(ctx.packageManager),
                it.loadLabel(ctx.packageManager).toString(),
                ctx.packageManager.getLaunchIntentForPackage(it.packageName)?.component?.className
                    ?: "未知的包名：${it.packageName}",
                PrefsHelper.isExcludedApp(it.packageName)
            )
            tmpAppsList.add(appInfo)
        }
        return ""
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: String?) {
        // 按中文排序
        tmpAppsList.sortWith(c1)

        appsList.addAll(tmpAppsList)
        adapter.notifyDataSetChanged()

        isDoing = false
        dataSendToActivity.sendData("显示完毕，隐藏进度条")
    }
}