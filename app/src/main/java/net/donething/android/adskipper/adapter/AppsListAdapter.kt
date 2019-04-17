package net.donething.android.adskipper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import net.donething.android.adskipper.R
import net.donething.android.adskipper.entity.AppInfo

class AppsListAdapter(ctx: Context, private val appsList: List<AppInfo>) : BaseAdapter() {
    private val mInflater = LayoutInflater.from(ctx)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val retView: View

        if (convertView == null) {
            retView = mInflater.inflate(R.layout.app_item_layout, parent, false)
            holder = ViewHolder(retView)
            retView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            retView = convertView
        }
        holder.icon.setImageDrawable(appsList[position].icon)
        holder.label.text = appsList[position].label
        holder.launch.text = appsList[position].launch

        return retView
    }

    override fun getItem(position: Int): Any {
        return appsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return appsList.size
    }
}

//在外面先定义，ViewHolder静态类
private class ViewHolder(v: View) {
    var icon: ImageView = v.findViewById(R.id.ivAppIcon)
    var label: TextView = v.findViewById(R.id.tvAppLabel)
    var launch: TextView = v.findViewById(R.id.tvappLaunch)
}