package net.donething.android.adskipper.task

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import net.donething.android.adskipper.R
import net.donething.android.adskipper.entity.AppInfo
import net.donething.android.adskipper.fragments.AppsFragment
import net.donething.android.adskipper.utils.Debug
import net.donething.android.adskipper.utils.PrefsHelper
import java.text.Collator

/**
 * 正在执行任务，避免重复执行任务
 */
private var isDoing = false

/**
 * 按中文排序
 * 被排除的应用排在前面，再按中文名称排序，依次为：数字、中文、英文
 * @link https://juejin.im/entry/58ccb36b570c3500589c8809
 */
private val com = Collator.getInstance(java.util.Locale.CHINA)

/**
 * 排序
 * 被排除的应用排在前面
 */
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

fun AppsFragment.getAppsList() {
    if (isDoing) {
        Debug.log(Debug.D, "GetAppsList", "正在获取应用列表，无需重复执行")
        return
    }

    /**
     * 已获取到应用的信息列表
     */
    var appsInfos = mutableListOf<ApplicationInfo>()
    val pm = context!!.packageManager

    lifecycleScope.executeAsyncTask(onPreExecute = {
        // ... runs in Main Thread
        isDoing = true
        appsList.clear()

        appsInfos = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
        } else {
            pm.getInstalledApplications(
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            )
        }

        // 设置进度条
        progressBar.progress = 0
        progressBar.max = appsInfos.size
        txtProgress.text = getString(R.string.pb_progress, 0, appsInfos.size)
        llProgress.visibility = LinearLayout.VISIBLE
    }, doInBackground = { publishProgress: suspend (progress: Int) -> Unit ->
        // ... runs in Worker(Background) Thread
        appsInfos.forEachIndexed { index, it ->
            //过滤掉系统app
            // if (Utils.isSysApp(it)) return@forEach

            // 过滤不含启动Activity的应用
            if (pm.getLaunchIntentForPackage(it.packageName) == null) {
                publishProgress(index + 1)
                Debug.log(Debug.D, "", "直接排除不含启动Activity的应用：${it.packageName}")
                return@forEachIndexed
            }

            val appInfo = AppInfo(
                it.packageName,
                it.loadIcon(pm),
                it.loadLabel(pm).toString(),
                pm.getLaunchIntentForPackage(it.packageName)!!.component!!.className,
                PrefsHelper.isExcludedApp(it.packageName)
            )

            // 按序插入
            // @link https://stackoverflow.com/a/70619177/8179418
            var i = appsList.binarySearch(appInfo, c1)
            if (i < 0) {
                i = -(i + 1)
            }
            // 注意用 add()，使用set()会导致 out of index
            appsList.add(i, appInfo)

            publishProgress(index + 1)

            // 延时，方便看清读取应用的过程
            // delay(100)
        }
        "Result" // send data to "onPostExecute"
    }, onPostExecute = {
        // runs in Main Thread
        // ... here "it" is the data returned from "doInBackground"
        adapter.notifyDataSetChanged()

        isDoing = false
        llProgress.visibility = LinearLayout.GONE
    }, onProgressUpdate = {
        progressBar.progress = it
        txtProgress.text = getString(R.string.pb_progress, it, appsInfos.size)

        adapter.notifyDataSetChanged()
    })
}