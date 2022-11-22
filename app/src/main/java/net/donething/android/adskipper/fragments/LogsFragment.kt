package net.donething.android.adskipper.fragments

import android.content.Context
import android.content.DialogInterface
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
    private val logsList = mutableListOf<String>() // 日志列表
    private lateinit var lvLog: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        adapter =
            ArrayAdapter(activity as Context, R.layout.log_item_layout, R.id.etLogItem, logsList)
        val view = inflater.inflate(R.layout.fragment_log, container, false)
        lvLog = view.findViewById(R.id.lvLog)
        lvLog.adapter = adapter
        lvLog.setOnItemClickListener { _, v, _, _ ->
            val tv = (v as ViewGroup)[0] as TextView
            Utils.copyText(tv.text.toString())
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

    override fun onStart() {
        super.onStart()

        // 显示日志
        val v = view
        v ?: return
        fillAllLogs()
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