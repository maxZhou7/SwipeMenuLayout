package mcxtzhang.swipedelmenu.viewpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import mcxtzhang.listswipemenudemo.R

class ViewPagerActivity : AppCompatActivity() {
    private lateinit var mViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        mViewPager = findViewById(R.id.viewPager)
        mViewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment =
                FullDemoFragment.newInstance(position)

            override fun getCount(): Int = 3
        }
    }
}
