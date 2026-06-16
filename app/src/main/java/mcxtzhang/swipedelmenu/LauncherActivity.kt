package mcxtzhang.swipedelmenu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import mcxtzhang.listswipemenudemo.R
import mcxtzhang.swipedelmenu.FullDemo.FullDelDemoActivity
import mcxtzhang.swipedelmenu.viewpager.ViewPagerActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        findViewById<android.view.View>(R.id.rv).setOnClickListener {
            startActivity(Intent(it.context, FullDelDemoActivity::class.java))
        }

        findViewById<android.view.View>(R.id.lv).setOnClickListener {
            startActivity(Intent(it.context, ListViewDelDemoActivity::class.java))
        }

        findViewById<android.view.View>(R.id.ll).setOnClickListener {
            startActivity(Intent(it.context, LinearLayoutDelDemoActivity::class.java))
        }

        findViewById<android.view.View>(R.id.viewPager).setOnClickListener {
            startActivity(Intent(this@LauncherActivity, ViewPagerActivity::class.java))
        }
    }
}
