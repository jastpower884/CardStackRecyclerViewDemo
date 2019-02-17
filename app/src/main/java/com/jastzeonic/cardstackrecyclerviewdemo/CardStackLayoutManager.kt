package com.jastzeonic.cardstackrecyclerviewdemo

import android.support.v7.widget.RecyclerView
import android.util.Log
import java.text.FieldPosition

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

            itemHeight = height / 2
            firstItemStartPoint = lp.topMargin
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

            // 這樣判斷是因為如果直接除 2
            // 尾巴會少一半
            totalHeight += if (index < itemCount - 1) {
                (height / 2)
            } else {
                height + lp.bottomMargin
            }
        }


        Log.v("CardStackLayoutManager", "child Count:$childCount")

    }


    private var itemHeight = 0
    private var firstVisibleItemIndex = 0
    private var firstItemStartPoint = 0

    private fun dealCard(dy: Int, startMovementPosition: Int) {

        for (index in startMovementPosition until itemCount) {
            val view = findViewByPosition(index) ?: return

            val left = getDecoratedLeft(view)
            val right = getDecoratedRight(view)
            val bottom = getDecoratedBottom(view) - dy
            val decoratedTop = getDecoratedTop(view)

            //這裡是為了應付第一個 item 的 Margin
            val top = if (index == startMovementPosition
                && decoratedTop - dy <= firstItemStartPoint
            ) {
                firstItemStartPoint
            } else {
                decoratedTop - dy

            }

            layoutDecorated(view, left, top, right, bottom)
        }
    }

    var offsetCount = 0

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State?): Int {
        offsetCount += dy
        val moveStartPosition = firstVisibleItemIndex + 1
        val result: Int
        val offset: Int
        when {
            offsetCount <= 0 -> {
                offset = dy + Math.abs(offsetCount)
                result = if (firstVisibleItemIndex != 0) {
                    offsetCount = itemHeight
                    firstVisibleItemIndex--
                    dy
                } else {
                    offsetCount = 0
                    0
                }
            }
            offsetCount >= itemHeight -> {
                offset = itemHeight - (offsetCount - dy)
                offsetCount = 0
                result = if (firstVisibleItemIndex < itemCount) {
                    firstVisibleItemIndex++
                    dy
                } else {
                    0
                }
            }
            else -> {
                offset = dy
                result = dy
            }
        }

        dealCard(offset, moveStartPosition)
        return result
    }

    override fun canScrollVertically(): Boolean {
        return true
    }


}