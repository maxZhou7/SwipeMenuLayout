package com.mcxtzhang.swipemenulib

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * Intro: 解决滑动冲突的 ViewPager
 *
 * 因为ViewPager 和 SwipeMenuLayout都是水平方向滑动的控件。
 * 所以在一起使用时会有冲突，
 * 使用本控件(CstViewPager)，可以在ViewPager的第一页使用左滑。在ViewPager的最后一页使用右滑菜单。
 *
 * Author: zhangxutong
 * E-mail: mcxtzhang@163.com
 * Created: 2017/9/27.
 */
class CstViewPager : ViewPager {

    private var mLastX = 0
    private var mLastY = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x.toInt()
        val y = ev.y.toInt()
        var intercept = false
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_MOVE -> {
                if (isHorizontalScroll(x, y)) {
                    // 除了在第一页的手指向右滑，最后一页的左滑，其他时刻都是父控件需要拦截事件
                    intercept = when {
                        isReachFirstPage() && isScrollRight(x) -> false    // 第一页右滑：不拦截
                        isReachLastPage() && isScrollLeft(x) -> false      // 最后一页左滑：不拦截
                        else -> true                                        // 其他情况：拦截
                    }
                }
            }
            else -> {}
        }

        mLastX = x
        mLastY = y

        val superIntercept = super.onInterceptTouchEvent(ev)
        return intercept || superIntercept
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean = super.onTouchEvent(ev)

    private fun isHorizontalScroll(x: Int, y: Int): Boolean =
        abs(y - mLastY) < abs(x - mLastX)

    private fun isReachLastPage(): Boolean {
        val adapter = adapter ?: return false
        return adapter.count - 1 == currentItem
    }

    private fun isReachFirstPage(): Boolean = currentItem == 0

    private fun isScrollLeft(x: Int): Boolean = x - mLastX < 0
    private fun isScrollRight(x: Int): Boolean = x - mLastX > 0
}
