package net.donething.android.adskipper

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import net.donething.android.adskipper.accessibility.AccessibilityUtil
import net.donething.android.adskipper.accessibility.MyAccessibilityService
import net.donething.android.adskipper.fragments.AppsFragment
import net.donething.android.adskipper.fragments.LogsFragment
import net.donething.android.adskipper.fragments.PrefFragment
import net.donething.android.adskipper.utils.PrefsHelper
import net.donething.android.adskipper.utils.Utils

class MainActivity : AppCompatActivity() {
    private var logsFragment: LogsFragment? = null
    private var appsFragment: AppsFragment? = null
    private var prefFragment: PrefFragment? = null

    private var swStatus: SwitchCompat? = null

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_logs -> {
                    if (logsFragment == null) {
                        logsFragment = LogsFragment()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_main, logsFragment!!, "logs_fragment")
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_apps -> {
                    if (appsFragment == null) {
                        appsFragment = AppsFragment()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_main, appsFragment!!, "apps_fragment")
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_settings -> {
                    if (prefFragment == null) {
                        prefFragment = PrefFragment()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_main, prefFragment!!, "pref_fragment")
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(my_toolbar)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        supportFragmentManager.beginTransaction()
            .replace(R.id.content_main, LogsFragment(), "log_fragment").commit()
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

        swStatus = menuStatus.actionView.findViewById(R.id.swStatus)
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
                DialogInterface.OnClickListener { _, _ -> AccessibilityUtil.openSetting(this) },
                "取消",
                DialogInterface.OnClickListener { _, _ -> swStatus?.isChecked = false }
            ).show()
        }
    }
}