package kve.ru.firstproject.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.R
import kotlin.math.roundToInt

class FavoriteItemDecoration(
    private val context: Context,
    private val offset: Int
) :
    DividerItemDecoration(context, LinearLayoutManager.VERTICAL) {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        ContextCompat.getDrawable(context, R.drawable.divider_shape_vertical)?.let {
            drawVertical(c, parent, it)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val off = if (offset < 10) 10 else offset
        outRect.set(off, off, off, off)
    }

    private fun drawVertical(
        canvas: Canvas,
        parent: RecyclerView,
        mDivider: Drawable
    ) {
        canvas.save()
        val mBounds = Rect()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + child.translationY.roundToInt()
            val top = bottom - mDivider.intrinsicHeight
            mDivider.setBounds(
                left + 10,
                top,
                right - 10,
                bottom
            )
            mDivider.draw(canvas)
        }
        canvas.restore()
    }
}