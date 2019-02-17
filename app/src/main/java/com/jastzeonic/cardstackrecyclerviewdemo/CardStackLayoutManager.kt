package com.jastzeonic.cardstackrecyclerviewdemo

import android.support.v7.widget.RecyclerView
import android.util.Log

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

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        var endLoop = false

        for (index in 0 until itemCount) {

            lastItemIndex = index

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
            val top = if (lastItemStartPoint == 0) {
                lp.topMargin
            } else {
                lastItemStartPoint
            }
            val right = lp.rightMargin + width
            val bottom = lastItemStartPoint + height
            // 5. layout View 本身
            layoutDecorated(view, left, top, right, bottom)
            if (endLoop) {
                break
            }
            endLoop = lastItemStartPoint > height

            lastItemStartPoint += if (index < itemCount - 1) {
                (height / 2)
            } else {
                height + lp.bottomMargin
            }
        }


        Log.v("CardStackLayoutManager", "child Count:$childCount")

    }

    private fun addViewFromStart(recycler: RecyclerView.Recycler) {

        if (firstVisibleItemIndex < 0) {
            return
        }

        // 1. 利用 position 挖出需要的 View
        val view = recycler.getViewForPosition(firstVisibleItemIndex)
        // 2. 把 View 塞進 RecyclerView 裏頭
        addView(view, 0)

        // 3. 測量(measure) View 的佈局(layout)資訊
        measureChildWithMargins(view, 0, 0)
        // 4. 取得測量後的資訊
        val width = getDecoratedMeasuredWidth(view)
        val height = getDecoratedMeasuredHeight(view)


        val lp = (view.layoutParams as RecyclerView.LayoutParams)

        val left = lp.leftMargin
        val right = lp.rightMargin + width
        val bottom = lp.topMargin + height + lp.bottomMargin
        // 5. layout View 本身
        layoutDecorated(view, left, firstItemStartPoint, right, bottom)

    }

    private fun addViewFromEnd(recycler: RecyclerView.Recycler) {

        if (lastItemIndex >= itemCount) {
            return
        }

        // 1. 利用 position 挖出需要的 View
        val view = recycler.getViewForPosition(lastItemIndex)
        // 2. 把 View 塞進 RecyclerView 裏頭
        addView(view)

        // 3. 測量(measure) View 的佈局(layout)資訊
        measureChildWithMargins(view, 0, 0)
        // 4. 取得測量後的資訊
        val width = getDecoratedMeasuredWidth(view)
        val height = getDecoratedMeasuredHeight(view)


        val lp = (view.layoutParams as RecyclerView.LayoutParams)

        val left = lp.leftMargin
        val top = lastItemStartPoint
        val right = lp.rightMargin + width
        val bottom = lastItemStartPoint + height + lp.bottomMargin
        // 5. layout View 本身
        layoutDecorated(view, left, top, right, bottom)


    }


    private var itemHeight = 0
    private var firstVisibleItemIndex = 0
    private var lastItemIndex = 0
    private var firstItemStartPoint = 0
    private var lastItemStartPoint = 0

    private fun dealCard(offset: Int, startMovementPosition: Int) {
        for (index in startMovementPosition..lastItemIndex) {

            val view = findViewByPosition(index) ?: return

            val left = getDecoratedLeft(view)
            val right = getDecoratedRight(view)
            val bottom = getDecoratedMeasuredHeight(view)
            val decoratedTop = getDecoratedTop(view)

            //這裡是為了應付第一個 item 的 Margin
            val top = if (index == startMovementPosition
                && decoratedTop - offset <= firstItemStartPoint
            ) {
                firstItemStartPoint
            } else {
                decoratedTop - offset
            }
            layoutDecorated(view, left, top, right, top + bottom)
            Log.v("CardStackLayoutManager", "width:${bottom - top}")
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
                    lastItemIndex--
                    addViewFromStart(recycler)
                    dy
                } else {
                    offsetCount = 0
                    0
                }
            }
            offsetCount >= itemHeight -> {
                offset = itemHeight - (offsetCount - dy)
                offsetCount = 0
                result = if (firstVisibleItemIndex < itemCount - 1) {
                    firstVisibleItemIndex++
                    lastItemIndex++
                    addViewFromEnd(recycler)
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
        removeViewOutOfRange(recycler)
        return result
    }

    private fun removeViewOutOfRange(recycler: RecyclerView.Recycler) {

        for (index in 0 until childCount) {
            val view = getChildAt(index) ?: return
            if (getPosition(view) < firstVisibleItemIndex || getPosition(view) > lastItemIndex) {
                removeAndRecycleView(view, recycler)
            }

        }
    }

    override fun canScrollVertically(): Boolean {
        return true
    }


}