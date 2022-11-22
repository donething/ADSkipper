package net.donething.android.adskipper.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import net.donething.android.adskipper.R
import net.donething.android.adskipper.entity.AppInfo
import net.donething.android.adskipper.utils.PrefsHelper

class AppsListAdapter(ctx: Context, private var appsList: List<AppInfo>) : BaseAdapter(),
    Filterable {
    private val mInflater = LayoutInflater.from(ctx)
    private var originAppsList = appsList

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
        holder.exclude.isChecked = appsList[position].excluded

        holder.exclude.tag = position
        // 不能使用setOnCheckedChangeListener，否则选择状态会出现混乱
        // 参考：https://www.jianshu.com/p/b10917cdfc77
        holder.exclude.setOnClickListener {
            val cb = it as CheckBox
            appsList[position].excluded = cb.isChecked
            PrefsHelper.setExcludedApp(appsList[position].pkname, cb.isChecked)
        }

        // 系统应用着色强调。注意由于复用视图，非系统应用需要手动恢复原色（默认白色）
        if (appsList[position].isSys) {
            retView.setBackgroundColor(Color.parseColor("#DDDDDD"))
        } else {
            retView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

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

    /**
     * 搜索过滤器
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val filteredArrList = ArrayList<AppInfo>()
                if (constraint == null || constraint.isEmpty()) {
                    // set the Original result to return
                    results.count = originAppsList.size
                    results.values = originAppsList
                } else {
                    originAppsList.forEach {
                        if (it.label.contains(constraint.toString())) {
                            // Product(mOriginalValues.get(i).name, mOriginalValues.get(i).price)
                            filteredArrList.add(it)
                        }
                    }
                    // set the Filtered result to return
                    results.count = filteredArrList.size
                    results.values = filteredArrList
                }
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                appsList = results?.values as List<AppInfo>
                notifyDataSetChanged()
            }
        }
    }

    // 类
    private class ViewHolder(v: View) {
        var icon: ImageView = v.findViewById(R.id.ivAppIcon)
        var label: TextView = v.findViewById(R.id.tvAppLabel)
        var launch: TextView = v.findViewById(R.id.tvappLaunch)
        var exclude: CheckBox = v.findViewById(R.id.cbExcludeApp)
    }
}