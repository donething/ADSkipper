package net.donething.android.adskipper.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.donething.android.adskipper.MyApp
import net.donething.android.adskipper.R
import net.donething.android.adskipper.adapter.AppsListAdapter
import net.donething.android.adskipper.entity.AppInfo
import net.donething.android.adskipper.utils.Debug
import net.donething.android.adskipper.utils.Utils

class AppsListFragment : Fragment() {
    private val appsList = mutableListOf<AppInfo>() // 应用列表
    private lateinit var adapter: AppsListAdapter   // 应用列表数据适配器

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_apps_list, container, false)
        val ctx = context
        if (ctx != null) {
            adapter = AppsListAdapter(ctx, appsList)
            val lvApps = view.findViewById<ListView>(R.id.lvApps)
            lvApps.adapter = adapter
            // MyAsyncTask(appsList, adapter).execute()
            displayInstalledApps()
        } else {
            Debug.log(Debug.W, "显示应用列表时出错，context为null")
        }

        return view
    }

    /**
     * 显示应用列表
     */
    private fun displayInstalledApps() {
        val ctx = context
        ctx ?: return
        val packageInfos = ctx.packageManager.getInstalledPackages(0)
        var i = 0
        for (info in packageInfos) {
            //过滤掉系统app
            if (Utils.isSysApp(info.applicationInfo)) continue
            val appInfo = AppInfo(
                info.packageName,
                info.applicationInfo.loadIcon(ctx.packageManager),
                Utils.getAppLabel(MyApp.app, info.packageName),
                ctx.packageManager.getLaunchIntentForPackage(info.packageName)?.component?.className
                    ?: "未知的包名：${info.packageName}"
            )
            appsList.add(appInfo)
            i++
            if (i % 20 == 0) {
                adapter.notifyDataSetChanged()
            }
        }
        adapter.notifyDataSetChanged()
        Toast.makeText(MyApp.app, "所有用户应用显示完毕", Toast.LENGTH_SHORT).show()
    }
}
