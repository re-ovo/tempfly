package me.rerere.tempfly.util

import me.rerere.tempfly.locale
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration

private val dateFormat = SimpleDateFormat("yyyy/MM/dd")

fun today() = dateFormat.format(Date())

fun Duration.formatToString() = this.toString()
    .replace("h", locale("time_format.hour"))
    .replace("s", locale("time_format.second"))
    .replace("m", locale("time_format.minutes"))
    .replace("d", locale("time_format.day"))