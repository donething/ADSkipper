package net.donething.android.adskipper.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import net.donething.android.adskipper.MyApp
import net.donething.android.adskipper.R
import net.donething.android.adskipper.utils.PrefsHelper
import net.donething.android.adskipper.utils.Utils

class LogsFragment : Fragment() {
    // 日志列表
    private val logsList = mutableListOf<String>()
    private lateinit var lvLog: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        adapter = ArrayAdapter(requireContext(), R.layout.log_item_layout, R.id.etLogItem, logsList)
        val view = inflater.inflate(R.layout.fragment_log, container, false)
        lvLog = view.findViewById(R.id.lvLog)
        lvLog.adapter = adapter

        // 长按复制文本
        lvLog.setOnItemLongClickListener { _, v, _, _ ->
            // 注意`Item`指的是`log_item_layout.xml`中的布局，需要提取里面的组件（此种情况为其第一个元素）
            val tv = (v as ViewGroup)[0] as TextView
            Utils.copyText(tv.text.toString())
            true
        }

        val bnScrollDown = view.findViewById<ImageButton>(R.id.bnLogScrollDown)
        val bnScrollUp = view.findViewById<ImageButton>(R.id.bnLogScrollUp)
        val bnCopyLog = view.findViewById<ImageButton>(R.id.bnLogCopy)
        val bnClearLog = view.findViewById<ImageButton>(R.id.bnLogDel)

        bnScrollDown.setOnClickListener {
            lvLog.setSelection(logsList.size - 1)
        }
        bnScrollUp.setOnClickListener {
            lvLog.setSelection(0)
        }

        // 复制日志
        bnCopyLog.setOnClickListener {
            Utils.copyText(logsList.joinToString("\n"))
        }

        // 清除日志
        bnClearLog.setOnClickListener {
            val ac = activity
            ac ?: return@setOnClickListener
            Utils.buildDialog(
                ac, "确认", "清除广告日志", "清除日志",
                { _, _ ->
                    if (PrefsHelper.clearADLog()) {
                        logsList.clear()
                        adapter.notifyDataSetChanged()
                        Toast.makeText(MyApp.app, "日志清除成功", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(MyApp.app, "日志清除失败", Toast.LENGTH_SHORT).show()
                    }
                },
                "取消"
            ).show()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // 显示日志
        fillAllLogs()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            // 显示日志
            fillAllLogs()
        }
    }

    /**
     * 显示所有日志
     */
    private fun fillAllLogs() {
        val logs = PrefsHelper.readADLog()
        logsList.clear()
        logs.forEach {
            logsList.add(it)
        }
        adapter.notifyDataSetChanged()
        lvLog.setSelection(logsList.size - 1)
    }
}