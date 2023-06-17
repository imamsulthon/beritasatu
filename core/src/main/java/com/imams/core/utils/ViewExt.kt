package com.imams.core.utils

import android.view.View
import androidx.appcompat.widget.AppCompatTextView

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun AppCompatTextView.setOrGone(label: String) {
    if (label.isEmpty()) {
        this.gone()
    } else {
        this.visible()
        this.text = label
    }
}