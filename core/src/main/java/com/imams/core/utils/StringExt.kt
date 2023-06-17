package com.imams.core.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
fun String.simpleFormattedDate(): String {
    return try {
        val output = SimpleDateFormat("dd MMM yyyy, HH:mm:ss")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date: Date = sdf.parse(this)
        return output.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}