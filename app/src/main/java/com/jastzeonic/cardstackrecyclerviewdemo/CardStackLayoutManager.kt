package com.jastzeonic.cardstackrecyclerviewdemo

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup

class CardStackLayoutManager(
    private var mItemHeightWidthRatio: Float = 0.9f,
    private var mScale: Float = 0.9f,
    private var mOrientation: Int = VERTICAL
) : RecyclerView.LayoutManager() {

    companion object {
        private val INVALIDATE_SCROLL_OFFSET = Integer.MAX_VALUE
        private val DEFAULT_CHILD_LAYOUT_OFFSET = 0.2f
        val UNLIMITED = 0
        val VERTICAL = 1
        val HORIZONTAL = 0
    }

    private var firstVisibleItemIndex = 0

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    // 方便堆疊計算，這邊加上一個 "總高度" 的變數
    private var totalHeight = 0

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        for (index in 0 until itemCount) {
            // 1. 利用 position 挖出需要的 View
            val view = recycler.getViewForPosition(index)
            // 2. 把 View 塞進 RecyclerView 裏頭
            addView(view)
            // 3. 測量(measure) View 的佈局(layout)資訊
            measureChildWithMargins(view, 0, 0)
            // 4. 取得測量後的資訊
            val width = getDecoratedMeasuredWidth(view)
            val height = getDecoratedMeasuredHeight(view)

            val lp = (view.layoutParams as RecyclerView.LayoutParams)

            val left = lp.leftMargin
            val top = if (totalHeight == 0) {
                lp.topMargin
            } else {
                totalHeight
            }
            val right = lp.rightMargin + width
            val bottom = totalHeight + height
            // 5. layout View 本身
            layoutDecorated(view, left, top, right, bottom)

            totalHeight += (height / 2)
        }


        Log.v("CardStackLayoutManager", "child Count:$childCount")

    }


    var offsetCount = 0

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {

        offsetCount += dy
        if (dy < 0) {
            return if (offsetCount <= 0) {
                // 這是因為 dy 滑動量可能會直接超過判斷量
                // 例如 offsetCount = 10 但 dy = 20 的情況
                // 這樣會於下 10 需要滑動
                // 這一步就是將 10 這個值算出來
                val remainingOffset = dy + Math.abs(offsetCount)
                offsetChildrenVertical(-remainingOffset)
                offsetCount = 0
                0
            } else {
                offsetChildrenVertical(-dy)
                dy
            }
        } else if (dy > 0) {
            return if (offsetCount >= (totalHeight - height)) {
                val remainingOffset = (totalHeight - height) - (offsetCount - dy)
                offsetChildrenVertical(-remainingOffset)
                offsetCount = totalHeight - height
                0
            } else {
                offsetChildrenVertical(-dy)
                dy
            }
        }
        return 0
    }

    override fun canScrollVertically(): Boolean {
        return true
    }


}