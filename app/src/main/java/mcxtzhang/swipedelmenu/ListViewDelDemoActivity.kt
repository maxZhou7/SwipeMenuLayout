package mcxtzhang.swipedelmenu

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter
import com.mcxtzhang.commonadapter.lvgv.ViewHolder
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import mcxtzhang.listswipemenudemo.R

class ListViewDelDemoActivity : AppCompatActivity() {
    private val TAG = "zxt"
    private lateinit var mLv: ListView
    private val mDatas: MutableList<SwipeBean> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mLv = findViewById(R.id.test)

        initDatas()
        mLv.adapter = object : CommonAdapter<SwipeBean>(
            this, mDatas, R.layout.item_cst_swipe
        ) {
            override fun convert(holder: ViewHolder, swipeBean: SwipeBean, position: Int) {
                holder.setText(R.id.content, swipeBean.name)
                holder.setOnClickListener(R.id.content) {
                    Toast.makeText(this@ListViewDelDemoActivity, "position:$position", Toast.LENGTH_SHORT).show()
                }

                holder.setOnClickListener(R.id.btnDelete) {
                    Toast.makeText(this@ListViewDelDemoActivity, "删除:$position", Toast.LENGTH_SHORT).show()
                    (holder.convertView as SwipeMenuLayout).quickClose()
                    mDatas.removeAt(position)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun initDatas() {
        for (i in 0 until 20) {
            mDatas.add(SwipeBean("$i"))
        }
    }
}
