package net.donething.android.adskipper

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import net.donething.android.adskipper.accessibility.AccessibilityUtil
import net.donething.android.adskipper.accessibility.MyAccessibilityService
import net.donething.android.adskipper.databinding.ActivityMainBinding
import net.donething.android.adskipper.fragments.AppsFragment
import net.donething.android.adskipper.fragments.LogsFragment
import net.donething.android.adskipper.fragments.PrefFragment
import net.donething.android.adskipper.utils.PrefsHelper
import net.donething.android.adskipper.utils.Utils

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    /**
     * 是否开启功能
     */
    private var swStatus: SwitchCompat? = null

    /**
     * 当前显示的页面
     */
    private var currentFg: Fragment? = null

    /**
     * 已实例化的页面，以供显示
     * 键值为`页面布局文件的ID`、`页面实例`
     */
    private val fgMap = mutableMapOf<Int, Fragment>()

    /**
     * 根据设置的默认页面，选中对应的导航栏菜单项
     * 键值为：`页面布局文件的ID`、`导航栏菜单项的ID`
     */
    private val navMap = mapOf(
        R.layout.fragment_log to R.id.navigation_logs,
        R.layout.fragment_apps_list to R.id.navigation_apps,
        R.xml.preferences to R.id.navigation_settings
    )

    /**
     * 显示指定页面
     * @param id 页面布局文件的ID。如 `R.layout.fragment_apps_list`、`R.xml.preferences`
     */
    private fun toFragment(id: Int) {
        // 用于根据默认页面，选中对应的导航栏菜单项
        var correctID = id
        // 先从实例映射中读取页面实例，为空时需要实例化页面，并保存到映射中
        var fg = fgMap[id]
        if (fg == null) {
            fg = when (id) {
                R.layout.fragment_log -> LogsFragment()
                R.layout.fragment_apps_list -> AppsFragment()
                R.xml.preferences -> PrefFragment()
                // 默认页面、对应的导航栏菜单项
                else -> {
                    correctID = R.layout.fragment_apps_list
                    AppsFragment()
                }
            }

            // 此处不能用 ID，需要使用修正后的 correct ID，避免当返回默认页面时ID错误
            fgMap[correctID] = fg
        }

        // 开启事务，显示页面
        val tr = supportFragmentManager.beginTransaction()
        // 没有被添加到过activity的页面，需要先添加
        if (!fg.isAdded) {
            tr.add(R.id.content_main, fg, fg::class.java.name)
        }
        // 当前页面不为空时，先隐藏
        if (currentFg != null) {
            tr.hide(currentFg!!)
        }
        // 显示目标页面
        tr.show(fg)
        tr.commit()

        // 选中对应页面的导航栏菜单项
        // 不能使用`binding.navigation.selectedItemId`，会报错
        // @link https://stackoverflow.com/a/43246106/8179418
        binding.navigation.menu.findItem(navMap[correctID]!!).isChecked = true

        currentFg = fg
    }

    private val mOnNavigationItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_logs -> {
                    toFragment(R.layout.fragment_log)
                    return@OnItemSelectedListener true
                }
                R.id.navigation_apps -> {
                    toFragment(R.layout.fragment_apps_list)
                    return@OnItemSelectedListener true
                }
                R.id.navigation_settings -> {
                    toFragment(R.xml.preferences)
                    return@OnItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        binding.navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener)

        // 默认页面
        toFragment(0)
    }

    override fun onStart() {
        super.onStart()
        swStatus?.isChecked = PrefsHelper.enable
        if (PrefsHelper.enable) checkAccessibility()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val menuStatus = menu.findItem(R.id.menuStatus)
        menuStatus.setActionView(R.layout.menu_items)

        swStatus = menuStatus.actionView?.findViewById(R.id.swStatus)
        swStatus?.isChecked = PrefsHelper.enable

        swStatus?.setOnCheckedChangeListener { _, isChecked ->
            PrefsHelper.enable = isChecked
            if (PrefsHelper.enable) checkAccessibility()
        }
        return true
    }

    /**
     * 检测“无障碍”是否开启
     */
    private fun checkAccessibility() {
        // 不能放在onStart()中
        if (!AccessibilityUtil.isAccessibilityEnabled(MyAccessibilityService::class.java, this)) {
            Utils.buildDialog(
                this,
                "打开 无障碍界面",
                "跳过广告功能需要激活本应用的'无障碍'开关",
                "去打开",
                { _, _ -> AccessibilityUtil.openSetting(this) },
                "取消",
                { _, _ -> swStatus?.isChecked = false }
            ).show()
        }
    }
}