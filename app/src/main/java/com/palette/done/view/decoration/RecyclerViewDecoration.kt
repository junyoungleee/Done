package com.palette.done.view.decoration

import android.content.res.Resources
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.view.util.Util

class RecyclerViewDecoration(divWidth: Int) : RecyclerView.ItemDecoration() {

    var divWidth: Int = divWidth
    val util = Util()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.right = util.dpToPx(divWidth) // dp -> px
    }
}