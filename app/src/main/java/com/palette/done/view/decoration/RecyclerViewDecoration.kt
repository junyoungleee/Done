package com.palette.done.view.decoration

import android.content.res.Resources
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewDecoration(divWidth: Int) : RecyclerView.ItemDecoration() {

    var divWidth: Int = divWidth

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val density = Resources.getSystem().displayMetrics.density
        outRect.right = (divWidth * density).toInt()  // dp -> px
    }
}