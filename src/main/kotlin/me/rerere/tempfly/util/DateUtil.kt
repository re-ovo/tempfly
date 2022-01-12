package me.rerere.tempfly.util

import java.text.SimpleDateFormat
import java.util.*

private val dateFormat = SimpleDateFormat("yyyy/MM/dd")

fun today() = dateFormat.format(Date())