package com.leomarkpaway.todoapp.common.util

import androidx.recyclerview.widget.ConcatAdapter


fun ConcatAdapter.getPositionAndAdapterId(): ArrayList<Pair<Int, String>> {
    val pairs = ArrayList<Pair<Int, String>>()
    for ((index, adapter) in this.adapters.withIndex()) {
        val pair = Pair(index, adapter.toString())
        pairs.add(pair)
    }
    return pairs
}