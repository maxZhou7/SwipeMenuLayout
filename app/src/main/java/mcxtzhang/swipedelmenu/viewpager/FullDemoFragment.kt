package mcxtzhang.swipedelmenu.viewpager

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcxtzhang.commonadapter.rv.CommonAdapter
import com.mcxtzhang.commonadapter.rv.ViewHolder
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import mcxtzhang.listswipemenudemo.R
import mcxtzhang.swipedelmenu.SwipeBean

class FullDemoFragment : Fragment() {
    companion object {
        private const val TAG = "zxt"

        fun newInstance(position: Int): FullDemoFragment {
            val args = Bundle()
            args.putInt("index", position)
            val fragment = FullDemoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: CommonAdapter<SwipeBean>
    private lateinit var mLayoutManager: LinearLayoutManager
    private val mDatas: MutableList<SwipeBean> = ArrayList()
    private var mIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIndex = arguments?.getInt("index") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = LayoutInflater.from(context)
            .inflate(R.layout.fragment_full_del_demo, container, false)

        mRecyclerView = inflate.findViewById(R.id.rv)
        // 区分一下不同页面
        mRecyclerView.setBackgroundColor(
            when (mIndex) {
                0 -> Color.WHITE
                1 -> Color.YELLOW
                else -> Color.GREEN
            }
        )

        initDatas()
        mAdapter = object : CommonAdapter<SwipeBean>(
            context, mDatas, R.layout.item_cst_swipe
        ) {
            override fun convert(holder: ViewHolder, swipeBean: SwipeBean) {
                (holder.itemView as SwipeMenuLayout).setIos(true)
                    .setLeftSwipe(mIndex != 0)

                holder.setText(
                    R.id.content,
                    swipeBean.name + if (mIndex == 0) "我左青龙" else "我右白虎"
                )

                // 验证长按
                holder.setOnLongClickListener(R.id.content) {
                    Toast.makeText(mContext, "longclig", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "onLongClick() called with: v = [$it]")
                    false
                }

                holder.setOnClickListener(R.id.btnDelete) {
                    val pos = holder.layoutPosition
                    if (pos in 0 until mDatas.size) {
                        Toast.makeText(context, "删除:$pos", Toast.LENGTH_SHORT).show()
                        mDatas.removeAt(pos)
                        mAdapter.notifyItemRemoved(pos)
                    }
                }

                holder.setOnClickListener(R.id.content) {
                    Toast.makeText(
                        mContext,
                        "onClick:${mDatas[holder.adapterPosition].name}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("TAG", "onClick() called with: v = [$it]")
                }

                // 置顶
                holder.setOnClickListener(R.id.btnTop) {
                    val pos = holder.layoutPosition
                    if (pos > 0 && pos < mDatas.size) {
                        val swipeBean = mDatas[pos]
                        mDatas.remove(swipeBean)
                        mAdapter.notifyItemInserted(0)
                        mDatas.add(0, swipeBean)
                        mAdapter.notifyItemRemoved(pos + 1)
                        if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
                            mRecyclerView.scrollToPosition(0)
                        }
                    }
                }
            }
        }

        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context).also { mLayoutManager = it }

        // 点击外部空白区域时，关闭正在展开的侧滑菜单
        mRecyclerView.setOnTouchListener(SwipeMenuLayout.makeOuterTouchListener())

        return inflate
    }

    private fun initDatas() {
        for (i in 0 until 20) {
            mDatas.add(SwipeBean("$i"))
        }
    }
}
