package net.donething.android.adskipper.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import net.donething.android.adskipper.R
import net.donething.android.adskipper.utils.PrefsHelper

class LogFragment : Fragment() {
    private val logsList = mutableListOf<String>() // 日志列表
    private lateinit var lvLog: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        adapter = ArrayAdapter(activity as Context, R.layout.log_item_layout, R.id.etLogItem, logsList)
        val view = inflater.inflate(R.layout.fragment_log, container, false)
        lvLog = view.findViewById(R.id.lvLog)
        lvLog.adapter = adapter

        val bnScrollDown = view.findViewById<Button>(R.id.bnLogScrollDown)
        val bnScrollUp = view.findViewById<Button>(R.id.bnLogScrollUp)

        bnScrollDown.setOnClickListener {
            lvLog.setSelection(logsList.size - 1)
        }
        bnScrollUp.setOnClickListener {
            lvLog.setSelection(0)
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
        val logs = PrefsHelper.readADInfo()
        logsList.clear()
        logs.forEach {
            logsList.add(it)
        }
        adapter.notifyDataSetChanged()
        lvLog.setSelection(logsList.size - 1)
    }
}
