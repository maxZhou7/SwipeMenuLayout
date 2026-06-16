package com.mcxtzhang.swipemenulib

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator

/**
 * 【Item侧滑删除菜单】
 * 继承自ViewGroup，实现滑动出现删除等选项的效果，
 *
 * Created by zhangxutong .
 * Date: 16/04/24
 */
class SwipeMenuLayout : ViewGroup {

    companion object {
        private const val TAG = "zxt/SwipeMenuLayout"

        // 存储的是当前正在展开的View
        @JvmField
        var mViewCache: SwipeMenuLayout? = null

        // 防止多只手指一起滑我的flag
        @JvmField
        var isTouching = false

        // 当有另一个侧滑菜单正在关闭时，拦截其他View的触摸事件，实现"优先收起"的效果
        @JvmField
        @Volatile
        var sIsAnotherMenuClosing = false

        /**
         * 返回ViewCache
         */
        @JvmStatic
        fun getViewCache(): SwipeMenuLayout? = mViewCache

        /**
         * 给外层容器（RecyclerView/ListView/ScrollView等）设置触摸监听，
         * 实现点击空白区域自动关闭展开的侧滑菜单。
         * 用法：parentView.setOnTouchListener(SwipeMenuLayout.makeOuterTouchListener())
         */
        @JvmStatic
        fun makeOuterTouchListener(): OnTouchListener = OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mViewCache?.smoothClose()
            }
            false
        }
    }

    private var mScaleTouchSlop = 0   // 为了处理单击事件的冲突
    private var mMaxVelocity = 0      // 计算滑动速度用
    private var mPointerId = 0        // 多点触摸只算第一根手指的速度
    private var mHeight = 0           // 自己的高度
    private var mRightMenuWidths = 0  // 右侧菜单宽度总和(最大滑动距离)
    private var mLimit = 0            // 滑动判定临界值

    private var mContentView: View? = null

    private var mLastP = PointF()
    private var mFirstP = PointF()
    private var isUnMoved = true
    private var isUserSwiped = false

    private var mExpandAnim: ValueAnimator? = null
    private var mCloseAnim: ValueAnimator? = null
    private var isExpand = false

    private var mVelocityTracker: VelocityTracker? = null

    var isSwipeEnable = true    // 右滑删除功能的开关,默认开
    var isIos = true            // IOS、QQ式交互，默认开
    var isLeftSwipe = true      // 左滑右滑的开关,默认左滑打开菜单
    private var iosInterceptFlag = false

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    fun setIos(ios: Boolean): SwipeMenuLayout {
        isIos = ios
        return this
    }

    fun setLeftSwipe(leftSwipe: Boolean): SwipeMenuLayout {
        isLeftSwipe = leftSwipe
        return this
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        mScaleTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mMaxVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity

        isSwipeEnable = true
        isIos = true
        isLeftSwipe = true

        val ta: TypedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.SwipeMenuLayout, defStyleAttr, 0
        )
        for (i in 0 until ta.indexCount) {
            val attr = ta.getIndex(i)
            when (attr) {
                R.styleable.SwipeMenuLayout_swipeEnable -> isSwipeEnable = ta.getBoolean(attr, true)
                R.styleable.SwipeMenuLayout_ios -> isIos = ta.getBoolean(attr, true)
                R.styleable.SwipeMenuLayout_leftSwipe -> isLeftSwipe = ta.getBoolean(attr, true)
            }
        }
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        isClickable = true

        mRightMenuWidths = 0
        mHeight = 0
        var contentWidth = 0
        val childCount = childCount

        val measureMatchParentChildren =
            MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY
        var isNeedMeasureChildHeight = false

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            childView.isClickable = true
            if (childView.visibility != GONE) {
                measureChild(childView, widthMeasureSpec, heightMeasureSpec)
                val lp = childView.layoutParams as MarginLayoutParams
                mHeight = maxOf(mHeight, childView.measuredHeight)
                if (measureMatchParentChildren && lp.height == LayoutParams.MATCH_PARENT) {
                    isNeedMeasureChildHeight = true
                }
                if (i > 0) {
                    mRightMenuWidths += childView.measuredWidth
                } else {
                    mContentView = childView
                    contentWidth = childView.measuredWidth
                }
            }
        }
        setMeasuredDimension(
            paddingLeft + paddingRight + contentWidth,
            mHeight + paddingTop + paddingBottom
        )
        mLimit = mRightMenuWidths * 4 / 10

        if (isNeedMeasureChildHeight) {
            forceUniformHeight(childCount, widthMeasureSpec)
        }
    }

    private fun forceUniformHeight(count: Int, widthMeasureSpec: Int) {
        val uniformMeasureSpec = MeasureSpec.makeMeasureSpec(
            measuredHeight, MeasureSpec.EXACTLY
        )
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                val lp = child.layoutParams as MarginLayoutParams
                if (lp.height == LayoutParams.MATCH_PARENT) {
                    val oldWidth = lp.width
                    lp.width = child.measuredWidth
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0)
                    lp.width = oldWidth
                }
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams =
        MarginLayoutParams(context, attrs)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        var left = 0 + paddingLeft
        var right = 0 + paddingLeft
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView.visibility != GONE) {
                if (i == 0) {
                    childView.layout(left, paddingTop,
                        left + childView.measuredWidth, paddingTop + childView.measuredHeight)
                    left += childView.measuredWidth
                } else {
                    if (isLeftSwipe) {
                        childView.layout(left, paddingTop,
                            left + childView.measuredWidth, paddingTop + childView.measuredHeight)
                        left += childView.measuredWidth
                    } else {
                        childView.layout(right - childView.measuredWidth, paddingTop,
                            right, paddingTop + childView.measuredHeight)
                        right -= childView.measuredWidth
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!isSwipeEnable) return super.dispatchTouchEvent(ev)

        acquireVelocityTracker(ev)
        val verTracker = mVelocityTracker

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isUserSwiped = false
                isUnMoved = true
                iosInterceptFlag = false
                if (isTouching) {
                    return false
                } else {
                    isTouching = true
                }
                mLastP.set(ev.rawX, ev.rawY)
                mFirstP.set(ev.rawX, ev.rawY)

                // 如果down，view和cacheview不一样，则立马让它还原
                val cache = mViewCache
                if (cache != null) {
                    if (cache !== this) {
                        cache.smoothClose()
                    }
                    // 只要有一个侧滑菜单处于打开状态，就不给外层布局上下滑动了
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                mPointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                if (!iosInterceptFlag) {
                    val gap = mLastP.x - ev.rawX
                    if (Math.abs(gap) > 10 || Math.abs(scrollX) > 10) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    if (Math.abs(gap) > mScaleTouchSlop) {
                        isUnMoved = false
                    }
                    scrollBy(gap.toInt(), 0)

                    // 越界修正
                    if (isLeftSwipe) {
                        if (scrollX < 0) scrollTo(0, 0)
                        if (scrollX > mRightMenuWidths) scrollTo(mRightMenuWidths, 0)
                    } else {
                        if (scrollX < -mRightMenuWidths) scrollTo(-mRightMenuWidths, 0)
                        if (scrollX > 0) scrollTo(0, 0)
                    }

                    mLastP.set(ev.rawX, ev.rawY)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (Math.abs(ev.rawX - mFirstP.x) > mScaleTouchSlop) {
                    isUserSwiped = true
                }

                if (!iosInterceptFlag) {
                    verTracker?.computeCurrentVelocity(1000, mMaxVelocity.toFloat())
                    val velocityX = verTracker?.getXVelocity(mPointerId) ?: 0f
                    if (Math.abs(velocityX) > 1000) {
                        if (velocityX < -1000) {
                            if (isLeftSwipe) smoothExpand() else smoothClose()
                        } else {
                            if (isLeftSwipe) smoothClose() else smoothExpand()
                        }
                    } else {
                        if (Math.abs(scrollX) > mLimit) {
                            smoothExpand()
                        } else {
                            smoothClose()
                        }
                    }
                }
                releaseVelocityTracker()
                isTouching = false
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isSwipeEnable) return super.onInterceptTouchEvent(ev)

        when (ev.action) {
            MotionEvent.ACTION_MOVE -> {
                if (Math.abs(ev.rawX - mFirstP.x) > mScaleTouchSlop) {
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isLeftSwipe) {
                    if (scrollX > mScaleTouchSlop) {
                        if (ev.x < width - scrollX) {
                            if (isUnMoved) smoothClose()
                            return true
                        }
                    }
                } else {
                    if (-scrollX > mScaleTouchSlop) {
                        if (ev.x > -scrollX) {
                            if (isUnMoved) smoothClose()
                            return true
                        }
                    }
                }
                if (isUserSwiped) return true
            }
        }

        if (iosInterceptFlag) return true
        if (sIsAnotherMenuClosing) return true

        return super.onInterceptTouchEvent(ev)
    }

    fun smoothExpand() {
        mViewCache = this

        mContentView?.isLongClickable = false

        cancelAnim()
        val target = if (isLeftSwipe) mRightMenuWidths else -mRightMenuWidths
        mExpandAnim = ValueAnimator.ofInt(scrollX, target).apply {
            addUpdateListener { animation ->
                scrollTo(animation.animatedValue as Int, 0)
            }
            interpolator = OvershootInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isExpand = true
                }
            })
            duration = 300
        }.also { it.start() }
    }

    private fun cancelAnim() {
        mCloseAnim?.let {
            if (it.isRunning) {
                it.cancel()
                sIsAnotherMenuClosing = false
            }
        }
        mExpandAnim?.let {
            if (it.isRunning) it.cancel()
        }
    }

    fun smoothClose() {
        mViewCache = null
        sIsAnotherMenuClosing = true

        mContentView?.isLongClickable = true

        cancelAnim()
        mCloseAnim = ValueAnimator.ofInt(scrollX, 0).apply {
            addUpdateListener { animation ->
                scrollTo(animation.animatedValue as Int, 0)
            }
            interpolator = AccelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isExpand = false
                    sIsAnotherMenuClosing = false
                }
            })
            duration = 300
        }.also { it.start() }
    }

    private fun acquireVelocityTracker(event: MotionEvent) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker?.addMovement(event)
    }

    private fun releaseVelocityTracker() {
        mVelocityTracker?.let {
            it.clear()
            it.recycle()
        }
        mVelocityTracker = null
    }

    override fun onDetachedFromWindow() {
        if (this === mViewCache) {
            mViewCache?.smoothClose()
            mViewCache = null
        }
        super.onDetachedFromWindow()
    }

    override fun performLongClick(): Boolean {
        if (Math.abs(scrollX) > mScaleTouchSlop) return false
        return super.performLongClick()
    }

    fun quickClose() {
        if (this === mViewCache) {
            cancelAnim()
            mViewCache?.scrollTo(0, 0)
            mViewCache = null
        }
    }
}
