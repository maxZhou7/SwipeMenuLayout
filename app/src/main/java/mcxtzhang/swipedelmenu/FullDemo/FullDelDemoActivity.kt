package mcxtzhang.swipedelmenu.FullDemo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import mcxtzhang.listswipemenudemo.R
import mcxtzhang.swipedelmenu.SwipeBean

class FullDelDemoActivity : Activity() {
    private val TAG = "zxt"
    private lateinit var mRv: RecyclerView
    private lateinit var mAdapter: FullDelDemoAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private val mDatas: MutableList<SwipeBean> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_del_demo)
        mRv = findViewById(R.id.rv)

        initDatas()
        mAdapter = FullDelDemoAdapter(this, mDatas)
        mAdapter.onSwipeListener = object : FullDelDemoAdapter.OnSwipeListener {
            override fun onDel(pos: Int) {
                if (pos in 0 until mDatas.size) {
                    Toast.makeText(this@FullDelDemoActivity, "删除:$pos", Toast.LENGTH_SHORT).show()
                    mDatas.removeAt(pos)
                    mAdapter.notifyItemRemoved(pos)
                }
            }

            override fun onTop(pos: Int) {
                if (pos > 0 && pos < mDatas.size) {
                    val swipeBean = mDatas[pos]
                    mDatas.remove(swipeBean)
                    mAdapter.notifyItemInserted(0)
                    mDatas.add(0, swipeBean)
                    mAdapter.notifyItemRemoved(pos + 1)
                    if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
                        mRv.scrollToPosition(0)
                    }
                }
            }
        }
        mRv.adapter = mAdapter
        mRv.layoutManager = GridLayoutManager(this, 1).also { mLayoutManager = it }

        // 点击外部空白区域时，关闭正在展开的侧滑菜单
        mRv.setOnTouchListener(SwipeMenuLayout.makeOuterTouchListener())
    }

    private fun initDatas() {
        for (i in 0 until 5) {
            mDatas.add(SwipeBean("$i"))
        }
    }
}
