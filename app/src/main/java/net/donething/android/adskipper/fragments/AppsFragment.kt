package net.donething.android.adskipper.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.donething.android.adskipper.R
import net.donething.android.adskipper.adapter.AppsListAdapter
import net.donething.android.adskipper.entity.AppInfo
import net.donething.android.adskipper.task.getAppsList

class AppsFragment : Fragment() {
    companion object {
        private val TAG = AppsFragment::class.java.name
    }

    /**
     * 应用列表
     */
    val appsList = mutableListOf<AppInfo>()

    /**
     * 应用列表数据适配器
     */
    lateinit var adapter: AppsListAdapter

    /**
     * 进度条布局
     */
    lateinit var llProgress: LinearLayout

    /**
     * 进度条
     */
    lateinit var progressBar: ProgressBar

    /**
     * 进度条的文本进度
     */
    lateinit var txtProgress: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        llProgress = view.findViewById(R.id.llProgress)
        progressBar = view.findViewById(R.id.pbGetApps)
        txtProgress = view.findViewById(R.id.txtProgress)

        // 显示应用列表
        adapter = AppsListAdapter(ctx, appsList)
        val lvApps = view.findViewById<ListView>(R.id.lvApps)
        lvApps.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()
        getAppsList()
    }
}