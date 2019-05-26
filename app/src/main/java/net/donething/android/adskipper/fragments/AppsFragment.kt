package net.donething.android.adskipper.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import net.donething.android.adskipper.R
import net.donething.android.adskipper.adapter.AppsListAdapter
import net.donething.android.adskipper.entity.AppInfo
import net.donething.android.adskipper.interfaces.OnDataSendToActivity
import net.donething.android.adskipper.task.GetAppsTask
import net.donething.android.adskipper.utils.Debug

class AppsFragment : Fragment(), OnDataSendToActivity {
    companion object {
        private val TAG = AppsFragment::class.java.name
    }

    private val appsList = mutableListOf<AppInfo>() // 应用列表
    private lateinit var adapter: AppsListAdapter   // 应用列表数据适配器
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_apps_list, container, false)
        val ctx = context
        ctx ?: return view

        // 搜索应用
        val etSearch = view.findViewById<EditText>(R.id.etAppsSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
        progressBar = view.findViewById(R.id.pbGetApps)

        // 显示应用列表
        adapter = AppsListAdapter(ctx, appsList)
        val lvApps = view.findViewById<ListView>(R.id.lvApps)
        lvApps.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()
        displayInstalledApps()
    }

    /**
     * 显示应用列表
     */
    private fun displayInstalledApps() {
        // 同一时间，只能有一个获取应用列表的任务，否则出现交叉更改appsList会报错
        if (GetAppsTask.isDoing) {
            Debug.log(Debug.D, TAG, "获取应用列表的任务正在后台执行")
        } else {
            GetAppsTask(appsList, adapter, this).execute()
        }
    }

    // AsyncTask完成任务后，调用
    override fun sendData(str: String) {
        progressBar.visibility = ProgressBar.GONE
    }
}