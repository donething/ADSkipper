package net.donething.android.adskipper

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import net.donething.android.adskipper.accessibility.AccessibilityUtil
import net.donething.android.adskipper.accessibility.MyAccessibilityService
import net.donething.android.adskipper.fragments.LogFragment
import net.donething.android.adskipper.utils.Utils

class MainActivity : AppCompatActivity() {
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_logs -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // 不能放在onStart()中
        if (!AccessibilityUtil.isAccessibilityEnabled(MyAccessibilityService::class.java, this)) {
            Utils.buildDialog(
                this, "打开 无障碍界面", "跳过广告功能需要激活本应用的'无障碍'开关",
                "去打开", DialogInterface.OnClickListener { _, _ -> AccessibilityUtil.openSetting(this) },
                "取消"
            ).show()
        }

        supportFragmentManager.beginTransaction().replace(R.id.content_main, LogFragment(), "log_fragment").commit()
    }
}
