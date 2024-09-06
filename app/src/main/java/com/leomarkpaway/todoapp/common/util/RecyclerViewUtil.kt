package com.leomarkpaway.todoapp.common.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                left = space
            }
            right = space
        }
    }
}

class NonScrollableLinearHorizontal(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {
    override fun canScrollHorizontally(): Boolean {
        return false
    }
}

class NonScrollableLinearVertical(context: Context) : LinearLayoutManager(context, VERTICAL, false) {
    override fun canScrollHorizontally(): Boolean {
        return false
    }
}